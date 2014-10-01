package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.vm.Verify;

public class UnknownTest extends BaseTest {
    public UnknownTest() {
        config.add("+panda.interpolation=true");
        config.add("+panda.interpolation=true");
        //config.add("+listener+=,gov.nasa.jpf.abstraction.util.InstructionTracker");
        //config.add("+listener+=,gov.nasa.jpf.abstraction.util.PredicateValuationMonitor");
        //config.add("+listener+=,gov.nasa.jpf.abstraction.util.CounterexampleListener");
        config.add("+vm.storage.class="); // Disable state matching
    }

    @Test
    public static void test() {
        int i = Verify.getInt(0, 500); // Should register choice (starting with for example 0) and mark the constant as NONDET

        int j = i + 2;

        if (i > 498) { // Inconsistent branching will take Trace Formula and derive such a value of NONDET different from previously derived values
            assert j > 500;
        }

        // There will be one concrete path avoiding the body of the IF
        //   lets say its for i: 0
        //
        // There will be exactly one concrete path hitting the body of the IF
        //   the only value of i possible: 499
    }
};
