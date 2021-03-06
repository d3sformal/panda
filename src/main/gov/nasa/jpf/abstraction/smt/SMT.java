/*
 * Copyright (C) 2015, Charles University in Prague.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.nasa.jpf.abstraction.smt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.Method;
import gov.nasa.jpf.abstraction.PandaConfig;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.Step;
import gov.nasa.jpf.abstraction.TraceFormula;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Disjunction;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Implication;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitable;
import gov.nasa.jpf.abstraction.common.PredicatesFactory;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.UpdatedPredicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.SpecialVariable;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultFresh;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.state.universe.Universe;
import gov.nasa.jpf.abstraction.util.Pair;

/**
 * Class responsible for invocations of SMT, transformation of predicates into input that the SMT can solve
 */
public class SMT {

    private static boolean USE_CACHE = true;
    private static boolean USE_MODELS_CACHE = false;
    private static boolean USE_LOG_FILE = PandaConfig.getInstance().logSMT();
    private static List<SMTListener> listeners = new LinkedList<SMTListener>();
    private static SMTCache cache = new SMTCache();
    private static int isSat;
    private static int itp;
    private static long elapsed;

    public static void reset() {
        listeners.clear();
        isSat = 0;
        itp = 0;
        elapsed = 0;
    }

    public static int getIsSat() {
        return isSat;
    }

    public static int getItp() {
        return itp;
    }

    public static long getElapsed() {
        return elapsed;
    }

    public static void registerListener(SMTListener listener) {
        listeners.add(listener);
        listener.registerCache(cache);
    }

    public static void unregisterListeners() {
        listeners.clear();
    }

