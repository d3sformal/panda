package gov.nasa.jpf.abstraction.predicate.smt;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.AccessPathElement;
import gov.nasa.jpf.abstraction.common.AccessPathSubElement;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.predicate.common.Conjunction;
import gov.nasa.jpf.abstraction.predicate.common.Implication;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.common.Tautology;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SMT {
	
	private static String SEPARATOR = "";
	
	private BufferedWriter in = null;
	private BufferedReader out = null;
	
	public SMT() throws SMTException {
		try {
			String[] args = new String[] {
				System.getProperty("user.dir") + "/bin/mathsat",
				"-theory.arr.enabled=true"
			};
			String[] env  = new String[] {};

			Process mathsat = Runtime.getRuntime().exec(args, env);

			OutputStream instream = mathsat.getOutputStream();
			OutputStreamWriter inwriter = new OutputStreamWriter(instream);

			in = new BufferedWriter(inwriter);

			InputStream outstream = mathsat.getInputStream();
			InputStreamReader outreader = new InputStreamReader(outstream);

			out = new BufferedReader(outreader);
		} catch (IOException e) {
			System.err.println("SMT will not start.");
			
			throw new SMTException(e);
		}
	}
	
	private Boolean[] isValid(String input) throws SMTException {
		List<Boolean> values = new ArrayList<Boolean>();
			
		String output = "";
		
		try {
			in.write(input);
			in.flush();
		} catch (IOException e) {
			System.err.println("SMT refuses input.");
			
			throw new SMTException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw new SMTException(e);
				}
			}
		}

		try {
			while ((output = out.readLine()) != null) {
				if (!output.matches("^(un)?sat$")) {
					throw new SMTException("SMT replied with '" + output + "'");
				}
				values.add(output.matches("^unsat$"));
			}
		} catch (IOException e) {
			System.err.println("SMT refuses to provide output.");
			
			throw new SMTException(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					throw new SMTException(e);
				}
			}
		}
		
		return values.toArray(new Boolean[values.size()]);
	}
	
	private String prepareInput(Set<String> vars, Set<String> fields, List<String> formulas, String separator) {
		String input = "(set-logic QF_AUFLIA)" + separator;
		
		input += "(declare-fun arr () (Array Int (Array Int Int)))" + separator;
		input += separator;
		
		for (String var : vars) {
			input += "(declare-fun " + var + " () Int)" + separator;
		}
		
		for (String field : fields) {
			input += "(declare-fun " + field + " () (Array Int Int))" + separator;
		}

		for (String formula : formulas) {
			input +=
				separator +
				"(push 1)" + separator +
				"(assert (not " + formula + "))" + separator +
				"(check-sat)" + separator +
				"(pop 1)" + separator +
				separator;
		}
		
		input += separator + "(exit)" + separator;
		
		return input;
	}

	private String prepareInput(Map<Predicate, PredicateDeterminant> predicates, String separator) {
		Set<String> vars = new HashSet<String>();
		Set<String> fields = new HashSet<String>();
		List<String> formulas = new LinkedList<String>();

		/**
		 * Collect all variable and field names from all weakest preconditions
		 */
		for (Predicate predicate : predicates.keySet()) {
			collectVarsAndFields(vars, fields, predicates.get(predicate).positiveWeakestPrecondition);
			collectVarsAndFields(vars, fields, predicates.get(predicate).negativeWeakestPrecondition);
		}
		
		/**
		 * Collect all variable and field names from all relevant 
		 */
		for (Predicate predicate : predicates.keySet()) {
			Set<Predicate> determinants = predicates.get(predicate).determinants.keySet();
			
			for (Predicate determinant : determinants) {
				collectVarsAndFields(vars, fields, determinant);
			}
		}
		
		for (Predicate predicate : predicates.keySet()) {
			PredicateDeterminant det = predicates.get(predicate);
			
			formulas.add(createFormula(det.positiveWeakestPrecondition, det.determinants));
			formulas.add(createFormula(det.negativeWeakestPrecondition, det.determinants));
		}
		
		return prepareInput(vars, fields, formulas, separator);
	}
	
	private String prepareInput(Set<Predicate> predicates, String separator) {
		Set<String> vars = new HashSet<String>();
		Set<String> fields = new HashSet<String>();
		List<String> formulas = new LinkedList<String>();

		/**
		 * Collect all variable and field names from all weakest preconditions
		 */
		for (Predicate predicate : predicates) {
			collectVarsAndFields(vars, fields, predicate);
		}
		
		for (Predicate predicate : predicates) {		
			formulas.add(createFormula(predicate));
			formulas.add(createFormula(Negation.create(predicate)));
		}
		
		return prepareInput(vars, fields, formulas, separator);
	}

	private void collectVarsAndFields(Set<String> vars, Set<String> fields, Predicate predicate) {
		for (AccessPath path : predicate.getPaths()) {
			AccessPathElement element = path.getRoot();
			
			vars.add("var_" + path.getRoot().getName());
			
			while (element != null) {
				if (element instanceof AccessPathSubElement) {
					AccessPathSubElement subElement = (AccessPathSubElement) element;

					fields.add("field_" + subElement.getName());
				}
				
				element = element.getNext();
			}
		}
	}
	
	private static String createFormula(Predicate predicate) {
		PredicatesSMTStringifier stringifier = new PredicatesSMTStringifier();
		
		predicate.accept(stringifier);
		
		return stringifier.getString();
	}
	
	private static String createFormula(Predicate weakestPrecondition, Map<Predicate, TruthValue> determinants) {
		PredicatesSMTStringifier stringifier = new PredicatesSMTStringifier();

		Predicate formula = Tautology.create();
		
		for (Predicate predicate : determinants.keySet()) {		
			switch (determinants.get(predicate)) {
			case TRUE:
				formula = Conjunction.create(formula, predicate);
				break;
			case FALSE:
				formula = Conjunction.create(formula, Negation.create(predicate));
				break;
			default:
				/**
				 * UNKNOWN: (a or not(a)) ~ true ... redundant
				 * UNDEFINED: value cannot be affected by this predicate
				 */
				break;
			}
		}
		
		formula = Implication.create(formula, weakestPrecondition);
		
		formula.accept(stringifier);
				
		return stringifier.getString();
	}
	
	public Map<Predicate, TruthValue> valuatePredicates(Map<Predicate, PredicateDeterminant> predicates) throws SMTException {	
		String input = prepareInput(predicates, SEPARATOR);
	
		return evaluate(predicates.keySet(), input);
	}
	
	public Map<Predicate, TruthValue> valuatePredicates(Set<Predicate> predicates) throws SMTException {
		String input = prepareInput(predicates, SEPARATOR);
		
		return evaluate(predicates, input);
	}

	private Map<Predicate, TruthValue> evaluate(Set<Predicate> predicates, String input) {
		Map<Predicate, TruthValue> valuation = new HashMap<Predicate, TruthValue>();
		
		Boolean[] valid;
		
		try {
			valid = isValid(input);
		} catch (SMTException e) {
			throw new SMTException("SMT failed on:\n" + prepareInput(predicates, "\n") + "\n" + e.getMessage());
		}

		int i = 0;
		
		for (Predicate predicate : predicates) {
			if (valid[i] && valid[i + 1]) {
				valuation.put(predicate, TruthValue.UNKNOWN);
			} else if (valid[i]) {
				valuation.put(predicate, TruthValue.TRUE);
			} else if (valid[i + 1]) {
				valuation.put(predicate, TruthValue.FALSE);
			} else {
				valuation.put(predicate, TruthValue.UNKNOWN);
			}
			
			i += 2;
		}
		
		return valuation;
	}
}
