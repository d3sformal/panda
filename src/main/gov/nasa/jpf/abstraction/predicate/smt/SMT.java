package gov.nasa.jpf.abstraction.predicate.smt;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.PredicatesVisitable;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SMT {
	
	private static List<SMTListener> listeners = new LinkedList<SMTListener>();
	
	public static void registerListener(SMTListener listener) {
		listeners.add(listener);
	}
	
	private static void valuatePredicatesInvoked(Map<Predicate, PredicateDeterminant> predicates) {
		for (SMTListener listener : listeners) {
			listener.valuatePredicatesInvoked(predicates);
		}
	}
	private static void valuatePredicatesInvoked(Set<Predicate> predicates) {
		for (SMTListener listener : listeners) {
			listener.valuatePredicatesInvoked(predicates);
		}
	}
	private static void valuatePredicatesInputGenerated(String input) {
		for (SMTListener listener : listeners) {
			listener.valuatePredicatesInputGenerated(input);
		}
	}
	private static void valuatePredicatesExecuted(Map<Predicate, TruthValue> valuation) {
		for (SMTListener listener : listeners) {
			listener.valuatePredicatesExecuted(valuation);
		}
	}
	
	private static String SEPARATOR = "";
	private static String DEBUG_SEPARATOR = "\n";
	
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
	
	private String prepareInput(Set<String> vars, Set<String> fields, Set<AccessExpression> objects, List<String> formulas, String separator) {
		String input = "(set-logic QF_AUFLIA)" + separator;
		
		input += "(declare-fun arr () (Array Int (Array Int Int)))" + separator;
		input += "(declare-fun arrlen () (Array Int Int))" + separator;
		input += "(declare-fun fresh () Int)" + separator;
		input += separator;
		
		for (String var : vars) {
			input += "(declare-fun var_" + var + " () Int)" + separator;
		}
		input += separator;
		
		for (String field : fields) {
			input += "(declare-fun field_" + field + " () (Array Int Int))" + separator;
		}
		input += separator;
		
		for (AccessExpression object : objects) {
			input += "(assert (distinct fresh " + convertToString(object) + "))" + separator;
		}
		input += separator;

		for (String formula : formulas) {
			input +=
				"(push 1)" + separator +
				"(assert " + formula + ")" + separator +
				"(check-sat)" + separator +
				"(pop 1)" + separator +
				separator;
		}
		
		input += "(exit)" + separator;
		
		return input;
	}

	private String prepareInput(Map<Predicate, PredicateDeterminant> predicates, String separator) {
		List<String> formulas = new LinkedList<String>();
		
		PredicatesSMTInfoCollector collector = new PredicatesSMTInfoCollector();

		/**
		 * Collect all variable and field names from all weakest preconditions
		 */
		for (Predicate predicate : predicates.keySet()) {
			//predicate.accept(collector);
			collector.collect(predicates.get(predicate).positiveWeakestPrecondition);
			collector.collect(predicates.get(predicate).negativeWeakestPrecondition);
		}
		
		/**
		 * Collect all variable and field names from all relevant 
		 */
		for (Predicate predicate : predicates.keySet()) {
			Set<Predicate> determinants = predicates.get(predicate).determinants.keySet();
			
			for (Predicate determinant : determinants) {
				collector.collect(determinant);
			}
		}
				
		for (Predicate predicate : predicates.keySet()) {
			PredicateDeterminant det = predicates.get(predicate);
			
			Set<Predicate> additionalPredicates = collector.getAdditionalPredicates(predicate);
			
			formulas.add(createFormula(det.positiveWeakestPrecondition, det.determinants, additionalPredicates));
			formulas.add(createFormula(det.negativeWeakestPrecondition, det.determinants, additionalPredicates));
		}
		
		Set<String> vars = collector.getVars();
		Set<String> fields = collector.getFields();
		Set<AccessExpression> objects = collector.getObjects();
		
		return prepareInput(vars, fields, objects, formulas, separator);
	}
	
	private String prepareInput(Set<Predicate> predicates, String separator) {
		List<String> formulas = new LinkedList<String>();
		
		PredicatesSMTInfoCollector collector = new PredicatesSMTInfoCollector();

		/**
		 * Collect all variable and field names from all weakest preconditions
		 */
		for (Predicate predicate : predicates) {
			collector.collect(predicate);
		}
				
		for (Predicate predicate : predicates) {
			Set<Predicate> additionalPredicates = collector.getAdditionalPredicates(predicate);
			
			formulas.add(createFormula(predicate, additionalPredicates));
			formulas.add(createFormula(Negation.create(predicate), additionalPredicates));
		}
		
		Set<String> vars = collector.getVars();
		Set<String> fields = collector.getFields();
		Set<AccessExpression> objects = collector.getObjects();
		
		return prepareInput(vars, fields, objects, formulas, separator);
	}
	
	private static String createFormula(Predicate predicate, Set<Predicate> additionalClauses) {
		PredicatesSMTStringifier stringifier = new PredicatesSMTStringifier();
		
		Predicate formula = Negation.create(predicate);
		
		for (Predicate clause : additionalClauses) {
			formula = Conjunction.create(formula, clause);
		}
		
		formula.accept(stringifier);
		
		return stringifier.getString();
	}
	
	private static String convertToString(PredicatesVisitable visitable) {
		PredicatesSMTStringifier stringifier = new PredicatesSMTStringifier();
		
		visitable.accept(stringifier);
		
		return stringifier.getString();
	}
	
	private static String createFormula(Predicate weakestPrecondition, Map<Predicate, TruthValue> determinants, Set<Predicate> additionalClauses) {
		Predicate formula = Tautology.create();
		
		for (Predicate clause : additionalClauses) {
			formula = Conjunction.create(formula, clause);
		}
		
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
		
		return convertToString(Negation.create(formula));
	}
	
	public Map<Predicate, TruthValue> valuatePredicates(Map<Predicate, PredicateDeterminant> predicates) throws SMTException {
		valuatePredicatesInvoked(predicates);
		
		return evaluate(predicates.keySet(), prepareInput(predicates, SEPARATOR), prepareInput(predicates, DEBUG_SEPARATOR));
	}
	
	public Map<Predicate, TruthValue> valuatePredicates(Set<Predicate> predicates) throws SMTException {
		valuatePredicatesInvoked(predicates);
		
		return evaluate(predicates, prepareInput(predicates, SEPARATOR), prepareInput(predicates, DEBUG_SEPARATOR));
	}

	private Map<Predicate, TruthValue> evaluate(Set<Predicate> predicates, String input, String debugInput) {
		valuatePredicatesInputGenerated(debugInput);
		
		Map<Predicate, TruthValue> valuation = new HashMap<Predicate, TruthValue>();
		
		Boolean[] valid;
		
		try {
			valid = isValid(input);
		} catch (SMTException e) {
			throw new SMTException("SMT failed on:\n" + debugInput + "\n" + e.getMessage());
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
		
		valuatePredicatesExecuted(valuation);
		
		return valuation;
	}
}