    // Notify about invocations
    private static void notifyIsSatisfiableInvoked(List<Predicate> formulas) {
        for (SMTListener listener : listeners) {
            listener.isSatisfiableInvoked(formulas);
        }
    }
    private static void notifyInterpolateInvoked(TraceFormula traceFormula) {
        for (SMTListener listener : listeners) {
            listener.interpolateInvoked(traceFormula);
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
    private static void notifyGetModelsInvoked(Predicate formula, AccessExpression[] exprs) {
        for (SMTListener listener : listeners) {
            listener.getModelsInvoked(formula, exprs);
        }
    }

    // Notify about generation of input
    private static void notifyIsSatisfiableInputGenerated(String input) {
        for (SMTListener listener : listeners) {
            listener.isSatisfiableInputGenerated(input);
        }
    }
    private static void notifyInterpolateInputGenerated(String input) {
        for (SMTListener listener : listeners) {
            listener.interpolateInputGenerated(input);
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
    private static void notifyGetModelsInputGenerated(String input) {
        for (SMTListener listener : listeners) {
            listener.getModelsInputGenerated(input);
        }
    }

    // Notify about finished execution
    private static void notifyIsSatisfiableExecuted(List<Predicate> formulas, boolean[] satisfiable) {
        for (SMTListener listener : listeners) {
            listener.isSatisfiableExecuted(formulas, satisfiable);
        }
    }
    private static void notifyInterpolateExecuted(Predicate[] interpolants) {
        for (SMTListener listener : listeners) {
            listener.interpolateExecuted(interpolants);
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
    private static void notifyGetModelsExecuted(AccessExpression[] exprs, int[] models) {
        for (SMTListener listener : listeners) {
            listener.getModelsExecuted(exprs, models);
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

    OutputStreamWriter inwriter;
    InputStreamReader outreader;

    interface SMTOutputReader extends Closeable {
        public String readLine() throws IOException;

        @Override
        public void close() throws IOException;
    }

    private BufferedWriter in = null;
    private SMTOutputReader out = null;

    private int indent = 0;

    public enum SupportedSMT {
        MathSAT,
        SMTInterpol,
        CVC4,
        Z3;

        public int logFileID = 0;
    }

    private static SupportedSMT defaultSMT = PandaConfig.getInstance().getSMT();
    private SupportedSMT type;

    /**
     * Starts a process of a supported SMT solver
     *
     * Supported: MathSAT, SMTInterpol
     */
    private void prepareSMTProcess(SupportedSMT smt) throws IOException {
        String[] args = null;
        String[] env = new String[] {};

        switch (smt) {
            case MathSAT:
                args = new String[] {
                    System.getProperty("user.dir") + "/bin/mathsat",
                    "-theory.arr.enabled=true",
                    "-theory.arr.enable_row_lemmas=true",
                    "-theory.arr.enable_wr_lemmas=true",
                    "-theory.arr.auto_wr_lemma=true",
                    "-theory.arr.max_ext_lemmas=0",
                    "-theory.arr.max_row_lemmas=0",
                    "-theory.arr.max_wr_lemmas=0"
                };

                break;
            case SMTInterpol:
                args = new String[] {
                    "java",
                    "-jar",
                    System.getProperty("user.dir") + "/lib/smtinterpol.jar"
                };

                break;
            case CVC4:
                args = new String[] {
                    System.getProperty("user.dir") + "/bin/cvc4-1.4-x86_64-linux-opt",
                    "--lang=smt",
                    "--incremental",
                    "--continued-execution"
                };

                break;
           case Z3:
                args = new String[] {
                    System.getProperty("user.dir") + "/bin/z3",
                    "-smt2",
                    "-in"
                };

                break;
        }

        Process process = Runtime.getRuntime().exec(args, env);

        OutputStream instream = process.getOutputStream();
        inwriter = new OutputStreamWriter(instream);

        InputStream outstream = process.getInputStream();
        outreader = new InputStreamReader(outstream);

        if (PandaConfig.getInstance().enabledVerbose(this.getClass())) {
            System.out.println("Started " + smt + " solver.");
        }
    }

    /**
     * Feed the SMT process with runtime configuration necessary for its correct functionality (e.g., enabling model generation)
     *
     * MathSAT configured to be used for satisfiability checks and model generations
     * SMTInterpol configured to be used for interpolant generation
     */
    private void configureSMTProcess(SupportedSMT smt) throws IOException {
        String separator = InputType.NORMAL.getSeparator();

        switch (smt) {
            case CVC4:
            case MathSAT:
                in.write(
                    "(set-option :produce-models true)" + separator +

                    "(set-logic QF_AUFLIA)" + separator
                );

                break;
            case SMTInterpol:
                in.write(
                    "(set-option :print-success false)" + separator +
                    "(set-option :verbosity 3)" + separator +
                    "(set-option :produce-interpolants true)" + separator +

                    "(set-logic QF_AUFLIA)" + separator
                );

                break;
            case Z3:
                in.write(
                    "(set-option :produce-interpolants true)" + separator
                );

                break;
        }

        in.write(
            // Memory model symbols
            "(declare-fun arrlen () (Array Int Int))" + separator +

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

            // Fresh symbol discriminator
            "(declare-fun ref (Int) Int)" + separator +

            separator
        );

        in.flush();

        if (PandaConfig.getInstance().enabledVerbose(this.getClass())) {
            System.out.println("Configured " + smt + " solver.");
        }
    }

    /**
     * The default SMT solver used for the core functionality (maintaining valuations of abstraction predicates during forward state space exploration, maintaining the heap abstraction - models of array index expressions)
     */
    public SMT() throws SMTException {
        this(defaultSMT);
    }

    /**
     * Attach to an SMT solver process
     */
    public SMT(SupportedSMT smt) throws SMTException {
        type = smt;
        try {
            prepareSMTProcess(smt);

            if (USE_LOG_FILE) {
                File dir = new File("output");

                if (!dir.exists()) {
                    dir.mkdir();
                }

                final BufferedWriter log = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output/smt." + smt + ".log." + (++smt.logFileID))));

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

            configureSMTProcess(smt);

            switch (smt) {
                case Z3:
                    out = new SMTOutputReader() {
                        private Z3LineReader lr = new Z3LineReader(outreader);

                        @Override
                        public String readLine() throws IOException {
                            return lr.readLine();
                        }

                        @Override
                        public void close() throws IOException {
                            lr.close();
                        }
                    };
                    break;

                default:
                    out = new SMTOutputReader() {
                        private BufferedReader br = new BufferedReader(outreader);

                        @Override
                        public String readLine() throws IOException {
                            return br.readLine();
                        }

                        @Override
                        public void close() throws IOException {
                            br.close();
                        }
                    };
            }
        } catch (IOException e) {
            System.err.println("SMT will not start.");

            throw new SMTException(e);
        }
    }

    /**
     * Detach and terminate the SMT process (also closes input logs)
     */
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

    private static final int PLAIN = 0;
    private static final int PREFIX_IN_FRONT = 1 << 0;
    private static final int NESTED_IN_PLACE = 1 << 1;

    private class InterpolationEntry {
        int method;
        int format;
        String input;

        InterpolationEntry(int method, int format, String input) {
            this.method = method;
            this.format = format;
            this.input = input;
        }
    }

    private void prepareInterpolationForMethod(SupportedSMT type, int method, TraceFormula traceFormula, List<InterpolationEntry> queries, String separator) {
        Method m = traceFormula.getMethods().get(method);
        StringBuilder input = new StringBuilder();

        if (m.get(0) > 0) {
            input.append("; Method "); input.append(m.getInfo().getFullName());

            for (int i : m) {
                input.append(" g");
                input.append(i + 1);
            }

            input.append(separator);
        }

        int voidFeatures = PLAIN; // Do not generate duplicate query if it is going to look the same due to missing features

        /**
         * Trace:
         *
         * (m1) (m2 (m3) (m4)) (m5 (m6)) (m7 (m8) (m9 (m10)))
         *
         * Features:
         *
         * 1) PREFIX IN FRONT + NESTED IN PLACE
         *
         * query(m5) = (and m1 m2 m3 m4) (m5 (m6)) (and m7 m8 m9 m10)
         *
         * 2) NESTED IN PLACE
         *
         * query(m5) = (m5 (m6)) (and m1 m2 m3 m4 m7 m8 m9 m10)
         *
         * 3) PLAIN
         *
         * query(m5) = (m5) (and m1 m2 m3 m4 m6 m7 m8 m9 m10)
         *
         *
         *
         * Example of missing features:
         *
         * 1) PREFIX IN FRONT + NESTED IN PLACE
         *
         * query(m1) = (m1) (and ...)
         *
         * 2) NESTED IN PLACE
         *
         * query(m1) = (m1) (and ...)
         *
         * 3) PLAIN
         *
         * query(m1) = (m1) (and ...)
         *
         * ... The three queries are identical
         *
         *
         *
         * Another example:
         *
         * 1) PREFIX IN FRONT + NESTED IN PLACE
         *
         * query(m4) = (and m1 m2 m3) (m4) (and m5 m6 m7 m8 m9 m10)
         * 
         * 2) NESTED IN PLACE
         *
         * query(m4) = (m4) (and m1 m2 m3 m5 m6 m7 m8 m9 m10)
         *
         * 3) PLAIN
         *
         * query(m4) = (m4) (and m1 m2 m3 m5 m6 m7 m8 m9 m10)
         *
         * ... The last two queries are identical
         */

        // Encode as:

        // (and prefix) g1 g2 (and nested1) ... gN (and rest)
        voidFeatures = prepareInterpolationForMethod(type, method, traceFormula, input, separator, PREFIX_IN_FRONT | NESTED_IN_PLACE);

        queries.add(new InterpolationEntry(method, PREFIX_IN_FRONT | NESTED_IN_PLACE, input.toString()));
        input.setLength(0);

        // g1 g2 (and nested1) ... gN (and rest)
        if ((voidFeatures & PREFIX_IN_FRONT) != PREFIX_IN_FRONT) { // Only if the previous query does not cover this one
            voidFeatures = prepareInterpolationForMethod(type, method, traceFormula, input, separator, NESTED_IN_PLACE);

            queries.add(new InterpolationEntry(method, NESTED_IN_PLACE, input.toString()));
            input.setLength(0);
        }

        // g1 g2 ... gN (and rest)
        if ((voidFeatures & NESTED_IN_PLACE) != NESTED_IN_PLACE) { // Only if the previous query does not cover this one
            voidFeatures = prepareInterpolationForMethod(type, method, traceFormula, input, separator, PLAIN);

            queries.add(new InterpolationEntry(method, PLAIN, input.toString()));
            input.setLength(0);
        }
    }

    /**
     * For a method: step0 ... stepN
     * Interpolate the following sequence
     *
     * (and prefix step0) step1 ... (nestedStep0 ... nestedStepM) ... stepN (and rest)
     *
     * where
     *   prefix contains all methods that have exited before this one (none of its local symbols is referenced later)
     *   rest contains the enveloping methods (caller and its caller ...) and all methods called after this one exits
     *
     * The return value specifies which features of the trace were empty.
     * When some of the features (prefix, nested methods) are empty, the query is essentially the same as a query formatted in a simpler way.
     */
    private int prepareInterpolationForMethod(SupportedSMT type, int method, TraceFormula traceFormula, StringBuilder input, String separator, int format) {
        Method m = traceFormula.getMethods().get(method);

        int ret = PREFIX_IN_FRONT | NESTED_IN_PLACE;

        if (m.get(0) > 0) {
            switch (type) {
                case SMTInterpol:
                    input.append("(get-interpolants");
                    break;
                case Z3:
                    input.append("(get-interpolant");
                    break;
            }

            int nestedMethodSteps = 0;

            Set<Integer> steps = new HashSet<Integer>();

            input.append(" (and g0 g0"); // Make sure the operation is at least binary

            if ((format & PREFIX_IN_FRONT) != 0) {
                int startM = m.get(0);

                for (int i = 0; i < startM; ++i) {
                    input.append(" g"); input.append(i + 1);
                    steps.add(i);

                    ret &= ~PREFIX_IN_FRONT;
                }

                for (Method m2 : traceFormula.getMethods()) {
                    int start = m2.get(0);
                    int end = m2.get(m2.size() - 1);

                    if (start < startM && startM < end) { // Method started before this one (m) but not finished yet (it is a caller)
                        for (int i : m2) { // Add code of the method (including parts that are after the call to this one (m))
                            if (!steps.contains(i)) {
                                input.append(" g"); input.append(i + 1);
                                steps.add(i);
                            }
                        }
                    }
                }
            }

            input.append(")");

            for (int i = m.get(0); i <= m.get(m.size() - 1); ++i) {
                if (m.contains(i)) {
                    if (nestedMethodSteps == 1) {
                        input.append(" g0");
                    }
                    if (nestedMethodSteps > 0) {
                        input.append(")");

                        nestedMethodSteps = 0;
                    }

                    input.append(" g"); input.append(i + 1);
                    steps.add(i);
                } else if ((format & NESTED_IN_PLACE) != 0) {
                    ++nestedMethodSteps;

                    if (nestedMethodSteps == 1) {
                        input.append(" (and");
                    }

                    input.append(" g"); input.append(i + 1);
                    steps.add(i);

                    ret &= ~NESTED_IN_PLACE;
                }
            }

            input.append(" (and g0 g0"); // Make sure the operation is at least binary
            for (int i = 0; i < traceFormula.size(); ++i) {
                if (!steps.contains(i)) {
                    input.append(" g"); input.append(i + 1);
                }
            }
            input.append(")");

            input.append(")"); input.append(separator);
        }

        return ret;
    }

    private void interpolateMethod(SMT interpol, boolean satisfiable, int method, TraceFormula traceFormula, Map<Integer, Set<Predicate>> interpolants, int format) {
        ++itp;

        PandaConfig config = PandaConfig.getInstance();

        Method m = traceFormula.getMethods().get(method);

        if (config.enabledVerbose(this.getClass())) {
            Step start = m.getStep(0);
            Step end = m.getStep(m.size() - 1);
            System.out.println("Method [" + start.getMethod().getName() + ":" + start.getPC() + ".." + end.getMethod().getName() + ":" + end.getPC() + "]");

            for (int i : m) {
                {
                    int j = i - 1;

                    if (j > m.get(0) && !m.contains(j)) {
                        Step ps = traceFormula.get(j);
                        System.out.println("\t" + (j + 1) + ": " +  ps.getMethod().getName() + ":" + ps.getPC() + " return");
                    }
                }

                Step s = traceFormula.get(i);
                System.out.println("\t" + (i + 1) + ": " +  s.getMethod().getName() + ":" + s.getPC());

                {
                    int j = i + 1;

                    if (j < m.get(m.size() - 1) && !m.contains(j)) {
                        Step ns = traceFormula.get(j);
                        System.out.println("\t" + (j + 1) + ": " +  ns.getMethod().getName() + ":" + ns.getPC() + " call");
                    }
                }
            }
        }

        String output;

        if (m.get(0) > 0) {
            Map<Integer, Set<Predicate>> methodInterpolants = new HashMap<Integer, Set<Predicate>>();

            for (int i = 0; i < m.size(); ++i) {
                methodInterpolants.put(i, new HashSet<Predicate>());
            }

            switch (interpol.type) {
                case SMTInterpol:
                    try {
                        output = interpol.out.readLine();
                    } catch (IOException e) {
                        System.err.println("SMT refuses to provide output.");

                        throw new SMTException(e);
                    }

                    if (!satisfiable) {
                        Predicate[] itps = PredicatesFactory.createInterpolantsFromString(output);

                        int j = 0;

                        methodInterpolants.get(0).add(itps[j++]);

                        int lastStep = 0;
                        for (int i = 0; i < m.size(); ++i) {
                            if (i > 0) {
                                if ((format & NESTED_IN_PLACE) != 0 && lastStep != m.get(i) - 1) {
                                    Predicate nested = itps[j++];

                                    methodInterpolants.get(i - 1).add(nested);

                                    if (!(nested instanceof Tautology) && config.enabledVerbose(this.getClass())) {
                                        System.out.println("\t... inserting nested interpolant `" + nested + "` to " + (m.get(i - 1) + 1));
                                    }
                                }
                            }

                            Predicate itp = itps[j++];

                            methodInterpolants.get(i).add(itp);

                            if (!(itp instanceof Tautology) && config.enabledVerbose(this.getClass())) {
                                System.out.println("\t... inserting interpolant `" + itp + "` to " + (m.get(i) + 1));
                            }

                            lastStep = m.get(i);
                        }
                    }
                    break;
                case Z3:
                    if (satisfiable) {
                        try {
                            output = interpol.out.readLine();
                        } catch (IOException e) {
                            System.err.println("SMT refuses to provide output.");

                            throw new SMTException(e);
                        }
                    } else {
                        try {
                            output = interpol.out.readLine();
                        } catch (IOException e) {
                            System.err.println("SMT refuses to provide output.");

                            throw new SMTException(e);
                        }

                        methodInterpolants.get(0).add(PredicatesFactory.createInterpolantFromString(output));

                        int lastStep = 0;
                        for (int i = 0; i < m.size(); ++i) {
                            if (i > 0) {
                                if ((format & NESTED_IN_PLACE) != 0 && lastStep != m.get(i) - 1) {
                                    try {
                                        output = interpol.out.readLine();
                                    } catch (IOException e) {
                                        System.err.println("SMT refuses to provide output.");

                                        throw new SMTException(e);
                                    }

                                    Predicate nested = PredicatesFactory.createInterpolantFromString(output);

                                    methodInterpolants.get(i - 1).add(nested);

                                    if (!(nested instanceof Tautology) && config.enabledVerbose(this.getClass())) {
                                        System.out.println("\t... inserting nested interpolant `" + nested + "` to " + (m.get(i - 1) + 1));
                                    }
                                }
                            }

                            try {
                                output = interpol.out.readLine();
                            } catch (IOException e) {
                                System.err.println("SMT refuses to provide output.");

                                throw new SMTException(e);
                            }

                            Predicate itp = PredicatesFactory.createInterpolantFromString(output);

                            methodInterpolants.get(i).add(itp);

                            if (!(itp instanceof Tautology) && config.enabledVerbose(this.getClass())) {
                                System.out.println("\t... inserting interpolant `" + itp + "` to " + (m.get(i) + 1));
                            }

                            lastStep = m.get(i);
                        }
                    }
                    break;
            }

            if (!satisfiable) {
                for (int i = 0; i < m.size(); ++i) {
                    if (m.get(i) < traceFormula.size() - 1) {
                        interpolants.get(m.get(i)).addAll(methodInterpolants.get(i));

                        if (config.enabledMethodGlobalRefinement()) {
                            interpolants.get(m.get(i)).addAll(methodInterpolants.get(m.size() - 1)); // The last interpolant overapproximates the whole method and may be necessary (not globally but what can you do :))
                            // Also check if there was not a return location where we might also want to track the predicate
                            int j = m.get(i) - 1;

                            if (j > m.get(0) && !m.contains(j)) {
                                interpolants.get(j).addAll(methodInterpolants.get(m.size() - 1));
                            }

                        }
                    }
                }

                if (config.enabledMethodGlobalRefinement()) {
                    if (m.get(m.size() - 1) >= traceFormula.size() - 1) {
                        for (int i : interpolants.keySet()) {
                            interpolants.get(i).addAll(methodInterpolants.get(m.size() - 1));
                        }
                    }
                    if (!methodInterpolants.get(m.size() - 1).isEmpty() && config.enabledVerbose(this.getClass())) {
                        System.out.println("\t... inserting interpolant `" + methodInterpolants.get(m.size() - 1) + "` globally to the whole method");
                    }
                }
            }
        }
    }

    /**
     * Given a Trace Formula (basically expressing a conjunction of constraints that model an execution run of the program-to-be-verified)
     * produces interpolants in all program locations (after each step - assignment, branching, ...)
     *
     * @returns null if the Trace Formula is satisfiable. Otherwise an array of !FORMULAS! (non-atomic predicates) is returned (the length of the array corresponds to the length of the Trace Formula)
     */
    public Predicate[] interpolate(TraceFormula traceFormula) throws SMTException {
        PandaConfig config = PandaConfig.getInstance();
        Predicate[] ret = new Predicate[traceFormula.size() - 1];

        for (int i = 0; i < ret.length; ++i) {
            ret[i] = Tautology.create();
        }

        boolean satisfiable = true;

        if (config.enabledCustomRefinement()) {
            satisfiable &= interpolateCustom(traceFormula, ret);
        }
        if (config.enabledNestedRefinement()) {
            satisfiable &= interpolateNested(traceFormula, ret);
        }

        if (satisfiable) {
            return null;
        }

        return ret;
    }

    public boolean interpolateCustom(TraceFormula traceFormula, Predicate[] ret) throws SMTException {
        notifyInterpolateInvoked(traceFormula);

        Date startTime = new Date();
        SupportedSMT type = PandaConfig.getInstance().getInterpolationSMT();

        SMT interpol = new SMT(type);
        PandaConfig config = PandaConfig.getInstance();

        PredicatesSMTInfoCollector collector = new PredicatesSMTInfoCollector();

        // Treat all steps separately
        // False-conjunct may turn the conjunction into False
        // Not having any symbols
        for (Step s : traceFormula) {
            collector.collect(s.getPredicate());
        }

        Set<String> classes = collector.getClasses();
        Set<String> variables = collector.getVars();
        Set<String> fields = collector.getFields();
        Set<String> arrays = collector.getArrays();
        Set<AccessExpression> objects = collector.getObjects();
        Set<Integer> fresh = collector.getFresh();

        String separator = InputType.DEBUG.getSeparator();

        StringBuilder head = new StringBuilder();

        appendClassDeclarations(classes, head, separator);
        appendVariableDeclarations(variables, head, separator);
        appendFieldDeclarations(fields, head, separator);
        appendArraysDeclarations(arrays, head, separator);

        if (!fresh.isEmpty()) {
            for (int id : fresh) {
                head.append("(declare-fun fresh_" + id + " () Int)" + separator);
            }
        }

        int interpolationGroup = 0;

        head.append("(assert (! true :named g0))"); head.append(separator); // SMTInterpol requires all nodes in the interpolation query to be named (not constant true)
        for (Step s : traceFormula) {
            head.append("(assert (! "); head.append(convertToString(s.getPredicate())); head.append(" :named g"); head.append(++interpolationGroup); head.append("))"); head.append(separator);
        }

        head.append("(check-sat)"); head.append(separator);

        // Overall (Global) interpolants (No notion of method scopes)
        if (config.enabledGlobalRefinement()) {
            switch (type) {
                case SMTInterpol:
                    head.append("(get-interpolants");
                    break;
                case Z3:
                    head.append("(get-interpolant");
                    break;
            }
            for (int i = 1; i <= interpolationGroup; ++i) {
                head.append(" g"); head.append(i);
            }
            head.append(")"); head.append(separator);
        }

        StringBuilder input = new StringBuilder();
        int batchMaxSize = 1;

        List<InterpolationEntry> batch = new LinkedList<InterpolationEntry>();
        List<List<InterpolationEntry>> batches = new LinkedList<List<InterpolationEntry>>();

        // Method scopes intepolants
        for (int i = 0; i < traceFormula.getMethods().size(); ++i) {
            prepareInterpolationForMethod(type, i, traceFormula, batch, separator);

            if (batch.size() > batchMaxSize) {
                List<InterpolationEntry> newBatch = new LinkedList<InterpolationEntry>();

                int k = 0;
                Iterator<InterpolationEntry> it = batch.iterator();

                while (k < batchMaxSize) {
                    newBatch.add(it.next());
                    it.remove();
                    ++k;
                }

                batches.add(newBatch);
            }
        }

        if (!batch.isEmpty()) {
            while (batch.size() > batchMaxSize) {
                List<InterpolationEntry> newBatch = new LinkedList<InterpolationEntry>();

                int i = 0;
                Iterator<InterpolationEntry> it = batch.iterator();

                while (i < batchMaxSize) {
                    newBatch.add(it.next());
                    it.remove();
                    ++i;
                }

                batches.add(newBatch);
            }

            batches.add(batch);
        }

        notifyInterpolateInputGenerated(input.toString());

        try {
            interpol.in.write("(push 1)");
            interpol.in.write(separator);
            interpol.in.write(head.toString());
            interpol.in.flush();
        } catch (IOException e) {
            System.err.println("SMT refuses input.");

            throw new SMTException(e);
        }

        String output;
        boolean satisfiable = true;

        try {
            output = interpol.out.readLine();

            if (output == null || !output.matches("^(un)?sat$")) {
                throw new SMTException("SMT replied with '" + output + "'");
            }

            satisfiable = output.matches("^sat$");
        } catch (IOException e) {
            System.err.println("SMT refuses to provide output.");

            throw new SMTException(e);
        }

        Map<Integer, Set<Predicate>> interpolants = new HashMap<Integer, Set<Predicate>>();

        for (int i = 0; i < traceFormula.size() - 1; ++i) {
            interpolants.put(i, new HashSet<Predicate>());
        }

        try {
            if (config.enabledGlobalRefinement()) {
                ++itp;

                switch (type) {
                    case SMTInterpol:
                        output = interpol.out.readLine();

                        if (!satisfiable) {
                            Predicate[] global = PredicatesFactory.createInterpolantsFromString(output);

                            for (int i = 0; i < global.length; ++i) {
                                interpolants.get(i).add(global[i]);
                            }
                        }
                        break;
                    case Z3:
                        if (satisfiable) {
                            output = interpol.out.readLine();
                        } else {
                            for (int i = 0; i < traceFormula.size() - 1; ++i) {
                                output = interpol.out.readLine();

                                interpolants.get(i).add(PredicatesFactory.createInterpolantFromString(output));
                            }
                        }
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("SMT refuses to provide output.");

            throw new SMTException(e);
        }

        Iterator<List<InterpolationEntry>> it = batches.iterator();

        while (it.hasNext()) {
            batch = it.next();

            try {
                for (InterpolationEntry entry : batch) {
                    interpol.in.write(entry.input);
                }
                interpol.in.flush();
            } catch (IOException e) {
                System.err.println("SMT refuses input.");

                throw new SMTException(e);
            }

            // Inject method-scoped interpolants
            for (InterpolationEntry entry : batch) {
                interpolateMethod(interpol, satisfiable, entry.method, traceFormula, interpolants, entry.format);
            }
        }

        for (int step : interpolants.keySet()) {
            for (Predicate itp : interpolants.get(step)) {
                if (!(itp instanceof Contradiction)) {
                    ret[step] = Conjunction.create(ret[step], itp);
                }
            }
        }

        notifyInterpolateExecuted(ret);

        if (config.enabledVerbose(this.getClass())) {
            System.out.println("Interpolants: ");

            for (int i = 0; i < ret.length; ++i) {
                System.out.println("\t" + ret[i]);
            }
        }

        try {
            interpol.in.write("(pop 1)");
            interpol.in.write(separator);
            interpol.in.flush();
            interpol.close();
        } catch (IOException e) {
            System.err.println("SMT refuses input.");

            throw new SMTException(e);
        } finally {
            Date endTime = new Date();

            elapsed += endTime.getTime() - startTime.getTime();
        }

        return satisfiable;
    }

    private boolean interpolateNested(TraceFormula traceFormula, Predicate[] interpolants) {
        ++itp;

        notifyInterpolateInvoked(traceFormula);

        Date startTime = new Date();
        SupportedSMT type = PandaConfig.getInstance().getInterpolationSMT();

        SMT interpol = new SMT(type);
        PandaConfig config = PandaConfig.getInstance();

        PredicatesSMTInfoCollector collector = new PredicatesSMTInfoCollector();

        // Treat all steps separately
        // False-conjunct may turn the conjunction into False
        // Not having any symbols
        for (Step s : traceFormula) {
            collector.collect(s.getPredicate());
        }

        Set<String> classes = collector.getClasses();
        Set<String> variables = collector.getVars();
        Set<String> fields = collector.getFields();
        Set<String> arrays = collector.getArrays();
        Set<AccessExpression> objects = collector.getObjects();
        Set<Integer> fresh = collector.getFresh();

        String separator = InputType.DEBUG.getSeparator();

        StringBuilder head = new StringBuilder();

        appendClassDeclarations(classes, head, separator);
        appendVariableDeclarations(variables, head, separator);
        appendFieldDeclarations(fields, head, separator);
        appendArraysDeclarations(arrays, head, separator);

        if (!fresh.isEmpty()) {
            for (int id : fresh) {
                head.append("(declare-fun fresh_" + id + " () Int)" + separator);
            }
        }

        try {
            interpol.in.write(head.toString());
            interpol.in.flush();
        } catch (IOException e) {
            System.err.println("SMT refuses input.");

            throw new SMTException(e);
        }

        head.setLength(0);
        head.append("(push 1)"); head.append(separator);

        int interpolationGroup = 0;

        head.append("(assert (! true :named g0))"); head.append(separator); // SMTInterpol requires all nodes in the interpolation query to be named (not constant true)
        for (Step s : traceFormula) {
            head.append("(assert (! "); head.append(convertToString(s.getPredicate())); head.append(" :named g"); head.append(++interpolationGroup); head.append("))"); head.append(separator);
        }
        head.append("(check-sat)"); head.append(separator);
        head.append("; Interpolants"); head.append(separator);

        // Overall (Global) interpolants (No notion of method scopes)
        if (config.enabledGlobalRefinement()) {
            switch (type) {
                case SMTInterpol:
                    head.append("(get-interpolants");
                    break;
                case Z3:
                    head.append("(get-interpolant");
                    break;
            }
            for (int i = 1; i <= interpolationGroup; ++i) {
                head.append(" g"); head.append(i);
            }
            head.append(")"); head.append(separator);
        }

        String output;
        boolean satisfiable = true;

        List<Method> methods2 = traceFormula.getMethods();
        List<Method> methods1 = new ArrayList<Method>();

        methods1.addAll(methods2);

        Collections.sort(methods1, new Comparator<Method>() {
            @Override
            public int compare(Method m1, Method m2) {
                return m1.getCall() - m2.getCall();
            }
        });

        // Ordered by call, maps calls to returns
        int k = 0;
        int[] calls1 = new int[methods1.size()];
        Integer[] returns1 = new Integer[methods1.size()];

        for (Method m : methods1) {
            calls1[k] = m.getCall();

            if (m.getReturn() < traceFormula.size()) {
                returns1[k] = m.getReturn();
            } else {
                returns1[k] = null;
            }

            ++k;
        }

        // Ordered by return, maps returns to calls
        int l = 0;
        int[] calls2 = new int[methods2.size()];
        Integer[] returns2 = new Integer[methods2.size()];

        for (Method m : methods2) {
            calls2[l] = m.getCall();

            if (m.getReturn() < traceFormula.size()) {
                returns2[l] = m.getReturn();
            } else {
                returns2[l] = null;
            }

            ++l;
        }

        int n = traceFormula.size();
        int rawItp = ++interpolationGroup;
        Predicate[] itps = new Predicate[n + 1];
        Predicate[] ret = new Predicate[n - 1];

        itps[0] = Tautology.create();

        StringBuilder input = new StringBuilder();

        // Closest calls/returns to the current step
        k = 0;
        l = returns2.length - 1;

        head.append("(assert (! true :named g"); head.append(interpolationGroup); head.append("))"); head.append(separator);
        for (int i = 0; i < n; ++i) {
            notifyInterpolateInputGenerated(head.toString());

            try {
                interpol.in.write(head.toString());
                interpol.in.flush();
            } catch (IOException e) {
                System.err.println("SMT refuses input.");

                throw new SMTException(e);
            }

            try {
                interpol.in.write(input.toString());
                interpol.in.flush();
            } catch (IOException e) {
                System.err.println("SMT refuses input.");

                throw new SMTException(e);
            }

            input.setLength(0);

            try {
                output = interpol.out.readLine();

                if (output == null || !output.matches("^(un)?sat$")) {
                    throw new SMTException("SMT replied with '" + output + "'");
                }

                satisfiable = output.matches("^sat$");
            } catch (IOException e) {
                System.err.println("SMT refuses to provide output.");

                throw new SMTException(e);
            }

            if (calls1[k] == i) {
                input.append("; call "); input.append(methods1.get(k).getInfo().getFullName()); input.append(separator);
            }
            if (returns2[l] != null && returns2[l] == i) {
                input.append("; return from "); input.append(methods2.get(l).getInfo().getFullName()); input.append(separator);
            }
            input.append("(compute-interpolant ");

            String stepClass = null;

            // psi-
            input.append("(and g0 g0");

            if (calls1[k] != i && (returns2[l] == null || returns2[l] != i)) { // Internal
                input.append(" g"); input.append(rawItp + i); input.append(" g"); input.append(i + 1);

                stepClass = "internal";
            } else if (calls1[k] == i) { // Call
                //if (k > 0 && returns1[k - 1] == null) { // Parent
                    input.append(" g"); input.append(rawItp + i); input.append(" g"); input.append(i + 1);
                //}
                /**
                 * According to Nested Interpolants (POPL 2010), the above rule is only applied for "parent" calls
                 * Error traces like in the test gov.nasa.jpf.abstraction.NestedInterpolantsTest will not produces sufficient itps
                 *
                 * A() {this.a = new int[42]}
                 *
                 * a = new A(); m1(); m2(a); m3(); assert a.a.length == 42;
                 *
                 * when interpolating call to m1 it is necessary that phi- is not just true
                 */

                stepClass = "call " + methods1.get(k).getInfo().getName();
            } else if (returns2[l] == i) { // Return
                input.append(" g"); input.append(rawItp + i); input.append(" g"); input.append(i + 1);
                input.append(" g"); input.append(rawItp + calls2[l]); input.append(" g"); input.append(calls2[l] + 1);

                stepClass = "return " + methods2.get(l).getInfo().getName();
            }
            input.append(")");

            // Find the closest call/return to next step
            while (k < calls1.length - 1 && calls1[k] < i + 1) {
                ++k;
            }
            while (l > 0 && returns2[l] != null && returns2[l] < i) {
                --l;
            }

            // psi+
            input.append(" (and g0 g0");
            for (int j = i + 1; j < n; ++j) {
                input.append(" g"); input.append(j + 1);
            }
            for (int j = 0; j <= k; ++j) {
                if (returns1[j] != null && returns1[j] > i) { // Pending calls
                    input.append(" g"); input.append(rawItp + j); input.append(" g"); input.append(calls1[j] + 1);
                }
            }
            input.append(")");

            input.append(")");
            input.append(separator);

            input.append("(pop 1)"); input.append(separator);

            // Query
            try {
                notifyInterpolateInputGenerated(input.toString());

                interpol.in.write(input.toString());
                interpol.in.flush();

                input.setLength(0);
            } catch (IOException e) {
                System.err.println("SMT refuses input.");

                throw new SMTException(e);
            }

            try {
                output = interpol.out.readLine();

                boolean stepSatisfiable = output.matches("^sat$");

                String newRawItp;

                if (stepSatisfiable) {
                    System.out.println("[WARNING] Invalid interpolation step.");

                    newRawItp = "true";
                    itps[i + 1] = Tautology.create();
                } else {
                    output = interpol.out.readLine();

                    newRawItp = output.replaceAll("\\s+", " ");
                    itps[i + 1] = PredicatesFactory.createInterpolantFromString(newRawItp);
                }

                head.append("(assert (! "); head.append(newRawItp); head.append(" :named g"); head.append(++interpolationGroup); head.append(")) ; computed at step "); head.append(i + 1); head.append(" as "); head.append(stepClass); head.append(separator);
            } catch (IOException e) {
                System.err.println("SMT refuses to provide output.");

                throw new SMTException(e);
            }

            if (i < n - 1) {
                interpolants[i] = Conjunction.create(interpolants[i], itps[i + 1]);
            }
        }

        notifyInterpolateExecuted(ret);

        if (config.enabledVerbose(this.getClass())) {
            System.out.println("Interpolants: ");

            for (int i = 0; i < ret.length; ++i) {
                System.out.println("\t" + ret[i]);
            }
        }

        Date endTime = new Date();

        elapsed += endTime.getTime() - startTime.getTime();

        return satisfiable;
    }

    /**
     * Checks satisfiability of a bundle of formulas encoded as assertions in the input.
     * It is expected that the (check-sat) is invoked count-times.
     *
     * If extractModels is true it is also expected that (get-value ...) is invoked and that it succeeds when the query is SAT
     */
    private QueryResponse[] isSatisfiable(int count, String input, boolean extractModels) throws SMTException {
        isSat += count;
        Date startTime = new Date();
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

                        // Account for SMTLIB encoding of negative numbers (unary minus applied to a positive number)
                        Pattern pattern = Pattern.compile("^\\( *\\(value ((?<positivevalue>[0-9]*)|\\(- (?<negativevalue>[0-9]*)\\))\\) *\\)$");
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
        } finally {
            Date endTime = new Date();

            elapsed += endTime.getTime() - startTime.getTime();
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

    /**
     * Envelopes a given string representation of a formula so that it can be checked for satisfiability in isolation (with potential extraction of model)
     */
    private String prepareFormulaEvaluation(Predicate predicate, String formula, FormulaType formulaType, InputType inputType, Boolean cachedIsSatisfiable, Integer cachedModel, boolean extractModel) {
        boolean cacheDisabled = cachedIsSatisfiable == null || (extractModel && cachedModel == null);
        String separator = inputType.getSeparator();
        String linePrefix = cacheDisabled ? "" : "; ";
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
        if (inputType == InputType.DEBUG || cacheDisabled) {
            ret.append(linePrefix); ret.append("(push 1)"); ret.append(separator);
            ret.append(linePrefix); ret.append("(assert "); ret.append(formula); ret.append(")"); ret.append(separator);
            ret.append(linePrefix); ret.append("(check-sat)"); ret.append(separator);

            if (extractModel) {
                ret.append(linePrefix); ret.append("(get-value (value))"); ret.append(separator);
            }

            ret.append(linePrefix); ret.append("(pop 1)"); ret.append(separator);
        } else { // Reuse of cached values
            ret.append("(push 1)"); ret.append(separator);
            ret.append("(assert "); ret.append(cachedIsSatisfiable); ret.append(")"); ret.append(separator);

            if (extractModel) {
                ret.append("(assert (= value ");
                if (cachedModel < 0) {
                    ret.append("(- "); ret.append(-cachedModel); ret.append(")");
                } else {
                    ret.append(cachedModel);
                }
                ret.append("))"); ret.append(separator);
            }

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

    /**
     * Methods for generating declarations of symbols used in SMT queries
     */

    private void appendClassDeclarations(Set<String> classes, StringBuilder input, String separator) {
        for (String c : classes) {
            if (!isDefined(c)) {
                input.append("(declare-fun class_"); input.append(c.replace("_", "__").replace('.', '_')); input.append(" () Int)"); input.append(separator);

                define(c);
            }
        }
        input.append(separator);
    }

    private void appendVariableDeclarations(Set<String> vars, StringBuilder input, String separator) {
        for (String var : vars) {
            if (!isDefined(var)) {
                input.append("(declare-fun var_"); input.append(var); input.append(" () Int)"); input.append(separator);

                define(var);
            }
        }
        input.append(separator);
    }

    private void appendSpecialDeclarations(Set<String> specials, StringBuilder input, String separator) {
        for (String special : specials) {
            if (!isDefined(special)) {
                input.append("(declare-fun "); input.append(special); input.append(" () Int)"); input.append(separator);

                define(special);
            }
        }
        input.append(separator);
    }

    private void appendFieldDeclarations(Set<String> fields, StringBuilder input, String separator) {
        for (String field : fields) {
            if (!isDefined(field)) {
                input.append("(declare-fun field_"); input.append(field); input.append(" () (Array Int Int))"); input.append(separator);

                define(field);
            }
        }
        input.append(separator);
    }

    private void appendArraysDeclarations(Set<String> arraysSets, StringBuilder input, String separator) {
        for (String arrays : arraysSets) {
            if (!isDefined(arrays)) {
                input.append("(declare-fun "); input.append(arrays); input.append(" () (Array Int (Array Int Int)))"); input.append(separator);

                define(arrays);
            }
        }
        input.append(separator);
    }

    /**
     * It is necessary to explicitely capture uniqueness of fresh symbols
     */
    private void appendFreshConstraints(Set<Integer> fresh, Set<AccessExpression> objects, StringBuilder input, String separator) {
        Set<String> freshConstraints = new HashSet<String>();

        PredicateAbstraction abs = PredicateAbstraction.getInstance();
        Universe universe = abs.getSymbolTable().get(0).getUniverse();
        AnonymousObject anon = AnonymousObject.create(universe.getFresh());

        fresh.add(anon.getReference().getReferenceNumber());

        for (int id : fresh) {
            input.append("(declare-fun fresh_" + id + " () Int)" + separator);
        }

        // All fresh symbols are different
        if (fresh.size() > 1) {
            input.append("(assert (distinct");
            for (int id : fresh) {
                input.append(" fresh_");
                input.append(id);
            }
            input.append("))");
            input.append(separator);
        }

        for (AccessExpression object : objects) {
            Predicate distinction = Implication.create(Negation.create(object.getPreconditionForBeingFresh().replace(DefaultFresh.create(), anon)), Negation.create(Equals.createUnminimized(anon, object)));

            freshConstraints.add("(assert " + convertToString(distinction) + ")" + separator);
        }

        for (String constraint : freshConstraints) {
            input.append(constraint);
        }
        input.append(separator);
    }

    /**
     * Prepare an input for the SMT to query a model of an expression (constrained by other predicates)
     */
    private String prepareGetModelInput(Set<String> classes, Set<String> vars, Set<String> fields, Set<String> arrays, Set<Integer> fresh, Predicate predicate, String formula, InputType inputType) {
        String separator = inputType.getSeparator();

        StringBuilder input = new StringBuilder();

        Set<AccessExpression> objects = Collections.emptySet();

        input.append("(push 1)"); input.append(separator);
        appendClassDeclarations(classes, input, separator);
        appendVariableDeclarations(vars, input, separator);
        appendFieldDeclarations(fields, input, separator);
        appendArraysDeclarations(arrays, input, separator);
        appendFreshConstraints(fresh, objects, input, separator);

        Boolean cachedValue = cache.get(formula).getSatisfiability();
        Integer cachedModel = cache.get(formula).getModel();

        input.append(prepareFormulaEvaluation(predicate, formula, FormulaType.MODEL_QUERY, inputType, cachedValue, cachedModel, true));

        input.append("(pop 1)"); input.append(separator);

        return input.toString();
    }

    /**
     * Prepare an input for the SMT to derive valuation of predicate (in POST state) based on the PRE state
     */
    private String prepareValuatePredicatesInput(Set<String> classes, Set<String> vars, Set<String> fields, Set<String> arrays, Set<AccessExpression> objects, Set<Integer> fresh, Predicate[] predicates, String[] formulas, InputType inputType) {
        String separator = inputType.getSeparator();

        StringBuilder input = new StringBuilder();

        input.append("(push 1)"); input.append(separator);
        appendClassDeclarations(classes, input, separator);
        appendVariableDeclarations(vars, input, separator);
        appendFieldDeclarations(fields, input, separator);
        appendArraysDeclarations(arrays, input, separator);

        if (!fresh.isEmpty()) {
            appendFreshConstraints(fresh, objects, input, separator);
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

    /**
     * Prepare input for an SMT to check simple satisfiability of formulas
     */
    private String prepareIsSatisfiableInput(Set<String> classes, Set<String> vars, Set<String> fields, Set<String> arrays, Set<Integer> fresh, List<Predicate> predicates, String[] formulas, InputType inputType) {
        String separator = inputType.getSeparator();

        StringBuilder input = new StringBuilder();

        Set<AccessExpression> objects = new HashSet<AccessExpression>();

        input.append("(push 1)"); input.append(separator);
        appendClassDeclarations(classes, input, separator);
        appendVariableDeclarations(vars, input, separator);
        appendFieldDeclarations(fields, input, separator);
        appendArraysDeclarations(arrays, input, separator);
        appendFreshConstraints(fresh, objects, input, separator);

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

    /**
     * Retrieves any arbitrary models of variables/fields/... based on constraints imposed by the state
     */
    public int[] getModels(Predicate stateFormula, AccessExpression[] expressions) {
        return getModels(stateFormula, expressions, true);
    }

    public int[] getModels(Predicate stateFormula, AccessExpression[] expressions, boolean useCache) {
        notifyGetModelsInvoked(stateFormula, expressions);

        String separator = InputType.NORMAL.getSeparator();
        StringBuilder input = new StringBuilder();
        int[] ret = new int[expressions.length];

        String state = convertToString(stateFormula);
        int cachedCount = 0;
        boolean[] cached = null;
        String[] queries = null;

        if (USE_MODELS_CACHE && useCache) {
            cached = new boolean[expressions.length];
            queries = new String[expressions.length];
        }

        PredicatesSMTInfoCollector collector = new PredicatesSMTInfoCollector();

        collector.collect(stateFormula);

        for (AccessExpression e : expressions) {
            collector.collect(e);
        }

        Set<String> classes = collector.getClasses();
        Set<String> variables = collector.getVars();
        Set<String> specials = collector.getSpecials();
        Set<String> fields = collector.getFields();
        Set<String> arrays = collector.getArrays();
        Set<AccessExpression> objects = collector.getObjects();
        Set<Integer> fresh = collector.getFresh();

        Set<AccessExpression> exprs = new HashSet<AccessExpression>();

        for (AccessExpression e1 : expressions) {
            exprs.clear();
            e1.addAccessExpressionsToSet(exprs);

            for (AccessExpression e2 : exprs) {
                Root r = e2.getRoot();

                if (r instanceof PackageAndClass) {
                    classes.add(r.getName());
                } else if (r instanceof AnonymousObject) {
                    AnonymousObject o = (AnonymousObject) r;
                    fresh.add(o.getReference().getReferenceNumber());
                } else {
                    variables.add(r.getName());
                }
            }
        }

        input.append("(push 1)"); input.append(separator);
        appendClassDeclarations(classes, input, separator);
        appendVariableDeclarations(variables, input, separator);
        appendSpecialDeclarations(specials, input, separator);
        appendFieldDeclarations(fields, input, separator);
        appendArraysDeclarations(arrays, input, separator);

        if (!fresh.isEmpty()) {
            Set<AccessExpression> nonFreshObjects = Collections.emptySet();

            appendFreshConstraints(fresh, nonFreshObjects, input, separator);
        }

        input.append("(assert "); input.append(state); input.append(")"); input.append(separator);

        for (int i = 0; i < expressions.length; ++i) {
            AccessExpression expr = expressions[i];

            Predicate valueConstraint = Equals.createUnminimized(SpecialVariable.create("value"), expr);

            String query = convertToString(valueConstraint);

            if (USE_MODELS_CACHE && cache.getQueries().contains(query) && useCache) {
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

        notifyGetModelsInputGenerated(input.toString());

        QueryResponse[] responses = isSatisfiable(expressions.length - cachedCount, input.toString(), true);

        for (int i = 0, j = 0; i < responses.length; ++i, ++j) {
            if (USE_MODELS_CACHE && useCache) {
                // Cache responses
                cache.put(state + queries[i], responses[i]);

                // Skip cached values
                while (cached[j]) {
                    ++j;
                }
            }

            // Insert responses
            Integer model = responses[i].getModel();

            if (model == null) {
                return null;
            }

            ret[j] = model;
        }

        notifyGetModelsExecuted(expressions, ret);

        return ret;
    }

    /**
     * Retrieves a model (if there is any) of an expression (+ - * / ...) based on the state
     */
    public Integer getModel(Expression expression, List<Pair<Predicate, TruthValue>> determinants) {
        notifyGetModelInvoked(expression, determinants);

        Predicate valueConstraint = Equals.createUnminimized(SpecialVariable.create("value"), expression);
        PredicatesSMTInfoCollector collector = new PredicatesSMTInfoCollector();

        collector.collect(valueConstraint);

        for (Pair<Predicate, TruthValue> pair : determinants) {
            Predicate determinant = pair.getFirst();

            collector.collect(determinant);
        }

        Set<String> classes = collector.getClasses();
        Set<String> variables = collector.getVars();
        Set<String> fields = collector.getFields();
        Set<String> arrays = collector.getArrays();
        Set<Integer> fresh = collector.getFresh();

        String formula = createFormula(valueConstraint, determinants, collector.getAdditionalPredicates(valueConstraint));

        String input = prepareGetModelInput(classes, variables, fields, arrays, fresh, valueConstraint, formula, InputType.NORMAL);

        if (!listeners.isEmpty()) {
            String debugInput = prepareGetModelInput(classes, variables, fields, arrays, fresh, valueConstraint, formula, InputType.DEBUG);

            notifyGetModelInputGenerated(debugInput);
        }

        QueryResponse response = isSatisfiable(1, input, true)[0];

        if (USE_CACHE) {
            cache.put(formula, response);
        }

        notifyGetModelExecuted(response.getSatisfiability(), response.getModel());

        return response.getModel();
    }

    /**
     * Checks satisfiability of individual formulas
     */
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
        Set<String> arrays = collector.getArrays();
        Set<Integer> fresh = collector.getFresh();

        String[] formulas = new String[predicates.size()];

        int i = 0;
        for (Predicate predicate : predicates) {
            formulas[i] = convertToString(predicate);
            ++i;
        }

        String input = prepareIsSatisfiableInput(classes, variables, fields, arrays, fresh, predicates, formulas, InputType.NORMAL);

        if (!listeners.isEmpty()) {
            String debugInput = prepareIsSatisfiableInput(classes, variables, fields, arrays, fresh, predicates, formulas, InputType.DEBUG);

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

    public static boolean checkEquivalence(Expression e1, Expression e2) {
        if (e1.equals(e2)) {
            return true;
        }

        if (PandaConfig.getInstance().enabledVerbose(SMT.class)) {
            System.out.println("Check: " + e1 + " ~ " + e2);
        }

        List<Predicate> ps = new ArrayList<Predicate>();

        ps.add(Negation.create(Equals.createUnminimized(e1, e2)));

        SMT smt = new SMT();

        boolean ret = !smt.isSatisfiable(ps)[0];

        smt.close();

        return ret;
    }

    public static boolean checkEquivalence(Predicate p1, Predicate p2) {
        if (p1.equals(p2)) {
            return true;
        }

        if (PandaConfig.getInstance().enabledVerbose(SMT.class)) {
            System.out.println("Check: " + p1 + " ~ " + p2);
        }

        List<Predicate> ps = new ArrayList<Predicate>();

        ps.add(
            Negation.create(
                Disjunction.create(
                    Conjunction.create(p1, p2),
                    Conjunction.create(Negation.create(p1), Negation.create(p2))
                )
            )
        );

        SMT smt = new SMT();

        boolean ret = !smt.isSatisfiable(ps)[0];

        smt.close();

        return ret;
    }

    private Stack<Set<String>> definedSymbols = null;

    public void assertStep(Predicate p) {
        PredicatesSMTInfoCollector collector = new PredicatesSMTInfoCollector();

        collector.collect(p);

        Set<String> classes = collector.getClasses();
        Set<String> variables = collector.getVars();
        Set<String> fields = collector.getFields();
        Set<String> arrays = collector.getArrays();
        Set<AccessExpression> objects = collector.getObjects();
        Set<Integer> fresh = collector.getFresh();

        String separator = InputType.DEBUG.getSeparator();

        StringBuilder input = new StringBuilder();

        appendClassDeclarations(classes, input, separator);
        appendVariableDeclarations(variables, input, separator);
        appendFieldDeclarations(fields, input, separator);
        appendArraysDeclarations(arrays, input, separator);

        if (!fresh.isEmpty()) {
            for (int id : fresh) {
                if (!isDefined("fresh_" + id)) {
                    input.append("(declare-fun fresh_" + id + " () Int)" + separator);

                    define("fresh_" + id);
                }
            }
        }

        input.append("(assert " + convertToString(p) + ")" + separator);

        try {
            in.write(input.toString());
            in.flush();
        } catch (Exception e) {
            throw new SMTException("SMT failed:\n" + e.getMessage());
        }
    }

    public boolean isDefined(String s) {
        if (definedSymbols != null) {
            for (Set<String> set : definedSymbols) {
                if (set.contains(s)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void define(String s) {
        if (definedSymbols != null) {
            definedSymbols.peek().add(s);
        }
    }

    public void push(int stateId) {
        if (definedSymbols == null) {
            definedSymbols = new Stack<Set<String>>();
        }

        definedSymbols.push(new HashSet<String>());

        try {
            in.write("; State " + stateId + "\n");
            in.write("(push 1)\n");
            in.flush();
        } catch (Exception e) {
            throw new SMTException("SMT failed:\n" + e.getMessage());
        }
    }

    public void pop() {
        definedSymbols.pop();

        try {
            in.write("(pop 1)\n");
            in.flush();
        } catch (Exception e) {
            throw new SMTException("SMT failed:\n" + e.getMessage());
        }
    }

    /**
     * Determines values of predicates in POST state based on PRE state
     */
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
        Set<String> arrays = collector.getArrays();
        Set<AccessExpression> objects = collector.getObjects();
        Set<Integer> fresh = collector.getFresh();

        String input = prepareValuatePredicatesInput(classes, vars, fields, arrays, objects, fresh, predicatesArray, formulasArray, InputType.NORMAL);

        if (!listeners.isEmpty()) {
            String debugInput = prepareValuatePredicatesInput(classes, vars, fields, arrays, objects, fresh, predicatesArray, formulasArray, InputType.DEBUG);

            notifyValuatePredicatesInputGenerated(debugInput);
        }

        return evaluate(input, predicatesArray, formulasArray);
    }

    /**
     * Evaluates predicates regardless of state (in any state) - useful to detect tautologies/contradictions
     */
    public Map<Predicate, TruthValue> valuatePredicates(Set<Predicate> predicates) throws SMTException {
        Map<Predicate, PredicateValueDeterminingInfo> predicateDeterminingInfos = new HashMap<Predicate, PredicateValueDeterminingInfo>();

        for (Predicate predicate : predicates) {
            Map<Predicate, TruthValue> determinants = Collections.emptyMap();

            PredicateValueDeterminingInfo determiningInfo = new PredicateValueDeterminingInfo(predicate, Negation.create(predicate), determinants);

            predicateDeterminingInfos.put(predicate, determiningInfo);
        }

        return valuatePredicates(predicateDeterminingInfos);
    }

    /**
     * Helper for producing SMTLIB compliant string representations of formulas and such
     */
    private static String convertToString(PredicatesComponentVisitable visitable) {
        PredicatesSMTStringifier stringifier = new PredicatesSMTStringifier();

        visitable.accept(stringifier);

        return stringifier.getString();
    }

    /**
     * Helper for encoding model queries together with all the determinants (which may be conflicting: x = 0, x != 0, when probing for additional models)
     */
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
        }


        for (Predicate additional : additionalClauses) {
            formula = Conjunction.create(formula, additional);
        }

        return convertToString(formula);
    }

    /**
     * Helper for encoding PRE-POST state valuation of predicates (encodes determinants and weakest precondition)
     */
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

    /**
     * Evaluation of multiple predicates in POST state based on PRE state consists of two queries for VALIDITY
     *
     * det => WP(statement, pred)
     * det => WP(statement, not pred)
     *
     * These are reduced here to SAT queries and recombined
     *
     * Cache of old queries is updated here
     */
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
