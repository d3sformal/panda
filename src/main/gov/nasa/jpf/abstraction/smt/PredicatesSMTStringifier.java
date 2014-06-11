package gov.nasa.jpf.abstraction.smt;

import gov.nasa.jpf.abstraction.common.Add;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Disjunction;
import gov.nasa.jpf.abstraction.common.Divide;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Implication;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Modulo;
import gov.nasa.jpf.abstraction.common.Multiply;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicatesStringifier;
import gov.nasa.jpf.abstraction.common.Subtract;
import gov.nasa.jpf.abstraction.common.UninterpretedShiftLeft;
import gov.nasa.jpf.abstraction.common.UninterpretedShiftRight;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ArrayElementWrite;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthRead;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthWrite;
import gov.nasa.jpf.abstraction.common.access.Fresh;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrays;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultField;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;

/**
 * Transforms predicates into syntax that the SMT understands (SMTLIB)
 */
public class PredicatesSMTStringifier extends PredicatesStringifier {

    @Override
    public void visit(Negation predicate) {
        ret.append("(not ");

        predicate.predicate.accept(this);

        ret.append(")");
    }

    private void inlineConjunction(Predicate predicate) {
        if (predicate.getClass().equals(Conjunction.class)) {
            Conjunction c = (Conjunction) predicate;

            inlineConjunction(c.a);

            ret.append(" ");

            inlineConjunction(c.b);
        } else {
            predicate.accept(this);
        }
    }

    @Override
    public void visit(Conjunction predicate) {
        ret.append("(and ");

        inlineConjunction(predicate.a);

        ret.append(" ");

        inlineConjunction(predicate.b);

        ret.append(")");
    }

    private void inlineDisjunction(Predicate predicate) {
        if (predicate.getClass().equals(Disjunction.class)) {
            Disjunction d = (Disjunction) predicate;

            inlineDisjunction(d.a);

            ret.append(" ");

            inlineDisjunction(d.b);
        } else {
            predicate.accept(this);
        }
    }

    @Override
    public void visit(Disjunction predicate) {
        ret.append("(or ");

        inlineDisjunction(predicate.a);

        ret.append(" ");

        inlineDisjunction(predicate.b);

        ret.append(")");
    }

    @Override
    public void visit(Implication predicate) {
        ret.append("(=> ");

        predicate.a.accept(this);

        ret.append(" ");

        predicate.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(LessThan predicate) {
        ret.append("(< ");

        predicate.a.accept(this);

        ret.append(" ");

        predicate.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(Equals predicate) {
        ret.append("(= ");

        predicate.a.accept(this);

        ret.append(" ");

        predicate.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(Add expression) {
        ret.append("(+ ");

        expression.a.accept(this);

        ret.append(" ");

        expression.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(Subtract expression) {
        ret.append("(- ");

        expression.a.accept(this);

        ret.append(" ");

        expression.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(Multiply expression) {
        ret.append("(* ");

        expression.a.accept(this);

        ret.append(" ");

        expression.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(Divide expression) {
        ret.append("(/ ");

        expression.a.accept(this);

        ret.append(" ");

        expression.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(Modulo expression) {
        // see gov.nasa.jpf.abstraction.common.Modulo for its implementation
        visit((Subtract)expression);
    }

    @Override
    public void visit(UninterpretedShiftLeft expression) {
        ret.append("(shl ");

        expression.a.accept(this);

        ret.append(" ");

        expression.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(UninterpretedShiftRight expression) {
        ret.append("(shr ");

        expression.a.accept(this);

        ret.append(" ");

        expression.b.accept(this);

        ret.append(")");
    }

    @Override
    public void visit(Constant expression) {
        if (expression.value.doubleValue() < 0) {
            ret.append("(- " + (-expression.value.doubleValue()) + ")");
        } else {
            ret.append(expression);
        }
    }

    @Override
    public void visit(AnonymousArray expression) {
        ret.append("fresh");
    }

    @Override
    public void visit(AnonymousObject expression) {
        ret.append("fresh");
    }

    @Override
    public void visit(Root expression) {
        ret.append("var_" + expression.getName());
    }

    @Override
    public void visit(Fresh expression) {
        ret.append("fresh");
    }

    @Override
    public void visit(ObjectFieldRead expression) {
        ret.append("(select ");

        expression.getField().accept(this);

        ret.append(" ");

        expression.getObject().accept(this);

        ret.append(")");
    }

    @Override
    public void visit(ObjectFieldWrite expression) {
        ret.append("(store ");

        expression.getField().accept(this);

        ret.append(" ");

        expression.getObject().accept(this);

        ret.append(" ");

        expression.getNewValue().accept(this);

        ret.append(")");
    }

    @Override
    public void visit(ArrayElementRead expression) {
        ret.append("(select ");

        ret.append("(select ");

        expression.getArrays().accept(this);

        ret.append(" ");

        expression.getArray().accept(this);

        ret.append(")");

        ret.append(" ");

        expression.getIndex().accept(this);

        ret.append(")");
    }

    @Override
    public void visit(ArrayElementWrite expression) {
        ret.append("(store "); // Store the updated array into arrays

        expression.getArrays().accept(this);

        ret.append(" ");

        expression.getArray().accept(this);

        ret.append(" ");

        ret.append("(store "); // Update the array

        ret.append("(select "); // Select the concrete array

        expression.getArrays().accept(this);

        ret.append(" ");

        expression.getArray().accept(this);

        ret.append(")");

        ret.append(" ");

        expression.getIndex().accept(this);

        ret.append(" ");

        expression.getNewValue().accept(this);

        ret.append(")");

        ret.append(")");
    }

    @Override
    public void visit(ArrayLengthRead expression) {
        ret.append("(select ");

        expression.getArrayLengths().accept(this);

        ret.append(" ");

        expression.getArray().accept(this);

        ret.append(")");
    }

    @Override
    public void visit(ArrayLengthWrite expression) {
        ret.append("(store ");

        expression.getArrayLengths().accept(this);

        ret.append(" ");

        expression.getArray().accept(this);

        ret.append(" ");

        expression.getNewValue().accept(this);

        ret.append(")");
    }

    @Override
    public void visit(DefaultArrays meta) {
        ret.append("arr");
    }

    @Override
    public void visit(DefaultField meta) {
        ret.append("field_" + meta.getName());
    }

    @Override
    public void visit(PackageAndClass packageAndClass) {
        ret.append("class_" + packageAndClass.getName().replace("_", "__").replace('.', '_'));
    }

}
