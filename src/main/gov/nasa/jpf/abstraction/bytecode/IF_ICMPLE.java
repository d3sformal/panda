package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.state.TruthValue;

/**
 * Branch if int comparison succeeds
 * ..., value1, value2 => ...
 */
public class IF_ICMPLE extends gov.nasa.jpf.jvm.bytecode.IF_ICMPLE implements BinaryAbstractBranching {

    BinaryIfInstructionExecutor executor = new BinaryIfInstructionExecutor();
    Predicate last;

    public IF_ICMPLE(int targetPc) {
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
    public boolean getConcreteBranchValue(int v1, int v2) {
        return v1 <= v2;
    }

    @Override
    public Predicate createPredicate(Expression expr1, Expression expr2) {
        last = Negation.create(LessThan.create(expr2, expr1));
        return last;
    }

    @Override
    public Predicate getLastPredicate() {
        return last;
    }

    @Override
    public Instruction getDefaultTarget() {
        return getTarget();
    }

    @Override
    public Instruction getTarget(ThreadInfo ti, int num) {
        if (num == 0) {
            return getNext(ti);
        }

        return getTarget();
    }

}
