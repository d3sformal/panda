package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

import java.util.LinkedList;
import java.util.List;

public class Root extends AccessExpression {
	private String name;
	
	protected Root(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public Root getRoot() {
		return this;
	}
	
	public static Root create(String name) {
		if (name == null) {
			return null;
		}
		
		return new Root(name);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public List<AccessExpression> getSubAccessExpressions() {
		return new LinkedList<AccessExpression>();
	}

	@Override
	public AccessExpression clone() {
		return create(getName());
	}

	@Override
	public Expression replace(AccessExpression formerPath, Expression expression) {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}

	@Override
	public int getLength() {
		return 1;
	}
	
	@Override
	public AccessExpression cutTail() {
		return clone();
	}
	
}
