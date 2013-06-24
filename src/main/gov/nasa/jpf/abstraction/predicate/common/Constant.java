package gov.nasa.jpf.abstraction.predicate.common;

public class Constant extends Expression {
	public int value;
	
	public Constant(int value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return ((Integer)value).toString();
	}
}
