package gov.nasa.jpf.abstraction.common;

import java.util.SortedSet;

import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Disjunction;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Implication;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.MethodPredicateContext;
import gov.nasa.jpf.abstraction.common.ObjectPredicateContext;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicateContext;
import gov.nasa.jpf.abstraction.common.Predicates;
import gov.nasa.jpf.abstraction.common.StaticPredicateContext;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.UpdatedPredicate;
import gov.nasa.jpf.abstraction.common.access.Method;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;
import gov.nasa.jpf.abstraction.common.access.SpecialVariable;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrayLengths;
import gov.nasa.jpf.abstraction.common.impl.ArraysAssign;
import gov.nasa.jpf.abstraction.common.impl.FieldAssign;
import gov.nasa.jpf.abstraction.common.impl.NullExpression;
import gov.nasa.jpf.abstraction.common.impl.VariableAssign;
import gov.nasa.jpf.abstraction.concrete.EmptyExpression;

/**
 * A special visitor of the hierarchy:
 *
 * predicates
 *   -> context
 *     -> predicate
 *       -> expression
 *
 * to be used to produce a string representation of the captured hierarchy.
 */
public abstract class PredicatesStringifier implements PredicatesComponentVisitor {

    protected StringBuilder ret = new StringBuilder();
    protected boolean topmost = true;

    public String getString() {
        return ret.toString();
    }

    @Override
    public void visit(Predicates predicates) {
        for (PredicateContext c : predicates.contexts) {
            c.accept(this);
            ret.append("\n");
        }
    }

    @Override
    public void visit(Expressions expressions) {
        for (ExpressionContext c : expressions.contexts) {
            c.accept(this);
            ret.append("\n");
        }
    }

    private void visitPredicate(Predicate p) {
        BytecodeRange scope = p.getScope();

        if (!(scope instanceof BytecodeUnlimitedRange)) {
            ret.append(scope.toString());
            ret.append(": ");
        }

        p.accept(this);
        ret.append("\n");
    }

    @Override
    public void visit(ObjectPredicateContext context) {
        ret.append("[object ");

        context.getPackageAndClass().accept(this);

        ret.append("]\n");

        for (Predicate p : context.predicates) {
            visitPredicate(p);
        }
    }

    @Override
    public void visit(MethodPredicateContext context) {
        ret.append("[method ");

        context.getMethod().accept(this);

        ret.append("]\n");

        for (Predicate p : context.predicates) {
            visitPredicate(p);
        }
    }

    @Override
    public void visit(MethodAssumePrePredicateContext context) {
        ret.append("[method assume pre ");

        context.getMethod().accept(this);

        ret.append("]\n");

        for (Predicate p : context.predicates) {
            visitPredicate(p);
        }
    }

    @Override
    public void visit(MethodAssumePostPredicateContext context) {
        ret.append("[method assume post ");

        context.getMethod().accept(this);

        ret.append("]\n");

        for (Predicate p : context.predicates) {
            visitPredicate(p);
        }
    }

    @Override
    public void visit(StaticPredicateContext context) {
        ret.append("[static]\n");

        for (Predicate p : context.predicates) {
            visitPredicate(p);
        }
    }

    @Override
    public void visit(ObjectExpressionContext context) {
        ret.append("[object ");

        context.getPackageAndClass().accept(this);

        ret.append("]\n");

        for (Expression e : context.expressions) {
            e.accept(this);
            ret.append("\n");
        }
    }

    @Override
    public void visit(MethodExpressionContext context) {
        ret.append("[method ");

        context.getMethod().accept(this);

        ret.append("]\n");

        for (Expression e : context.expressions) {
            e.accept(this);
            ret.append("\n");
        }
    }

    @Override
    public void visit(StaticExpressionContext context) {
        ret.append("[static]\n");

        for (Expression e : context.expressions) {
            e.accept(this);
            ret.append("\n");
        }
    }

