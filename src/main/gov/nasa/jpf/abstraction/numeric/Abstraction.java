package gov.nasa.jpf.abstraction.numeric;

public abstract class Abstraction {
	public abstract Abstraction _plus(Abstraction right);
	public abstract Abstraction _plus(int right);
	public abstract Abstraction _minus(Abstraction right);
	public abstract Abstraction _minus(int right);
	public abstract Abstraction _minus_reverse(int right);

	public abstract AbstractBoolean _lt(Abstraction right);
	public abstract AbstractBoolean _lt(int right);
	public abstract AbstractBoolean _le(Abstraction right);
	public abstract AbstractBoolean _le(int right);
	public abstract AbstractBoolean _gt(Abstraction right);
	public abstract AbstractBoolean _gt(int right);
	public abstract AbstractBoolean _ge(Abstraction right);
	public abstract AbstractBoolean _ge(int right);
}
