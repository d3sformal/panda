package gov.nasa.jpf.abstraction.predicate.grammar;

import java.util.ArrayList;
import java.util.List;

public class Constant extends Expression {
	public int value;
	
	public Constant(int value) {
		this.value = value;
	}

	@Override
	public List<AccessPath> getPaths() {
		return new ArrayList<AccessPath>();
	}
	
	@Override
	public String toString() {
		return ((Integer)value).toString();
	}
}