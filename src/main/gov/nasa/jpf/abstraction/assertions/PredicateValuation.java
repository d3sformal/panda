package gov.nasa.jpf.abstraction.assertions;

import java.util.TreeMap;
import java.util.Comparator;
import java.util.Iterator;

import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;

public class PredicateValuation extends TreeMap<Predicate, TruthValue> {
    public static final long serialVersionUID = 1L;

    private int hashCode = 1;

    public PredicateValuation() {
        super(new Comparator<Predicate>() {
            @Override
            public int compare(Predicate p1, Predicate p2) {
                return p1.toString(Notation.DOT_NOTATION).compareTo(p2.toString(Notation.DOT_NOTATION));
            }
        });
    }

    @Override
    public TruthValue put(Predicate p, TruthValue v) {
        hashCode += p.hashCode() + v.hashCode();

        return super.put(p, v);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PredicateValuation) {
            PredicateValuation assertion = (PredicateValuation) o;

            if (size() != assertion.size()) {
                return false;
            }

            Iterator<Predicate> it1 = keySet().iterator();
            Iterator<Predicate> it2 = assertion.keySet().iterator();

            while (it1.hasNext()) {
                Predicate p1 = it1.next();
                Predicate p2 = it2.next();

                if (!p1.equals(p2) || get(p1) != assertion.get(p2)) {
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
        for (Predicate predicate : keySet()) {
            if (predicate != firstKey()) {
                ret.append(", ");
            }

            ret.append(predicate.toString(Notation.DOT_NOTATION));
            ret.append(": ");
            ret.append(get(predicate));
        }
        ret.append("]");

        return ret.toString();
    }
}
