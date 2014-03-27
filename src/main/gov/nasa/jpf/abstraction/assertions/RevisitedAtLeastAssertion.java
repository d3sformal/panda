package gov.nasa.jpf.abstraction.assertions;

public class RevisitedAtLeastAssertion implements LocationAssertion {
    private int limit;
    private int visits = 0;

    @Override
    public void update(Object... o) {
        if (o[0] instanceof Integer) {
            limit = 1 + (Integer) o[0];
            ++visits;
        }
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
