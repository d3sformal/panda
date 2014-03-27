package gov.nasa.jpf.abstraction.assertions;

public class AssertValuationVisitedAtMostHandler extends AssertValuationVisitedHandler {

    @Override
    public Class<? extends LocationAssertion> getAssertionClass() {
        return ValuationVisitedAtMostAssertion.class;
    }

}