    @Override
    public void visit(Negation predicate) {
        ret.append("not(");

        predicate.predicate.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(LessThan predicate) {
        predicate.a.accept(this);

        ret.append(" < ");

        predicate.b.accept(this);
    }

    @Override
    public void visit(Equals predicate) {
        predicate.a.accept(this);

        ret.append(" = ");

        predicate.b.accept(this);
    }

    @Override
    public void visit(Tautology predicate) {
        ret.append("true");
    }

    @Override
    public void visit(Contradiction predicate) {
        ret.append("false");
    }

    private void inlineConjunction(Predicate predicate) {
        if (predicate.getClass().equals(Conjunction.class)) {
            Conjunction c = (Conjunction) predicate;

            inlineConjunction(c.a);

            ret.append(" and ");

            inlineConjunction(c.b);
        } else {
            predicate.accept(this);
        }
    }

    @Override
    public void visit(Conjunction predicate) {
        boolean topmost = this.topmost;
        this.topmost = false;

        if (!topmost) ret.append("(");

        inlineConjunction(predicate.a);

        ret.append(" and ");

        inlineConjunction(predicate.b);

        if (!topmost) ret.append(")");
    }

    private void inlineDisjunction(Predicate predicate) {
        if (predicate.getClass().equals(Disjunction.class)) {
            Disjunction d = (Disjunction) predicate;

            inlineDisjunction(d.a);

            ret.append(" or ");

            inlineDisjunction(d.b);
        } else {
            predicate.accept(this);
        }
    }

    @Override
    public void visit(Disjunction predicate) {
        boolean topmost = this.topmost;
        this.topmost = false;

        if (!topmost) ret.append("(");

        inlineDisjunction(predicate.a);

        ret.append(" or ");

        inlineDisjunction(predicate.b);

        if (!topmost) ret.append(")");
    }

    @Override
    public void visit(Implication predicate) {
        boolean topmost = this.topmost;
        this.topmost = false;

        if (!topmost) ret.append("(");

        predicate.a.accept(this);

        ret.append(" => ");

        predicate.b.accept(this);

        if (!topmost) ret.append(")");
    }

    @Override
    public void visit(VariableAssign predicate) {
        predicate.variable.accept(this);

        ret.append(" = ");

        predicate.expression.accept(this);
    }

    @Override
    public void visit(FieldAssign predicate) {
        predicate.field.accept(this);

        ret.append(" = ");

        predicate.newField.accept(this);
    }

    @Override
    public void visit(ArraysAssign predicate) {
        predicate.arrays.accept(this);

        ret.append(" = ");

        predicate.newArrays.accept(this);
    }

    @Override
    public void visit(EmptyExpression expression) {
        ret.append(" ? ");
    }

    @Override
    public void visit(NullExpression expression) {
        ret.append("null");
    }

    @Override
    public void visit(Add expression) {
        ret.append("(");

        expression.a.accept(this);

        ret.append(" + ");

        expression.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(Subtract expression) {
        ret.append("(");

        expression.a.accept(this);

        ret.append(" - ");

        expression.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(Multiply expression) {
        ret.append("(");

        expression.a.accept(this);

        ret.append(" * ");

        expression.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(Divide expression) {
        ret.append("(");

        expression.a.accept(this);

        ret.append(" / ");

        expression.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(Modulo expression) {
        ret.append("(");

        expression.a.accept(this);

        ret.append(" % ");

        expression.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(UninterpretedShiftLeft expression) {
        ret.append("SHL(");

        expression.a.accept(this);

        ret.append(", ");

        expression.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(UninterpretedShiftRight expression) {
        ret.append("SHR(");

        expression.a.accept(this);

        ret.append(", ");

        expression.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(Constant expression) {
        ret.append(expression.value);
    }

    @Override
    public void visit(DefaultArrayLengths meta) {
        ret.append("arrlen");
    }

    @Override
    public void visit(Undefined expression) {
        ret.append("<<UNDEFINED>>");
    }

    @Override
    public void visit(UpdatedPredicate predicate) {
        predicate.apply().accept(this);
    }

    @Override
    public void visit(PackageAndClass packageAndClass) {
        ret.append(packageAndClass.getName());
    }

    @Override
    public void visit(Method method) {
        method.getPackageAndClass().accept(this);

        ret.append(".");

        ret.append(method.getName());
    }

    @Override
    public void visit(SpecialVariable expression) {
        ret.append(expression.getName());
    }

}
