package gov.nasa.jpf.abstraction.assertions;

import java.util.Set;
import java.util.HashSet;

public class DifferentValuationOnEveryVisitAssertion implements LocationAssertion {
    private Set<PredicateValuation> valuations = new HashSet<PredicateValuation>();
    private Set<PredicateValuation> duplicateValuations = new HashSet<PredicateValuation>();

    @Override
    public void update(Object... o) {
        if (o[0] instanceof PredicateValuation) {
            PredicateValuation valuation = (PredicateValuation) o[0];

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
