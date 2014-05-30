package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.BranchingConditionInfo;

/**
 * Possible valuations of a predicate
 *
 * UNDEFINED ... the initial value (not valuated yet, cannot be valuated)
 * UNKNOWN   ... cannot rule out neither true nor false
 * TRUE      ... true
 * FALSE     ... false
 */
public enum TruthValue implements BranchingConditionInfo {
    UNDEFINED, // 0 .. 00
    TRUE,      // 1 .. 01
    FALSE,     // 2 .. 10
    UNKNOWN;   // 3 .. 11

    private static TruthValue[] ALL_VALUES = new TruthValue[] {UNDEFINED, TRUE, FALSE, UNKNOWN};

    private static TruthValue create(int i) {
        if (i == UNDEFINED.ordinal()) return UNDEFINED;
        if (i == TRUE.ordinal()) return TRUE;
        if (i == FALSE.ordinal()) return FALSE;
        if (i == UNKNOWN.ordinal()) return UNKNOWN;

        assert false : "Unknown truth value ordinal";

        return UNDEFINED;
    }

    public static TruthValue create(boolean isTrue, boolean isFalse) {
        if (isTrue && isFalse) {
            return UNKNOWN;
        }

        if (isTrue) return TRUE;
        if (isFalse) return FALSE;

        return UNDEFINED;
    }

    public static TruthValue create(boolean isTrue) {
        return create(isTrue, !isTrue);
    }

    public static TruthValue create(String str) {
        for (TruthValue t : ALL_VALUES) {
            if (t.toString().equals(str.trim().toUpperCase())) {
                return t;
            }
        }

        return UNDEFINED;
    }

    /**
     * Returns the greatest common predecesor of both the elements of the lattice over {0, 1}
     *
     * 00 and ?? ... UNDEFINED and ??       ... UNDEFINED
     * 01 and 01 ... TRUE      and TRUE     ... TRUE
     * 01 and 10 ... TRUE      and FALSE    ... UNDEFINED
     * 01 and 11 ... TRUE      and UNKNOWN  ... TRUE
     * 10 and 10 ... FALSE     and FALSE    ... FALSE
     * 10 and 11 ... FALSE     and UNKNOWN  ... FALSE
     * 11 and 11 ... UNKNOWN   and UNKNOWN  ... UNKNOWN
     */
    public static TruthValue and(TruthValue a, TruthValue b) {
        return create(a.ordinal() & b.ordinal());
    }

    /**
     * Returns the least common successor of both the elements of the lattice over {0, 1}
     *
     * 00 or 00 ... UNDEFINED or UNDEFINED ... UNDEFINED
     * 00 or 01 ... UNDEFINED or TRUE      ... TRUE
     * 00 or 10 ... UNDEFINED or FALSE     ... FALSE
     * 01 or 01 ... TRUE      or TRUE      ... TRUE
     * 01 or 10 ... TRUE      or FALSE     ... UNKNOWN
     * 10 or 10 ... FALSE     or FALSE     ... FALSE
     * 11 or ?? ... UNKNOWN   or ??        ... UNKNOWN
     */
    public static TruthValue or(TruthValue a, TruthValue b) {
        return create(a.ordinal() | b.ordinal());
    }

    /**
     * Switches TRUE for FALSE
     *
     * 00 becomes 00
     * 01 becomes 10
     * 10 becomes 01
     * 11 becomes 11
     */
    public static TruthValue neg(TruthValue a) {
        return create(((a.ordinal() & 1) << 1) | ((a.ordinal() & 2) >> 1));
    }

    @Override
    public BranchingConditionInfo combine(BranchingConditionInfo info) {
        if (info == BranchingConditionInfo.NONE) {
            return this;
        }
        if (info instanceof TruthValue) {
            return or(this, (TruthValue) info);
        }

        return BranchingConditionInfo.NONE;
    }
}
