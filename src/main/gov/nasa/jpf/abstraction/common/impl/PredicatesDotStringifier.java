package gov.nasa.jpf.abstraction.common.impl;

import gov.nasa.jpf.abstraction.common.PredicatesStringifier;
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
 * Used to transform predicates into string representation in Java-like notation (dot-notation)
 *
 * @see gov.nasa.jpf.abstraction.common.Notation
 */
public class PredicatesDotStringifier extends PredicatesStringifier {

    @Override
    public void visit(Root expression) {
        ret.append(expression.getName());
    }

    @Override
    public void visit(Fresh expression) {
        ret.append(expression.getName());
    }

    @Override
    public void visit(ObjectFieldRead expression) {
        boolean isStatic = expression.getObject() instanceof PackageAndClass;

        if (isStatic) ret.append("class(");

        expression.getObject().accept(this);

        if (isStatic) ret.append(")");

        ret.append(".");

        expression.getField().accept(this);
    }

    @Override
    public void visit(ObjectFieldWrite expression) {
        ret.append(expression.getName());

        ret.append("{");

        boolean isStatic = expression.getObject() instanceof PackageAndClass;

        if (isStatic) ret.append("class(");

        expression.getObject().accept(this);

        if (isStatic) ret.append(")");

        ret.append(".");

        ret.append(expression.getName());

        ret.append(" := ");

        expression.getNewValue().accept(this);

        ret.append("}");
    }

    @Override
    public void visit(ArrayElementRead expression) {
        expression.getArray().accept(this);

        ret.append("[");

        expression.getIndex().accept(this);

        ret.append("]");

        expression.getArrays().accept(this);
    }

    @Override
    public void visit(ArrayElementWrite expression) {
        ret.append("{");

        expression.getArray().accept(this);

        ret.append("[");

        expression.getIndex().accept(this);

        ret.append("]");

        ret.append(" := ");

        expression.getNewValue().accept(this);

        ret.append("}");
    }

    @Override
    public void visit(ArrayLengthRead expression) {
        ret.append("alength(");

        expression.getArrayLengths().accept(this);

        ret.append(", ");

        expression.getArray().accept(this);

        ret.append(")");
    }

    @Override
    public void visit(ArrayLengthWrite expression) {
        ret.append("alengthupdate(");

        expression.getArrayLengths().accept(this);

        ret.append(", ");

        expression.getArray().accept(this);

        ret.append(", ");

        expression.getNewValue().accept(this);

        ret.append(")");
    }

    @Override
    public void visit(AnonymousObject expression) {
        ret.append("object(" + expression.getReference() + ")");
    }

    @Override
    public void visit(AnonymousArray expression) {
        ret.append("array(" + expression.getReference() + ")");
    }

    @Override
    public void visit(DefaultField meta) {
        ret.append(meta.getName());
    }

    @Override
    public void visit(DefaultArrays meta) {
    }

}
