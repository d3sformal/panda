package gov.nasa.jpf.abstraction.common;

import java.util.ArrayList;
import java.util.List;

public class Constant extends Expression {
	public Object value;
	
	public Constant(Object value) {
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
	public Expression replace(AccessPath formerPath, Expression expression) {
		return this;
	}
}
