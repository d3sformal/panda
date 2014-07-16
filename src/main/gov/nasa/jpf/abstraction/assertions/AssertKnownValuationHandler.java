package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.state.TruthValue;

public class AssertKnownValuationHandler extends AssertConjunctionHandler {

    @Override
    protected void checkValuation(Predicate assertedFact, TruthValue assertedValuation) {
        TruthValue knownValuation = PredicateAbstraction.getInstance().getPredicateValuation().get(assertedFact);

        if (assertedValuation != knownValuation) {
            throw new RuntimeException("Asserted incorrect predicate valuation: `" + assertedFact + "` expected to valuate to `" + assertedValuation + "` but actually valuated to `" + knownValuation + "`");
        }
    }

}
