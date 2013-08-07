package gov.nasa.jpf.abstraction.common.access;

import java.util.List;

public abstract class ArrayExpression extends AccessExpression {
	
	private AccessExpression array;
	
	protected ArrayExpression(AccessExpression array) {
		this.array = array;
	}
	
	public AccessExpression getArray() {
		return array;
	}
	
	@Override
	public Root getRoot() {
		return array.getRoot();
	}
	
	@Override
	public List<AccessExpression> getSubAccessExpressions() {
		return getArray().getSubAccessExpressions();
	}
	
	@Override
	public int getLength() {
		return array.getLength() + 1;
	}
	
	@Override
	public AccessExpression cutTail() {
		return array.clone();
	}
}
