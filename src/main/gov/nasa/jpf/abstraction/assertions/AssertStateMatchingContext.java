package gov.nasa.jpf.abstraction.assertions;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import gov.nasa.jpf.vm.Instruction;

public class AssertStateMatchingContext {
    public static Map<Instruction, LocationAssertion> assertions = new HashMap<Instruction, LocationAssertion>();

    public static boolean update(Instruction pc, Class<? extends LocationAssertion> assertionClass, Object... o) {
        if (!assertions.containsKey(pc)) {
            try {
                assertions.put(pc, assertionClass.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        LocationAssertion locationAssertion = assertions.get(pc);

        locationAssertion.update(o);

        return !locationAssertion.isViolated();
    }

    public static Set<Instruction> getLocations() {
        return assertions.keySet();
    }

    public static LocationAssertion get(Instruction insn) {
        return assertions.get(insn);
    }
}
