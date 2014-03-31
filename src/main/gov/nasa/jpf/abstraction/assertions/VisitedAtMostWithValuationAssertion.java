package gov.nasa.jpf.abstraction.assertions;

public class VisitedAtMostWithValuationAssertion implements LocationAssertion {
    private PredicateValuationMap valuation = null;
    private int limit = 0;
    private int visits = 0;

    @Override
    public void update(Object... o) {
        if (o[0] instanceof PredicateValuationMap && o[1] instanceof PredicateValuationMap && o[2] instanceof Integer) {
            valuation = (PredicateValuationMap) o[0];

            limit = (Integer) o[2];

            if (valuation.equals((PredicateValuationMap) o[1])) {
                ++visits;
            }
        }
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
