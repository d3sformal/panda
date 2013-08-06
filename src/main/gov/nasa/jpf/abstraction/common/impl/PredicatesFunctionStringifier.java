package gov.nasa.jpf.abstraction.common.impl;

import gov.nasa.jpf.abstraction.common.AccessPathIndexElement;
import gov.nasa.jpf.abstraction.common.AccessPathRootElement;
import gov.nasa.jpf.abstraction.common.AccessPathSubElement;
import gov.nasa.jpf.abstraction.common.ArrayLength;
import gov.nasa.jpf.abstraction.common.ArrayPath;
import gov.nasa.jpf.abstraction.common.PredicatesStringifier;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;

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
		
		if (updatedPath != null && updatedPath.getTail() instanceof AccessPathSubElement) {
			AccessPathSubElement updatedField = (AccessPathSubElement) updatedPath.getTail();
			
			if (updatedField.getName().equals(element.getName())) {
				PredicatesFunctionStringifier updatePrefixVisitor = new PredicatesFunctionStringifier();
				PredicatesFunctionStringifier updateExpressionVisitor = new PredicatesFunctionStringifier();
				
				updatedPath.cutTail().accept(updatePrefixVisitor);
				newExpression.accept(updateExpressionVisitor);
				
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
		
		if (updatedPath != null && updatedPath.getTail() instanceof AccessPathIndexElement) {
			AccessPathIndexElement index = (AccessPathIndexElement) updatedPath.getTail();
			PredicatesFunctionStringifier updatePrefixVisitor = new PredicatesFunctionStringifier();
			PredicatesFunctionStringifier updateIndexVisitor = new PredicatesFunctionStringifier();
			PredicatesFunctionStringifier updateExpressionVisitor = new PredicatesFunctionStringifier();
				
			updatedPath.cutTail().accept(updatePrefixVisitor);
			index.getIndex().accept(updateIndexVisitor);
			newExpression.accept(updateExpressionVisitor);
				
			array = "awrite(arr, " + updatePrefixVisitor.getString() + ", " + updateIndexVisitor.getString() + ", " + updateExpressionVisitor.getString() + ")";
		}
		
		ret = String.format(ret, "aread(" + array + ", %s, " + indexVisitor.getString() + ")");
	}
	
	@Override
	public void visit(AnonymousArray expression) {
		PredicatesFunctionStringifier lengthStringifier = new PredicatesFunctionStringifier();
		
		expression.length.accept(lengthStringifier);
		
		ret += "array(0, ";
		
		ret += lengthStringifier.getString();

		ret += ")";
	}

	@Override
	public void visit(AnonymousObject expression) {
		ret += "object(";
		
		ret += expression.ei.getObjectRef();

		ret += ")";
	}
	
	@Override
	public void visit(ArrayLength expression) {	
		ret += "alength(";
		
		if (updatedPath != null && (newExpression instanceof ArrayPath || newExpression instanceof AnonymousArray)) {
			PredicatesDotStringifier updatedPathStringifier = new PredicatesDotStringifier();
			PredicatesDotStringifier updatedExpressionStringifier = new PredicatesDotStringifier();
			
			updatedPath.accept(updatedPathStringifier);
			
			ret += "alengthupdate(arrlen, ";
			
			ret += updatedPathStringifier.getString();
			
			ret += ", ";
			
			String newlength = "";
			
			if (newExpression instanceof AnonymousArray) {
				AnonymousArray array = (AnonymousArray) newExpression;
				
				array.length.accept(updatedExpressionStringifier);
				
				newlength = updatedExpressionStringifier.getString();
			} else {
				newExpression.accept(updatedExpressionStringifier);

				newlength = "alength(arrlen, " + updatedExpressionStringifier.getString() + ")";
			}
			
			ret += newlength;
			
			ret += ")";
		} else {
			ret += "arrlen, ";
		}
		
		expression.path.accept(this);
		
		ret += ")";
	}

}
