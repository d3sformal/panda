package gov.nasa.jpf.abstraction.common.access;

import java.util.List;

public abstract class ObjectFieldExpression extends AccessExpression {
	private AccessExpression object;
	private String name;
	
	protected ObjectFieldExpression(AccessExpression object, String name) {
		this.object = object;
		this.name = name;
	}
	
	public AccessExpression getObject() {
		return object;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public Root getRoot() {
		return object.getRoot();
	}
	
	@Override
	public List<AccessExpression> getSubAccessExpressions() {
		return object.getSubAccessExpressions();
	}
	
	@Override
	public int getLength() {
		return object.getLength() + 1;
	}
	
	@Override
	public AccessExpression cutTail() {
		return object.clone();
	}
}
