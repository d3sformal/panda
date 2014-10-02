package gov.nasa.jpf.abstraction.statematch;

import gov.nasa.jpf.abstraction.Test;
import gov.nasa.jpf.abstraction.Debug;

public class UnknownTest extends StateMatchingTest {
    public UnknownTest() {
        config.add("+panda.interpolation=true");
        config.add("+panda.branch.prune_infeasible=true");
        config.add("+listener+=,gov.nasa.jpf.listener.ExecTracker");
        //config.add("+listener+=,gov.nasa.jpf.abstraction.util.InstructionTracker");
        //config.add("+listener+=,gov.nasa.jpf.abstraction.util.PredicateValuationMonitor");
        //config.add("+listener+=,gov.nasa.jpf.abstraction.util.CounterexampleListener");
        config.add("+vm.storage.class="); // Disable state matching
    }

    @Test
    public static void test1() {
        int i = Debug.unknownInt();

        if (i == 1) {
        }

        assertRevisitedAtLeast(1);
    }

    //@Test
    public static void test2() {
        int i = Debug.unknownInt(); // Should register choice (starting with for example 0) and mark the constant as NONDET

        int j = i + 2;

        if (i > 498) { // Inconsistent branching will take Trace Formula and derive such a value of NONDET different from previously derived values
            assert j > 500;
        }

        // There will be one concrete path avoiding the body of the IF
        //   lets say its for i: 0
        //
        // There will be exactly one concrete path hitting the body of the IF
        //   lets say its for i: 499

        assertRevisitedAtLeast(1);
        assertVisitedAtMost(2);
    }
};
