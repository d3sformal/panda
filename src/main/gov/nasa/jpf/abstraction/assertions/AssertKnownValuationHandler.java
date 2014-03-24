package gov.nasa.jpf.abstraction.assertions;

import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;

public class AssertKnownValuationHandler extends AssertConjunctionHandler {

    @Override
    protected void checkValuation(Predicate assertedFact, TruthValue assertedValuation) {
        TruthValue knownValuation = ((PredicateAbstraction) GlobalAbstraction.getInstance().get()).getPredicateValuation().get(assertedFact);

        if (assertedValuation != knownValuation) {
            throw new RuntimeException("Asserted incorrect predicate valuation: `" + assertedFact + "` expected to valuate to `" + assertedValuation + "` but actually valuated to `" + knownValuation + "`");
        }
    }

}