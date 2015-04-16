package gov.nasa.jpf.abstraction.common;

import java.util.Map;

import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.PandaConfig;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.Undefined;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.smt.SMT;

/**
 * Predicate on inequality of two symbolic expressions. (e.g. x < y)
 */
public class LessThan extends Comparison {
    protected LessThan(Expression a, Expression b) {
        super(a, b);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Predicate replace(Map<AccessExpression, Expression> replacements) {
        Expression newA = a.replace(replacements);
        Expression newB = b.replace(replacements);

        if (newA == a && newB == b) return this;
        else return create(newA, newB);
    }

    public static Predicate createUnminimized(Expression a, Expression b) {
        if (!argumentsDefined(a, b)) return null;

        if (a instanceof Undefined) return Contradiction.create();
        if (b instanceof Undefined) return Contradiction.create();

        return new LessThan(a, b);
    }

    public static Predicate create(Expression a, Expression b) {
        Predicate min = createMinimized(a, b);

        if (VM.getVM() != null && PandaConfig.getInstance().checkLanguageMinimization()) {
            Predicate raw = createUnminimized(a, b);

            if (!SMT.checkEquivalence(min, raw)) {
                throw new RuntimeException("Wrong convertion from: " + raw + " to: " + min);
            }
        }

        return min;
    }

    public static Predicate createMinimized(Expression a, Expression b) {
        if (!argumentsDefined(a, b)) return null;

        if (a instanceof Undefined) return Contradiction.create();
        if (b instanceof Undefined) return Contradiction.create();

        if (a instanceof Constant && b instanceof Subtract) {
            Constant c = (Constant) a;
            Subtract s = (Subtract) b;

            if (c.value.intValue() == 0) {
                return new LessThan(s.b, s.a);
            }

            if (s.a instanceof Constant) {
                Constant e = (Constant) s.a;

                if (e.value.intValue() == 0) {
                    return new LessThan(s.b, Constant.create(-c.value.intValue()));
                }
            }
        }

        if (a instanceof Subtract && b instanceof Constant) {
            Subtract s = (Subtract) a;
            Constant d = (Constant) b;

            if (d.value.intValue() == 0) {
                return new LessThan(s.a, s.b);
            }

            if (s.a instanceof Constant) {
                Constant e = (Constant) s.a;

                if (e.value.intValue() == 0) {
                    return new LessThan(Constant.create(-d.value.intValue()), s.b);
                }
            }
        }

        return new LessThan(a, b);
    }

    @Override
    public Predicate update(AccessExpression expression, Expression newExpression) {
        Expression newA = a.update(expression, newExpression);
        Expression newB = b.update(expression, newExpression);

        if (newA == a && newB == b) return this;
        else return create(newA, newB);
    }

    @Override
    public LessThan clone() {
        return (LessThan)create(a, b);
    }
}
