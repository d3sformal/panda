package gov.nasa.jpf.abstraction.assertions;

public class RevisitedAtLeastAssertion implements LocationAssertion {
    private int limit;
    private int visits = 0;

    public RevisitedAtLeastAssertion update(Integer count) {
        limit = 1 + count;
        ++visits;

        return this;
    }

    @Override
    public boolean isViolated() {
        return visits < limit;
    }

    @Override
    public String getError() {
        return "location revisited too few times (" + (visits - 1) + ")";
    }
}
