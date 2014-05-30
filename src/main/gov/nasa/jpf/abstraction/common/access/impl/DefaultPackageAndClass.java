package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;

/**
 * A grammar element used to specify a target of a [object ...] context
 *
 * @see gov.nasa.jpf.abstraction.common.ObjectContext
 */
public class DefaultPackageAndClass extends DefaultRoot implements PackageAndClass {
    private static Map<String, DefaultPackageAndClass> instances = new HashMap<String, DefaultPackageAndClass>();
    private Integer hashCodeValue;

    protected DefaultPackageAndClass(List<String> name) {
        this(createName(name));
    }

    protected DefaultPackageAndClass(String name) {
        super(name);
    }

    private static String createName(List<String> name) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < name.size() - 1; ++i) {
            builder.append(name.get(i));
            builder.append('.');
        }

        builder.append(name.get(name.size() - 1));

        return builder.toString();
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean isEqualToSlow(AccessExpression o) {
        if (o instanceof PackageAndClass) {
            PackageAndClass r = (PackageAndClass) o;

            return getName().equals(r.getName());
        }

        return false;
    }

    @Override
    public int hashCode() {
        if (hashCodeValue == null) {
            hashCodeValue = ("class_" + getName()).hashCode();
        }

        return hashCodeValue;
    }

    @Override
    public DefaultPackageAndClass createShallowCopy() {
        return this;
    }

    public static DefaultPackageAndClass create(List<String> name) {
        if (name == null || name.size() == 0) {
            return null;
        }

        String nameStr = createName(name);

        //return new DefaultPackageAndClass(name);
        if (!instances.containsKey(nameStr)) {
            instances.put(nameStr, new DefaultPackageAndClass(nameStr));
        }

        return instances.get(nameStr);
    }

    public static DefaultPackageAndClass create(String name) {
        List<String> items = new LinkedList<String>();

        for (String item : name.split("\\.")) {
            items.add(item);
        }

        return create(items);
    }
}
