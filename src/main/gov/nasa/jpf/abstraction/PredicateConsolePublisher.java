package gov.nasa.jpf.abstraction;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.report.ConsolePublisher;
import gov.nasa.jpf.report.Reporter;
import gov.nasa.jpf.util.Left;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Path;
import gov.nasa.jpf.vm.Step;
import gov.nasa.jpf.vm.Transition;

import gov.nasa.jpf.abstraction.common.MethodPredicateContext;
import gov.nasa.jpf.abstraction.common.ObjectPredicateContext;
import gov.nasa.jpf.abstraction.common.StaticPredicateContext;
import gov.nasa.jpf.abstraction.common.BytecodeInterval;
import gov.nasa.jpf.abstraction.common.BytecodeIntervals;
import gov.nasa.jpf.abstraction.common.BytecodeRange;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicateContext;
import gov.nasa.jpf.abstraction.common.Predicates;
import gov.nasa.jpf.abstraction.common.access.Method;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultMethod;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultPackageAndClass;
import gov.nasa.jpf.abstraction.smt.SMT;
import gov.nasa.jpf.abstraction.state.MethodFramePredicateValuation;
import gov.nasa.jpf.abstraction.state.State;
import gov.nasa.jpf.abstraction.state.Trace;

public class PredicateConsolePublisher extends ConsolePublisher {

    // whether to skip static init instructions when printing code
    private static boolean skip;

    public PredicateConsolePublisher(Config conf, Reporter reporter) {
        super(conf, reporter);
    }

    public void printTrace() {
        publishTrace();
    }

    @Override
    protected void publishTrace() {
        publishTopicStart("trace " + reporter.getCurrentErrorId());

        publishTrace(out, reporter.getPath());
    }

    public static void publishTrace(PrintStream out, Path path) {
        publishTrace(new PrintWriter(out, true), path);
    }

    public static void publishTrace(PrintWriter out, Path path) {
        publishTrace(out, path, true, true, true, true, true, true);
    }

    private static void publishTrace(PrintWriter out, Path path, boolean showCG, boolean showSteps, boolean showMethod, boolean showSource, boolean showLocation, boolean showCode) {
        if (path.size() == 0) {
            return; // nothing to publish
        }

        skip = true;

        Trace trace = PredicateAbstraction.getInstance().getTrace();
        int progressAlongTrace = 1;

        // get main thread and method
        ThreadInfo tiMain = path.get(0).getThreadInfo();
        MethodInfo miMain = tiMain.getEntryMethod();

        for (int i = 0; i < path.size(); ++i) {
            Transition t = path.get(i);

            out.print("------------------------------------------------------ ");
            out.println("transition #" + i + " thread: " + t.getThreadIndex());

            State state = null;

            if (progressAlongTrace < trace.size()) {
                state = trace.get(progressAlongTrace);
            }

            if (showCG) {
                out.println("choice: " + t.getChoiceGenerator());
                out.println("");
            }

            if (showSteps) {
                out.println("instructions:");

                String lastLine = null;
                MethodInfo lastMi = null;
                int nNoSrc = 0;

                boolean skipped = false;

                for (Step s : t) {
                    Instruction insn = s.getInstruction();

                    // start printing code in the main method
                    if (insn.getMethodInfo() == miMain) skip = false;

                    if (skip) {
                        if (!skipped) {
                            System.out.println("... skipping ...");
                        }
                        skipped = true;
                        continue;
                    } else {
                        skipped = false;
                    }

                    if (showMethod) {
                        MethodInfo mi = insn.getMethodInfo();

                        if (mi != lastMi) {
                            ClassInfo mci = mi.getClassInfo();

                            out.print("  ");

                            if (mci != null) {
                                out.print(mci.getName());
                                out.print(".");
                            }

                            out.println(mi.getUniqueName());

                            lastMi = mi;
                        }
                    }

                    if (showSource) {
                        String line = s.getLineString();

                        if (line != null) {
                            if (!line.equals(lastLine)) {
                                if (nNoSrc > 0) {
                                    out.println("        [" + nNoSrc + " insn w/o sources]");
                                }

                                out.print("    ");

                                if (showLocation) {
                                    out.print(Left.format(s.getLocationString(),30));
                                    out.print(" : ");
                                }

                                out.println(line.trim());
                                nNoSrc = 0;
                            }
                        } else { // no source
                            nNoSrc++;
                        }

                        lastLine = line;
                    }

                    if (showCode) {
                        out.print("      ");
                        out.println(insn);
                    }
                }

                if (showSource && !showCode && (nNoSrc > 0)) {
                    out.println("      [" + nNoSrc + " insn w/o sources]");
                }

                out.println("");
            }

            if (state != null && i + 1 < path.size()) {
                Transition nextT = path.get(i + 1);

                if (nextT != null) {
                    Step nextS = nextT.getStep(0);

                    if (nextS != null) {
                        Instruction insn = nextS.getInstruction();

                        MethodFramePredicateValuation valuation = state.predicateValuationStacks.get(state.currentThread).top();

                        out.println("predicates: ");
                        out.println("\t" + valuation.toString(insn.getPosition()).replaceAll("\n", "\n\t"));
                    }
                }
            }

            ++progressAlongTrace;
        }
    }

