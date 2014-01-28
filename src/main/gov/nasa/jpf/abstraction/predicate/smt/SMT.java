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
import gov.nasa.jpf.abstraction.common.Notation;

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
import java.util.Iterator;

/**
 * Class responsible for invocations of SMT, transformation of predicates into input that the SMT can solve
 */
public class SMT {
	
    private static boolean USE_CACHE = true;
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
	private static void notifyValuatePredicatesInputGenerated(String input) {
		for (SMTListener listener : listeners) {
			listener.valuatePredicatesInputGenerated(input);
		}
	}
	private static void notifyValuatePredicatesExecuted(Map<Predicate, TruthValue> valuation) {
		for (SMTListener listener : listeners) {
			listener.valuatePredicatesExecuted(valuation);
		}
	}

    private static enum InputType {
        NORMAL,
        DEBUG;

        public String getSeparator() {
            switch (this) {
                case NORMAL: return "";
                case DEBUG:  return "\n";
                default:     return null;
            }
        }
    }
    private static enum FormulaType {
        POSITIVE_WEAKEST_PRECONDITION_CHECK,
        NEGATIVE_WEAKEST_PRECONDITION_CHECK;

        @Override
        public String toString() {
            switch (this) {
                case POSITIVE_WEAKEST_PRECONDITION_CHECK: return "positive weakest precondition check";
                case NEGATIVE_WEAKEST_PRECONDITION_CHECK: return "negative weakest precondition check";
                default:                                  return null;
            }
        }
    }
	
	private BufferedWriter in = null;
	private BufferedReader out = null;
    private SMTCache cache = new SMTCache();
	
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

            String separator = InputType.NORMAL.getSeparator();

			in = new BufferedWriter(inwriter);
            in.write(
                "(set-logic QF_AUFLIA)" + separator +
		        "(declare-fun arr () (Array Int (Array Int Int)))" + separator +
    		    "(declare-fun arrlen () (Array Int Int))" + separator +
	    	    "(declare-fun fresh () Int)" + separator +
		        "(declare-fun null () Int)" + separator +
    		    separator
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

    private String prepareFormula(Predicate predicate, String formula, FormulaType formulaType, InputType inputType, Boolean cachedIsValid) {
        String separator = inputType.getSeparator();
        String linePrefix = cachedIsValid == null ? "" : "; ";
        StringBuilder ret = new StringBuilder();

        if (inputType == InputType.DEBUG) {
            ret.append("; Predicate: " + predicate.toString(Notation.DOT_NOTATION) + " (" + formulaType + ")\n");
        }

        if (inputType == InputType.DEBUG || cachedIsValid == null) {
            ret.append(linePrefix); ret.append("(push 1)"); ret.append(separator);
            ret.append(linePrefix); ret.append("(assert "); ret.append(formula); ret.append(")"); ret.append(separator);
		    ret.append(linePrefix); ret.append("(check-sat)"); ret.append(separator);
			ret.append(linePrefix); ret.append("(pop 1)"); ret.append(separator);
        }

		// when the cached value says "is valid" (boolean value of the corresponding parameter is 'true'),
		// the SMT solver must answer "unsat" and therefore input must be "assert false"

        if (inputType == InputType.NORMAL && cachedIsValid != null) {
            ret.append("(push 1)"); ret.append(separator);
            ret.append("(assert "); ret.append(cachedIsValid ? "false" : "true"); ret.append(")"); ret.append(separator);
            ret.append("(check-sat)"); ret.append(separator);
            ret.append("(pop 1)"); ret.append(separator);
        }

        if (inputType == InputType.DEBUG && cachedIsValid != null) {
            ret.append("; cached "); ret.append(cachedIsValid ? "unsat" : "sat"); ret.append(separator);
        }

        ret.append(separator);

        return ret.toString();
    }
	
	private String prepareInput(Set<String> classes, Set<String> vars, Set<String> fields, Set<AccessExpression> objects, List<Predicate> predicates, List<String> formulas, InputType inputType) {
        String separator = inputType.getSeparator();

		StringBuilder input = new StringBuilder();

        input.append("(push 1)"); input.append(separator);

		for (String c : classes) {
			input.append("(declare-fun class_"); input.append(c.replace("_", "__").replace('.', '_')); input.append(" () Int)"); input.append(separator);
		}
		input.append(separator);
		
		for (String var : vars) {
			input.append("(declare-fun var_"); input.append(var); input.append(" () Int)"); input.append(separator);
		}
		input.append(separator);
		
		for (String field : fields) {
			input.append("(declare-fun field_"); input.append(field); input.append(" () (Array Int Int))"); input.append(separator);
		}
		input.append(separator);
		
		for (AccessExpression object : objects) {
			Predicate distinction = Implication.create(Negation.create(object.getPreconditionForBeingFresh()), Negation.create(Equals.create(DefaultFresh.create(), object)));
			
			input.append("(assert "); input.append(convertToString(distinction)); input.append(")"); input.append(separator);
		}
		input.append(separator);

        Iterator<Predicate> predicatesIterator = predicates.iterator();
        Iterator<String> formulasIterator = formulas.iterator();

		while (predicatesIterator.hasNext()) {
            Predicate predicate = predicatesIterator.next();

            String positiveWeakestPreconditionFormula = formulasIterator.next();
            String negativeWeakestPreconditionFormula = formulasIterator.next();

            Boolean cachedPositiveValue = cache.get(positiveWeakestPreconditionFormula);
            Boolean cachedNegativeValue = cache.get(negativeWeakestPreconditionFormula);

			input.append(prepareFormula(predicate, positiveWeakestPreconditionFormula, FormulaType.POSITIVE_WEAKEST_PRECONDITION_CHECK, inputType, cachedPositiveValue));
			input.append(prepareFormula(predicate, negativeWeakestPreconditionFormula, FormulaType.NEGATIVE_WEAKEST_PRECONDITION_CHECK, inputType, cachedNegativeValue));
		}
		
        input.append("(pop 1)");
		
		return input.toString();
	}

	public Map<Predicate, TruthValue> valuatePredicates(Map<Predicate, PredicateValueDeterminingInfo> predicates) throws SMTException {
		notifyValuatePredicatesInvoked(predicates);

        List<Predicate> predicatesList = new LinkedList<Predicate>();
		List<String> formulasList = new LinkedList<String>();
		
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
			
            predicatesList.add(predicate);

			formulasList.add(createFormula(det.positiveWeakestPrecondition, det.determinants, additionalPredicates));
			formulasList.add(createFormula(det.negativeWeakestPrecondition, det.determinants, additionalPredicates));
		}
		
		Set<String> classes = collector.getClasses();
		Set<String> vars = collector.getVars();
		Set<String> fields = collector.getFields();
		Set<AccessExpression> objects = collector.getObjects();
		
		String input = prepareInput(classes, vars, fields, objects, predicatesList, formulasList, InputType.NORMAL);
		String debugInput = prepareInput(classes, vars, fields, objects, predicatesList, formulasList, InputType.DEBUG);

		notifyValuatePredicatesInputGenerated(debugInput);
		
        return evaluate(input, debugInput, predicatesList, formulasList);
	}
	
