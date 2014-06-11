package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.common.Add;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Increment local variable by constant
 * No change
 */
public class IINC extends gov.nasa.jpf.jvm.bytecode.IINC {

    public IINC(int localVarIndex, int increment) {
        super(localVarIndex, increment);
    }

    public Instruction execute(ThreadInfo ti) {
        SystemState ss = ti.getVM().getSystemState();
        StackFrame sf = ti.getModifiableTopFrame();

        AccessExpression path = DefaultRoot.create(sf.getLocalVarInfo(getIndex()) == null ? null : sf.getLocalVarInfo(getIndex()).getName(), getIndex());
        Expression expression = Add.create(path, Constant.create(increment));

        sf.setLocalAttr(index, expression);
        sf.setLocalVariable(index, sf.getLocalVariable(index) + increment, false);

        PredicateAbstraction.getInstance().processPrimitiveStore(expression, path);

        return getNext(ti);
    }

}
