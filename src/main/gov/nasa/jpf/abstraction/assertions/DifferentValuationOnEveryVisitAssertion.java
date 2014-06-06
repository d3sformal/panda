package gov.nasa.jpf.abstraction.assertions;

import java.util.HashSet;
import java.util.Set;

public class DifferentValuationOnEveryVisitAssertion implements LocationAssertion {
    private Set<PredicateValuationMap> valuations = new HashSet<PredicateValuationMap>();
    private Set<PredicateValuationMap> duplicateValuations = new HashSet<PredicateValuationMap>();

    public DifferentValuationOnEveryVisitAssertion update(PredicateValuationMap valuation) {
        if (valuations.contains(valuation)) {
            duplicateValuations.add(valuation);
        }

        valuations.add(valuation);

        return this;
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
