package gov.nasa.jpf.abstraction.common.impl;

import java.util.Set;

import gov.nasa.jpf.abstraction.common.Assign;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.meta.Field;

public class FieldAssign extends Assign {

    public Field field;
    public Field newField;

    private FieldAssign(Field field, Field newField) {
        this.field = field;
        this.newField = newField;
    }

    @Override
    public void addAccessExpressionsToSet(Set<AccessExpression> out) {
        if (newField instanceof ObjectFieldWrite) {
            ((ObjectFieldWrite) newField).addAccessExpressionsToSet(out);
        }
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    public static Predicate create(Field field, Field newField) {
        return new FieldAssign(field, newField);
    }
}
