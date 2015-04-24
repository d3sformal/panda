package gov.nasa.jpf.abstraction.common;

import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.state.TruthValue;

/**
 * Corresponds to one static section in the input file
 *
 * [static]
 * b = a - 1
 * a * b = 6
 * ...
 *
 * <<< SOME OTHER SECTION OR EOF (End of File)
 *
 * @see gov.nasa.jpf.abstraction.grammar (grammar file Predicates.g4)
 */
public class StaticPredicateContext extends PredicateContext {

    public StaticPredicateContext(List<Predicate> predicates) {
        super(predicates);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public TruthValue put(Predicate p, TruthValue v) {
        if (!(p.getScope() instanceof BytecodeUnlimitedRange)) {
            throw new RuntimeException("Static predicates should not have a specific scope set");
        }

        return super.put(p, v);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof StaticPredicateContext;
    }

}
