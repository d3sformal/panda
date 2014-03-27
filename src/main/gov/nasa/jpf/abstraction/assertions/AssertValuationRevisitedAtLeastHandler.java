package gov.nasa.jpf.abstraction.assertions;

public class AssertValuationRevisitedAtLeastHandler extends AssertValuationVisitedHandler {

    @Override
    public Class<? extends LocationAssertion> getAssertionClass() {
        return ValuationRevisitedAtLeastAssertion.class;
    }

}
