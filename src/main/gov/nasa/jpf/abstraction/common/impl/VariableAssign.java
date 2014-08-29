package gov.nasa.jpf.abstraction.common.impl;

import java.util.Set;

import gov.nasa.jpf.abstraction.common.Assign;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicateNotCloneableException;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;

public class VariableAssign extends Assign {

    public Root variable;
    public Expression expression;

    private VariableAssign(Root variable, Expression expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public void addAccessExpressionsToSet(Set<AccessExpression> out) {
        variable.addAccessExpressionsToSet(out);
        expression.addAccessExpressionsToSet(out);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    public static Predicate create(Root variable, Expression expression) {
        return new VariableAssign(variable, expression);
    }

    @Override
    public VariableAssign clone() {
        throw new PredicateNotCloneableException("Should not be copying this");
    }
}
