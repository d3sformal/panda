package gov.nasa.jpf.abstraction.numeric;

import gov.nasa.jpf.abstraction.AbstractValue;

import java.util.HashSet;
import java.util.Set;

public class RangeValue extends AbstractValue {

    private Set<Integer> values = new HashSet<Integer>();

    public RangeValue(int key) {
        super(key);
    }

    @Override
    public Set<AbstractValue> getTokens() {
        Set<AbstractValue> tokens = new HashSet<AbstractValue>();
        for (Integer e : values)
            tokens.add(abs.abstractMap(e));
        return tokens;
    }

    // returns possible tokens from TOP in order {NEG, ZERO, POS}
    @Override
    public AbstractValue getToken(int key) {
        int num = getTokensNumber();
        if (key < 0 || key >= num)
            throw new RuntimeException("Wrong TOP token");
        return abs.abstractMap((Integer)(values.toArray()[key]));
    }

    public int get_value() {
        return getKey()+((RangeAbstraction)abs).MIN-1;
    }

    public Set<Integer> getValues() {
        return values;
    }

    @Override
    public int getTokensNumber() {
        return values.size();
    }

    /**
     * @return true, if this abstraction is a single value from the domain;
     * false, if this abstraction represents a set of values from the domain.
     */
    @Override
    public boolean isComposite() {
        return values.size() > 1;
    }

    @Override
    public String toString() {
        if (isComposite()) {
            String res = "";
            for (Integer abs : values)
                res += " or " + abs;
            return res;
        } else if (get_value() < ((RangeAbstraction)abs).MIN)
            return String.format("(-INF, %d)", ((RangeAbstraction)abs).MIN);
        else if (get_value() > ((RangeAbstraction)abs).MAX)
            return String.format("(%d, +INF)", ((RangeAbstraction)abs).MAX);
        else
            return Integer.toString(get_value());
    }

}
