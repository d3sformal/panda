package gov.nasa.jpf.abstraction.numeric;

import java.util.HashSet;
import java.util.Set;

public class EvennessValue extends AbstractValue {
	
	public EvennessValue(int key) {
		super(key);
		abs = EvennessAbstraction.getInstance();
	}
	
	public boolean can_be_ODD() {
		return getKey() != 0;
	}

	public boolean can_be_EVEN() {
		return getKey() != 1;
	}

	@Override
	public Set<AbstractValue> getTokens() {
		Set<AbstractValue> tokens = new HashSet<AbstractValue>();
		if (can_be_EVEN())
			tokens.add(EvennessAbstraction.EVEN);
		if (can_be_ODD())
			tokens.add(EvennessAbstraction.ODD);
		return tokens;
	}

	// returns possible tokens from TOP in order {EVEN, ODD}
	@Override
	public AbstractValue getToken(int key) {
		int num = getTokensNumber();
		if (key < 0 || key >= num)
			throw new RuntimeException("Wrong TOP token");
		if (can_be_EVEN())
			return (key == 0)? EvennessAbstraction.EVEN : EvennessAbstraction.ODD;
		else
			return EvennessAbstraction.ODD;
	}

	@Override
	public int getTokensNumber() {
		int result = 0;
		if (can_be_EVEN())
			++result;
		if (can_be_ODD())
			++result;
		return result;
	}
	
	@Override
	public boolean isComposite() {
		return this == EvennessAbstraction.TOP;
	}
	
	@Override
	public String toString() {
		if (this == EvennessAbstraction.EVEN)
			return "EVEN";
		if (this == EvennessAbstraction.ODD)
			return "ODD";
		if (this.isComposite())
			return "TOP";

		return "";
	}
}
