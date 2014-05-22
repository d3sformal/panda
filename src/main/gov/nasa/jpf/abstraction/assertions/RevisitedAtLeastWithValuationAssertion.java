package gov.nasa.jpf.abstraction.assertions;

public class RevisitedAtLeastWithValuationAssertion implements LocationAssertion {
    private PredicateValuationMap valuation = null;
    private int limit = 0;
    private int visits = 0;

    public RevisitedAtLeastWithValuationAssertion update(PredicateValuationMap valuationReference, PredicateValuationMap currentValuation, Integer count) {
        limit = 1 + count;

        valuation = valuationReference;

        if (valuation.equals(currentValuation)) {
            ++visits;
        }

        return this;
    }

    @Override
    public boolean isViolated() {
        return visits < limit;
    }

    @Override
    public String getError() {
        return "location revisited too few times with valuation " + valuation + " (" + (visits - 1) + ")";
    }
}
