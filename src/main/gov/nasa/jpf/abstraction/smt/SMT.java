package gov.nasa.jpf.abstraction.smt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Implication;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitable;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.UpdatedPredicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.SpecialVariable;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultFresh;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.util.Pair;

/**
 * Class responsible for invocations of SMT, transformation of predicates into input that the SMT can solve
 */
public class SMT {

    private static boolean USE_CACHE = true;
    private static boolean USE_MODELS_CACHE = false;
    private static boolean USE_LOG_FILE = true;
    private static List<SMTListener> listeners = new LinkedList<SMTListener>();
    private static SMTCache cache = new SMTCache();

    public static void registerListener(SMTListener listener) {
        listeners.add(listener);
        listener.registerCache(cache);
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
                case NORMAL: return "\n";
                case DEBUG:  return "\n";
                default:     return null;
            }
        }
    }
    private static enum FormulaType {
        SATISFIABILITY_CHECK,
        POSITIVE_WEAKEST_PRECONDITION_CHECK,
        NEGATIVE_WEAKEST_PRECONDITION_CHECK,
        MODEL_QUERY;

        @Override
        public String toString() {
            switch (this) {
                case SATISFIABILITY_CHECK:                return "satisfiability check";
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

        @Override
        public String toString() {
            if (satisfiable == null) {
                return "NULL";
            }

            return (satisfiable ? "SAT" : "UNSAT") + (model == null ? "" : " Model: " + model);
        }
    }

    private BufferedWriter in = null;
    private BufferedReader out = null;

    private int indent = 0;

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

            if (USE_LOG_FILE) {
                final BufferedWriter log = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("smt.log")));

                in = new BufferedWriter(inwriter) {
                    @Override
                    public void write(String str) throws IOException {
                        super.write(str);

                        ArrayList<String> items = new ArrayList<String>();

                        while (true) {
                            int push = str.indexOf("(push 1)");
                            int pop = str.indexOf("(pop 1)");

                            if (push > -1 && (pop < 0 || push < pop)) {
                                items.add(str.substring(0, push));
                                items.add("(push 1)");
                                str = str.substring(push + "(push 1)".length());
                            }

                            if (pop > -1 && (push < 0 || pop < push)) {
                                items.add(str.substring(0, pop));
                                items.add("(pop 1)");
                                str = str.substring(pop + "(pop 1)".length());
                            }

                            if (push == -1 && pop == -1) {
                                break;
                            }
                        }

                        items.add(str);

                        for (int i = 0; i < items.size(); ++i) {
                            if (items.get(i).equals("(pop 1)")) {
                                --indent;
                            }

                            String[] lines = items.get(i).split("\n");

                            for (String line : lines) {
                                if (line.length() > 0) {
                                    for (int k = 0; k < 2 * indent; ++k) {
                                        log.write(" ");
                                    }
                                    log.write(line.trim());
                                    log.write("\n");
                                }
                            }

                            if (items.get(i).equals("(push 1)")) {
                                ++indent;
                            }
                        }
                    }

                    @Override
                    public void flush() throws IOException {
                        super.flush();
                        log.flush();
                    }

                    @Override
                    public void close() throws IOException {
                        super.close();
                        log.close();
                    }
                };
            } else {
                in = new BufferedWriter(inwriter);
            }

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

                        Pattern pattern = Pattern.compile("^\\( \\(value ((?<positivevalue>[0-9]*)|\\(- (?<negativevalue>[0-9]*)\\))\\) \\)$");
                        Matcher matcher = pattern.matcher(output);

                        if (matcher.matches()) {
                            String positivevalue = matcher.group("positivevalue");
                            String negativevalue = matcher.group("negativevalue");

                            try {
                                if (positivevalue != null) {
                                    model = Integer.valueOf(positivevalue);
                                } else {
                                    model = -Integer.valueOf(negativevalue);
                                }
                            } catch (Exception e) {
                                model = null; // Could not decode integer - probably the number is too large
                            }
                        }
                    } else {
                        output = out.readLine();
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

    private static int queryCountTotal = 0;
    private static int queryCount = 0;
    private static int cacheHitCount = 0;

    public static int getQueryCountTotal() {
        return queryCountTotal;
    }

    public static int getQueryCount() {
        return queryCount;
    }

    public static int getCacheHitCount() {
        return cacheHitCount;
    }

    private String prepareFormulaEvaluation(Predicate predicate, String formula, FormulaType formulaType, InputType inputType, Boolean cachedIsSatisfiable, Integer cachedModel, boolean extractModel) {
        String separator = inputType.getSeparator();
        String linePrefix = cachedIsSatisfiable == null ? "" : "; ";
        StringBuilder ret = new StringBuilder();

        if (inputType == InputType.DEBUG) {
            ret.append("; Predicate: " + predicate.toString(Notation.DOT_NOTATION) + " (" + formulaType + ")\n");
        }

        if (inputType == InputType.NORMAL) {
            ++queryCountTotal;

            if (cachedIsSatisfiable == null) {
                ++queryCount;
            } else {
                ++cacheHitCount;
            }
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

    private void appendFreshConstraints(Set<AccessExpression> objects, StringBuilder input, String separator) {
        Set<String> freshConstraints = new HashSet<String>();

        for (AccessExpression object : objects) {
            Predicate distinction = Implication.create(Negation.create(object.getPreconditionForBeingFresh()), Negation.create(Equals.create(DefaultFresh.create(), object)));

            freshConstraints.add("(assert " + convertToString(distinction) + ")" + separator);
        }

        for (String constraint : freshConstraints) {
            input.append(constraint);
        }
        input.append(separator);
    }

    private String prepareGetModelInput(Set<String> classes, Set<String> vars, Set<String> fields, Predicate predicate, String formula, InputType inputType) {
        String separator = inputType.getSeparator();

        StringBuilder input = new StringBuilder();

        input.append("(push 1)"); input.append(separator);
        appendClassDeclarations(classes, input, separator);
        appendVariableDeclarations(vars, input, separator);
        appendFieldDeclarations(fields, input, separator);

        Boolean cachedValue = cache.get(formula).getSatisfiability();
        Integer cachedModel = cache.get(formula).getModel();

        input.append(prepareFormulaEvaluation(predicate, formula, FormulaType.MODEL_QUERY, inputType, cachedValue, cachedModel, true));

        input.append("(pop 1)"); input.append(separator);

        return input.toString();
    }

    private String prepareValuatePredicatesInput(Set<String> classes, Set<String> vars, Set<String> fields, Set<AccessExpression> objects, boolean hasFresh, Predicate[] predicates, String[] formulas, InputType inputType) {
        String separator = inputType.getSeparator();

        StringBuilder input = new StringBuilder();

        input.append("(push 1)"); input.append(separator);
        appendClassDeclarations(classes, input, separator);
        appendVariableDeclarations(vars, input, separator);
        appendFieldDeclarations(fields, input, separator);

        if (hasFresh) {
            appendFreshConstraints(objects, input, separator);
        }

        for (int i = 0; i < predicates.length; ++i) {
            Predicate predicate = predicates[i];

            String positiveWeakestPreconditionFormula = formulas[2 * i];
            String negativeWeakestPreconditionFormula = formulas[2 * i + 1];

            Boolean cachedPositiveValue = cache.get(positiveWeakestPreconditionFormula).getSatisfiability();
            Boolean cachedNegativeValue = cache.get(negativeWeakestPreconditionFormula).getSatisfiability();

            input.append(prepareFormulaEvaluation(predicate, positiveWeakestPreconditionFormula, FormulaType.POSITIVE_WEAKEST_PRECONDITION_CHECK, inputType, cachedPositiveValue, null, false));
            input.append(prepareFormulaEvaluation(predicate, negativeWeakestPreconditionFormula, FormulaType.NEGATIVE_WEAKEST_PRECONDITION_CHECK, inputType, cachedNegativeValue, null, false));
        }

        input.append("(pop 1)"); input.append(separator);

        return input.toString();
    }

    private String prepareIsSatisfiableInput(Set<String> classes, Set<String> vars, Set<String> fields, List<Predicate> predicates, String[] formulas, InputType inputType) {
        String separator = inputType.getSeparator();

        StringBuilder input = new StringBuilder();

        input.append("(push 1)"); input.append(separator);
        appendClassDeclarations(classes, input, separator);
        appendVariableDeclarations(vars, input, separator);
        appendFieldDeclarations(fields, input, separator);

        Iterator<Predicate> predicatesIterator = predicates.iterator();

        int i = 0;
        while (predicatesIterator.hasNext()) {
            Predicate predicate = predicatesIterator.next();

            String formula = formulas[i];

            Boolean cachedValue = cache.get(formula).getSatisfiability();

            input.append(prepareFormulaEvaluation(predicate, formula, FormulaType.SATISFIABILITY_CHECK, inputType, cachedValue, null, false));
            ++i;
        }

        input.append("(pop 1)"); input.append(separator);

        return input.toString();
    }

    public int[] getModels(Predicate stateFormula, AccessExpression[] expressions) {
        String separator = InputType.NORMAL.getSeparator();
        StringBuilder input = new StringBuilder();
        int[] ret = new int[expressions.length];

        String state = convertToString(stateFormula);
        int cachedCount = 0;
        boolean[] cached = null;
        String[] queries = null;

        if (USE_MODELS_CACHE) {
            cached = new boolean[expressions.length];
            queries = new String[expressions.length];
        }

        PredicatesSMTInfoCollector collector = new PredicatesSMTInfoCollector();

        collector.collect(stateFormula);

        Set<String> classes = collector.getClasses();
        Set<String> variables = collector.getVars();
        Set<String> fields = collector.getFields();

        input.append("(push 1)"); input.append(separator);
        appendClassDeclarations(classes, input, separator);
        appendVariableDeclarations(variables, input, separator);
        appendFieldDeclarations(fields, input, separator);

        input.append("(assert "); input.append(state); input.append(")"); input.append(separator);

        for (int i = 0; i < expressions.length; ++i) {
            AccessExpression expr = expressions[i];

            Predicate valueConstraint = Equals.create(SpecialVariable.create("value"), expr);

            String query = convertToString(valueConstraint);

            if (USE_MODELS_CACHE && cache.getQueries().contains(query)) {
                ret[i] = cache.get(query).getModel();
                ++cachedCount;
                cached[i] = true;
                queries[i] = query;
            } else {
                input.append("(push 1)"); input.append(separator);
                input.append("(assert "); input.append(query); input.append(")"); input.append(separator);
                input.append("(check-sat)"); input.append(separator);
                input.append("(get-value (value))"); input.append(separator);
                input.append("(pop 1)"); input.append(separator);
            }
        }

        input.append("(pop 1)"); input.append(separator);

        QueryResponse[] responses = isSatisfiable(expressions.length - cachedCount, input.toString(), true);

        for (int i = 0, j = 0; i < responses.length; ++i, ++j) {
            if (USE_MODELS_CACHE) {
                // Cache responses
                cache.put(state + queries[i], responses[i]);

                // Skip cached values
                while (cached[j]) {
                    ++j;
                }
            }

            // Insert responses
            ret[j] = responses[i].getModel();
        }

        return ret;
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

        String formula = createFormula(valueConstraint, determinants, collector.getAdditionalPredicates(valueConstraint));

        String input = prepareGetModelInput(classes, variables, fields, valueConstraint, formula, InputType.NORMAL);

        if (!listeners.isEmpty()) {
            String debugInput = prepareGetModelInput(classes, variables, fields, valueConstraint, formula, InputType.DEBUG);

            notifyGetModelInputGenerated(debugInput);
        }

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

        String[] formulas = new String[predicates.size()];

        int i = 0;
        for (Predicate predicate : predicates) {
            formulas[i] = convertToString(predicate);
            ++i;
        }

        String input = prepareIsSatisfiableInput(classes, variables, fields, predicates, formulas, InputType.NORMAL);

        if (!listeners.isEmpty()) {
            String debugInput = prepareIsSatisfiableInput(classes, variables, fields, predicates, formulas, InputType.DEBUG);

            notifyIsSatisfiableInputGenerated(debugInput);
        }

        QueryResponse[] responses = isSatisfiable(predicates.size(), input, false);

        for (i = 0; i < responses.length; ++i) {
            ret[i] = responses[i].getSatisfiability();

            if (USE_CACHE) {
                cache.put(formulas[i], responses[i]);
            }
        }

        notifyIsSatisfiableExecuted(predicates, ret);

        return ret;
    }

    public Map<Predicate, TruthValue> valuatePredicates(Map<Predicate, PredicateValueDeterminingInfo> predicates) throws SMTException {
        notifyValuatePredicatesInvoked(predicates);

        Predicate[] predicatesArray = new Predicate[predicates.keySet().size()];
        String[] formulasArray = new String[predicates.keySet().size() * 2];

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

        int i = 0;
        for (Predicate predicate : predicates.keySet()) {
            PredicateValueDeterminingInfo det = predicates.get(predicate);

            Set<Predicate> additionalPredicates = collector.getAdditionalPredicates(predicate);

            predicatesArray[i] = predicate;

            formulasArray[2 * i] = createFormula(det.positiveWeakestPrecondition, det.determinants, additionalPredicates);
            formulasArray[2 * i + 1] = createFormula(det.negativeWeakestPrecondition, det.determinants, additionalPredicates);

            ++i;
        }

        Set<String> classes = collector.getClasses();
        Set<String> vars = collector.getVars();
        Set<String> fields = collector.getFields();
        Set<AccessExpression> objects = collector.getObjects();
        boolean hasFresh = collector.hasFresh();

        String input = prepareValuatePredicatesInput(classes, vars, fields, objects, hasFresh, predicatesArray, formulasArray, InputType.NORMAL);

        if (!listeners.isEmpty()) {
            String debugInput = prepareValuatePredicatesInput(classes, vars, fields, objects, hasFresh, predicatesArray, formulasArray, InputType.DEBUG);

            notifyValuatePredicatesInputGenerated(debugInput);
        }

        return evaluate(input, predicatesArray, formulasArray);
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

    private Map<Predicate, TruthValue> evaluate(String input, Predicate[] predicates, String[] formulas) throws SMTException {
        Map<Predicate, TruthValue> valuation = new HashMap<Predicate, TruthValue>();

        QueryResponse[] negationSatisfiable;

        try {
            negationSatisfiable = isSatisfiable(formulas.length, input, false);
        } catch (SMTException e) {
            throw new SMTException("SMT failed:\n" + e.getMessage());
        }

        for (int i = 0; i < predicates.length; ++i) {
            Predicate predicate = predicates[i];
            String positiveWeakestPrecondition = formulas[2 * i];
            String negativeWeakestPrecondition = formulas[2 * i + 1];

            if (!negationSatisfiable[2 * i].getSatisfiability() && !negationSatisfiable[2 * i + 1].getSatisfiability()) {
                valuation.put(predicate, TruthValue.UNKNOWN);
            } else if (!negationSatisfiable[2 * i].getSatisfiability()) {
                valuation.put(predicate, TruthValue.TRUE);
            } else if (!negationSatisfiable[2 * i + 1].getSatisfiability()) {
                valuation.put(predicate, TruthValue.FALSE);
            } else {
                valuation.put(predicate, TruthValue.UNKNOWN);
            }

            if (USE_CACHE) {
                cache.put(positiveWeakestPrecondition, negationSatisfiable[2 * i]);
                cache.put(negativeWeakestPrecondition, negationSatisfiable[2 * i + 1]);
            }
        }

        notifyValuatePredicatesExecuted(valuation);

        return valuation;
    }
}
