package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.state.TruthValue;

/**
 * Means of letting predicate abstraction know about valuation of the branching condition under the selected branch.
 */
public class BranchingConditionValuation implements BranchingDecision {
    private Predicate condition;
    private TruthValue valuation;

    public BranchingConditionValuation(Predicate condition, TruthValue valuation) {
        this.condition = condition;
        this.valuation = valuation;
    }

    public Predicate getCondition() {
        return condition;
    }

    public TruthValue getValuation() {
        return valuation;
    }
}
