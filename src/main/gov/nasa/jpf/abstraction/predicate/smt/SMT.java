package gov.nasa.jpf.abstraction.predicate.smt;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.SpecialVariable;
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
import gov.nasa.jpf.abstraction.util.Pair;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Collections;

/**
 * Class responsible for invocations of SMT, transformation of predicates into input that the SMT can solve
 */
public class SMT {
	
    private static boolean USE_CACHE = true;
	private static List<SMTListener> listeners = new LinkedList<SMTListener>();
	
	public static void registerListener(SMTListener listener) {
		listeners.add(listener);
	}
	
    // Notify about invocations
	private static void notifyIsSatisfiableInvoked(List<Predicate> formulas) {
		for (SMTListener listener : listeners) {
			listener.isSatisfiableInvoked(formulas);
		}
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
	private static void notifyGetModelInvoked(Expression expression, List<Pair<Predicate, TruthValue>> determinants) {
		for (SMTListener listener : listeners) {
			listener.getModelInvoked(expression, determinants);
		}
	}

    // Notify about generation of input
	private static void notifyIsSatisfiableInputGenerated(String input) {
		for (SMTListener listener : listeners) {
			listener.isSatisfiableInputGenerated(input);
		}
	}
	private static void notifyValuatePredicatesInputGenerated(String input) {
		for (SMTListener listener : listeners) {
			listener.valuatePredicatesInputGenerated(input);
		}
	}
	private static void notifyGetModelInputGenerated(String input) {
		for (SMTListener listener : listeners) {
			listener.getModelInputGenerated(input);
		}
	}

    // Notify about finished execution
	private static void notifyIsSatisfiableExecuted(List<Predicate> formulas, boolean[] satisfiable) {
		for (SMTListener listener : listeners) {
			listener.isSatisfiableExecuted(formulas, satisfiable);
		}
	}
	private static void notifyValuatePredicatesExecuted(Map<Predicate, TruthValue> valuation) {
		for (SMTListener listener : listeners) {
			listener.valuatePredicatesExecuted(valuation);
		}
	}
	private static void notifyGetModelExecuted(Boolean satisfiability, Integer model) {
		for (SMTListener listener : listeners) {
			listener.getModelExecuted(satisfiability, model);
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
        NEGATIVE_WEAKEST_PRECONDITION_CHECK,
        MODEL_QUERY;

        @Override
        public String toString() {
            switch (this) {
                case POSITIVE_WEAKEST_PRECONDITION_CHECK: return "positive weakest precondition check";
                case NEGATIVE_WEAKEST_PRECONDITION_CHECK: return "negative weakest precondition check";
                case MODEL_QUERY:                         return "model query";
                default:                                  return null;
            }
        }
    }
    public static class QueryResponse {
        private Boolean satisfiable;
        private Integer model;

        public QueryResponse(Boolean satisfiable, Integer model) {
            this.satisfiable = satisfiable;
            this.model = model;
        }

        public Boolean getSatisfiability() {
            return satisfiable;
        }

        public Integer getModel() {
            return model;
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
                "(set-option :produce-models true)" + separator +
                "(set-logic QF_AUFLIA)" + separator +

                // Memory model symbols
		        "(declare-fun arr () (Array Int (Array Int Int)))" + separator +
    		    "(declare-fun arrlen () (Array Int Int))" + separator +
	    	    "(declare-fun fresh () Int)" + separator + // new object

                // Language symbols
		        "(declare-fun null () Int)" + separator +  // java null
                "(assert (= null 0))" + separator +

                // Special variables
		        "(declare-fun dummy () Int)" + separator + // for emulating variables in native returns

                // Auxiliary logical variables
		        "(declare-fun value () Int)" + separator + // for extracting models

                // Uninterpreted functions
		        "(declare-fun shl (Int Int) Int)" + separator +
		        "(declare-fun shr (Int Int) Int)" + separator +

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
	
	private QueryResponse[] isSatisfiable(int count, String input, boolean extractModels) throws SMTException {
		QueryResponse[] values = new QueryResponse[count];
			
		String output = "";
		
		try {
            // Avoid flushing / sync / etc. on pipe to the SMT
            if (count > 0) {
    			in.write(input);
    			in.flush();
            }
		} catch (IOException e) {
			System.err.println("SMT refuses input.");
			
			throw new SMTException(e);
		}

		try {
            for (int i = 0; i < count; ++i) {
                output = out.readLine();
				if (output == null || !output.matches("^(un)?sat$")) {
					throw new SMTException("SMT replied with '" + output + "'");
				}

				Boolean satisfiable = output.matches("^sat$");
                Integer model = null;

                if (extractModels) {
			        if (satisfiable) {
                        output = out.readLine();

                        Pattern pattern = Pattern.compile("^\\( \\(value (?<value>[0-9]*)\\) \\)$");
                        Matcher matcher = pattern.matcher(output);

                        if (matcher.matches()) {
                            String value = matcher.group("value");
                            model = Integer.valueOf(value);
                        }
                    } else {
                        out.readLine();
                    }
                }

                values[i] = new QueryResponse(satisfiable, model);
			}
		} catch (IOException e) {
			System.err.println("SMT refuses to provide output.");
			
			throw new SMTException(e);
		}

		return values;
	}

    private String prepareFormulaEvaluation(Predicate predicate, String formula, FormulaType formulaType, InputType inputType, Boolean cachedIsSatisfiable, Integer cachedModel, boolean extractModel) {
        String separator = inputType.getSeparator();
        String linePrefix = cachedIsSatisfiable == null ? "" : "; ";
        StringBuilder ret = new StringBuilder();

        if (inputType == InputType.DEBUG) {
            ret.append("; Predicate: " + predicate.toString(Notation.DOT_NOTATION) + " (" + formulaType + ")\n");
        }

        // The actual call to SMT (formula, model retrieval)
        if (inputType == InputType.DEBUG || cachedIsSatisfiable == null) {
            ret.append(linePrefix); ret.append("(push 1)"); ret.append(separator);
            ret.append(linePrefix); ret.append("(assert "); ret.append(formula); ret.append(")"); ret.append(separator);
		    ret.append(linePrefix); ret.append("(check-sat)"); ret.append(separator);

            if (extractModel) {
    		    ret.append(linePrefix); ret.append("(get-value (value))"); ret.append(separator);
            }

			ret.append(linePrefix); ret.append("(pop 1)"); ret.append(separator);
        }

        // Reuse of cached values
        if (inputType == InputType.NORMAL && cachedIsSatisfiable != null) {
            ret.append("(push 1)"); ret.append(separator);
            ret.append("(assert "); ret.append(cachedIsSatisfiable); ret.append(")"); ret.append(separator);
            ret.append("(assert (= value "); ret.append(cachedModel); ret.append("))"); ret.append(separator);
            ret.append("(check-sat)"); ret.append(separator);

            if (extractModel) {
                ret.append("(get-value (value))"); ret.append(separator);
            }

            ret.append("(pop 1)"); ret.append(separator);
        }

        // Report cached values
        if (inputType == InputType.DEBUG && cachedIsSatisfiable != null) {
            ret.append("; cached "); ret.append(cachedIsSatisfiable ? "sat" + (extractModel ? " " + cachedModel : "") : "unsat"); ret.append(separator);
        }

        ret.append(separator);

        return ret.toString();
    }

    private void appendClassDeclarations(Set<String> classes, StringBuilder input, String separator) {
		for (String c : classes) {
			input.append("(declare-fun class_"); input.append(c.replace("_", "__").replace('.', '_')); input.append(" () Int)"); input.append(separator);
		}
		input.append(separator);
    }
	
    private void appendVariableDeclarations(Set<String> vars, StringBuilder input, String separator) {
		for (String var : vars) {
			input.append("(declare-fun var_"); input.append(var); input.append(" () Int)"); input.append(separator);
		}
		input.append(separator);
    }
	
    private void appendFieldDeclarations(Set<String> fields, StringBuilder input, String separator) {
		for (String field : fields) {
			input.append("(declare-fun field_"); input.append(field); input.append(" () (Array Int Int))"); input.append(separator);
		}
		input.append(separator);
    }

	private String prepareGetModelInput(Set<String> classes, Set<String> vars, Set<String> fields, Set<AccessExpression> objects, Predicate predicate, String formula, InputType inputType) {
        String separator = inputType.getSeparator();

		StringBuilder input = new StringBuilder();

        input.append("(push 1)"); input.append(separator);
        appendClassDeclarations(classes, input, separator);
        appendVariableDeclarations(vars, input, separator);
        appendFieldDeclarations(fields, input, separator);
		
        Set<String> freshConstraints = new HashSet<String>();

		for (AccessExpression object : objects) {
			Predicate distinction = Implication.create(Negation.create(object.getPreconditionForBeingFresh()), Negation.create(Equals.create(DefaultFresh.create(), object)));
			
			freshConstraints.add("(assert " + convertToString(distinction) + ")" + separator);
		}

        for (String constraint : freshConstraints) {
            input.append(constraint);
        }
		input.append(separator);

        Boolean cachedValue = cache.get(formula).getSatisfiability();
        Integer cachedModel = cache.get(formula).getModel();

		input.append(prepareFormulaEvaluation(predicate, formula, FormulaType.MODEL_QUERY, inputType, cachedValue, cachedModel, true));
		
        input.append("(pop 1)"); input.append(separator);
		
		return input.toString();
    }

	private String prepareValuatePredicatesInput(Set<String> classes, Set<String> vars, Set<String> fields, Set<AccessExpression> objects, List<Predicate> predicates, List<String> formulas, InputType inputType) {
        return prepareIsSatisfiableInput(classes, vars, fields, objects, predicates, formulas, inputType);
    }

	private String prepareIsSatisfiableInput(Set<String> classes, Set<String> vars, Set<String> fields, Set<AccessExpression> objects, List<Predicate> predicates, List<String> formulas, InputType inputType) {
        String separator = inputType.getSeparator();

		StringBuilder input = new StringBuilder();

        input.append("(push 1)"); input.append(separator);
        appendClassDeclarations(classes, input, separator);
        appendVariableDeclarations(vars, input, separator);
        appendFieldDeclarations(fields, input, separator);
		
        Set<String> freshConstraints = new HashSet<String>();

		for (AccessExpression object : objects) {
			Predicate distinction = Implication.create(Negation.create(object.getPreconditionForBeingFresh()), Negation.create(Equals.create(DefaultFresh.create(), object)));
			
			freshConstraints.add("(assert " + convertToString(distinction) + ")" + separator);
		}

        for (String constraint : freshConstraints) {
            input.append(constraint);
        }
		input.append(separator);

        Iterator<Predicate> predicatesIterator = predicates.iterator();
        Iterator<String> formulasIterator = formulas.iterator();

		while (predicatesIterator.hasNext()) {
            Predicate predicate = predicatesIterator.next();

            String positiveWeakestPreconditionFormula = formulasIterator.next();
            String negativeWeakestPreconditionFormula = formulasIterator.next();

            Boolean cachedPositiveValue = cache.get(positiveWeakestPreconditionFormula).getSatisfiability();
            Boolean cachedNegativeValue = cache.get(negativeWeakestPreconditionFormula).getSatisfiability();

			input.append(prepareFormulaEvaluation(predicate, positiveWeakestPreconditionFormula, FormulaType.POSITIVE_WEAKEST_PRECONDITION_CHECK, inputType, cachedPositiveValue, null, false));
			input.append(prepareFormulaEvaluation(predicate, negativeWeakestPreconditionFormula, FormulaType.NEGATIVE_WEAKEST_PRECONDITION_CHECK, inputType, cachedNegativeValue, null, false));
		}
		
        input.append("(pop 1)"); input.append(separator);
		
		return input.toString();
	}

    public Integer getModel(Expression expression, List<Pair<Predicate, TruthValue>> determinants) {
		notifyGetModelInvoked(expression, determinants);

        Predicate valueConstraint = Equals.create(SpecialVariable.create("value"), expression);
        PredicatesSMTInfoCollector collector = new PredicatesSMTInfoCollector();

        collector.collect(valueConstraint);

        for (Pair<Predicate, TruthValue> pair : determinants) {
            Predicate determinant = pair.getFirst();

            collector.collect(determinant);
        }

		Set<String> classes = collector.getClasses();
		Set<String> variables = collector.getVars();
		Set<String> fields = collector.getFields();
		Set<AccessExpression> objects = Collections.emptySet();

        String formula = createFormula(valueConstraint, determinants, collector.getAdditionalPredicates(valueConstraint));

        String input = prepareGetModelInput(classes, variables, fields, objects, valueConstraint, formula, InputType.NORMAL);
        String debugInput = prepareGetModelInput(classes, variables, fields, objects, valueConstraint, formula, InputType.DEBUG);

		notifyGetModelInputGenerated(debugInput);

        QueryResponse response = isSatisfiable(1, input, true)[0];

        if (USE_CACHE) {
            cache.put(formula, response);
        }

		notifyGetModelExecuted(response.getSatisfiability(), response.getModel());

        return response.getModel();
    }

    public boolean[] isSatisfiable(List<Predicate> predicates) {
        notifyIsSatisfiableInvoked(predicates);

        boolean[] ret = new boolean[predicates.size()];

        PredicatesSMTInfoCollector collector = new PredicatesSMTInfoCollector();

        for (Predicate predicate : predicates) {
            collector.collect(predicate);
        }

		Set<String> classes = collector.getClasses();
		Set<String> variables = collector.getVars();
		Set<String> fields = collector.getFields();
		Set<AccessExpression> objects = Collections.emptySet();

        List<String> formulas = new ArrayList<String>(predicates.size());

        for (Predicate predicate : predicates) {
            formulas.add(convertToString(predicate));
        }

        String input = prepareIsSatisfiableInput(classes, variables, fields, objects, predicates, formulas, InputType.NORMAL);
        String debugInput = prepareIsSatisfiableInput(classes, variables, fields, objects, predicates, formulas, InputType.DEBUG);

        notifyIsSatisfiableInputGenerated(debugInput);

        QueryResponse[] responses = isSatisfiable(1, input, false);

        for (int i = 0; i < responses.length; ++i) {
            ret[i] = responses[i].getSatisfiability();

            if (USE_CACHE) {
                cache.put(formulas.get(i), responses[i]);
            }
        }

        notifyIsSatisfiableExecuted(predicates, ret);

        return ret;
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
		
		String input = prepareValuatePredicatesInput(classes, vars, fields, objects, predicatesList, formulasList, InputType.NORMAL);
		String debugInput = prepareValuatePredicatesInput(classes, vars, fields, objects, predicatesList, formulasList, InputType.DEBUG);

		notifyValuatePredicatesInputGenerated(debugInput);
		
        return evaluate(input, debugInput, predicatesList, formulasList);
	}
	
	public Map<Predicate, TruthValue> valuatePredicates(Set<Predicate> predicates) throws SMTException {
        Map<Predicate, PredicateValueDeterminingInfo> predicateDeterminingInfos = new HashMap<Predicate, PredicateValueDeterminingInfo>();

        for (Predicate predicate : predicates) {
            Map<Predicate, TruthValue> determinants = Collections.emptyMap();

            PredicateValueDeterminingInfo determiningInfo = new PredicateValueDeterminingInfo(predicate, Negation.create(predicate), determinants);

            predicateDeterminingInfos.put(predicate, determiningInfo);
        }

        return valuatePredicates(predicateDeterminingInfos);
	}
	
	private static String convertToString(PredicatesComponentVisitable visitable) {
		PredicatesSMTStringifier stringifier = new PredicatesSMTStringifier();
		
		visitable.accept(stringifier);
		
		return stringifier.getString();
	}

    private static String createFormula(Predicate valueConstraint, List<Pair<Predicate, TruthValue>> determinants, Set<Predicate> additionalClauses) {
        Predicate formula = valueConstraint;

        for (Pair<Predicate, TruthValue> pair : determinants) {
            Predicate determinant = pair.getFirst();
            TruthValue value = pair.getSecond();

            switch (value) {
                case TRUE:
                    formula = Conjunction.create(formula, determinant);
                    break;
                    
                case FALSE:
                    formula = Conjunction.create(formula, Negation.create(determinant));
                    break;

                default:
            }
        
            for (Predicate additional : additionalClauses) {
                formula = Conjunction.create(formula, additional);
            }
        }

        return convertToString(formula);
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
		
		QueryResponse[] negationSatisfiable;

        try {
    		negationSatisfiable = isSatisfiable(2 * predicates.size(), input, false);
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

			if (!negationSatisfiable[i].getSatisfiability() && !negationSatisfiable[i + 1].getSatisfiability()) {
				valuation.put(predicate, TruthValue.UNKNOWN);
			} else if (!negationSatisfiable[i].getSatisfiability()) {
				valuation.put(predicate, TruthValue.TRUE);
			} else if (!negationSatisfiable[i + 1].getSatisfiability()) {
				valuation.put(predicate, TruthValue.FALSE);
			} else {
				valuation.put(predicate, TruthValue.UNKNOWN);
			}

            if (USE_CACHE) {
                cache.put(positiveWeakestPrecondition, negationSatisfiable[i]);
                cache.put(negativeWeakestPrecondition, negationSatisfiable[i + 1]);
            }
			
			i += 2;
		}
		
		notifyValuatePredicatesExecuted(valuation);
		
		return valuation;
	}
}
