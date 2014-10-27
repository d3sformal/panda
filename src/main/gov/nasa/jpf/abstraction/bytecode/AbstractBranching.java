package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.state.TruthValue;

/**
 * AbstractBranching defines an interface for all IF instructions
 */
public interface AbstractBranching {

    public Instruction executeConcrete(ThreadInfo ti);

    public Instruction getSelf();

    public Predicate createPredicate(Expression expr1, Expression expr2);
    public boolean getConcreteBranchValue(int v1, int v2);

    public Instruction getDefaultTarget();
    public Instruction getNext(ThreadInfo ti);
    public Instruction getTarget(ThreadInfo ti, int num);

}
