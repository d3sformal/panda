package gov.nasa.jpf.abstraction.common.access;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;

public class SpecialVariable extends DefaultRoot {
    private static Map<String, SpecialVariable> instances = new HashMap<String, SpecialVariable>();

    private SpecialVariable(String name) {
        super(name);
    }

    public static SpecialVariable create(String name) {
        if (!instances.containsKey(name)) {
            instances.put(name, new SpecialVariable(name));
        }

        return instances.get(name);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }
}
