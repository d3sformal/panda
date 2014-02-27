package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;

import java.util.Map;

public class UninterpretedShiftLeft extends Operation {
    protected UninterpretedShiftLeft(Expression a, Expression b) {
        super(a, b);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

	@Override
	public UninterpretedShiftLeft replace(Map<AccessExpression, Expression> replacements) {
		Expression newA = a.replace(replacements);
		Expression newB = b.replace(replacements);

		if (newA == a && newB == b) return this;
		else return new UninterpretedShiftLeft(newA, newB);
	}

	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
		Expression newA = a.update(expression, newExpression);
		Expression newB = b.update(expression, newExpression);

		if (newA == a && newB == b) return this;
		else return create(newA, newB);
	}

    public static Operation create(Expression a, Expression b) {
		if (!argumentsDefined(a, b)) return null;
		
		if (a instanceof Undefined) return UndefinedOperationResult.create();
		if (b instanceof Undefined) return UndefinedOperationResult.create();
		
		return new UninterpretedShiftLeft(a, b);
    }
}
