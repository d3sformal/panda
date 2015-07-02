package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.Method;
import gov.nasa.jpf.abstraction.state.TruthValue;

public class AddObjectAbstractionPredicateHandler extends AddAbstractionPredicateHandler {
    @Override
    public void addPredicate(Predicate p, Method m) {
        getContext(m.getPackageAndClass()).put(p, TruthValue.UNKNOWN);
    }
}