	public Map<Predicate, TruthValue> valuatePredicates(Set<Predicate> predicates) throws SMTException {
        Map<Predicate, PredicateValueDeterminingInfo> predicateDeterminingInfos = new HashMap<Predicate, PredicateValueDeterminingInfo>();

        for (Predicate predicate : predicates) {
            PredicateValueDeterminingInfo determiningInfo = new PredicateValueDeterminingInfo(predicate, Negation.create(predicate), new HashMap<Predicate, TruthValue>());

            predicateDeterminingInfos.put(predicate, determiningInfo);
        }

        return valuatePredicates(predicateDeterminingInfos);
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
	
	private Map<Predicate, TruthValue> evaluate(String input, String debugInput, List<Predicate> predicates, List<String> formulas) throws SMTException {
		Map<Predicate, TruthValue> valuation = new HashMap<Predicate, TruthValue>();
		
		Boolean[] valid;

        try {
    		valid = isValid(predicates.size(), input);
        } catch (SMTException e) {
    	    throw new SMTException("SMT failed on:\n" + debugInput + "\n" + e.getMessage());
        }

		int i = 0;

        Iterator<Predicate> predicatesIterator = predicates.iterator();
        Iterator<String> formulasIterator = formulas.iterator();
		
		while (predicatesIterator.hasNext()) {
            Predicate predicate = predicatesIterator.next();
            String positiveWeakestPrecondition = formulasIterator.next();
            String negativeWeakestPrecondition = formulasIterator.next();

			if (valid[i] && valid[i + 1]) {
				valuation.put(predicate, TruthValue.UNKNOWN);
			} else if (valid[i]) {
				valuation.put(predicate, TruthValue.TRUE);
			} else if (valid[i + 1]) {
				valuation.put(predicate, TruthValue.FALSE);
			} else {
				valuation.put(predicate, TruthValue.UNKNOWN);
			}

            if (USE_CACHE) {
                cache.put(positiveWeakestPrecondition, valid[i]);
                cache.put(negativeWeakestPrecondition, valid[i + 1]);
            }
			
			i += 2;
		}
		
		notifyValuatePredicatesExecuted(valuation);
		
		return valuation;
	}
}
