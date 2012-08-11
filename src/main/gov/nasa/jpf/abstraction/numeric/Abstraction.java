package gov.nasa.jpf.abstraction.numeric;

import java.util.Set;

public class Abstraction {

	int key;

	public int get_key() {
		return key;
	}

	public void set_key(int key) {
		this.key = key;
	}

	// returns the abstract token corresponding to the key
	public Abstraction get_token(int key) {
		throw new RuntimeException("get_token not implemented");
	}

	public Set<Abstraction> get_tokens() {
		throw new RuntimeException("get_tokens not implemented");
	}

	// returns number of tokens in abstract domain
	public int get_num_tokens() {
		throw new RuntimeException("get_num_tokens not implemented");
	}

	boolean isTop = false;

	public boolean isTop() {
		return isTop;
	}

	public Abstraction abstract_map(int v) {
		throw new RuntimeException("abstract_map not implemented");
	}

	public Abstraction abstract_map(float v) {
		throw new RuntimeException("abstract_map not implemented");
	}

	public Abstraction abstract_map(long v) {
		throw new RuntimeException("abstract_map not implemented");
	}

	public Abstraction abstract_map(double v) {
		throw new RuntimeException("abstract_map not implemented");
	}

	public static Abstraction _add(double v1, Abstraction abs_v1, double v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._plus(abs_v1);
			else
				result = abs_v2._plus(v1);
		} else if (abs_v1 != null)
			result = abs_v1._plus(v2);
		return result;
	}

	public static Abstraction _add(float v1, Abstraction abs_v1, float v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._plus(abs_v1);
			else
				result = abs_v2._plus(v1);
		} else if (abs_v1 != null)
			result = abs_v1._plus(v2);
		return result;
	}

	public static Abstraction _add(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._plus(abs_v1);
			else
				result = abs_v2._plus(v1);
		} else if (abs_v1 != null)
			result = abs_v1._plus(v2);
		return result;
	}

	public static Abstraction _add(long v1, Abstraction abs_v1, long v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._plus(abs_v1);
			else
				result = abs_v2._plus(v1);
		} else if (abs_v1 != null)
			result = abs_v1._plus(v2);
		return result;
	}

	public static Abstraction _and(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._bitwise_and(abs_v1);
			else
				result = abs_v2._bitwise_and(v1);
		} else if (abs_v1 != null)
			result = abs_v1._bitwise_and(v2);
		return result;
	}

	public static Abstraction _and(long v1, Abstraction abs_v1, long v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._bitwise_and(abs_v1);
			else
				result = abs_v2._bitwise_and(v1);
		} else if (abs_v1 != null)
			result = abs_v1._bitwise_and(v2);
		return result;
	}

	public static Abstraction _cmpg(double v1, Abstraction abs_v1, double v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._cmpg(abs_v1);
			else
				result = abs_v2._cmpg(v1);
		} else if (abs_v1 != null)
			result = abs_v1._cmpg_reverse(v2);
		return result;
	}

	public static Abstraction _cmpg(float v1, Abstraction abs_v1, float v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._cmpg(abs_v1);
			else
				result = abs_v2._cmpg(v1);
		} else if (abs_v1 != null)
			result = abs_v1._cmpg_reverse(v2);
		return result;
	}

	public static Abstraction _cmpl(double v1, Abstraction abs_v1, double v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._cmpl(abs_v1);
			else
				result = abs_v2._cmpl(v1);
		} else if (abs_v1 != null)
			result = abs_v1._cmpl_reverse(v2);
		return result;
	}

	public static Abstraction _cmpl(float v1, Abstraction abs_v1, float v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._cmpl(abs_v1);
			else
				result = abs_v2._cmpl(v1);
		} else if (abs_v1 != null)
			result = abs_v1._cmpl_reverse(v2);
		return result;
	}

	public static Abstraction _div(double v1, Abstraction abs_v1, double v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._div(abs_v1);
			else
				result = abs_v2._div(v1);
		} else if (abs_v1 != null)
			result = abs_v1._div_reverse(v2);
		return result;
	}

	public static Abstraction _div(float v1, Abstraction abs_v1, float v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._div(abs_v1);
			else
				result = abs_v2._div(v1);
		} else if (abs_v1 != null)
			result = abs_v1._div_reverse(v2);
		return result;
	}

	public static Abstraction _div(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._div(abs_v1);
			else
				result = abs_v2._div(v1);
		} else if (abs_v1 != null)
			result = abs_v1._div_reverse(v2);
		return result;
	}

	public static Abstraction _div(long v1, Abstraction abs_v1, long v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._div(abs_v1);
			else
				result = abs_v2._div(v1);
		} else if (abs_v1 != null)
			result = abs_v1._div_reverse(v2);
		return result;
	}

	public static AbstractBoolean _eq(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		AbstractBoolean result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._eq(abs_v1);
			else
				result = abs_v2._eq(v1);
		} else if (abs_v1 != null)
			result = abs_v1._eq(v2);
		return result;
	}

	public static AbstractBoolean _ge(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		AbstractBoolean result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._ge(abs_v1);
			else
				result = abs_v2._ge(v1);
		} else if (abs_v1 != null)
			result = abs_v1._ge_reverse(v2);
		return result;
	}

	public static AbstractBoolean _gt(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		AbstractBoolean result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._gt(abs_v1);
			else
				result = abs_v2._gt(v1);
		} else if (abs_v1 != null)
			result = abs_v1._gt_reverse(v2);
		return result;
	}

	public static AbstractBoolean _le(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		AbstractBoolean result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._le(abs_v1);
			else
				result = abs_v2._le(v1);
		} else if (abs_v1 != null)
			result = abs_v1._le_reverse(v2);
		return result;
	}

	public static AbstractBoolean _lt(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		AbstractBoolean result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._lt(abs_v1);
			else
				result = abs_v2._lt(v1);
		} else if (abs_v1 != null)
			result = abs_v1._lt_reverse(v2);
		return result;
	}

	public static Abstraction _mul(double v1, Abstraction abs_v1, double v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._mul(abs_v1);
			else
				result = abs_v2._mul(v1);
		} else if (abs_v1 != null)
			result = abs_v1._mul(v2);
		return result;
	}

	public static Abstraction _mul(float v1, Abstraction abs_v1, float v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._mul(abs_v1);
			else
				result = abs_v2._mul(v1);
		} else if (abs_v1 != null)
			result = abs_v1._mul(v2);
		return result;
	}

	public static Abstraction _mul(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._mul(abs_v1);
			else
				result = abs_v2._mul(v1);
		} else if (abs_v1 != null)
			result = abs_v1._mul(v2);
		return result;
	}

	public static Abstraction _mul(long v1, Abstraction abs_v1, long v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._mul(abs_v1);
			else
				result = abs_v2._mul(v1);
		} else if (abs_v1 != null)
			result = abs_v1._mul(v2);
		return result;
	}

	public static AbstractBoolean _ne(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		AbstractBoolean result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._ne(abs_v1);
			else
				result = abs_v2._ne(v1);
		} else if (abs_v1 != null)
			result = abs_v1._ne(v2);
		return result;
	}

	public static Abstraction _neg(Abstraction abs_v1) {
		if (abs_v1 != null)
			return abs_v1._neg();
		else
			return null;
	}

	public static Abstraction _or(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._bitwise_or(abs_v1);
			else
				result = abs_v2._bitwise_or(v1);
		} else if (abs_v1 != null)
			result = abs_v1._bitwise_or(v2);
		return result;
	}

	public static Abstraction _or(long v1, Abstraction abs_v1, long v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._bitwise_or(abs_v1);
			else
				result = abs_v2._bitwise_or(v1);
		} else if (abs_v1 != null)
			result = abs_v1._bitwise_or(v2);
		return result;
	}

	public static Abstraction _rem(double v1, Abstraction abs_v1, double v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._rem(abs_v1);
			else
				result = abs_v2._rem(v1);
		} else if (abs_v1 != null)
			result = abs_v1._rem_reverse(v2);
		return result;
	}

	public static Abstraction _rem(float v1, Abstraction abs_v1, float v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._rem(abs_v1);
			else
				result = abs_v2._rem(v1);
		} else if (abs_v1 != null)
			result = abs_v1._rem_reverse(v2);
		return result;
	}

	public static Abstraction _rem(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._rem(abs_v1);
			else
				result = abs_v2._rem(v1);
		} else if (abs_v1 != null)
			result = abs_v1._rem_reverse(v2);
		return result;
	}

	public static Abstraction _rem(long v1, Abstraction abs_v1, long v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._rem(abs_v1);
			else
				result = abs_v2._rem(v1);
		} else if (abs_v1 != null)
			result = abs_v1._rem_reverse(v2);
		return result;
	}

	public static Abstraction _shl(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._shift_left(abs_v1);
			else
				result = abs_v2._shift_left(v1);
		} else if (abs_v1 != null)
			result = abs_v1._shift_left_reverse(v2);
		return result;
	}

	public static Abstraction _shl(long v1, Abstraction abs_v1, long v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._shift_left(abs_v1);
			else
				result = abs_v2._shift_left(v1);
		} else if (abs_v1 != null)
			result = abs_v1._shift_left_reverse(v2);
		return result;
	}

	public static Abstraction _shr(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._shift_right(abs_v1);
			else
				result = abs_v2._shift_right(v1);
		} else if (abs_v1 != null)
			result = abs_v1._shift_right_reverse(v2);
		return result;
	}

	public static Abstraction _shr(long v1, Abstraction abs_v1, long v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._shift_right(abs_v1);
			else
				result = abs_v2._shift_right(v1);
		} else if (abs_v1 != null)
			result = abs_v1._shift_right_reverse(v2);
		return result;
	}

	public static Abstraction _sub(double v1, Abstraction abs_v1, double v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._minus(abs_v1);
			else
				result = abs_v2._minus(v1);
		} else if (abs_v1 != null)
			result = abs_v1._minus_reverse(v2);
		return result;
	}

	public static Abstraction _sub(float v1, Abstraction abs_v1, float v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._minus(abs_v1);
			else
				result = abs_v2._minus(v1);
		} else if (abs_v1 != null)
			result = abs_v1._minus_reverse(v2);
		return result;
	}

	public static Abstraction _sub(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._minus(abs_v1);
			else
				result = abs_v2._minus(v1);
		} else if (abs_v1 != null)
			result = abs_v1._minus_reverse(v2);
		return result;
	}

	public static Abstraction _sub(long v1, Abstraction abs_v1, long v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._minus(abs_v1);
			else
				result = abs_v2._minus(v1);
		} else if (abs_v1 != null)
			result = abs_v1._minus_reverse(v2);
		return result;
	}

	public static Abstraction _ushr(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._unsigned_shift_right(abs_v1);
			else
				result = abs_v2._unsigned_shift_right(v1);
		} else if (abs_v1 != null)
			result = abs_v1._unsigned_shift_right_reverse(v2);
		return result;
	}

	public static Abstraction _ushr(long v1, Abstraction abs_v1, long v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._unsigned_shift_right(abs_v1);
			else
				result = abs_v2._unsigned_shift_right(v1);
		} else if (abs_v1 != null)
			result = abs_v1._unsigned_shift_right_reverse(v2);
		return result;
	}

	public static Abstraction _xor(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._bitwise_xor(abs_v1);
			else
				result = abs_v2._bitwise_xor(v1);
		} else if (abs_v1 != null)
			result = abs_v1._bitwise_xor(v2);
		return result;
	}

	public static Abstraction _xor(long v1, Abstraction abs_v1, long v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2._bitwise_xor(abs_v1);
			else
				result = abs_v2._bitwise_xor(v1);
		} else if (abs_v1 != null)
			result = abs_v1._bitwise_xor(v2);
		return result;
	}

	public Abstraction _cmpg(Abstraction right) {
		// TODO: move to particular abstractions
		boolean n = false, z = false, p = false;
		if (this._gt(right) != AbstractBoolean.FALSE)
			p = true;
		if (this._lt(right) != AbstractBoolean.FALSE)
			n = true;
		if (this._gt(right) != AbstractBoolean.TRUE
				&& this._lt(right) != AbstractBoolean.TRUE)
			z = true;
		return Signs.construct_top(n, z, p);
	}

	public Abstraction _cmpg(float right) {
		return this._cmpg(abstract_map(right));
	}

	public Abstraction _cmpg(double right) {
		return this._cmpg(abstract_map(right));
	}

	public Abstraction _cmpl(Abstraction right) {
		// TODO: move to particular abstractions
		boolean n = false, z = false, p = false;
		if (this._gt(right) != AbstractBoolean.FALSE)
			p = true;
		if (this._lt(right) != AbstractBoolean.FALSE)
			n = true;
		if (this._gt(right) != AbstractBoolean.TRUE
				&& this._lt(right) != AbstractBoolean.TRUE)
			z = true;
		return Signs.construct_top(n, z, p);
	}

	public Abstraction _cmpl(float right) {
		return this._cmpl(abstract_map(right));
	}

	public Abstraction _cmpl(double right) {
		return this._cmpl(abstract_map(right));
	}

	public Abstraction _bitwise_and(Abstraction right) {
		throw new RuntimeException("bitwise and not implemented");
	}

	public Abstraction _bitwise_and(int right) {
		throw new RuntimeException("bitwise and not implemented");
	}

	public Abstraction _bitwise_and(long right) {
		throw new RuntimeException("bitwise and not implemented");
	}

	public Abstraction _bitwise_or(Abstraction right) {
		throw new RuntimeException("bitwise or not implemented");
	}

	public Abstraction _bitwise_or(int right) {
		throw new RuntimeException("bitwise or not implemented");
	}

	public Abstraction _bitwise_or(long right) {
		throw new RuntimeException("bitwise or not implemented");
	}

	public Abstraction _bitwise_xor(Abstraction right) {
		throw new RuntimeException("bitwise xor not implemented");
	}

	public Abstraction _bitwise_xor(int right) {
		throw new RuntimeException("bitwise xor not implemented");
	}

	public Abstraction _bitwise_xor(long right) {
		throw new RuntimeException("bitwise xor not implemented");
	}

	protected Abstraction _cmpg_reverse(double right) {
		throw new RuntimeException("cmpg not implemented");
	}

	protected Abstraction _cmpg_reverse(float right) {
		throw new RuntimeException("cmpg not implemented");
	}

	protected Abstraction _cmpl_reverse(double right) {
		throw new RuntimeException("cmpl not implemented");
	}

	protected Abstraction _cmpl_reverse(float right) {
		throw new RuntimeException("cmpl not implemented");
	}

	public Abstraction _div(Abstraction right) {
		throw new RuntimeException("div not implemented");
	}

	public Abstraction _div(double right) {
		throw new RuntimeException("div not implemented");
	}

	public Abstraction _div(float right) {
		throw new RuntimeException("div not implemented");
	}

	public Abstraction _div(int right) {
		throw new RuntimeException("div not implemented");
	}

	public Abstraction _div(long right) {
		throw new RuntimeException("div not implemented");
	}

	protected Abstraction _div_reverse(double right) {
		throw new RuntimeException("div not implemented");
	}

	protected Abstraction _div_reverse(float right) {
		throw new RuntimeException("div not implemented");
	}

	protected Abstraction _div_reverse(int right) {
		throw new RuntimeException("div not implemented");
	}

	protected Abstraction _div_reverse(long right) {
		throw new RuntimeException("div not implemented");
	}

	public AbstractBoolean _eq(Abstraction right) {
		throw new RuntimeException("ge not implemented");
	}

	public AbstractBoolean _eq(int right) {
		throw new RuntimeException("ge not implemented");
	}

	public AbstractBoolean _ge(Abstraction right) {
		throw new RuntimeException("ge not implemented");
	}

	public AbstractBoolean _ge(int right) {
		throw new RuntimeException("ge not implemented");
	}

	protected AbstractBoolean _ge_reverse(int right) {
		throw new RuntimeException("ge not implemented");
	}

	public AbstractBoolean _gt(Abstraction right) {
		throw new RuntimeException("gt not implemented");
	}

	public AbstractBoolean _gt(int right) {
		throw new RuntimeException("gt not implemented");
	}

	protected AbstractBoolean _gt_reverse(int right) {
		throw new RuntimeException("gt not implemented");
	}

	public AbstractBoolean _le(Abstraction right) {
		throw new RuntimeException("le not implemented");
	}

	public AbstractBoolean _le(int right) {
		throw new RuntimeException("le not implemented");
	}

	protected AbstractBoolean _le_reverse(int right) {
		throw new RuntimeException("le not implemented");
	}

	public AbstractBoolean _lt(Abstraction right) {
		throw new RuntimeException("lt not implemented");
	}

	public AbstractBoolean _lt(int right) {
		throw new RuntimeException("lt not implemented");
	}

	protected AbstractBoolean _lt_reverse(int right) {
		throw new RuntimeException("lt not implemented");
	}

	public Abstraction _minus(Abstraction right) {
		throw new RuntimeException("minus not implemented");
	}

	public Abstraction _minus(double right) {
		throw new RuntimeException("minus not implemented");
	}

	public Abstraction _minus(float right) {
		throw new RuntimeException("minus not implemented");
	}

	public Abstraction _minus(int right) {
		throw new RuntimeException("minus not implemented");
	}

	public Abstraction _minus(long right) {
		throw new RuntimeException("minus not implemented");
	}

	protected Abstraction _minus_reverse(double right) {
		throw new RuntimeException("minus not implemented");
	}

	protected Abstraction _minus_reverse(float right) {
		throw new RuntimeException("minus not implemented");
	}

	protected Abstraction _minus_reverse(int right) {
		throw new RuntimeException("minus not implemented");
	}

	protected Abstraction _minus_reverse(long right) {
		throw new RuntimeException("minus not implemented");
	}

	public Abstraction _mul(Abstraction right) {
		throw new RuntimeException("mul not implemented");
	}

	public Abstraction _mul(double right) {
		throw new RuntimeException("mul not implemented");
	}

	public Abstraction _mul(float right) {
		throw new RuntimeException("mul not implemented");
	}

	public Abstraction _mul(int right) {
		throw new RuntimeException("mul not implemented");
	}

	public Abstraction _mul(long right) {
		throw new RuntimeException("mul not implemented");
	}

	public AbstractBoolean _ne(Abstraction right) {
		throw new RuntimeException("ge not implemented");
	}

	public AbstractBoolean _ne(int right) {
		throw new RuntimeException("ge not implemented");
	}

	public Abstraction _neg() {
		throw new RuntimeException("negation not implemented");
	}

	public Abstraction _plus(Abstraction right) {
		throw new RuntimeException("plus not implemented");
	}

	public Abstraction _plus(double right) {
		throw new RuntimeException("plus not implemented");
	}

	public Abstraction _plus(float right) {
		throw new RuntimeException("plus not implemented");
	}

	public Abstraction _plus(int right) {
		throw new RuntimeException("plus not implemented");
	}

	public Abstraction _plus(long right) {
		throw new RuntimeException("plus not implemented");
	}

	public Abstraction _rem(Abstraction right) {
		throw new RuntimeException("rem not implemented");
	}

	public Abstraction _rem(double right) {
		throw new RuntimeException("rem not implemented");
	}

	public Abstraction _rem(float right) {
		throw new RuntimeException("rem not implemented");
	}

	public Abstraction _rem(int right) {
		throw new RuntimeException("rem not implemented");
	}

	public Abstraction _rem(long right) {
		throw new RuntimeException("rem not implemented");
	}

	protected Abstraction _rem_reverse(double right) {
		throw new RuntimeException("rem not implemented");
	}

	protected Abstraction _rem_reverse(float right) {
		throw new RuntimeException("rem not implemented");
	}

	protected Abstraction _rem_reverse(int right) {
		throw new RuntimeException("rem not implemented");
	}

	protected Abstraction _rem_reverse(long right) {
		throw new RuntimeException("rem not implemented");
	}

	public Abstraction _shift_left(Abstraction right) {
		throw new RuntimeException("shift left not implemented");
	}

	public Abstraction _shift_left(int right) {
		throw new RuntimeException("shift left not implemented");
	}

	public Abstraction _shift_left(long right) {
		throw new RuntimeException("shift left not implemented");
	}

	protected Abstraction _shift_left_reverse(int right) {
		throw new RuntimeException("shift left not implemented");
	}

	protected Abstraction _shift_left_reverse(long right) {
		throw new RuntimeException("shift left not implemented");
	}

	public Abstraction _shift_right(Abstraction right) {
		throw new RuntimeException("shift right not implemented");
	}

	public Abstraction _shift_right(int right) {
		throw new RuntimeException("shift right not implemented");
	}

	public Abstraction _shift_right(long right) {
		throw new RuntimeException("shift right not implemented");
	}

	protected Abstraction _shift_right_reverse(int right) {
		throw new RuntimeException("shift right not implemented");
	}

	protected Abstraction _shift_right_reverse(long right) {
		throw new RuntimeException("shift right not implemented");
	}

	public Abstraction _unsigned_shift_right(Abstraction right) {
		throw new RuntimeException("shift right not implemented");
	}

	public Abstraction _unsigned_shift_right(int right) {
		throw new RuntimeException("shift right not implemented");
	}

	public Abstraction _unsigned_shift_right(long right) {
		throw new RuntimeException("shift right not implemented");
	}

	protected Abstraction _unsigned_shift_right_reverse(int right) {
		throw new RuntimeException("unsigned shift right not implemented");
	}

	protected Abstraction _unsigned_shift_right_reverse(long right) {
		throw new RuntimeException("shift right not implemented");
	}

	public boolean equals(Abstraction abs) {
		if (!getClass().getName().equals(abs.getClass().getName()))
			throw new RuntimeException("Comparing different abstractions");
		return (getClass().getName().equals(abs.getClass().getName()))
				&& (this.get_key() == abs.get_key());
	}

}
