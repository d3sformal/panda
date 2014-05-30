package gov.nasa.jpf.abstraction.common.access.meta.impl;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.meta.Field;

/**
 * An unmodified field
 */
public class DefaultField implements Field {
    private static Map<String, DefaultField> instances = new HashMap<String, DefaultField>();

    public String name;

    protected DefaultField(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public static DefaultField create(String name) {
        if (name == null) {
            return null;
        }

        if (!instances.containsKey(name)) {
            instances.put(name, new DefaultField(name));
        }

        return instances.get(name);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DefaultField) {
            DefaultField f = (DefaultField) o;

            return getName().equals(f.getName());
        }

        return false;
    }

    @Override
    public void addAccessSubExpressionsToSet(Set<AccessExpression> out) {
    }
}
