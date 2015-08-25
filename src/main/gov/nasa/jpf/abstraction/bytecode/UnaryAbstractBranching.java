package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.Expression;

public interface UnaryAbstractBranching extends AbstractBranching {
    public Expression getSecondOperand();
}
