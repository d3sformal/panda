package gov.nasa.jpf.abstraction;

import java.io.PrintWriter;

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

import gov.nasa.jpf.abstraction.state.MethodFramePredicateValuation;
import gov.nasa.jpf.abstraction.state.State;
import gov.nasa.jpf.abstraction.state.Trace;

public class PredicateConsolePublisher extends ConsolePublisher {

    // whether to skip static init instructions when printing code
    private boolean skip;

    public PredicateConsolePublisher(Config conf, Reporter reporter) {
        super(conf, reporter);
    }

    public void printTrace() {
        publishTrace();
    }

    @Override
    protected void publishTrace() {
        Path path = reporter.getPath();

        if (path.size() == 0) {
            return; // nothing to publish
        }

        skip = true;

        publishTopicStart("trace " + reporter.getCurrentErrorId());

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

                for (Step s : t) {
                    Instruction insn = s.getInstruction();

                    // start printing code in the main method
                    if (insn.getMethodInfo() == miMain) skip = false;

                    if (skip) continue;

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

            if (state != null) {
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

        if (PandaConfig.getInstance().enabledRefinement()) {
            out.println("refinements:        " + PredicateAbstraction.getInstance().getNumberOfRefinements());
        }
    }
}
