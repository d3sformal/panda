package gov.nasa.jpf.abstraction.assertions;

public class VisitedAtMostWithValuationAssertion implements LocationAssertion {
    private PredicateValuationMap valuation = null;
    private int limit = 0;
    private int visits = 0;

    public VisitedAtMostWithValuationAssertion update(PredicateValuationMap valuationReference, PredicateValuationMap currentValuation, Integer count) {
        valuation = valuationReference;
        limit = count;

        if (valuationReference.equals(currentValuation)) {
            ++visits;
        }

        return this;
    }

    @Override
    public boolean isViolated() {
        return visits > limit;
    }

    @Override
    public String getError() {
        return "location visited too many times with valuation " + valuation + " (" + visits + ")";
    }
}
