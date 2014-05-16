package gov.nasa.jpf.abstraction.assertions;

import java.util.Set;
import java.util.HashSet;

public class SameAliasingOnEveryVisitAssertion implements LocationAssertion {
    private Set<AliasingMap> aliasings = new HashSet<AliasingMap>();

    public SameAliasingOnEveryVisitAssertion update(AliasingMap aliasing) {
        if (!aliasings.contains(aliasing)) {
            aliasings.add(aliasing);
        }

        return this;
    }

    @Override
    public boolean isViolated() {
        return aliasings.size() > 1;
    }

    @Override
    public String getError() {
        return "Different aliasings: " + aliasings;
    }
}
