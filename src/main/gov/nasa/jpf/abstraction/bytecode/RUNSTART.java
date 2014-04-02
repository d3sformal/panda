package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.StackFrame;

import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.impl.NonEmptyAttribute;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.predicate.state.universe.Reference;

public class RUNSTART extends gov.nasa.jpf.jvm.bytecode.RUNSTART {
    @Override
    public Instruction execute(ThreadInfo ti) {
        Instruction ret = super.execute(ti);

        StackFrame sf = ti.getModifiableTopFrame();

        AccessExpression thisExpr = DefaultRoot.create("this");
        AccessExpression threadObjectExpr = AnonymousObject.create(new Reference(ti.getElementInfo(sf.peek())));

        // Do not update Predicate Valuation (that has been setup at .start()V)
        ((PredicateAbstraction) GlobalAbstraction.getInstance().get()).getSymbolTable().processObjectStore(threadObjectExpr, thisExpr);

        sf.setOperandAttr(new NonEmptyAttribute(null, thisExpr));

        return ret;
    }
}
