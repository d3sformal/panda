package gov.nasa.jpf.abstraction.assertions;

public class AssertSameValuationOnEveryVisitHandler extends AssertValuationOnEveryVisitHandler {

    @Override
    public Class<? extends LocationAssertion> getAssertionClass() {
        return SameValuationOnEveryVisitAssertion.class;
    }

}
