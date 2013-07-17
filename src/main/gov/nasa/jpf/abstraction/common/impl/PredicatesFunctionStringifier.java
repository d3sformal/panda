package gov.nasa.jpf.abstraction.common.impl;

import gov.nasa.jpf.abstraction.common.AccessPathIndexElement;
import gov.nasa.jpf.abstraction.common.AccessPathRootElement;
import gov.nasa.jpf.abstraction.common.AccessPathSubElement;
import gov.nasa.jpf.abstraction.common.PredicatesStringifier;

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
		
		String field = element.getName();
		
		if (path != null && path.getTail() instanceof AccessPathSubElement) {
			AccessPathSubElement updatedField = (AccessPathSubElement) path.getTail();
			
			if (updatedField.getName().equals(element.getName())) {
				PredicatesFunctionStringifier updatePrefixVisitor = new PredicatesFunctionStringifier();
				PredicatesFunctionStringifier updateExpressionVisitor = new PredicatesFunctionStringifier();
				
				path.cutTail().accept(updatePrefixVisitor);
				expression.accept(updateExpressionVisitor);
				
				field = "fwrite(" + element.getName() + ", " + updatePrefixVisitor.getString() + ", " + updateExpressionVisitor.getString() + ")";
			}
		}
		
		ret = String.format(ret, "fread(" + field + ", %s)");
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
		
		String array = "arr";
		
		if (path != null && path.getTail() instanceof AccessPathIndexElement) {
			AccessPathIndexElement index = (AccessPathIndexElement) path.getTail();
			PredicatesFunctionStringifier updatePrefixVisitor = new PredicatesFunctionStringifier();
			PredicatesFunctionStringifier updateIndexVisitor = new PredicatesFunctionStringifier();
			PredicatesFunctionStringifier updateExpressionVisitor = new PredicatesFunctionStringifier();
				
			path.cutTail().accept(updatePrefixVisitor);
			index.getIndex().accept(updateIndexVisitor);
			expression.accept(updateExpressionVisitor);
				
			array = "awrite(arr, " + updatePrefixVisitor.getString() + ", " + updateIndexVisitor.getString() + ", " + updateExpressionVisitor.getString() + ")";
		}
		
		ret = String.format(ret, "aread(" + array + ", %s, " + indexVisitor.getString() + ")");
	}

}
