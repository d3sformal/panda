package gov.nasa.jpf.abstraction.numeric;

import gov.nasa.jpf.abstraction.AbstractValue;

import java.util.HashSet;
import java.util.Set;

public class IntervalValue extends AbstractValue {

	public IntervalValue(int key)
	{
		super(key);
	}

	public boolean can_be_GREATER() {
		int key = this.getKey();
		return key == 2 || key == 3 || key == 4 || key == 6;
	}

	public boolean can_be_LESS() {
		int key = this.getKey();
		return key == 0 || key == 4 || key == 5 || key == 6;
	}

	public boolean can_be_INSIDE() {
		int key = this.getKey();
		return key == 1 || key == 3 || key == 5 || key == 6;
	}
	
	@Override
	public Set<AbstractValue> getTokens() {
		Set<AbstractValue> tokens = new HashSet<AbstractValue>();
		if (can_be_LESS())
			tokens.add(((IntervalAbstraction)abs).createLess());
		if (can_be_INSIDE())
			tokens.add(((IntervalAbstraction)abs).createInside());
		if (can_be_GREATER())
			tokens.add(((IntervalAbstraction)abs).createGreater());
		return tokens;
	}

	// returns possible tokens (enumerated from 0) in order {NEG, ZERO, POS}
	@Override
	public AbstractValue getToken(int key) {
		int num = getTokensNumber();
		if (key < 0 || key >= num)
			throw new RuntimeException("Wrong TOP token");
		if (can_be_LESS())
			if (key == 0)
				return ((IntervalAbstraction)abs).createLess();
			else if (can_be_INSIDE())
				return (key == 1) ? ((IntervalAbstraction)abs).createInside() : ((IntervalAbstraction)abs).createGreater();
			else
				return ((IntervalAbstraction)abs).createGreater();
		else if (can_be_INSIDE())
			return (key == 0) ? ((IntervalAbstraction)abs).createInside() : ((IntervalAbstraction)abs).createGreater();
		else
			return ((IntervalAbstraction)abs).createGreater();
	}

	@Override
	public int getTokensNumber() {
		int result = 0;
		if (can_be_GREATER())
			++result;
		if (can_be_LESS())
			++result;
		if (can_be_INSIDE())
			++result;
		return result;
	}
	
	/**
	 * @return true, if this abstraction is a single value from the domain;
	 * false, if this abstraction represents a set of values from the domain.
	 */
	@Override
	public boolean isComposite() {
		return getKey() > 2;
	}

	@Override
	public String toString() {
		if (getKey() == IntervalAbstraction.AbstractIntervalValueType.LESS.key) {
			return "(-INF, MIN)";
		} else if (getKey() == IntervalAbstraction.AbstractIntervalValueType.INSIDE.key) {
			return "[MIN, MAX]";
		} else if (getKey() == IntervalAbstraction.AbstractIntervalValueType.GREATER.key) {
			return "(MAX, +INF)";
		} else {
			if (this.isComposite()) {
				String s = null;
				if (this.can_be_LESS())
					s = "(-INF, MIN)";
				if (this.can_be_INSIDE())
					if (s.isEmpty())
						s = "[MIN, MAX]";
					else
						s += "or [MIN, MAX]";
				if (this.can_be_GREATER())
					if (s.isEmpty())
						s = "(MAX, +INF)";
					else
						s += "or (MAX, +INF)";
				return s;
			}
			return "";
		}
	}
}
