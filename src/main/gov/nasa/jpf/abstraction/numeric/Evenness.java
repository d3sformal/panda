//Copyright (C) 2012 United States Government as represented by the
//Administrator of the National Aeronautics and Space Administration
//(NASA).  All Rights Reserved.

//This software is distributed under the NASA Open Source Agreement
//(NOSA), version 1.3.  The NOSA has been approved by the Open Source
//Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
//directory tree for the complete NOSA document.

//THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
//KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
//LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
//SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
//A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
//THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
//DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.

package gov.nasa.jpf.abstraction.numeric;

import java.util.HashSet;
import java.util.Set;

public class Evenness extends Abstraction {

	public static Evenness EVEN = new Evenness(0);
	public static Evenness ODD  = new Evenness(1);
	public static Evenness TOP = new Evenness(true, 2);

	public boolean could_be_ODD() {
		return get_key() != 0;
	}

	public boolean could_be_EVEN() {
		return get_key() != 1;
	}

	@Override
	public Set<Abstraction> get_tokens() {
		Set<Abstraction> tokens = new HashSet<Abstraction>();
		if (could_be_EVEN())
			tokens.add(EVEN);
		if (could_be_ODD())
			tokens.add(ODD);
		return tokens;
	}

	// returns possible tokens from TOP in order {EVEN, ODD}
	@Override
	public Abstraction get_token(int key) {
		int num = get_num_tokens();
		if (key < 0 || key >= num)
			throw new RuntimeException("Wrong TOP token");
		if (could_be_EVEN())
			return (key == 0)? EVEN : ODD;
		else
			return ODD;
	}

	@Override
	public int get_num_tokens() {
		int result = 0;
		if (could_be_EVEN())
			++result;
		if (could_be_ODD())
			++result;
		return result;
	}

	public Evenness(int key) {
		set_key(key);
	}

	public Evenness(boolean isTop, int key) {
		this(key);
		this.isTop = isTop;
	}

	private Evenness construct_top(boolean isEven, boolean isOdd) {
		if (isEven)
			if (isOdd)
				return TOP;
			else
				return EVEN;
		else
			if (isOdd)
				return ODD;
			else
				throw new RuntimeException("Abstraction is out of range");
	}

	@Override
	public Evenness abstract_map(int v) {
		if (v % 2 == 0)
			return EVEN;
		else
			return ODD;
	}

	@Override
	public Evenness abstract_map(long v) {
		if (v % 2 == 0)
			return EVEN;
		else
			return ODD;
	}

	@Override
	public Abstraction _plus(int right) {
		if (right == 1 || right == -1)
			return construct_top(could_be_ODD(), could_be_EVEN());
		else
			return _plus(abstract_map(right));
	}

	@Override
	public Abstraction _plus(long right) {
		if (right == 1 || right == -1)
			return construct_top(could_be_ODD(), could_be_EVEN());
		else
			return _plus(abstract_map(right));
	}
	
