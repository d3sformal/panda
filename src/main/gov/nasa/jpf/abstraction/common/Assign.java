package gov.nasa.jpf.abstraction.common;

import java.util.Map;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;

public abstract class Assign extends Predicate {
    @Override
    public Predicate update(AccessExpression path, Expression expr) {
        throw new RuntimeException("SSA form");
    }

    @Override
    public Predicate replace(Map<AccessExpression, Expression> replacements) {
        throw new RuntimeException("SSA form");
    }
}
