package gov.nasa.jpf.abstraction.predicate;

import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.Trace;
import gov.nasa.jpf.abstraction.predicate.state.State;
import gov.nasa.jpf.abstraction.predicate.state.PredicateValuation;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.report.Reporter;
import gov.nasa.jpf.report.ConsolePublisher;
import gov.nasa.jpf.vm.Transition;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.Path;
import gov.nasa.jpf.vm.Step;
import gov.nasa.jpf.util.Left;

import java.io.PrintWriter;

public class PredicateConsolePublisher extends ConsolePublisher {

    public PredicateConsolePublisher(Config conf, Reporter reporter) {
        super(conf, reporter);
    }

    @Override
    protected void publishTrace() {
        Path path = reporter.getPath();
        int i = 0;

        if (path.size() == 0) {
            return; // nothing to publish
        }

        publishTopicStart("trace " + reporter.getCurrentErrorId());

        Trace trace = ((PredicateAbstraction) GlobalAbstraction.getInstance().get()).getTrace();
        int progressAlongTrace = 1;

        for (Transition t : path) {
            out.print("------------------------------------------------------ ");
            out.println("transition #" + i++ + " thread: " + t.getThreadIndex());

            State state = trace.get(progressAlongTrace);

            out.println("predicates: ");

            PredicateValuation valuation = state.predicateValuationStacks.get(state.currentThread).top();

            out.println("\t" + valuation.toString().replaceAll("\n", "\n\t"));

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
                    if (showSource) {
                        String line = s.getLineString();

                        if (line != null) {
                            if (!line.equals(lastLine)) {
                                if (nNoSrc > 0) {
                                    out.println("      [" + nNoSrc + " insn w/o sources]");
                                }

                                out.print("  ");

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
                        Instruction insn = s.getInstruction();

                        if (showMethod) {
                            MethodInfo mi = insn.getMethodInfo();

                            if (mi != lastMi) {
                                ClassInfo mci = mi.getClassInfo();

                                out.print("    ");

                                if (mci != null) {
                                    out.print(mci.getName());
                                    out.print(".");
                                }

                                out.println(mi.getUniqueName());

                                lastMi = mi;
                            }
                        }

                        out.print("      ");
                        out.println(insn);
                    }
                }

                if (showSource && !showCode && (nNoSrc > 0)) {
                    out.println("      [" + nNoSrc + " insn w/o sources]");
                }
            }

            ++progressAlongTrace;
        }
    }
}
