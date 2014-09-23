package gov.nasa.jpf.abstraction.common;

import java.util.Map;

import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.smt.SMT;

/**
 * Subtract represents an arithmetic difference of two variables (e.g. a - b)
 */
public class Subtract extends Operation {
    protected Subtract(Expression a, Expression b) {
        super(a, b);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Subtract replace(Map<AccessExpression, Expression> replacements) {
        Expression newA = a.replace(replacements);
        Expression newB = b.replace(replacements);

        if (newA == a && newB == b) return this;
        else return new Subtract(newA, newB);
    }

    /**
     * Checked creation of the symbolic expression.
     *
     * No matter the validity of the arguments (e.g. being null) this method is responsible for coping with it
     *
     * @param a left hand side operand
     * @param b right hand side operand
     * @return symbolic expression representing the difference / undefined expression / null
     */
    public static Expression create(Expression a, Expression b) {
        Expression min = createMinimised(a, b);

        if (VM.getVM().getJPF().getConfig().getBoolean("panda.language.minimise")) {
            Expression raw = new Subtract(a, b);

            if (!SMT.checkEquivalence(min, raw)) {
                throw new RuntimeException("Wrong convertion from: " + raw + " to: " + min);
            }
        }

        return min;
    }

    public static Expression createMinimised(Expression a, Expression b) {
        if (!argumentsDefined(a, b)) return null;

        if (a instanceof Undefined) return UndefinedOperationResult.create();
        if (b instanceof Undefined) return UndefinedOperationResult.create();

        if (a instanceof Constant && b instanceof Constant) {
            Constant c = (Constant) a;
            Constant d = (Constant) b;

            return new Constant(c.value.intValue() - d.value.intValue());
        }

        if (b instanceof Constant) {
            Constant d = (Constant) b;

            if (d.value.intValue() < 0) {
                return Add.create(a, Constant.create(-d.value.intValue()));
            }

            if (d.value.intValue() == 0) {
                return a;
            }
        }

        if (b instanceof Subtract) {
            Subtract s = (Subtract) b;

            if (s.a instanceof Constant) {
                Constant e = (Constant) s.a;

                if (e.value.intValue() == 0) {
                    return Add.create(a, s.b);
                }
            }
        }

        return new Subtract(a, b);
    }

    @Override
    public Expression update(AccessExpression expression, Expression newExpression) {
        Expression newA = a.update(expression, newExpression);
        Expression newB = b.update(expression, newExpression);

        if (newA == a && newB == b) return this;
        else return create(newA, newB);
    }
}
