package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.jvm.bytecode.InstructionVisitor;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Access jump table by key match and jump
 * ..., key => ...
 */
public class LOOKUPSWITCH extends SwitchInstruction implements
        gov.nasa.jpf.vm.LookupSwitchInstruction {

    public LOOKUPSWITCH(int defaultTarget, int numberOfTargets) {
        super(defaultTarget, numberOfTargets);
    }

    @Override
    public void setTarget(int index, int match, int target) {
        targets[index] = target;
        matches[index] = match;
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        /**
         * Implementation is in SwitchInstruction, because now LOOKUPSWITCH's
         * implementation is used for TABLESWITCH as well
         */
        return super.execute(ti);
    }

    @Override
    public int getLength() {
        return 10 + 2 * (matches.length);
    }

    @Override
    public int getByteCode() {
        return 0xAB;
    }

    @Override
    public void accept(InstructionVisitor insVisitor) {
        insVisitor.visit(this);
    }

}
