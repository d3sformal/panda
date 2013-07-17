package gov.nasa.jpf.abstraction.common.impl;

import gov.nasa.jpf.abstraction.common.AccessPathIndexElement;
import gov.nasa.jpf.abstraction.common.AccessPathRootElement;
import gov.nasa.jpf.abstraction.common.AccessPathSubElement;
import gov.nasa.jpf.abstraction.common.PredicatesStringifier;

public class PredicatesDotStringifier extends PredicatesStringifier {

	@Override
	public void visit(AccessPathRootElement element) {
		ret += element.getName();
		
		if (element.getNext() == null) return;
		
		element.getNext().accept(this);
	}

	@Override
	public void visit(AccessPathSubElement element) {
		ret += "." + element.getName();
		
		if (path != null && path.getTail() instanceof AccessPathSubElement) {
			AccessPathSubElement field = (AccessPathSubElement) path.getTail();
			
			if (field.getName().equals(element.getName())) {
				ret += "|";
				
				path.accept(this);
				
				ret += " := ";
				
				expression.accept(this);
				
				ret += "|";
			}
		}
		
		if (element.getNext() == null) return;
		
		element.getNext().accept(this);
	}

	@Override
	public void visit(AccessPathIndexElement element) {
		ret += "[";
		
		element.getIndex().accept(this);
		
		ret += "]";
		
		if (path != null && path.getTail() instanceof AccessPathIndexElement) {	
			ret += "|";
				
			path.accept(this);
				
			ret += " := ";
				
			expression.accept(this);
				
			ret += "|";
		}
		
		if (element.getNext() == null) return;
		
		element.getNext().accept(this);
	}

}
