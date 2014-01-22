package gov.nasa.jpf.abstraction.predicate.smt;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultFresh;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitable;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Implication;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.UpdatedPredicate;
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

/**
 * Class responsible for invocations of SMT, transformation of predicates into input that the SMT can solve
 */
public class SMT {
	
	private static List<SMTListener> listeners = new LinkedList<SMTListener>();
	
	public static void registerListener(SMTListener listener) {
		listeners.add(listener);
	}
	
	private static void notifyValuatePredicatesInvoked(Map<Predicate, PredicateValueDeterminingInfo> predicates) {
		for (SMTListener listener : listeners) {
			listener.valuatePredicatesInvoked(predicates);
		}
	}
	private static void notifyValuatePredicatesInvoked(Set<Predicate> predicates) {
		for (SMTListener listener : listeners) {
			listener.valuatePredicatesInvoked(predicates);
		}
	}
	private static void notifyValuatePredicatesInputGenerated(Set<Predicate> predicates, String input) {
		for (SMTListener listener : listeners) {
			listener.valuatePredicatesInputGenerated(predicates, input);
		}
	}
	private static void notifyValuatePredicatesExecuted(Map<Predicate, TruthValue> valuation) {
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
            in.write(
                "(set-logic QF_AUFLIA)" + SEPARATOR +
		        "(declare-fun arr () (Array Int (Array Int Int)))" + SEPARATOR +
    		    "(declare-fun arrlen () (Array Int Int))" + SEPARATOR +
	    	    "(declare-fun fresh () Int)" + SEPARATOR +
		        "(declare-fun null () Int)" + SEPARATOR +
    		    SEPARATOR
            );
            in.flush();

			InputStream outstream = mathsat.getInputStream();
			InputStreamReader outreader = new InputStreamReader(outstream);

			out = new BufferedReader(outreader);
		} catch (IOException e) {
			System.err.println("SMT will not start.");
			
			throw new SMTException(e);
		}
	}
    
    public void close() {
        try {
            in.write("(exit)");
            in.flush();
            in.close();
        } catch (IOException e) {
            System.err.println("SMT would not terminate");

            throw new SMTException(e);
        }
        try {
            out.close();
        } catch (IOException e) {
            System.err.println("SMT would not terminate");

            throw new SMTException(e);
        }
    }
	
	private Boolean[] isValid(int count, String input) throws SMTException {
		List<Boolean> values = new ArrayList<Boolean>();
			
		String output = "";
		
		try {
			in.write(input);
			in.flush();
		} catch (IOException e) {
			System.err.println("SMT refuses input.");
			
			throw new SMTException(e);
		}

		try {
            for (int i = 0; i < 2 * count; ++i) {
                output = out.readLine();
				if (output == null || !output.matches("^(un)?sat$")) {
					throw new SMTException("SMT replied with '" + output + "'");
				}
				values.add(output.matches("^unsat$"));
			}
		} catch (IOException e) {
			System.err.println("SMT refuses to provide output.");
			
			throw new SMTException(e);
		}

		return values.toArray(new Boolean[values.size()]);
	}
	
	private String prepareInput(Set<String> classes, Set<String> vars, Set<String> fields, Set<AccessExpression> objects, List<String> formulas, String separator) {
		String input = "(push 1)";

		for (String c : classes) {
			input += "(declare-fun class_" + c.replace("_", "__").replace('.', '_') + " () Int)" + separator;
		}
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
			Predicate distinction = Implication.create(Negation.create(object.getPreconditionForBeingFresh()), Negation.create(Equals.create(DefaultFresh.create(), object)));
			
			input += "(assert " + convertToString(distinction) + ")" + separator;
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
		
        input += "(pop 1)";
		
		return input;
	}

	private String prepareInput(Map<Predicate, PredicateValueDeterminingInfo> predicates, String separator) {
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
			PredicateValueDeterminingInfo det = predicates.get(predicate);
			
			Set<Predicate> additionalPredicates = collector.getAdditionalPredicates(predicate);
			
			formulas.add(createFormula(det.positiveWeakestPrecondition, det.determinants, additionalPredicates));
			formulas.add(createFormula(det.negativeWeakestPrecondition, det.determinants, additionalPredicates));
		}
		
		Set<String> classes = collector.getClasses();
		Set<String> vars = collector.getVars();
		Set<String> fields = collector.getFields();
		Set<AccessExpression> objects = collector.getObjects();
		
		return prepareInput(classes, vars, fields, objects, formulas, separator);
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
		
		Set<String> classes = collector.getClasses();
		Set<String> vars = collector.getVars();
		Set<String> fields = collector.getFields();
		Set<AccessExpression> objects = collector.getObjects();
		
		return prepareInput(classes, vars, fields, objects, formulas, separator);
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
	
	private static String convertToString(PredicatesComponentVisitable visitable) {
		PredicatesSMTStringifier stringifier = new PredicatesSMTStringifier();
		
		visitable.accept(stringifier);
		
		return stringifier.getString();
	}
	
	private static String createFormula(Predicate weakestPrecondition, Map<Predicate, TruthValue> determinants, Set<Predicate> additionalClauses) {
		Predicate formula = Tautology.create();
		
		while (weakestPrecondition instanceof UpdatedPredicate) {
			weakestPrecondition = ((UpdatedPredicate) weakestPrecondition).apply();
		}
		
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
	
	public Map<Predicate, TruthValue> valuatePredicates(Map<Predicate, PredicateValueDeterminingInfo> predicates) throws SMTException {
		notifyValuatePredicatesInvoked(predicates);

    	return evaluate(predicates.keySet(), prepareInput(predicates, SEPARATOR), prepareInput(predicates, DEBUG_SEPARATOR));
	}
	
	public Map<Predicate, TruthValue> valuatePredicates(Set<Predicate> predicates) throws SMTException {
		notifyValuatePredicatesInvoked(predicates);

   		return evaluate(predicates, prepareInput(predicates, SEPARATOR), prepareInput(predicates, DEBUG_SEPARATOR));
	}

	private Map<Predicate, TruthValue> evaluate(Set<Predicate> predicates, String input, String debugInput) throws SMTException {
		notifyValuatePredicatesInputGenerated(predicates, debugInput);
		
		Map<Predicate, TruthValue> valuation = new HashMap<Predicate, TruthValue>();
		
		Boolean[] valid;
		
        try {
    		valid = isValid(predicates.size(), input);
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
		
		notifyValuatePredicatesExecuted(valuation);
		
		return valuation;
	}
}
