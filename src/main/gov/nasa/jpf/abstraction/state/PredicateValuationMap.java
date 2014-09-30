package gov.nasa.jpf.abstraction.state;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;

public class PredicateValuationMap extends HashMap<Predicate, TruthValue> {
    public static final long serialVersionUID = 1L;

    protected class UnknownPredicate extends RuntimeException {
        public static final long serialVersionUID = 1L;

        public UnknownPredicate(String msg) {
            super(msg);
        }
    }

    protected class UndefinedValuation extends RuntimeException {
        public static final long serialVersionUID = 1L;

        public UndefinedValuation(String msg) {
            super(msg);
        }
    }

    private static Predicate getCanonicalPredicateForm(Predicate predicate) {
        if (predicate instanceof Negation) {
            Negation n = (Negation) predicate;

            n.predicate.setScope(n.getScope());

            return n.predicate;
        }

        return predicate;
    }

    private static TruthValue getCanonicalPredicateValue(Predicate predicate, TruthValue value) {
        if (predicate instanceof Negation) {
            return TruthValue.neg(value);
        }

        return value;
    }

    private TruthValue putSymmetrical(Predicate predicate, TruthValue value) {
        Predicate canonical = getCanonicalPredicateForm(predicate);
        TruthValue canonicalValue = getCanonicalPredicateValue(predicate, value);

        if (canonical instanceof Equals) {
            Equals e = (Equals) canonical;
            Predicate eSym = Equals.create(e.b, e.a);

            eSym.setScope(e.getScope());

            if (super.containsKey(eSym)) {
                return super.put(eSym, canonicalValue);
            } else {
                return super.put(e, canonicalValue);
            }
        } else {
            return super.put(canonical, canonicalValue);
        }
    }

    @Override
    public TruthValue put(Predicate predicate, TruthValue value) {
        if (value == null) {
            throw new UndefinedValuation(predicate.toString(Notation.DOT_NOTATION) + ": " + value);
        }

        return putSymmetrical(predicate, value);
    }

    @Override
    public void putAll(Map<? extends Predicate, ? extends TruthValue> values) {
        for (Predicate predicate : values.keySet()) {
            put(predicate, values.get(predicate));
        }
    }

    private TruthValue getSymmetrical(Predicate predicate) {
        Predicate canonical = getCanonicalPredicateForm(predicate);

        if (canonical instanceof Equals) {
            Equals e = (Equals) canonical;
            Predicate eSym = Equals.create(e.b, e.a);

            if (super.containsKey(eSym)) {
                return getCanonicalPredicateValue(predicate, super.get(eSym));
            }
        }

        return getCanonicalPredicateValue(predicate, super.get(canonical));
    }

    public TruthValue get(Predicate predicate) {
        TruthValue value = getSymmetrical(predicate);

        if (value == null) {
            throw new UnknownPredicate(predicate.toString(Notation.DOT_NOTATION));
        }

        return value;
    }

    @Override
    public TruthValue get(Object o) {
        return get((Predicate) o);
    }

    private boolean containsSymmetrical(Predicate predicate) {
        Predicate canonical = getCanonicalPredicateForm(predicate);

        if (canonical instanceof Equals) {
            Equals e = (Equals) canonical;
            Predicate eSym = Equals.create(e.b, e.a);

            if (super.containsKey(eSym)) {
                return true;
            }
        }

        return super.containsKey(canonical);
    }

    public boolean containsKey(Predicate predicate) {
        return containsSymmetrical(predicate);
    }

    @Override
    public boolean containsKey(Object o) {
        return containsKey((Predicate) o);
    }

    public TruthValue remove(Predicate predicate) {
        return super.remove(getCanonicalPredicateForm(predicate));
    }

    @Override
    public TruthValue remove(Object o) {
        return remove((Predicate) o);
    }

    @Override
    public PredicateValuationMap clone() {
        PredicateValuationMap clone = new PredicateValuationMap();

        clone.putAll(this);

        return clone;
    }

}
