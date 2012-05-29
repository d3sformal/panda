package gov.nasa.jpf.abstraction.numeric;

import java.util.Set;

public class Abstraction {

	public Set<Abstraction>   get_tokens() {throw new RuntimeException("get_tokens not implemented");}
	boolean isTop = false;

	public boolean isTop() {
		return isTop;
	}

	public Abstraction abstract_map(int v) {throw new RuntimeException("abstract_map not implemented");}

	public static Abstraction _add(int v1, Abstraction abs_v1, int v2, Abstraction abs_v2) {
		Abstraction result = null;
		if(abs_v1!=null) {
			if (abs_v2!=null)
				result = abs_v1._plus(abs_v2);
			else // v2 is concrete
				result = abs_v1._plus(v2);
		}
		else if (abs_v2!=null)
			result = abs_v2._plus(v1);
		return result;
	}

	public static Abstraction _sub(int v1, Abstraction abs_v1, int v2, Abstraction abs_v2) {
		Abstraction result = null;
		if(abs_v2!=null) {
			if (abs_v1!=null)
				result = abs_v2._minus(abs_v1);
			else // v1 is concrete
				result = abs_v2._minus(v1);
		}
		else if (abs_v1!=null)
			result = abs_v1._minus_reverse(v2);
		return result;
	}

	public Abstraction _plus(Abstraction right) {throw new RuntimeException("plus not implemented");}
	public Abstraction _plus(int right) {throw new RuntimeException("plus not implemented");}
	public Abstraction _minus(Abstraction right) {throw new RuntimeException("minus not implemented");}
	public Abstraction _minus(int right) {throw new RuntimeException("minus not implemented");}
	public Abstraction _minus_reverse(int right) {throw new RuntimeException("minus_reverse not implemented");}

	public AbstractBoolean _lt(Abstraction right) {throw new RuntimeException("lt not implemented");}
	public AbstractBoolean _lt(int right) {throw new RuntimeException("lt not implemented");}
	public AbstractBoolean _le(Abstraction right) {throw new RuntimeException("le not implemented");}
	public AbstractBoolean _le(int right) {throw new RuntimeException("le not implemented");}
	public AbstractBoolean _gt(Abstraction right) {throw new RuntimeException("gt not implemented");}
	public AbstractBoolean _gt(int right) {throw new RuntimeException("gt not implemented");}
	public AbstractBoolean _ge(Abstraction right) {throw new RuntimeException("ge not implemented");}
	public AbstractBoolean _ge(int right) {throw new RuntimeException("ge not implemented");}
}
