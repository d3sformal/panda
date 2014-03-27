package gov.nasa.jpf.abstraction.assertions;

public interface LocationAssertion {
    public void update(Object... o);
    public boolean isViolated();
    public String getError();
}
