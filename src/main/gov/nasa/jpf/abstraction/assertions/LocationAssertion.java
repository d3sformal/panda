package gov.nasa.jpf.abstraction.assertions;

public interface LocationAssertion {
    public boolean isViolated();
    public String getError();
}
