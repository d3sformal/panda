package gov.nasa.jpf.abstraction.common;

import java.util.Map;

import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.smt.SMT;

/**
 * Add is a symbolic expression for arithmetic summation of two variable (e.g. a + b)
 */
public class Add extends Operation {
    protected Add(Expression a, Expression b) {
        super(a, b);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Add replace(Map<AccessExpression, Expression> replacements) {
        Expression newA = a.replace(replacements);
        Expression newB = b.replace(replacements);

        if (newA == a && newB == b) return this;
        else return new Add(newA, newB);
    }

    /**
     * Checked creation of the symbolic expression.
     *
     * No matter the validity of the arguments (e.g. being null) this method is responsible for coping with it
     *
     * @param a left hand side operand
     * @param b right hand side operand
     * @return symbolic expression representing the summation / undefined expression / null
     */
    public static Expression create(Expression a, Expression b) {
        Expression min = createMinimised(a, b);

        if (VM.getVM() != null && VM.getVM().getJPF().getConfig().getBoolean("panda.language.check_minimization")) {
            Expression raw = new Add(a, b);

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

            return new Constant(c.value.intValue() + d.value.intValue());
        } else if (a instanceof Constant) {
            return createMinimised(b, a);
        }

        if (b instanceof Constant) {
            Constant d = (Constant) b;

            if (d.value.intValue() < 0) {
                return Subtract.create(a, Constant.create(-d.value.intValue()));
            }

            if (d.value.intValue() == 0) {
                return a;
            }
        }

        if (a instanceof Subtract) {
            Subtract s = (Subtract) a;

            if (s.a instanceof Constant) {
                Constant e = (Constant) s.a;

                if (e.value.intValue() == 0) {
                    return Subtract.create(b, s.b);
                }
            }
        }

        if (b instanceof Subtract) {
            Subtract s = (Subtract) b;

            if (s.a instanceof Constant) {
                Constant e = (Constant) s.a;

                if (e.value.intValue() == 0) {
                    return Subtract.create(a, s.b);
                }
            }
        }

        return new Add(a, b);
    }

    @Override
    public Expression update(AccessExpression expression, Expression newExpression) {
        Expression newA = a.update(expression, newExpression);
        Expression newB = b.update(expression, newExpression);

        if (newA == a && newB == b) return this;
        else return create(newA, newB);
    }
}
