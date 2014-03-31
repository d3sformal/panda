package gov.nasa.jpf.abstraction.assertions;

public class RevisitedAtLeastWithValuationAssertion implements LocationAssertion {
    private PredicateValuationMap valuation = null;
    private int limit = 0;
    private int visits = 0;

    @Override
    public void update(Object... o) {
        if (o[0] instanceof PredicateValuationMap && o[1] instanceof PredicateValuationMap && o[2] instanceof Integer) {
            valuation = (PredicateValuationMap) o[0];

            limit = 1 + (Integer) o[2];

            if (valuation.equals((PredicateValuationMap) o[1])) {
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