    @Override
    public synchronized void printStatistics (PrintWriter out) {
        super.printStatistics(out);

        PredicateAbstraction abs = PredicateAbstraction.getInstance();

        Predicates preds = MethodFramePredicateValuation.getCumulativePredicateSet();
        Map<Method, Set<Predicate>> method2pred = new HashMap<Method, Set<Predicate>>();
        int method = 0;
        int location = 0;
        int total = 0;

        for (PredicateContext ctx : preds.contexts) {
            if (ctx instanceof MethodPredicateContext) {
                MethodPredicateContext mctx = (MethodPredicateContext) ctx;
                Method m = mctx.getMethod();

                boolean contains = PredicateAbstractionFactory.systemPredicates.contexts.contains(ctx);

                for (PredicateContext pkgCtxCandidate : PredicateAbstractionFactory.systemPredicates.contexts) {
                    if (pkgCtxCandidate instanceof ObjectPredicateContext) {
                        ObjectPredicateContext pkgCtx = (ObjectPredicateContext) pkgCtxCandidate;

                        contains |= pkgCtx.getPackageAndClass().contains(m.getPackageAndClass());
                    }
                }

                if (!contains) {
                    for (Predicate p : ctx.getPredicates()) {
                        if (!method2pred.containsKey(m)) {
                            method2pred.put(m, new HashSet<Predicate>());
                        }
                        if (method2pred.get(m).contains(p)) {
                            for (Predicate q : method2pred.get(m)) {
                                if (p.equals(q)) {
                                    q.setScope(q.getScope().merge(p.getScope()));
                                    break;
                                }
                            }
                        } else {
                            method2pred.get(m).add(p);
                        }
                    }
                }
            }
        }
        for (PredicateContext ctx : preds.contexts) {
            if (ctx instanceof ObjectPredicateContext) {
                ObjectPredicateContext octx = (ObjectPredicateContext) ctx;
                PackageAndClass pc = octx.getPackageAndClass();

                boolean contains = PredicateAbstractionFactory.systemPredicates.contexts.contains(ctx);

                for (PredicateContext pkgCtxCandidate : PredicateAbstractionFactory.systemPredicates.contexts) {
                    if (pkgCtxCandidate instanceof ObjectPredicateContext) {
                        ObjectPredicateContext pkgCtx = (ObjectPredicateContext) pkgCtxCandidate;

                        contains |= pkgCtx.getPackageAndClass().contains(pc);
                    }
                }

                if (!contains) {
                    boolean added = false;

                    for (Method m : method2pred.keySet()) {
                        if (m.getPackageAndClass().equals(pc)) {
                            added = true;

                            for (Predicate p : octx.getPredicates()) {
                                if (method2pred.get(m).contains(p)) {
                                    for (Predicate q : method2pred.get(m)) {
                                        if (p.equals(q)) {
                                            q.setScope(q.getScope().merge(p.getScope()));
                                            break;
                                        }
                                    }
                                } else {
                                    method2pred.get(m).add(p);
                                }
                            }
                        }
                    }

                    if (!added) {
                        Method m = DefaultMethod.create(pc, "<init>");

                        method2pred.put(m, new HashSet<Predicate>());
                        method2pred.get(m).addAll(octx.getPredicates());
                    }
                }
            }
        }
        for (PredicateContext ctx : preds.contexts) {
            if (!PredicateAbstractionFactory.systemPredicates.contexts.contains(ctx)) {
                if (ctx instanceof StaticPredicateContext) {
                    StaticPredicateContext sctx = (StaticPredicateContext) ctx;

                    if (method2pred.isEmpty()) {
                        method2pred.put(DefaultMethod.create(DefaultPackageAndClass.create(""), ""), new HashSet<Predicate>());
                    }

                    for (Method m : method2pred.keySet()) {
                        for (Predicate p : sctx.getPredicates()) {
                            if (method2pred.get(m).contains(p)) {
                                for (Predicate q : method2pred.get(m)) {
                                    if (p.equals(q)) {
                                        q.setScope(q.getScope().merge(p.getScope()));
                                        break;
                                    }
                                }
                            } else {
                                method2pred.get(m).add(p);
                            }
                        }
                    }
                }
            }
        }

        Method locationM = null;
        int locationPC = 0;

        for (Method m : method2pred.keySet()) {
            int min = -1;
            int max = -1;

            for (Predicate p : method2pred.get(m)) {
                BytecodeRange br = p.getScope();

                if (br instanceof BytecodeInterval) {
                    BytecodeInterval bi = (BytecodeInterval) br;

                    if (min > bi.getMin()) {
                        min = bi.getMin();
                    }

                    if (max < bi.getMax()) {
                        max = bi.getMax();
                    }
                }

                if (br instanceof BytecodeIntervals) {
                    BytecodeIntervals bis = (BytecodeIntervals) br;

                    if (min > bis.getMin()) {
                        min = bis.getMin();
                    }

                    if (max < bis.getMax()) {
                        max = bis.getMax();
                    }
                }
            }

            for (int i = min; i <= max; ++i) {
                int inScope = 0;

                for (Predicate p : method2pred.get(m)) {
                    if (p.isInScope(i)) {
                        ++inScope;
                    }
                }

                if (location < inScope) {
                    location = inScope;
                    locationM = m;
                    locationPC = i;
                }
            }
        }

        Method methodM = null;

        for (Method m : method2pred.keySet()) {
            if (method < method2pred.get(m).size()) {
                method = method2pred.get(m).size();
                methodM = m;
            }
        }

        for (PredicateContext ctx : preds.contexts) {
            if (!PredicateAbstractionFactory.systemPredicates.contexts.contains(ctx)) {
                for (Predicate p : ctx.getPredicates()) {
                    ++total;
                }
            }
        }

        out.println("predicates:         location=" + location + (locationM != null ? " (" + locationM + ":" + (locationPC >= 0 ? locationPC : "*") + ")" : "") + ",method=" + method + (methodM != null ? " (" + methodM + ")" : "") + ",total=" + total);
        out.println("smt:                sat=" + SMT.getIsSat() + ",itp=" + SMT.getItp() + ",elapsed=" + formatHMS(SMT.getElapsed()));

        if (PandaConfig.getInstance().enabledRefinement()) {
            out.println("refinements:        " + abs.getNumberOfRefinements());
        }
    }
}
