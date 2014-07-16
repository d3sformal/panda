package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.state.universe.Reference;

public class RUNSTART extends gov.nasa.jpf.jvm.bytecode.RUNSTART {
    @Override
    public Instruction execute(ThreadInfo ti) {
        Instruction ret = super.execute(ti);

        StackFrame sf = ti.getModifiableTopFrame();

        Root thisExpr = DefaultRoot.create("this");
        AccessExpression threadObjectExpr = AnonymousObject.create(new Reference(ti.getElementInfo(sf.peek())));

        // Do not update Predicate Valuation (that has been setup at .start()V)
        PredicateAbstraction.getInstance().getSymbolTable().get(0).addStructuredLocalVariable(thisExpr);
        PredicateAbstraction.getInstance().getSymbolTable().processObjectStore(threadObjectExpr, thisExpr);

        sf.setOperandAttr(thisExpr);

        return ret;
    }
}
