package gov.nasa.jpf.abstraction.numeric;

import gov.nasa.jpf.abstraction.AbstractValue;

import java.util.HashSet;
import java.util.Set;

public class SignsValue extends AbstractValue {
	public SignsValue(int key) {
		super(key);
		abs = SignsAbstraction.getInstance();
	}
	
	/**
	 * @return true, iff this abstraction is NEG, NON_ZERO, NON_POS or TOP
	 */
	public boolean can_be_NEG() {
		int key = getKey();
		return key == 0 || key == 4 || key == 5 || key == 6;
	}	

	/**
	 * @return true, iff this abstraction is ZERO, NON_NEG, NON_POS or TOP
	 */	
	public boolean can_be_ZERO() {
		int key = getKey();
		return key == 1 || key == 3 || key == 5 || key == 6;
	}

	/**
	 * @return true, iff this abstraction is POS, NON_NEG, NON_ZERO or TOP
	 */
	public boolean can_be_POS() {
		int key = getKey();
		return key == 2 || key == 3 || key == 4 || key == 6;
	}
	
	@Override
	public Set<AbstractValue> getTokens() {
		Set<AbstractValue> tokens = new HashSet<AbstractValue>();
		if (can_be_NEG())
			tokens.add(SignsAbstraction.NEG);
		if (can_be_ZERO())
			tokens.add(SignsAbstraction.ZERO);
		if (can_be_POS())
			tokens.add(SignsAbstraction.POS);
		return tokens;
	}

	// returns possible tokens from TOP in order {NEG, ZERO, POS}
	@Override
	public AbstractValue getToken(int idx) {
		int num = getTokensNumber();
		if (idx < 0 || idx >= num)
			throw new RuntimeException("### Error: out of range");
		if (can_be_NEG())
			if (idx == 0)
				return SignsAbstraction.NEG;
			else if (can_be_ZERO())
				return (idx == 1) ? SignsAbstraction.ZERO : SignsAbstraction.POS;
			else
				return SignsAbstraction.POS;
		else if (can_be_ZERO())
			return (idx == 0) ? SignsAbstraction.ZERO : SignsAbstraction.POS;
		else
			return SignsAbstraction.POS;
	}

	@Override
	public int getTokensNumber() {
		int result = 0;
		if (can_be_POS())
			++result;
		if (can_be_NEG())
			++result;
		if (can_be_ZERO())
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
		if (this instanceof SignsValue) {
			if (this.isComposite()) {
				String result = "";
				if (can_be_NEG())
					result = "NEG,";
				if (can_be_ZERO())
					result += (result.isEmpty()) ? "ZERO," : " ZERO,";
				if (can_be_POS())
					result += " POS";
				return "{ " + result + " }";
			}
			if (this == SignsAbstraction.ZERO)
				return "ZERO";
			if (this == SignsAbstraction.POS)
				return "POS";
			if (this == SignsAbstraction.NEG)
				return "NEG";
		}
		throw new RuntimeException("## Error: unknown abstraction");
	}
}
