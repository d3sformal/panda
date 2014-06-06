package gov.nasa.jpf.abstraction.assertions;

import java.util.HashSet;
import java.util.Set;

public class SameValuationOnEveryVisitAssertion implements LocationAssertion {
    private Set<PredicateValuationMap> valuations = new HashSet<PredicateValuationMap>();

    public SameValuationOnEveryVisitAssertion update(PredicateValuationMap valuation) {
        if (!valuations.contains(valuation)) {
            valuations.add(valuation);
        }

        return this;
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
