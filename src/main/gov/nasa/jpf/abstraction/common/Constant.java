package gov.nasa.jpf.abstraction.common;

import java.util.ArrayList;
import java.util.List;

public class Constant extends Expression {
	public Number value;
	
	protected Constant(Number value) {
		this.value = value;
	}

	@Override
	public List<AccessPath> getPaths() {
		return new ArrayList<AccessPath>();
	}
	
	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Constant replace(AccessPath formerPath, Expression expression) {
		return this;
	}
	
	public static Constant create(int value) {
		return new Constant(value);
	}
	
	public static Constant create(float value) {
		return new Constant(value);
	}
	
	public static Constant create(long value) {
		return new Constant(value);
	}
	
	public static Constant create(double value) {
		return new Constant(value);
	}
	
	@Override
	public Constant clone() {
		return new Constant(value);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Constant) {
			Constant c = (Constant) o;
			
			return value.equals(c.value);
		}
		
		return false;
	}
}
