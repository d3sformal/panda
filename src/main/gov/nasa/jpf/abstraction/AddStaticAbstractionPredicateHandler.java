package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.Method;
import gov.nasa.jpf.abstraction.state.TruthValue;

public class AddStaticAbstractionPredicateHandler extends AddAbstractionPredicateHandler {
    @Override
    public void addPredicate(Predicate p, Method m) {
        getContext().put(p, TruthValue.UNKNOWN);
    }
}
