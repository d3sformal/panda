package gov.nasa.jpf.abstraction.util;

import java.util.Stack;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.MethodInfo;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;

public class CounterexampleListener extends ListenerAdapter {
    public CounterexampleListener() {
        PredicateAbstraction.registerCounterexampleListener(this);
    }

    private int traceStep;
    private int traceStepCount;

    private static int countErrorConjuncts(Predicate formula) {
        if (formula instanceof Conjunction) {
            Conjunction c = (Conjunction) formula;

            return countErrorConjuncts(c.a) + countErrorConjuncts(c.b);
        } else {
            return 1;
        }
    }

    private static int log(int n) {
        int i = 0;

        while (n > 0) {
            n /= 10;

            ++i;
        }

        return i;
    }

    private void printErrorConjuncts(Predicate formula) {
        if (formula instanceof Conjunction) {
            Conjunction c = (Conjunction) formula;

            printErrorConjuncts(c.a);
            printErrorConjuncts(c.b);
        } else {
            System.out.print("\t");

            ++traceStep;

            for (int i = 0; i < log(traceStepCount) - log(traceStep); ++i) {
                System.out.print(" ");
            }

            System.out.println(traceStep + ": " + formula.toString(Notation.FUNCTION_NOTATION));
        }
    }

    public void counterexample(Predicate traceFormula, Stack<Pair<MethodInfo, Integer>> traceProgramLocations, Predicate[] interpolants) {
        System.out.println();
        System.out.println();
        System.out.println("Counterexample Trace Formula:");
        //System.out.println(traceFormula.toString(Notation.SMT_NOTATION));
        //System.out.println(traceFormula.toString(Notation.FUNCTION_NOTATION));

        traceStep = 0;
        traceStepCount = countErrorConjuncts(traceFormula);
        printErrorConjuncts(traceFormula);

        System.out.println();
        System.out.println("Feasible: " + (interpolants == null));

        if (interpolants != null) {
            int maxLen = 0;

            for (int i = 0; i < interpolants.length; ++i) {
                int pcLen = ("[" + traceProgramLocations.get(i).getFirst().getName() + ":" + traceProgramLocations.get(i).getSecond() + "]").length();

                if (maxLen < pcLen) {
                    maxLen = pcLen;
                }
            }

            for (int i = 0; i < interpolants.length; ++i) {
                Predicate interpolant = interpolants[i];
                int pc = traceProgramLocations.get(i).getSecond();
                String pcStr = "[" + traceProgramLocations.get(i).getFirst().getName() + ":" + traceProgramLocations.get(i).getSecond() + "]";
                int pcLen = pcStr.length();

                System.out.print("\t" + pcStr + ": ");
                for (int j = 0; j < maxLen - pcLen; ++j) {
                    System.out.print(" ");
                }
                System.out.println(interpolant);
            }
        }

        System.out.println();
        System.out.println();
    }
}