	@Override
	public Abstraction _plus(Abstraction right) {
		if (right instanceof Evenness) {			
			Evenness right_value = (Evenness) right;	
			boolean o = false, e = false;			
			if (this.could_be_EVEN() && right_value.could_be_EVEN())
				e = true;
			if (this.could_be_EVEN() && right_value.could_be_ODD())
				o = true;		
			if (this.could_be_ODD() && right_value.could_be_EVEN())
				o = true;
			if (this.could_be_ODD() && right_value.could_be_ODD())
				e = true;			
			return construct_top(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _minus(Abstraction right) {
		if (right instanceof Evenness) {			
			Evenness right_value = (Evenness) right;	
			boolean o = false, e = false;			
			if (this.could_be_EVEN() && right_value.could_be_EVEN())
				e = true;
			if (this.could_be_EVEN() && right_value.could_be_ODD())
				o = true;		
			if (this.could_be_ODD() && right_value.could_be_EVEN())
				o = true;
			if (this.could_be_ODD() && right_value.could_be_ODD())
				e = true;			
			return construct_top(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _minus(int right) {
		return _minus(abstract_map(right));
	}

	@Override
	public Abstraction _minus(long right) {
		return _minus(abstract_map(right));
	}

	@Override
	public Abstraction _minus_reverse(int right) {
		return abstract_map(right)._minus(this);
	}

	@Override
	public Abstraction _minus_reverse(long right) {
		return abstract_map(right)._minus(this);
	}
	
	@Override
	public Abstraction _mul(int right) {
		return _mul(abstract_map(right));
	}

	@Override
	public Abstraction _mul(long right) {
		return _mul(abstract_map(right));
	}
	
	@Override
	public Abstraction _mul(Abstraction right) {
		if (right instanceof Evenness) {			
			Evenness right_value = (Evenness) right;	
			boolean o = false, e = false;			
			if (this.could_be_EVEN() && right_value.could_be_EVEN())
				e = true;
			if (this.could_be_EVEN() && right_value.could_be_ODD())
				e = true;		
			if (this.could_be_ODD() && right_value.could_be_EVEN())
				e = true;
			if (this.could_be_ODD() && right_value.could_be_ODD())
				o = true;			
			return construct_top(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _div(int right) {
		return _div(abstract_map(right));
	}

	@Override
	public Abstraction _div(long right) {
		return _div(abstract_map(right));
	}

	@Override
	public Abstraction _div(Abstraction right) {
		if (right instanceof Evenness)	
			return TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _rem(int right) {
		return _rem(abstract_map(right));
	}

	@Override
	public Abstraction _rem(long right) {
		return _rem(abstract_map(right));
	}

	@Override
	public Abstraction _rem(Abstraction right) {
		if (right instanceof Evenness) {			
			Evenness right_value = (Evenness) right;	
			boolean o = false, e = false;			
			if (this.could_be_EVEN() && right_value.could_be_EVEN())
				e = true;
			if (this.could_be_EVEN() && right_value.could_be_ODD())
				e = o = true;		
			if (this.could_be_ODD() && right_value.could_be_EVEN())
				o = true;
			if (this.could_be_ODD() && right_value.could_be_ODD())
				e = o = true;			
			return construct_top(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _bitwise_and(int right) {
		return _bitwise_and(abstract_map(right));
	}

	@Override
	public Abstraction _bitwise_and(long right) {
		return _bitwise_and(abstract_map(right));
	}

	@Override
	public Abstraction _bitwise_and(Abstraction right) {
		if (right instanceof Evenness) {			
			Evenness right_value = (Evenness) right;	
			boolean o = false, e = false;			
			if (this.could_be_EVEN() && right_value.could_be_EVEN())
				e = true;
			if (this.could_be_EVEN() && right_value.could_be_ODD())
				e = true;		
			if (this.could_be_ODD() && right_value.could_be_EVEN())
				e = true;
			if (this.could_be_ODD() && right_value.could_be_ODD())
				o = true;			
			return construct_top(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _bitwise_or(int right) {
		return _bitwise_or(abstract_map(right));
	}

	@Override
	public Abstraction _bitwise_or(long right) {
		return _bitwise_or(abstract_map(right));
	}

	@Override
	public Abstraction _bitwise_or(Abstraction right) {
		if (right instanceof Evenness) {			
			Evenness right_value = (Evenness) right;	
			boolean o = false, e = false;			
			if (this.could_be_EVEN() && right_value.could_be_EVEN())
				e = true;
			if (this.could_be_EVEN() && right_value.could_be_ODD())
				o = true;		
			if (this.could_be_ODD() && right_value.could_be_EVEN())
				o = true;
			if (this.could_be_ODD() && right_value.could_be_ODD())
				o = true;			
			return construct_top(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");	
	}

	@Override
	public Abstraction _bitwise_xor(int right) {
		return _bitwise_xor(abstract_map(right));
	}
	
	@Override
	public Abstraction _bitwise_xor(long right) {
		return _bitwise_xor(abstract_map(right));
	}	

	@Override
	public Abstraction _bitwise_xor(Abstraction right) {
		if (right instanceof Evenness) {			
			Evenness right_value = (Evenness) right;	
			boolean o = false, e = false;			
			if (this.could_be_EVEN() && right_value.could_be_EVEN())
				e = true;
			if (this.could_be_EVEN() && right_value.could_be_ODD())
				o = true;		
			if (this.could_be_ODD() && right_value.could_be_EVEN())
				o = true;
			if (this.could_be_ODD() && right_value.could_be_ODD())
				e = true;			
			return construct_top(e, o);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _shift_left(int right) {
		return _shift_left(abstract_map(right));
	}

	@Override
	public Abstraction _shift_left(long right) {
		return _shift_left(abstract_map(right));
	}

	@Override
	public Abstraction _shift_left(Abstraction right) {
		if (right instanceof Evenness) {			
			Evenness right_value = (Evenness) right;	
			return construct_top(true, this.could_be_ODD() && right_value.could_be_ODD());
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _shift_right(int right) {
		return _shift_right(abstract_map(right));
	}

	@Override
	public Abstraction _shift_right(long right) {
		return _shift_right(abstract_map(right));
	}
 
	@Override
	public Abstraction _shift_right(Abstraction right) {
		if (right instanceof Evenness)			
			return TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _unsigned_shift_right(int right) {
		return _unsigned_shift_right(abstract_map(right));
	}

	@Override
	public Abstraction _unsigned_shift_right(long right) {
		return _unsigned_shift_right(abstract_map(right));
	}

	@Override
	public Abstraction _unsigned_shift_right(Abstraction right) {
		// sign does not matter
		return _shift_right(right);
	}

	@Override
	public Abstraction _neg() {
		return this;
	}

	@Override
	public AbstractBoolean _ge(Abstraction right) {
		if (right instanceof Evenness)
			return AbstractBoolean.TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _ge(int right) {
		return _ge(abstract_map(right));
	}

	@Override
	public AbstractBoolean _gt(Abstraction right) {
		if (right instanceof Evenness)
			return AbstractBoolean.TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _gt(int right) {
		return _gt(abstract_map(right));
	}

	@Override
	public AbstractBoolean _le(Abstraction right) {
		if (right instanceof Evenness)
			return AbstractBoolean.TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _le(int right) {
		return _le(abstract_map(right));
	}

	@Override
	public AbstractBoolean _lt(Abstraction right) {
		if (right instanceof Evenness)
			return AbstractBoolean.TOP;
		else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _lt(int right) {
		return _lt(abstract_map(right));
	}

	public AbstractBoolean _eq(Abstraction right) {
		if (right instanceof Evenness) {
			Evenness right_value = (Evenness) right;
			boolean t = (this.could_be_EVEN() && right_value.could_be_EVEN()) ||
					(this.could_be_ODD() && right_value.could_be_ODD());
			boolean f = (this.could_be_EVEN() && right_value.could_be_ODD()) ||
					(this.could_be_ODD() && right_value.could_be_EVEN());
			assert t || f;
			if (t & f)
				return AbstractBoolean.TOP;
			else if (t)
				return AbstractBoolean.TRUE;
			else
				return AbstractBoolean.TRUE;
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}	
	
	public AbstractBoolean _eq(int right) {
		return _eq(abstract_map(right));
	}		
	
	public AbstractBoolean _ne(Abstraction right) {
		return _eq(right).not();
	}

	@Override
	public AbstractBoolean _ne(int right) {
		return _ne(abstract_map(right));
	}

	@Override
	protected Abstraction _div_reverse(int right) {
		return abstract_map(right)._div(this);
	}

	@Override
	protected Abstraction _div_reverse(long right) {
		return abstract_map(right)._div(this);
	}

	@Override
	protected Abstraction _div_reverse(float right) {
		return abstract_map(right)._div(this);
	}

	@Override
	protected Abstraction _div_reverse(double right) {
		return abstract_map(right)._div(this);
	}

	@Override
	protected Abstraction _cmpl_reverse(float right) {
		return abstract_map(right)._cmpl(this);
	}

	@Override
	protected Abstraction _cmpl_reverse(double right) {
		return abstract_map(right)._cmpl(this);
	}

	@Override
	protected Abstraction _cmpg_reverse(float right) {
		return abstract_map(right)._cmpg(this);
	}

	@Override
	protected Abstraction _cmpg_reverse(double right) {
		return abstract_map(right)._cmpg(this);
	}

	@Override
	protected Abstraction _rem_reverse(int right) {
		return abstract_map(right)._rem(this);
	}

	@Override
	protected Abstraction _rem_reverse(long right) {
		return abstract_map(right)._rem(this);
	}

	@Override
	protected Abstraction _rem_reverse(float right) {
		return abstract_map(right)._rem(this);
	}

	@Override
	protected Abstraction _rem_reverse(double right) {
		return abstract_map(right)._rem(this);
	}

	@Override
	protected Abstraction _shift_left_reverse(int right) {
		return abstract_map(right)._shift_left(this);
	}

	@Override
	protected Abstraction _shift_left_reverse(long right) {
		return abstract_map(right)._shift_left(this);
	}

	@Override
	protected Abstraction _shift_right_reverse(int right) {
		return abstract_map(right)._shift_right(this);
	}

	@Override
	protected Abstraction _shift_right_reverse(long right) {
		return abstract_map(right)._shift_right(this);
	}

	@Override
	protected Abstraction _unsigned_shift_right_reverse(int right) {
		return abstract_map(right)._unsigned_shift_right(this);
	}

	@Override
	protected Abstraction _unsigned_shift_right_reverse(long right) {
		return abstract_map(right)._unsigned_shift_right(this);
	}

	@Override
	protected AbstractBoolean _lt_reverse(int right) {
		return abstract_map(right)._lt(this);
	}

	@Override
	protected AbstractBoolean _le_reverse(int right) {
		return abstract_map(right)._le(this);
	}

	@Override
	protected AbstractBoolean _ge_reverse(int right) {
		return abstract_map(right)._ge(this);
	}

	@Override
	protected AbstractBoolean _gt_reverse(int right) {
		return abstract_map(right)._gt(this);
	}
	
	public String toString() {
		if (this instanceof Evenness) {
			if (this == EVEN)
				return "EVEN";
			if (this == ODD)
				return "ODD";
			if (this.isTop())
				return "TOP";
		} else
			throw new RuntimeException("## Error: unknown abstraction");
		return "";
	}
		
}
