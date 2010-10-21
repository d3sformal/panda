package gov.nasa.jpf.abstraction.numeric;

public class Signs extends Abstraction {
	public static Signs POS = new Signs();
	public static Signs NEG = new Signs();
	public static Signs ZERO = new Signs();
	public static Signs TOP = new Signs();

	public Signs () {
	}

	public Signs abstract_map(int v) {
		if(v > 0) return Signs.POS;
		if(v == 0) return Signs.ZERO;
		// if (v < 0)
		return Signs.NEG;
	}

	@Override
	public Signs _plus(int right) {
		return _plus(abstract_map(right));
	}

	@Override
	public Signs _plus(Abstraction right) {
		if(right instanceof Signs) {
			Signs right_value = (Signs) right;
			if (this == Signs.POS && right_value == Signs.POS) return Signs.POS;
			if (this == Signs.POS && right_value == Signs.ZERO) return Signs.POS;
			if (this == Signs.ZERO && right_value == Signs.POS) return Signs.POS;
			if (this == Signs.ZERO && right_value == Signs.ZERO) return Signs.ZERO;
			if (this == Signs.ZERO && right_value == Signs.NEG) return Signs.NEG;
			if (this == Signs.NEG && right_value == Signs.NEG) return Signs.NEG;
			if (this == Signs.NEG && right_value == Signs.ZERO) return Signs.NEG;
			// else
			return Signs.TOP;
		}
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _minus(Abstraction right) {
		if(right instanceof Signs) {
			Signs right_value = (Signs) right;
			if (this == Signs.POS && right_value == Signs.ZERO) return Signs.POS;
			if (this == Signs.POS && right_value == Signs.NEG) return Signs.POS;
			if (this == Signs.ZERO && right_value == Signs.POS) return Signs.NEG;
			if (this == Signs.ZERO && right_value == Signs.ZERO) return Signs.ZERO;
			if (this == Signs.ZERO && right_value == Signs.NEG) return Signs.POS;
			if (this == Signs.NEG && right_value == Signs.POS) return Signs.NEG;
			if (this == Signs.NEG && right_value == Signs.ZERO) return Signs.NEG;
			// else
			return Signs.TOP;
		}
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _minus(int right) {
		return _minus(abstract_map(right));
	}

	@Override
	public Abstraction _minus_reverse(int right) {
		return abstract_map(right)._minus(this);
	}

	@Override
	public AbstractBoolean _ge(Abstraction right) {
		if(right instanceof Signs) {
			Signs right_value = (Signs) right;
			if(this == Signs.POS && right_value == Signs.ZERO) return AbstractBoolean.TRUE;
			if(this == Signs.POS && right_value == Signs.NEG) return AbstractBoolean.TRUE;
			if(this == Signs.ZERO && right_value == Signs.ZERO) return AbstractBoolean.TRUE;
			if(this == Signs.ZERO && right_value == Signs.NEG) return AbstractBoolean.TRUE;
			if(this == Signs.ZERO && right_value == Signs.POS) return AbstractBoolean.FALSE;
			if(this == Signs.NEG && right_value == Signs.ZERO) return AbstractBoolean.FALSE;
			if(this == Signs.NEG && right_value == Signs.POS) return AbstractBoolean.FALSE;
			// else
			return AbstractBoolean.TOP;
		}
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}
	@Override
	public AbstractBoolean _ge(int right) {
		return _ge(abstract_map(right));
	}
	@Override
	public AbstractBoolean _gt(Abstraction right) {
		if(right instanceof Signs) {
			Signs right_value = (Signs) right;
			if(this == Signs.POS && right_value == Signs.ZERO) return AbstractBoolean.TRUE;
			if(this == Signs.POS && right_value == Signs.NEG) return AbstractBoolean.TRUE;
			if(this == Signs.ZERO && right_value == Signs.ZERO) return AbstractBoolean.FALSE;
			if(this == Signs.ZERO && right_value == Signs.NEG) return AbstractBoolean.TRUE;
			if(this == Signs.ZERO && right_value == Signs.POS) return AbstractBoolean.FALSE;
			if(this == Signs.NEG && right_value == Signs.ZERO) return AbstractBoolean.FALSE;
			if(this == Signs.NEG && right_value == Signs.POS) return AbstractBoolean.FALSE;
			// else
			return AbstractBoolean.TOP;
		}
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}
	@Override
	public AbstractBoolean _gt(int right) {
		return _gt(abstract_map(right));
	}
	@Override
	public AbstractBoolean _le(Abstraction right) {
		if(right instanceof Signs) {
			Signs right_value = (Signs) right;
			if(this == Signs.ZERO && right_value == Signs.POS) return AbstractBoolean.TRUE;
			if(this == Signs.NEG && right_value == Signs.POS) return AbstractBoolean.TRUE;
			if(this == Signs.NEG && right_value == Signs.ZERO) return AbstractBoolean.TRUE;
			if(this == Signs.ZERO && right_value == Signs.ZERO) return AbstractBoolean.TRUE;
			if(this == Signs.ZERO && right_value == Signs.NEG) return AbstractBoolean.FALSE;
			if(this == Signs.POS && right_value == Signs.NEG) return AbstractBoolean.FALSE;
			if(this == Signs.POS && right_value == Signs.ZERO) return AbstractBoolean.FALSE;
			// else
			return AbstractBoolean.TOP;
		}
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}
	@Override
	public AbstractBoolean _le(int right) {
		return _le(abstract_map(right));
	}
	@Override
	public AbstractBoolean _lt(Abstraction right) {
		if(right instanceof Signs) {
			Signs right_value = (Signs) right;
			if(this == Signs.ZERO && right_value == Signs.POS) return AbstractBoolean.TRUE;
			if(this == Signs.NEG && right_value == Signs.POS) return AbstractBoolean.TRUE;
			if(this == Signs.NEG && right_value == Signs.ZERO) return AbstractBoolean.TRUE;
			if(this == Signs.ZERO && right_value == Signs.ZERO) return AbstractBoolean.FALSE;
			if(this == Signs.ZERO && right_value == Signs.NEG) return AbstractBoolean.FALSE;
			if(this == Signs.POS && right_value == Signs.NEG) return AbstractBoolean.FALSE;
			if(this == Signs.POS && right_value == Signs.ZERO) return AbstractBoolean.FALSE;
			// else
			return AbstractBoolean.TOP;
		}
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}
	@Override
	public AbstractBoolean _lt(int right) {
		return _lt(abstract_map(right));
	}

	public String toString () {
		if (this instanceof Signs) {
			if(this == Signs.ZERO) return "ZERO";
			if(this == Signs.POS) return "POS";
			if(this == Signs.NEG) return "NEG";
			if(this == Signs.TOP) return "TOP";
		}
		else
			throw new RuntimeException("## Error: unknown abstraction");
		return "";
	}
}
