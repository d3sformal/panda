package gov.nasa.jpf.abstraction.common;

import java.util.Map;

import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.PandaConfig;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.smt.SMT;

/**
 * Multiply represents symbolic expression for multiplication of two variables (e.g. a * b)
 */
public class Multiply extends Operation {
    protected Multiply(Expression a, Expression b) {
        super(a, b);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Multiply replace(Map<AccessExpression, Expression> replacements) {
        Expression newA = a.replace(replacements);
        Expression newB = b.replace(replacements);

        if (newA == a && newB == b) return this;
        else return new Multiply(newA, newB);
    }

    /**
     * Checked creation of the symbolic expression.
     *
     * No matter the validity of the arguments (e.g. being null) this method is responsible for coping with it
     *
     * @param a left hand side operand
     * @param b right hand side operand
     * @return symbolic expression representing the multiplication / undefined expression / null
     */
    public static Expression create(Expression a, Expression b) {
        Expression min = createMinimized(a, b);

        if (VM.getVM() != null && PandaConfig.getInstance().checkLanguageMinimization()) {
            Expression raw = new Multiply(a, b);

            if (!SMT.checkEquivalence(min, raw)) {
                throw new RuntimeException("Wrong convertion from: " + raw + " to: " + min);
            }
        }

        return min;
    }

    public static Expression createMinimized(Expression a, Expression b) {
        if (!argumentsDefined(a, b)) return null;

        if (a instanceof Undefined) return UndefinedOperationResult.create();
        if (b instanceof Undefined) return UndefinedOperationResult.create();

        if (a instanceof Constant && b instanceof Constant) {
            Constant c = (Constant) a;
            Constant d = (Constant) b;

            return new Constant(c.value.intValue() * d.value.intValue());
        }

        if (a instanceof Constant) {
            Constant c = (Constant) a;

            switch (c.value.intValue()) {
                case -1:
                    return Subtract.create(Constant.create(0), b);
                case 0:
                    return a;
                case 1:
                    return b;
            }
        }

        return new Multiply(a, b);
    }

    @Override
    public Expression update(AccessExpression expression, Expression newExpression) {
        Expression newA = a.update(expression, newExpression);
        Expression newB = b.update(expression, newExpression);

        if (newA == a && newB == b) return this;
        else return create(newA, newB);
    }
}
