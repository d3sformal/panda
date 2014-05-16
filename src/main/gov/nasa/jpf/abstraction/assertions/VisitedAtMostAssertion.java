package gov.nasa.jpf.abstraction.assertions;

public class VisitedAtMostAssertion implements LocationAssertion {
    private int limit;
    private int visits = 0;

    public VisitedAtMostAssertion update(Integer count) {
        limit = count;
        ++visits;

        return this;
    }

    @Override
    public boolean isViolated() {
        return visits > limit;
    }

    @Override
    public String getError() {
        return "location visited too many times (" + visits + ")";
    }
}
