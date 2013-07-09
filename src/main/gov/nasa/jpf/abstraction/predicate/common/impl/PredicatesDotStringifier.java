package gov.nasa.jpf.abstraction.predicate.common.impl;

import gov.nasa.jpf.abstraction.predicate.common.AccessPathIndexElement;
import gov.nasa.jpf.abstraction.predicate.common.AccessPathRootElement;
import gov.nasa.jpf.abstraction.predicate.common.AccessPathSubElement;
import gov.nasa.jpf.abstraction.predicate.common.PredicatesStringifier;

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
		
		if (element.getNext() == null) return;
		
		element.getNext().accept(this);
	}

	@Override
	public void visit(AccessPathIndexElement element) {
		ret += "[";
		
		element.getIndex().accept(this);
		
		ret += "]";
		
		if (element.getNext() == null) return;
		
		element.getNext().accept(this);
	}

}
