package gov.nasa.jpf.abstraction.common;

/**
 * An interface for hint provided by a particular abstraction about the condition
 *
 * This will advise the instruction in question what branches should be taken
 */
public interface BranchingConditionInfo {
    public static BranchingConditionInfo NONE = new BranchingConditionInfo() {
        @Override
        public BranchingConditionInfo combine(BranchingConditionInfo info) {
            return info;
        }
    };

    public BranchingConditionInfo combine(BranchingConditionInfo info);
}
