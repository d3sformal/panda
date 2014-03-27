package gov.nasa.jpf.abstraction.assertions;

public class AssertDifferentValuationOnEveryVisitHandler extends AssertValuationOnEveryVisitHandler {

    @Override
    public Class<? extends LocationAssertion> getAssertionClass() {
        return DifferentValuationOnEveryVisitAssertion.class;
    }

}
