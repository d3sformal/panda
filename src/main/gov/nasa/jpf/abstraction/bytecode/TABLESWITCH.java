package gov.nasa.jpf.abstraction.bytecode;

import java.util.ArrayList;

import gov.nasa.jpf.JPFException;
import gov.nasa.jpf.jvm.bytecode.InstructionVisitor;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Access jump table by index and jump
 * ..., index  => ...
 * WARNING: it actually duplicates LOOKUPSWITCH behavior
 */
public class TABLESWITCH extends SwitchInstruction implements gov.nasa.jpf.vm.TableSwitchInstruction {

    int min, max;

    public TABLESWITCH(int defaultTarget, int min, int max) {
        super(defaultTarget, (max - min + 1));
        this.min = min;
        this.max = max;
    }

    @Override
    public void setTarget(int value, int target) {
        int i = value - min;

        if (i >= 0 && i < targets.length) {
            targets[i] = target;
        } else {
            throw new JPFException("illegal tableswitch target: " + value);
        }
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        /*
         * TABLESWITCH is not implemented properly because it loses sense with
         * abstractions. Instead LOOKUPSWITCH behavior is used.
         */
        return super.execute(ti);
    }

    @Override
    public int getLength() {
        return 13 + 2 * (matches.length);
    }

    @Override
    public int getByteCode() {
        return 0xAA;
    }

    @Override
    public void accept(InstructionVisitor insVisitor) {
        insVisitor.visit(this);
    }

    @Override
    public int[] getMatches() {
        int[] matches = new int[max - min + 1];

        for (int i = 0; i < matches.length; ++i) {
            matches[i] = i;
        }

        return matches;
    }

}
