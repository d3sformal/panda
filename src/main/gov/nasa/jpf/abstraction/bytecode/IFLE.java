package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Branch if int comparison with zero succeeds
 * ..., value => ...
 */
public class IFLE extends gov.nasa.jpf.jvm.bytecode.IFLE implements AbstractBranching {

    UnaryIfInstructionExecutor executor = new UnaryIfInstructionExecutor(Constant.create(0));

    public IFLE(int targetPc) {
        super(targetPc);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        return executor.execute(this, ti);
    }

    @Override
    public Instruction executeConcrete(ThreadInfo ti) {
        return super.execute(ti);
    }

    @Override
    public Instruction getSelf() {
        return this;
    }

    @Override
    public TruthValue getConcreteBranchValue(int v1, int v2) {
        return TruthValue.create(v1 <= v2);
    }

    @Override
    public Predicate createPredicate(Expression expr1, Expression expr2) {
        return Negation.create(LessThan.create(expr2, expr1));
    }

}
