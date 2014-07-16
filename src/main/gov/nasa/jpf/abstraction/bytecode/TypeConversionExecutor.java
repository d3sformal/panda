package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.common.Expression;

public class TypeConversionExecutor {

    private DataWordManipulator source;
    private DataWordManipulator target;

    public TypeConversionExecutor(DataWordManipulator source, DataWordManipulator target) {
        this.source = source;
        this.target = target;
    }

    public Instruction execute(ThreadInfo ti, TypeConvertor ins) {
        StackFrame sf = ti.getModifiableTopFrame();
        Expression expr = source.getExpression(sf);
        Instruction ret;

        ret = ins.executeConcrete(ti);

        target.setExpression(sf, expr);

        return ret;
    }

}
