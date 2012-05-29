package gov.nasa.jpf.abstraction.numeric;

import java.util.HashSet;
import java.util.Set;

public class Signs extends Abstraction {
	public static Signs POS = new Signs();
	public static Signs NEG = new Signs();
	public static Signs ZERO = new Signs();
	public static Signs TOP = new Signs(true);



	public Set<Abstraction>   get_tokens() {
		Set<Abstraction> tokens = new HashSet<Abstraction>();
		tokens.add(POS);
		tokens.add(NEG);
		tokens.add(ZERO);
		return tokens;
	}

	public Signs () {
	}

	public Signs (boolean isTop) {
		this.isTop = isTop;
	}

	public Signs abstract_map(int v) {
		if(v > 0) return POS;
		if(v == 0) return ZERO;
		// if (v < 0)
		return NEG;
	}

	@Override
	public Abstraction _plus(int right) {
		return _plus(abstract_map(right));
	}

	@Override
	public Abstraction _plus(Abstraction right) {
		if(right instanceof Signs) {
			Signs right_value = (Signs) right;
			if (this == POS && right_value == POS) return POS;
			if (this == POS && right_value == ZERO) return POS;
			if (this == ZERO && right_value == POS) return POS;
			if (this == ZERO && right_value == ZERO) return ZERO;
			if (this == ZERO && right_value == NEG) return NEG;
			if (this == NEG && right_value == NEG) return NEG;
			if (this == NEG && right_value == ZERO) return NEG;
			// else
			return TOP;
		}
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _minus(Abstraction right) {
		if(right instanceof Signs) {
			Signs right_value = (Signs) right;
			if (this == POS && right_value == ZERO) return POS;
			if (this == POS && right_value == NEG) return POS;
			if (this == ZERO && right_value == POS) return NEG;
			if (this == ZERO && right_value == ZERO) return ZERO;
			if (this == ZERO && right_value == NEG) return POS;
			if (this == NEG && right_value == POS) return NEG;
			if (this == NEG && right_value == ZERO) return NEG;
			// else
			return TOP;
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
	public Abstraction _mul(int right) {
		return _mul(abstract_map(right));
	}

	@Override
	public Abstraction _mul(Abstraction right) {
		if(right instanceof Signs) {
			Signs right_value = (Signs) right;
			if (this == POS && right_value == POS) return POS;
			if (this == POS && right_value == ZERO) return ZERO;
			if (this == POS && right_value == NEG) return NEG;
			if (this == ZERO && right_value == POS) return ZERO;
			if (this == ZERO && right_value == ZERO) return ZERO;
			if (this == ZERO && right_value == NEG) return ZERO;
			if (this == NEG && right_value == POS) return NEG;
			if (this == NEG && right_value == ZERO) return ZERO;
			if (this == NEG && right_value == NEG) return POS;			
			// else (if this or right_value is TOP)
			return TOP;
		}
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}	

	@Override
	public Abstraction _div(int right) {
		return _div(abstract_map(right));
	}

	@Override
	public Abstraction _div(Abstraction right) {
		if(right instanceof Signs) {
			Signs right_value = (Signs) right;
			if (this == POS && right_value == POS) return POS;
			if (this == POS && right_value == NEG) return NEG;
			if (this == ZERO && right_value == POS) return ZERO;			
			if (this == ZERO && right_value == NEG) return ZERO;
			if (this == NEG && right_value == POS) return NEG;
			if (this == NEG && right_value == NEG) return POS;
			// TODO: raise a proper exception
			if (right_value == ZERO)
				throw new ArithmeticException("Division by zero (in abstract IDIV)");			
			// else (if this or right_value is TOP)
			return TOP;
		}
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}		
	
	@Override
	public Abstraction _neg() {
		if (this == POS)	return NEG;
		if (this == NEG)	return POS;
		if (this == ZERO)	return ZERO;
		// else (if this is TOP)
		return TOP;
	}
	
	@Override
	public AbstractBoolean _ge(Abstraction right) {
		if(right instanceof Signs) {
			Signs right_value = (Signs) right;
			if(this == POS && right_value == ZERO) return AbstractBoolean.TRUE;
			if(this == POS && right_value == NEG) return AbstractBoolean.TRUE;
			if(this == ZERO && right_value == ZERO) return AbstractBoolean.TRUE;
			if(this == ZERO && right_value == NEG) return AbstractBoolean.TRUE;
			if(this == ZERO && right_value == POS) return AbstractBoolean.FALSE;
			if(this == NEG && right_value == ZERO) return AbstractBoolean.FALSE;
			if(this == NEG && right_value == POS) return AbstractBoolean.FALSE;
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
			if(this == POS && right_value == ZERO) return AbstractBoolean.TRUE;
			if(this == POS && right_value == NEG) return AbstractBoolean.TRUE;
			if(this == ZERO && right_value == ZERO) return AbstractBoolean.FALSE;
			if(this == ZERO && right_value == NEG) return AbstractBoolean.TRUE;
			if(this == ZERO && right_value == POS) return AbstractBoolean.FALSE;
			if(this == NEG && right_value == ZERO) return AbstractBoolean.FALSE;
			if(this == NEG && right_value == POS) return AbstractBoolean.FALSE;
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
			if(this == ZERO && right_value == POS) return AbstractBoolean.TRUE;
			if(this == NEG && right_value == POS) return AbstractBoolean.TRUE;
			if(this == NEG && right_value == ZERO) return AbstractBoolean.TRUE;
			if(this == ZERO && right_value == ZERO) return AbstractBoolean.TRUE;
			if(this == ZERO && right_value == NEG) return AbstractBoolean.FALSE;
			if(this == POS && right_value == NEG) return AbstractBoolean.FALSE;
			if(this == POS && right_value == ZERO) return AbstractBoolean.FALSE;
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
			if(this == ZERO && right_value == POS) return AbstractBoolean.TRUE;
			if(this == NEG && right_value == POS) return AbstractBoolean.TRUE;
			if(this == NEG && right_value == ZERO) return AbstractBoolean.TRUE;
			if(this == ZERO && right_value == ZERO) return AbstractBoolean.FALSE;
			if(this == ZERO && right_value == NEG) return AbstractBoolean.FALSE;
			if(this == POS && right_value == NEG) return AbstractBoolean.FALSE;
			if(this == POS && right_value == ZERO) return AbstractBoolean.FALSE;
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
			if(this == ZERO) return "ZERO";
			if(this == POS) return "POS";
			if(this == NEG) return "NEG";
			if(this == TOP) return "TOP";
		}
		else
			throw new RuntimeException("## Error: unknown abstraction");
		return "";
	}
}
