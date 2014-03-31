package gov.nasa.jpf.abstraction.assertions;

import java.util.Set;
import java.util.HashSet;

public class SameValuationOnEveryVisitAssertion implements LocationAssertion {
    private Set<PredicateValuationMap> valuations = new HashSet<PredicateValuationMap>();

    @Override
    public void update(Object... o) {
        if (o[0] instanceof PredicateValuationMap) {
            PredicateValuationMap valuation = (PredicateValuationMap) o[0];

            if (!valuations.contains(valuation)) {
                valuations.add(valuation);
            }
        }
    }

    @Override
    public boolean isViolated() {
        return valuations.size() > 1;
    }

    @Override
    public String getError() {
        return "Different valuations: " + valuations;
    }
}
