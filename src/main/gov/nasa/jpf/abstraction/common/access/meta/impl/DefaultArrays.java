package gov.nasa.jpf.abstraction.common.access.meta.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;

/**
 * The unmodified symbol "arr"
 */
public class DefaultArrays implements Arrays {
    private static Map<String, DefaultArrays> instances = new HashMap<String, DefaultArrays>();

    private String name;

    protected DefaultArrays(String name) {
        this.name = name;
    }

    public static DefaultArrays create(String name) {
        if (!instances.containsKey(name)) {
            instances.put(name, new DefaultArrays(name));
        }

        return instances.get(name);
    }

    public static DefaultArrays create() {
        return create("arr");
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DefaultArrays) {
            return name.equals(((DefaultArrays) o).name);
        }

        return false;
    }

    @Override
    public void addAccessSubExpressionsToSet(Set<AccessExpression> out) {
    }
}
