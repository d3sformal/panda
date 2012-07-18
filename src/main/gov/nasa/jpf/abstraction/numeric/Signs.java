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

public class Signs extends Abstraction {
	public static Signs NEG = new Signs(0);
	public static Signs ZERO = new Signs(1);
	public static Signs POS = new Signs(2);
	public static Signs NON_NEG = new Signs(true, 3);
	public static Signs NON_ZERO = new Signs(true, 4);
	public static Signs NON_POS = new Signs(true, 5);
	public static Signs TOP = new Signs(true, 6);

	public boolean could_be_POS() {
		int key = this.get_key();
		return key == 2 || key == 3 || key == 4 || key == 6;
	}

	public boolean could_be_NEG() {
		int key = this.get_key();
		return key == 0 || key == 4 || key == 5 || key == 6;
	}

	public boolean could_be_ZERO() {
		int key = this.get_key();
		return key == 1 || key == 3 || key == 5 || key == 6;
	}

	@Override
	public Set<Abstraction> get_tokens() {
		Set<Abstraction> tokens = new HashSet<Abstraction>();
		if (could_be_NEG())
			tokens.add(NEG);
		if (could_be_ZERO())
			tokens.add(ZERO);
		if (could_be_POS())
			tokens.add(POS);
		return tokens;
	}

	// returns possible tokens from TOP in order {NEG, ZERO, POS}
	@Override
	public Abstraction get_token(int key) {
		int num = get_num_tokens();
		if (key < 0 || key >= num)
			throw new RuntimeException("Wrong TOP token");
		if (could_be_NEG())
			if (key == 0)
				return NEG;
			else 
				if (could_be_ZERO())
					return (key == 1)? ZERO : POS;
				else 
					return POS;
		else if (could_be_ZERO())
			return (key == 0)? ZERO : POS;
		else
			return POS;
	}

	@Override
	public int get_num_tokens() {
		int result = 0;
		if (could_be_POS())
			++result;
		if (could_be_NEG())
			++result;
		if (could_be_ZERO())
			++result;
		return result;
	}

	public Signs(int key) {
		set_key(key);
	}

	public Signs(boolean isTop, int key) {
		this(key);
		this.isTop = isTop;
	}

	private Signs construct_top(boolean isNeg, boolean isZero, boolean isPos) {
		if (isNeg)
			if (isZero)
				if (isPos)
					return TOP;
				else
					return NON_POS;
			else if (isPos)
				return NON_ZERO;
			else
				return NEG;
		else if (isZero)
			if (isPos)
				return NON_NEG;
			else
				return ZERO;
		else if (isPos)
			return POS;
		else
			throw new RuntimeException("Abstraction is out of range");
	}

	@Override
	public Signs abstract_map(int v) {
		if (v > 0)
			return POS;
		if (v == 0)
			return ZERO;
		// if (v < 0)
		return NEG;
	}

	@Override
	public Signs abstract_map(long v) {
		if (v > 0)
			return POS;
		if (v == 0)
			return ZERO;
		// if (v < 0)
		return NEG;
	}

	@Override
	public Signs abstract_map(float v) {
		if (v > 0)
			return POS;
		if (v == 0)
			return ZERO;
		if (v < 0)
			return NEG;
		// NaN or INF
		return null;
	}

	@Override
	public Signs abstract_map(double v) {
		if (v > 0)
			return POS;
		if (v == 0)
			return ZERO;
		if (v < 0)
			return NEG;
		// NaN or INF
		return null;
	}

	@Override
	public Abstraction _plus(int right) {
		if (right == 1) {
			boolean n = could_be_NEG();
			boolean z = could_be_NEG();
			boolean p = could_be_ZERO() || could_be_POS();
			return construct_top(n, z, p);
		} else if (right == -1) {
			boolean n = could_be_NEG() || could_be_ZERO();
			boolean z = could_be_POS();
			boolean p = could_be_POS();
			return construct_top(n, z, p);
		} else
			return _plus(abstract_map(right));
	}

