package gov.nasa.jpf.abstraction.common.impl;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;

public class FreshRootElement extends DefaultAccessPathRootElement {
	public static String name = "fresh";

	public FreshRootElement() {
		super(name);
	}
	
	@Override
	public FreshRootElement clone() {
		FreshRootElement clone = new FreshRootElement();
		
		if (getNext() != null) {
			clone.setNext(getNext().clone());
			clone.getNext().setPrevious(clone);
		}
		
		return clone;
	}
	
	@Override
	public DefaultAccessPathRootElement replace(AccessPath formerPath, Expression expression) {
		FreshRootElement replaced = new FreshRootElement();
		
		if (getNext() != null) {
			replaced.setNext(getNext().replace(formerPath, expression));
			replaced.getNext().setPrevious(replaced);
		}
		
		return replaced;
	}
}
