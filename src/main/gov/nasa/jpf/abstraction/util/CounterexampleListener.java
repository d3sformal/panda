package gov.nasa.jpf.abstraction.util;

import java.util.Stack;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.MethodInfo;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.Step;
import gov.nasa.jpf.abstraction.TraceFormula;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;

public class CounterexampleListener extends ListenerAdapter {
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

    private void printErrorConjuncts(TraceFormula traceFormula) {
        int traceStep = 0;
        int traceStepCount = traceFormula.size();

        for (Step s : traceFormula) {
            System.out.print("\t");

            ++traceStep;

            for (int i = 0; i < log(traceStepCount) - log(traceStep); ++i) {
                System.out.print(" ");
            }

            System.out.println(traceStep + ": " + s.getPredicate().toString(Notation.FUNCTION_NOTATION));
        }
    }

    public void counterexample(TraceFormula traceFormula, Predicate[] interpolants) {
        System.out.println();
        System.out.println();
        System.out.println("Counterexample Trace Formula:");
        //System.out.println(traceFormula.toString(Notation.SMT_NOTATION));
        //System.out.println(traceFormula.toString(Notation.FUNCTION_NOTATION));

        printErrorConjuncts(traceFormula);

        System.out.println();
        System.out.println("Feasible: " + (interpolants == null));

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

        System.out.println();
        System.out.println();
    }
}