	@Override
	public Abstraction _plus(long right) {
		if (right == 1) {
			boolean n = could_be_NEG();
			boolean z = could_be_NEG();
			boolean p = could_be_ZERO() || could_be_POS();
			return construct_top(n, z, p);
		} else if (right == -1) {
			boolean n = could_be_NEG() || could_be_ZERO();
			boolean z = could_be_POS();
			boolean p = could_be_POS();
			return construct_top(n, z, p);
		} else
			return _plus(abstract_map(right));
	}

	@Override
	public Abstraction _plus(float right) {
		return _plus(abstract_map(right));
	}

	@Override
	public Abstraction _plus(double right) {
		return _plus(abstract_map(right));
	}

	@Override
	public Abstraction _plus(Abstraction right) {
		if (right instanceof Signs) {		
			Signs right_value = (Signs) right;
			boolean p = false, n = false, z = false;	
			if (this.could_be_NEG() && right_value.could_be_NEG())
				n = true;
			if (this.could_be_NEG() && right_value.could_be_ZERO())
				n = true;
			if (this.could_be_NEG() && right_value.could_be_POS())
				n = z = p = true;
			if (this.could_be_ZERO() && right_value.could_be_NEG())
				n = true;
			if (this.could_be_ZERO() && right_value.could_be_ZERO())
				z = true;
			if (this.could_be_ZERO() && right_value.could_be_POS())
				p = true;
			if (this.could_be_POS() && right_value.could_be_NEG())
				n = z = p = true;
			if (this.could_be_POS() && right_value.could_be_ZERO())
				p = true;
			if (this.could_be_POS() && right_value.could_be_POS())
				p = true;			
			return construct_top(n, z, p);
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _minus(Abstraction right) {
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean p = false, n = false, z = false;			
			if (this.could_be_NEG() && right_value.could_be_NEG())
				n = z = p = true;
			if (this.could_be_NEG() && right_value.could_be_ZERO())
				n = true;
			if (this.could_be_NEG() && right_value.could_be_POS())
				n = true;
			if (this.could_be_ZERO() && right_value.could_be_NEG())
				p = true;
			if (this.could_be_ZERO() && right_value.could_be_ZERO())
				z = true;
			if (this.could_be_ZERO() && right_value.could_be_POS())
				n = true;
			if (this.could_be_POS() && right_value.could_be_NEG())
				p = true;
			if (this.could_be_POS() && right_value.could_be_ZERO())
				p = true;
			if (this.could_be_POS() && right_value.could_be_POS())
				n = z = p = true;			
			return construct_top(n, z, p);			
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
	public Abstraction _minus(float right) {
		return _minus(abstract_map(right));
	}

	@Override
	public Abstraction _minus(double right) {
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
	public Abstraction _minus_reverse(float right) {
		return abstract_map(right)._minus(this);
	}

	@Override
	public Abstraction _minus_reverse(double right) {
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
	public Abstraction _mul(float right) {
		return _mul(abstract_map(right));
	}

	@Override
	public Abstraction _mul(double right) {
		return _mul(abstract_map(right));
	}

	@Override
	public Abstraction _mul(Abstraction right) {
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean p = false, n = false, z = false;			
			if (this.could_be_NEG() && right_value.could_be_NEG())
				p = true;
			if (this.could_be_NEG() && right_value.could_be_ZERO())
				z = true;
			if (this.could_be_NEG() && right_value.could_be_POS())
				n = true;
			if (this.could_be_ZERO() && right_value.could_be_NEG())
				z = true;
			if (this.could_be_ZERO() && right_value.could_be_ZERO())
				z = true;
			if (this.could_be_ZERO() && right_value.could_be_POS())
				z = true;
			if (this.could_be_POS() && right_value.could_be_NEG())
				n = true;
			if (this.could_be_POS() && right_value.could_be_ZERO())
				z = true;
			if (this.could_be_POS() && right_value.could_be_POS())
				p = true;			
			return construct_top(n, z, p);			
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
	public Abstraction _div(float right) {
		return _div(abstract_map(right));
	}

	@Override
	public Abstraction _div(double right) {
		return _div(abstract_map(right));
	}

	@Override
	public Abstraction _div(Abstraction right) {
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			if (right_value.could_be_ZERO())
				throw new ArithmeticException(
						"Division by zero (in abstract IDIV)");			
			boolean p = false, n = false, z = false;			
			if (this.could_be_NEG() && right_value.could_be_NEG())
				p = true;
			if (this.could_be_NEG() && right_value.could_be_POS())
				n = true;
			if (this.could_be_ZERO() && right_value.could_be_NEG())
				z = true;
			if (this.could_be_ZERO() && right_value.could_be_POS())
				z = true;
			if (this.could_be_POS() && right_value.could_be_NEG())
				n = true;
			if (this.could_be_POS() && right_value.could_be_POS())
				p = true;			
			return construct_top(n, z, p);
		} else
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
	public Abstraction _rem(float right) {
		return _rem(abstract_map(right));
	}

	@Override
	public Abstraction _rem(double right) {
		return _rem(abstract_map(right));
	}

	@Override
	public Abstraction _rem(Abstraction right) {
		if (right instanceof Signs) {			
			Signs right_value = (Signs) right;
			if (right_value.could_be_ZERO())
				throw new ArithmeticException(
						"Division by zero (in abstract IDIV)");					
			boolean p = false, n = false, z = false;			
			if (this.could_be_NEG() && right_value.could_be_NEG())
				z = p = true;
			if (this.could_be_NEG() && right_value.could_be_POS())
				z = p = true;
			if (this.could_be_ZERO() && right_value.could_be_NEG())
				z = true;
			if (this.could_be_ZERO() && right_value.could_be_POS())
				z = true;
			if (this.could_be_POS() && right_value.could_be_NEG())
				z = p = true;
			if (this.could_be_POS() && right_value.could_be_POS())
				z = p = true;			
			return construct_top(n, z, p);				
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
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean p = false, n = false, z = false;			
			if (this.could_be_NEG() && right_value.could_be_NEG())
				n = true;
			if (this.could_be_NEG() && right_value.could_be_ZERO())
				z = true;
			if (this.could_be_NEG() && right_value.could_be_POS())
				z = p = true;
			if (this.could_be_ZERO() && right_value.could_be_NEG())
				z = true;
			if (this.could_be_ZERO() && right_value.could_be_ZERO())
				z = true;
			if (this.could_be_ZERO() && right_value.could_be_POS())
				z = true;
			if (this.could_be_POS() && right_value.could_be_NEG())
				z = p = true;
			if (this.could_be_POS() && right_value.could_be_ZERO())
				z = true;
			if (this.could_be_POS() && right_value.could_be_POS())
				z = p = true;			
			return construct_top(n, z, p);			
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
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean p = false, n = false, z = false;			
			if (this.could_be_NEG() && right_value.could_be_NEG())
				n = true;
			if (this.could_be_NEG() && right_value.could_be_ZERO())
				n = true;
			if (this.could_be_NEG() && right_value.could_be_POS())
				n = true;
			if (this.could_be_ZERO() && right_value.could_be_NEG())
				n = true;
			if (this.could_be_ZERO() && right_value.could_be_ZERO())
				z = true;
			if (this.could_be_ZERO() && right_value.could_be_POS())
				p = true;
			if (this.could_be_POS() && right_value.could_be_NEG())
				n = true;
			if (this.could_be_POS() && right_value.could_be_ZERO())
				p = true;
			if (this.could_be_POS() && right_value.could_be_POS())
				p = true;			
			return construct_top(n, z, p);			
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
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean p = false, n = false, z = false;			
			if (this.could_be_NEG() && right_value.could_be_NEG())
				z = p = true;
			if (this.could_be_NEG() && right_value.could_be_ZERO())
				n = true;
			if (this.could_be_NEG() && right_value.could_be_POS())
				n = true;
			if (this.could_be_ZERO() && right_value.could_be_NEG())
				n = true;
			if (this.could_be_ZERO() && right_value.could_be_ZERO())
				z = true;
			if (this.could_be_ZERO() && right_value.could_be_POS())
				p = true;
			if (this.could_be_POS() && right_value.could_be_NEG())
				n = true;
			if (this.could_be_POS() && right_value.could_be_ZERO())
				p = true;
			if (this.could_be_POS() && right_value.could_be_POS())
				z = p = true;			
			return construct_top(n, z, p);			
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

	// Note that x << y considers only the least five bits of y
	// Three disjoint cases:
	// * x = ZERO, y in { NEG, ZERO, POS}		=>	x << y = ZERO	
	// * x in { NEG, ZERO, POS}, y = ZERO		=>	x << y = x
	// * x in { NEG, POS }, y in { NEG, POS }	=>	x << y  = { POS, ZERO, NEG }
	@Override
	public Abstraction _shift_left(Abstraction right) {
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean p = false, n = false, z = this.could_be_ZERO();
			if (right_value.could_be_ZERO()) {
				n |= this.could_be_NEG();
				z |= this.could_be_ZERO();
				p |= this.could_be_POS();
			}
			if ( (this.could_be_NEG() || this.could_be_POS()) &&
					(right_value.could_be_NEG() || right_value.could_be_POS()) )
				n = z = p = true;
			return construct_top(n, z, p);				
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

	// Note that x >> y considers only the least five bits of y, sign of x is preserved
	// Four disjoint cases:
	// * x = ZERO, y in { NEG, ZERO, POS }	=>	x >> y = ZERO	
	// * x in { NEG, ZERO, POS }, y = ZERO	=>	x >> y = x
	// * x = POS,  y in { NEG, POS }		=>	x >> y = { ZERO, POS }
	// * x = NEG,  y in { NEG, POS }		=>	x >> y = NEG
	@Override
	public Abstraction _shift_right(Abstraction right) {
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean p = false, n = false, z = this.could_be_ZERO();
			if (right_value.could_be_ZERO()) {
				n |= this.could_be_NEG();
				z |= this.could_be_ZERO();
				p |= this.could_be_POS();
			}
			if (right_value.could_be_NEG() || right_value.could_be_POS()) {
				n |= this.could_be_NEG();
				z |= this.could_be_POS();
				p |= this.could_be_POS();
			}
			return construct_top(n, z, p);				
		} else
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

	// Note that x >>> y considers only the least five bits of y, sign of x is not preserved
	// Three disjoint cases:
	// * x = ZERO, y in { NEG, ZERO, POS }		=>	x >> y = ZERO	
	// * x in { NEG, ZERO, POS }, y = ZERO		=>	x >> y = x
	// * x in { NEG, POS },  y in { NEG, POS }	=>	x >> y = { ZERO, POS }
	@Override
	public Abstraction _unsigned_shift_right(Abstraction right) {
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean p = false, n = false, z = this.could_be_ZERO();
			if (right_value.could_be_ZERO()) {
				n |= this.could_be_NEG();
				z |= this.could_be_ZERO();
				p |= this.could_be_POS();
			}
			if ( (this.could_be_NEG() || this.could_be_POS()) &&
					(right_value.could_be_NEG() || right_value.could_be_POS()) )
				z = p = true;
			return construct_top(n, z, p);	
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public Abstraction _neg() {
		return construct_top(could_be_POS(), could_be_ZERO(), could_be_NEG());
	}

	@Override
	public AbstractBoolean _ge(Abstraction right) {
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;		
			boolean t = false, f = false;
			if (this.could_be_NEG() && right_value.could_be_NEG())
				t = f = true;
			if (this.could_be_NEG() && right_value.could_be_ZERO())
				f = true;
			if (this.could_be_NEG() && right_value.could_be_POS())
				f = true;
			if (this.could_be_ZERO() && right_value.could_be_NEG())
				t = true;
			if (this.could_be_ZERO() && right_value.could_be_ZERO())
				t = true;
			if (this.could_be_ZERO() && right_value.could_be_POS())
				f = true;
			if (this.could_be_POS() && right_value.could_be_NEG())
				t = true;
			if (this.could_be_POS() && right_value.could_be_ZERO())
				t = true;
			if (this.could_be_POS() && right_value.could_be_POS())
				t = f = true;			
			if (f & t)
				return AbstractBoolean.TOP;
			else return (f)? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _ge(int right) {
		return _ge(abstract_map(right));
	}

	@Override
	public AbstractBoolean _gt(Abstraction right) {
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean t = false, f = false;
			if (this.could_be_NEG() && right_value.could_be_NEG())
				t = f = true;
			if (this.could_be_NEG() && right_value.could_be_ZERO())
				f = true;
			if (this.could_be_NEG() && right_value.could_be_POS())
				f = true;
			if (this.could_be_ZERO() && right_value.could_be_NEG())
				t = true;
			if (this.could_be_ZERO() && right_value.could_be_ZERO())
				f = true;
			if (this.could_be_ZERO() && right_value.could_be_POS())
				f = true;
			if (this.could_be_POS() && right_value.could_be_NEG())
				t = true;
			if (this.could_be_POS() && right_value.could_be_ZERO())
				t = true;
			if (this.could_be_POS() && right_value.could_be_POS())
				t = f = true;			
			if (f & t)
				return AbstractBoolean.TOP;
			else return (f)? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _gt(int right) {
		return _gt(abstract_map(right));
	}

	@Override
	public AbstractBoolean _le(Abstraction right) {
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean t = false, f = false;
			if (this.could_be_NEG() && right_value.could_be_NEG())
				t = f = true;
			if (this.could_be_NEG() && right_value.could_be_ZERO())
				t = true;
			if (this.could_be_NEG() && right_value.could_be_POS())
				t = true;
			if (this.could_be_ZERO() && right_value.could_be_NEG())
				f = true;
			if (this.could_be_ZERO() && right_value.could_be_ZERO())
				t = true;
			if (this.could_be_ZERO() && right_value.could_be_POS())
				t = true;
			if (this.could_be_POS() && right_value.could_be_NEG())
				f = true;
			if (this.could_be_POS() && right_value.could_be_ZERO())
				f = true;
			if (this.could_be_POS() && right_value.could_be_POS())
				t = f = true;			
			if (f & t)
				return AbstractBoolean.TOP;
			else return (f)? AbstractBoolean.FALSE : AbstractBoolean.TRUE;			
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _le(int right) {
		return _le(abstract_map(right));
	}

	@Override
	public AbstractBoolean _lt(Abstraction right) {
		if (right instanceof Signs) {
			Signs right_value = (Signs) right;
			boolean t = false, f = false;
			if (this.could_be_NEG() && right_value.could_be_NEG())
				t = f = true;
			if (this.could_be_NEG() && right_value.could_be_ZERO())
				t = true;
			if (this.could_be_NEG() && right_value.could_be_POS())
				t = true;
			if (this.could_be_ZERO() && right_value.could_be_NEG())
				f = true;
			if (this.could_be_ZERO() && right_value.could_be_ZERO())
				f = true;
			if (this.could_be_ZERO() && right_value.could_be_POS())
				t = true;
			if (this.could_be_POS() && right_value.could_be_NEG())
				f = true;
			if (this.could_be_POS() && right_value.could_be_ZERO())
				f = true;
			if (this.could_be_POS() && right_value.could_be_POS())
				t = f = true;			
			if (f & t)
				return AbstractBoolean.TOP;
			else return (f)? AbstractBoolean.FALSE : AbstractBoolean.TRUE;
		} else
			throw new RuntimeException("## Error: unknown abstraction");
	}

	@Override
	public AbstractBoolean _lt(int right) {
		return _lt(abstract_map(right));
	}

	public String toString() {
		if (this instanceof Signs) {
			if (this.isTop())
				return "TOP";			
			if (this == ZERO)
				return "ZERO";
			if (this == POS)
				return "POS";
			if (this == NEG)
				return "NEG";
		} else
			throw new RuntimeException("## Error: unknown abstraction");
		return "";
	}
}
