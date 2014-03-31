package gov.nasa.jpf.abstraction.assertions;

import java.util.Set;
import java.util.HashSet;

public class DifferentValuationOnEveryVisitAssertion implements LocationAssertion {
    private Set<PredicateValuationMap> valuations = new HashSet<PredicateValuationMap>();
    private Set<PredicateValuationMap> duplicateValuations = new HashSet<PredicateValuationMap>();

    @Override
    public void update(Object... o) {
        if (o[0] instanceof PredicateValuationMap) {
            PredicateValuationMap valuation = (PredicateValuationMap) o[0];

            if (valuations.contains(valuation)) {
                duplicateValuations.add(valuation);
            }

            valuations.add(valuation);
        }
    }

    @Override
    public boolean isViolated() {
        return !duplicateValuations.isEmpty();
    }

    @Override
    public String getError() {
        return "Encountered a duplicate valuation: " + duplicateValuations.iterator().next();
    }
}
