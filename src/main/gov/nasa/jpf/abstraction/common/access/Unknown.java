package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.vm.Instruction;

import gov.nasa.jpf.abstraction.DynamicIntChoiceGenerator;

public class Unknown extends SpecialVariable {
    private DynamicIntChoiceGenerator cg;

    private Unknown(String name, DynamicIntChoiceGenerator cg) {
        super(name);

        this.cg = cg;
    }

    private static Unknown create(String name, DynamicIntChoiceGenerator cg) {
        return new Unknown(name, cg);
    }

    public static Unknown create(Instruction pc, DynamicIntChoiceGenerator cg) {
        return create("unknown_pc" + pc.getInstructionIndex(), cg);
    }

    public DynamicIntChoiceGenerator getChoiceGenerator() {
        return cg;
    }
}
