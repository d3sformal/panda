package gov.nasa.jpf.abstraction.assertions;

import java.util.Set;
import java.util.TreeMap;
import java.util.Comparator;
import java.util.Iterator;

import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseIdentifier;

public class AliasingMap extends TreeMap<AccessExpression, Set<UniverseIdentifier>> {
    public static final long serialVersionUID = 1L;

    private int hashCode = 1;

    public AliasingMap() {
        super(new Comparator<AccessExpression>() {
            @Override
            public int compare(AccessExpression ae1, AccessExpression ae2) {
                return ae1.toString(Notation.DOT_NOTATION).compareTo(ae2.toString(Notation.DOT_NOTATION));
            }
        });
    }

    @Override
    public Set<UniverseIdentifier> put(AccessExpression ae, Set<UniverseIdentifier> values) {
        hashCode += ae.hashCode();

        for (UniverseIdentifier id : values) {
            hashCode += id.hashCode();
        }

        return super.put(ae, values);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AliasingMap) {
            AliasingMap assertion = (AliasingMap) o;

            if (size() != assertion.size()) {
                return false;
            }

            Iterator<AccessExpression> it1 = keySet().iterator();
            Iterator<AccessExpression> it2 = assertion.keySet().iterator();

            while (it1.hasNext()) {
                AccessExpression ae1 = it1.next();
                AccessExpression ae2 = it2.next();

                if (!ae1.equals(ae2) || !get(ae1).equals(assertion.get(ae2))) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();

        ret.append("[");
        for (AccessExpression expression : keySet()) {
            if (expression != firstKey()) {
                ret.append(", ");
            }

            ret.append(expression.toString(Notation.DOT_NOTATION));
            ret.append(": ");
            ret.append(get(expression));
        }
        ret.append("]");

        return ret.toString();
    }
}
