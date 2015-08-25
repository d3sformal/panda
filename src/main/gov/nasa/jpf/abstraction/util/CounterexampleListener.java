package gov.nasa.jpf.abstraction.util;

import java.util.List;
import java.util.Stack;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.report.Publisher;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.PandaConfig;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.PredicateConsolePublisher;
import gov.nasa.jpf.abstraction.Step;
import gov.nasa.jpf.abstraction.TraceFormula;
import gov.nasa.jpf.abstraction.common.AssumePredicateContext;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.MethodPredicateContext;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicateContext;
import gov.nasa.jpf.abstraction.common.Predicates;

public class CounterexampleListener extends ListenerAdapter {
    public enum Format {
        SEPARATED,
        INTERLEAVED
    }

    private Format format = PandaConfig.getInstance().getCounterexamplePrintFormat();

    public CounterexampleListener() {
        PredicateAbstraction.registerCounterexampleListener(this);
    }

    private static int log(int n) {
        int i = 0;

        while (n > 0) {
            n /= 10;

            ++i;
        }

        return i;
    }

    public static void printErrorConjuncts(TraceFormula traceFormula) {
        int traceStep = 0;
        int traceStepCount = traceFormula.size();
        int maxLen = 0;

        for (Step s : traceFormula) {
            int pcLen = ("[" + s.getMethod().getName() + ":" + s.getPC() + "]").length();

            if (maxLen < pcLen) {
                maxLen = pcLen;
            }
        }

        for (Step s : traceFormula) {
            System.out.print("\t");

            ++traceStep;

            int pc = s.getPC();
            String pcStr = "[" + s.getMethod().getName() + ":" + s.getPC() + "]";
            int pcLen = pcStr.length();

            for (int i = 0; i < log(traceStepCount) - log(traceStep); ++i) {
                System.out.print(" ");
            }

            System.out.print(traceStep + ": " + pcStr + ": ");

            for (int i = 0; i < maxLen - pcLen; ++i) {
                System.out.print(" ");
            }

            System.out.println(s.getPredicate().toString(Notation.FUNCTION_NOTATION));
        }
    }

    private void printInterpolants(TraceFormula traceFormula, Predicate[] interpolants) {
        if (interpolants != null) {
            int maxLen = 0;

            for (int i = 0; i < interpolants.length; ++i) {
                int pcLen = ("[" + traceFormula.get(i).getMethod().getName() + ":" + traceFormula.get(i).getPC() + "]").length();

                if (maxLen < pcLen) {
                    maxLen = pcLen;
                }
            }

            for (int i = 0; i < interpolants.length; ++i) {
                Predicate interpolant = interpolants[i];
                int pc = traceFormula.get(i).getPC();
                String pcStr = "[" + traceFormula.get(i).getMethod().getName() + ":" + traceFormula.get(i).getPC() + "]";
                int pcLen = pcStr.length();

                System.out.print("\t" + pcStr + ": ");
                for (int j = 0; j < maxLen - pcLen; ++j) {
                    System.out.print(" ");
                }
                System.out.println(interpolant);
            }
        }
    }

    private void printInterpolatedCounterexample(TraceFormula traceFormula, Predicate[] interpolants) {
        int traceStep = 0;
        int traceStepCount = traceFormula.size();
        int maxLen = 0;
        int maxPLen = 0;

        for (Step s : traceFormula) {
            int pcLen = ("[" + s.getMethod().getName() + ":" + s.getPC() + "]").length();
            int pLen = s.getPredicate().toString(Notation.FUNCTION_NOTATION).length();

            if (maxLen < pcLen) {
                maxLen = pcLen;
            }

            if (maxPLen < pLen) {
                maxPLen = pLen;
            }
        }

        int interpolant = 0;

        for (Step s : traceFormula) {
            System.out.print("\t");

            ++traceStep;

            int pc = s.getPC();
            String pcStr = "[" + s.getMethod().getName() + ":" + s.getPC() + "]";
            int pcLen = pcStr.length();
            int pLen = s.getPredicate().toString().length();

            for (int i = 0; i < log(traceStepCount) - log(traceStep); ++i) {
                System.out.print(" ");
            }

            System.out.print(traceStep + ": " + pcStr + ": ");

            for (int i = 0; i < maxLen - pcLen; ++i) {
                System.out.print(" ");
            }

            System.out.print(s.getPredicate().toString(Notation.FUNCTION_NOTATION));

            if (interpolants != null && interpolant < interpolants.length) {
                for (int i = 0; i < maxPLen - pLen; ++i) {
                    System.out.print(" ");
                }

                System.out.print("\tinterpolant: " + interpolants[interpolant++]);
            }

            System.out.println();
        }
    }

    public void counterexample(TraceFormula traceFormula, Predicate[] interpolants) {
        PandaConfig config = PandaConfig.getInstance();

        switch (format) {
            default:
            case SEPARATED:
                System.out.println();
                System.out.println();
                System.out.println();

                System.out.println("Counterexample Concrete Trace:");

                if (config.printConcreteCounterexample()) {
                    for (Publisher p : VM.getVM().getJPF().getReporter().getPublishers()) {;
                        ((PredicateConsolePublisher) p).printTrace();
                    }
                    System.out.println();
                }

                System.out.println("Counterexample Trace Formula:");

                printErrorConjuncts(traceFormula);

                System.out.println();
                System.out.println("Feasible: " + (interpolants == null));

                printInterpolants(traceFormula, interpolants);

                System.out.println();
                System.out.println();
                break;
            case INTERLEAVED:
                System.out.println();
                System.out.println();
                System.out.println();

                System.out.println("Counterexample Concrete Trace:");

                if (config.printConcreteCounterexample()) {
                    for (Publisher p : VM.getVM().getJPF().getReporter().getPublishers()) {;
                        ((PredicateConsolePublisher) p).printTrace();
                    }
                    System.out.println();
                }

                System.out.println("Counterexample:");

                printInterpolatedCounterexample(traceFormula, interpolants);

                System.out.println();
                System.out.println();
                break;
        }
    }

    public void backtrackLevel(int lvl) {
        System.out.println("Backtrack to level " + lvl + " after refinement");
        System.out.println();
    }

    public void currentPredicateSet(Predicates predicateSet, List<MethodInfo> refinedMethods) {
        if (PandaConfig.getInstance().printRefinedPredicateContexts()) {
            System.out.println();
            System.out.println();
            System.out.println("Current Predicate Set for the Refined Methods:");
            System.out.println();
            for (PredicateContext context : predicateSet.contexts) {
                if (context instanceof AssumePredicateContext) continue;

                if (context instanceof MethodPredicateContext) {
                    MethodPredicateContext methodPredicateContext = (MethodPredicateContext) context;

                    boolean isRefined = false;

                    for (MethodInfo m : refinedMethods) {
                        isRefined |= methodPredicateContext.getMethod().toString().equals(m.getBaseName());
                    }

                    if (isRefined) {
                        System.out.println(Notation.convertToString(context));
                    }
                }
            }
            System.out.println();
            System.out.println();
        }
    }
}
