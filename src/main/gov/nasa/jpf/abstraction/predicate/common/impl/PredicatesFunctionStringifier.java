package gov.nasa.jpf.abstraction.predicate.common.impl;

import gov.nasa.jpf.abstraction.predicate.common.AccessPathIndexElement;
import gov.nasa.jpf.abstraction.predicate.common.AccessPathRootElement;
import gov.nasa.jpf.abstraction.predicate.common.AccessPathSubElement;
import gov.nasa.jpf.abstraction.predicate.common.PredicatesStringifier;

public class PredicatesFunctionStringifier extends PredicatesStringifier {

	@Override
	public void visit(AccessPathRootElement element) {
		if (element.getNext() == null) {
			ret += "%s";
		} else {
			element.getNext().accept(this);
		}
		
		ret = String.format(ret, element.getName());
	}

	@Override
	public void visit(AccessPathSubElement element) {
		if (element.getNext() == null) {
			ret += "%s";
		} else {
			element.getNext().accept(this);
		}
		
		ret = String.format(ret, "fread(" + element.getName() + ", %s)");
	}

	@Override
	public void visit(AccessPathIndexElement element) {
		if (element.getNext() == null) {
			ret += "%s";
		} else {
			element.getNext().accept(this);
		}
		
		PredicatesFunctionStringifier indexVisitor = new PredicatesFunctionStringifier();
		
		element.getIndex().accept(indexVisitor);
		
		ret = String.format(ret, "aread(arr, %s, " + indexVisitor.getString() + ")");
	}

}
