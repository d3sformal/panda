package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Tautology;

public class GOTO extends gov.nasa.jpf.jvm.bytecode.GOTO {
    public GOTO (int targetPosition){
        super(targetPosition);
    }

    public Instruction execute (ThreadInfo th) {
        Instruction ret = super.execute(th);

        PredicateAbstraction abs = PredicateAbstraction.getInstance();
        Predicate t = Tautology.create();

        abs.extendTraceFormulaWithConstraint(t, getMethodInfo(), getPosition(), true);
        abs.extendTraceFormulaWithConstraint(t, getMethodInfo(), targetPosition, true);

        return ret;
    }
}
