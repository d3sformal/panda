package gov.nasa.jpf.abstraction.numeric;



import java.util.Set;

public class Abstraction  {

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
	
	public Abstraction abstract_map(double v) {
		throw new RuntimeException("abstract_map not implemented");
	}
	
	public static Abstraction _add(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._plus(abs_v2);
			else
				result = abs_v1._plus(v2);
		} else if (abs_v2 != null)
			result = abs_v2._plus(v1);
		return result;
	}
	
	public static Abstraction _add(long v1, Abstraction abs_v1, long v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._plus(abs_v2);
			else
				result = abs_v1._plus(v2);
		} else if (abs_v2 != null)
			result = abs_v2._plus(v1);
		return result;
	}	

	public static Abstraction _add(float v1, Abstraction abs_v1, float v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._plus(abs_v2);
			else
				result = abs_v1._plus(v2);
		} else if (abs_v2 != null)
			result = abs_v2._plus(v1);
		return result;
	}

	public static Abstraction _add(double v1, Abstraction abs_v1, double v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._plus(abs_v2);
			else
				result = abs_v1._plus(v2);
		} else if (abs_v2 != null)
			result = abs_v2._plus(v1);
		return result;
	}	
	
	public static Abstraction _mul(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._mul(abs_v2);
			else
				result = abs_v1._mul(v2);
		} else if (abs_v2 != null)
			result = abs_v2._mul(v1);
		return result;
	}
	
	public static Abstraction _mul(long v1, Abstraction abs_v1, long v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._mul(abs_v2);
			else
				result = abs_v1._mul(v2);
		} else if (abs_v2 != null)
			result = abs_v2._mul(v1);
		return result;
	}	

	public static Abstraction _mul(float v1, Abstraction abs_v1, float v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._mul(abs_v2);
			else
				result = abs_v1._mul(v2);
		} else if (abs_v2 != null)
			result = abs_v2._mul(v1);
		return result;
	}	
	
	public static Abstraction _mul(double v1, Abstraction abs_v1, double v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._mul(abs_v2);
			else
				result = abs_v1._mul(v2);
		} else if (abs_v2 != null)
			result = abs_v2._mul(v1);
		return result;
	}		
	
	public static Abstraction _div(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._div(abs_v2);
			else
				result = abs_v1._div(v2);
		} else if (abs_v2 != null)
			result = abs_v2._div(v1);
		return result;
	}

	public static Abstraction _div(long v1, Abstraction abs_v1, long v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._div(abs_v2);
			else
				result = abs_v1._div(v2);
		} else if (abs_v2 != null)
			result = abs_v2._div(v1);
		return result;
	}	
	
	public static Abstraction _div(float v1, Abstraction abs_v1, float v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._div(abs_v2);
			else
				result = abs_v1._div(v2);
		} else if (abs_v2 != null)
			result = abs_v2._div(v1);
		return result;
	}	

	public static Abstraction _div(double v1, Abstraction abs_v1, double v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._div(abs_v2);
			else
				result = abs_v1._div(v2);
		} else if (abs_v2 != null)
			result = abs_v2._div(v1);
		return result;
	}		
	
	public static Abstraction _rem(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._rem(abs_v2);
			else
				result = abs_v1._rem(v2);
		} else if (abs_v2 != null)
			result = abs_v2._rem(v1);
		return result;
	}	
	
	public static Abstraction _rem(long v1, Abstraction abs_v1, long v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._rem(abs_v2);
			else
				result = abs_v1._rem(v2);
		} else if (abs_v2 != null)
			result = abs_v2._rem(v1);
		return result;
	}		

	public static Abstraction _rem(float v1, Abstraction abs_v1, float v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._rem(abs_v2);
			else
				result = abs_v1._rem(v2);
		} else if (abs_v2 != null)
			result = abs_v2._rem(v1);
		return result;
	}		
	
	public static Abstraction _rem(double v1, Abstraction abs_v1, double v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._rem(abs_v2);
			else
				result = abs_v1._rem(v2);
		} else if (abs_v2 != null)
			result = abs_v2._rem(v1);
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

	public static Abstraction _neg(Abstraction abs_v1) {
		if (abs_v1 != null)
			return abs_v1._neg();
		else
			return null;
	}

	public static Abstraction _and(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._bitwise_and(abs_v2);
			else
				result = abs_v1._bitwise_and(v2);
		} else if (abs_v2 != null)
			result = abs_v2._bitwise_and(v1);
		return result;
	}
	
	public static Abstraction _and(long v1, Abstraction abs_v1, long v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._bitwise_and(abs_v2);
			else
				result = abs_v1._bitwise_and(v2);
		} else if (abs_v2 != null)
			result = abs_v2._bitwise_and(v1);
		return result;
	}	
	
	public static Abstraction _or(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._bitwise_or(abs_v2);
			else
				result = abs_v1._bitwise_or(v2);
		} else if (abs_v2 != null)
			result = abs_v2._bitwise_or(v1);
		return result;
	}
	
	public static Abstraction _or(long v1, Abstraction abs_v1, long v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._bitwise_or(abs_v2);
			else
				result = abs_v1._bitwise_or(v2);
		} else if (abs_v2 != null)
			result = abs_v2._bitwise_or(v1);
		return result;
	}		
	
	public static Abstraction _xor(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._bitwise_xor(abs_v2);
			else
				result = abs_v1._bitwise_xor(v2);
		} else if (abs_v2 != null)
			result = abs_v2._bitwise_xor(v1);
		return result;
	}		

	public static Abstraction _xor(long v1, Abstraction abs_v1, long v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._bitwise_xor(abs_v2);
			else
				result = abs_v1._bitwise_xor(v2);
		} else if (abs_v2 != null)
			result = abs_v2._bitwise_xor(v1);
		return result;
	}			
	
	public static Abstraction _shl(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._shift_left(abs_v2);
			else
				result = abs_v1._shift_left(v2);
		} else if (abs_v2 != null)
			result = abs_v2._shift_left(v1);
		return result;
	}	

	public static Abstraction _shl(long v1, Abstraction abs_v1, long v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._shift_left(abs_v2);
			else
				result = abs_v1._shift_left(v2);
		} else if (abs_v2 != null)
			result = abs_v2._shift_left(v1);
		return result;
	}		
	
	public static Abstraction _shr(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._shift_right(abs_v2);
			else
				result = abs_v1._shift_right(v2);
		} else if (abs_v2 != null)
			result = abs_v2._shift_right(v1);
		return result;
	}		

	public static Abstraction _shr(long v1, Abstraction abs_v1, long v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._shift_right(abs_v2);
			else
				result = abs_v1._shift_right(v2);
		} else if (abs_v2 != null)
			result = abs_v2._shift_right(v1);
		return result;
	}			
	
	public static Abstraction _ushr(int v1, Abstraction abs_v1, int v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._unsigned_shift_right(abs_v2);
			else
				result = abs_v1._unsigned_shift_right(v2);
		} else if (abs_v2 != null)
			result = abs_v2._unsigned_shift_right(v1);
		return result;
	}
	
	public static Abstraction _ushr(long v1, Abstraction abs_v1, long v2,
			Abstraction abs_v2) {
		Abstraction result = null;
		if (abs_v1 != null) {
			if (abs_v2 != null)
				result = abs_v1._unsigned_shift_right(abs_v2);
			else
				result = abs_v1._unsigned_shift_right(v2);
		} else if (abs_v2 != null)
			result = abs_v2._unsigned_shift_right(v1);
		return result;
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
	
	public Abstraction _shift_left(Abstraction right) {
		throw new RuntimeException("shift left not implemented");
	}

	public Abstraction _shift_left(int right) {
		throw new RuntimeException("shift left not implemented");
	}		

	public Abstraction _shift_left(long right) {
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
	
	public Abstraction _unsigned_shift_right(Abstraction right) {
		throw new RuntimeException("shift right not implemented");
	}

	public Abstraction _unsigned_shift_right(int right) {
		throw new RuntimeException("shift right not implemented");
	}				

	public Abstraction _unsigned_shift_right(long right) {
		throw new RuntimeException("shift right not implemented");
	}					
	
	public Abstraction _plus(Abstraction right) {
		throw new RuntimeException("plus not implemented");
	}

	public Abstraction _plus(int right) {
		throw new RuntimeException("plus not implemented");
	}
	
	public Abstraction _plus(long right) {
		throw new RuntimeException("plus not implemented");
	}	

	public Abstraction _plus(float right) {
		throw new RuntimeException("plus not implemented");
	}	

	public Abstraction _plus(double right) {
		throw new RuntimeException("plus not implemented");
	}	
	
	
	public Abstraction _minus(Abstraction right) {
		throw new RuntimeException("minus not implemented");
	}

	public Abstraction _minus(int right) {
		throw new RuntimeException("minus not implemented");
	}

	public Abstraction _minus(long right) {
		throw new RuntimeException("minus not implemented");
	}

	public Abstraction _minus(float right) {
		throw new RuntimeException("minus not implemented");
	}

	public Abstraction _minus(double right) {
		throw new RuntimeException("minus not implemented");
	}
	
	
	public Abstraction _minus_reverse(int right) {
		throw new RuntimeException("minus_reverse not implemented");
	}	
	
	public Abstraction _minus_reverse(long right) {
		throw new RuntimeException("minus_reverse not implemented");
	}	
	
	public Abstraction _minus_reverse(float right) {
		throw new RuntimeException("minus_reverse not implemented");
	}	
	
	public Abstraction _minus_reverse(double right) {
		throw new RuntimeException("minus_reverse not implemented");
	}	
	
	public Abstraction _neg() {
		throw new RuntimeException("negation not implemented");
	}

	public Abstraction _mul(Abstraction right) {
		throw new RuntimeException("mul not implemented");
	}

	public Abstraction _mul(int right) {
		throw new RuntimeException("mul not implemented");
	}

	public Abstraction _mul(long right) {
		throw new RuntimeException("mul not implemented");
	}	
	
	public Abstraction _mul(float right) {
		throw new RuntimeException("mul not implemented");
	}	
	
	public Abstraction _mul(double right) {
		throw new RuntimeException("mul not implemented");
	}	
	
	public Abstraction _div(Abstraction right) {
		throw new RuntimeException("div not implemented");
	}

	public Abstraction _div(int right) {
		throw new RuntimeException("div not implemented");
	}
	
	public Abstraction _div(long right) {
		throw new RuntimeException("div not implemented");
	}	

	public Abstraction _div(float right) {
		throw new RuntimeException("div not implemented");
	}	

	public Abstraction _div(double right) {
		throw new RuntimeException("div not implemented");
	}	

	public Abstraction _rem(Abstraction right) {
		throw new RuntimeException("rem not implemented");
	}

	public Abstraction _rem(int right) {
		throw new RuntimeException("rem not implemented");
	}
	
	public Abstraction _rem(long right) {
		throw new RuntimeException("rem not implemented");
	}	

	public Abstraction _rem(float right) {
		throw new RuntimeException("rem not implemented");
	}	

	public Abstraction _rem(double right) {
		throw new RuntimeException("rem not implemented");
	}	

	public AbstractBoolean _lt(Abstraction right) {
		throw new RuntimeException("lt not implemented");
	}

	public AbstractBoolean _lt(int right) {
		throw new RuntimeException("lt not implemented");
	}

	public AbstractBoolean _le(Abstraction right) {
		throw new RuntimeException("le not implemented");
	}

	public AbstractBoolean _le(int right) {
		throw new RuntimeException("le not implemented");
	}

	public AbstractBoolean _gt(Abstraction right) {
		throw new RuntimeException("gt not implemented");
	}

	public AbstractBoolean _gt(int right) {
		throw new RuntimeException("gt not implemented");
	}

	public AbstractBoolean _ge(Abstraction right) {
		throw new RuntimeException("ge not implemented");
	}

	public AbstractBoolean _ge(int right) {
		throw new RuntimeException("ge not implemented");
	}
}
