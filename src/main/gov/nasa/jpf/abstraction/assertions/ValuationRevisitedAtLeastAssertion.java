package gov.nasa.jpf.abstraction.assertions;

public class ValuationRevisitedAtLeastAssertion implements LocationAssertion {
    private PredicateValuation valuation = null;
    private int limit = 0;
    private int visits = 0;

    @Override
    public void update(Object... o) {
        if (o[0] instanceof PredicateValuation && o[1] instanceof PredicateValuation && o[2] instanceof Integer) {
            valuation = (PredicateValuation) o[0];

            limit = 1 + (Integer) o[2];

            if (valuation.equals((PredicateValuation) o[1])) {
                ++visits;
            }
        }
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
