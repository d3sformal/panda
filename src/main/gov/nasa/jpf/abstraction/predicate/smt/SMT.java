package gov.nasa.jpf.abstraction.predicate.smt;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.AccessPathElement;
import gov.nasa.jpf.abstraction.common.AccessPathSubElement;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SMT {
	
	private static String SEPARATOR = "";
	
	private BufferedWriter in = null;
	private BufferedReader out = null;
	
	public SMT() throws IOException {
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
			
			throw e;
		}
	}
	
	private Boolean[] isSatisfiable(String input) throws IOException {
		List<Boolean> values = new ArrayList<Boolean>();
			
		String output = "";
		
		try {
			in.write(input);
			in.flush();
		} catch (IOException e) {
			System.err.println("SMT refuses input.");
			
			throw e;
		} finally {
			if (in != null) {
				in.close();
			}
		}

		try {
			while ((output = out.readLine()) != null) {
				values.add(output.matches("^unsat$"));
			}
		} catch (IOException e) {
			System.err.println("SMT refuses to provide output.");
			
			throw e;
		} finally {
			if (out != null) {
				out.close();
			}
		}
		
		return values.toArray(new Boolean[values.size()]);
	}

	private String prepareInput(Map<Predicate, PredicateDeterminant> predicates) {
		Set<String> vars = new HashSet<String>();
		Set<String> fields = new HashSet<String>();

		String input = "(set-logic QF_AUFLIA)" + SEPARATOR;
		
		input += "(declare-fun arr () (Array Int (Array Int Int)))" + SEPARATOR + SEPARATOR;

		/**
		 * Collect all variable and field names from all weakest preconditions
		 */
		for (Predicate predicate : predicates.keySet()) {
			collectVarsAndFields(vars, fields, predicates.get(predicate).positiveWeakestPrecondition);
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
		
		for (String var : vars) {
			input += "(declare-fun " + var + " () Int)" + SEPARATOR;
		}
		
		for (String field : fields) {
			input += "(declare-fun " + field + " (Int) Int)" + SEPARATOR;
		}

		for (Predicate predicate : predicates.keySet()) {
			PredicateDeterminant det = predicates.get(predicate);
			
			input +=
				SEPARATOR +
				"(push 1)" + SEPARATOR +
				"(assert (not " + predicateDeterminantToString(det.positiveWeakestPrecondition, det.determinants) + "))" + SEPARATOR +
				"(check-sat)" + SEPARATOR +
				"(pop 1)" + SEPARATOR +
				SEPARATOR +
				"(push 1)" + SEPARATOR +
				"(assert (not " + predicateDeterminantToString(det.negativeWeakestPrecondition, det.determinants) + "))" + SEPARATOR +
				"(check-sat)" + SEPARATOR +
				"(pop 1)" + SEPARATOR;
		}
		
		input += SEPARATOR + "(exit)" + SEPARATOR;
		
		return input;
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
	
	private static String predicateToString(Predicate predicate) {
		PredicatesSMTStringifier stringifier = new PredicatesSMTStringifier();
		
		predicate.accept(stringifier);
		
		return stringifier.getString();
	}
	
	private static String predicateDeterminantToString(Predicate weakestPrecondition, Map<Predicate, TruthValue> determinants) {
		String ret = "true";
		
		for (Predicate predicate : determinants.keySet()) {
			String condition = predicateToString(predicate);
			
			switch (determinants.get(predicate)) {
			case SATISFIABLE:
				ret = "(and " + ret + " " + condition + ")";
				break;
			case UNSATISFIABLE:
				ret = "(and " + ret + " (not " + condition + "))";
				break;
			default:
				/**
				 * UNKNOWN: (a or not(a)) ~ true ... redundant
				 * UNDEFINED: value cannot be affected by this predicate
				 */
				break;
			}
		}
		
		ret = "(=> " + ret + " " + predicateToString(weakestPrecondition) + ")";
		
		return ret;
	}
	
	public Map<Predicate, TruthValue> valuatePredicates(Map<Predicate, PredicateDeterminant> predicates) throws IOException {
		Map<Predicate, TruthValue> valuation = new HashMap<Predicate, TruthValue>();
		
		String input = prepareInput(predicates);
		
		Boolean[] sat = isSatisfiable(input);
		int i = 0;
		
		for (Predicate predicate : predicates.keySet()) {
			if (sat[i] && sat[i + 1]) {
				valuation.put(predicate, TruthValue.UNKNOWN);
			} else if (sat[i]) {
				valuation.put(predicate, TruthValue.SATISFIABLE);
			} else if (sat[i + 1]) {
				valuation.put(predicate, TruthValue.UNSATISFIABLE);
			} else {
				valuation.put(predicate, TruthValue.UNDEFINED);
			}
			
			i += 2;
		}
		
		return valuation;
	}
}
