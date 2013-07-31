package gov.nasa.jpf.abstraction.common.impl;

import gov.nasa.jpf.abstraction.common.AccessPathIndexElement;
import gov.nasa.jpf.abstraction.common.AccessPathRootElement;
import gov.nasa.jpf.abstraction.common.AccessPathSubElement;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.PredicatesStringifier;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.predicate.common.Equals;
import gov.nasa.jpf.abstraction.predicate.common.LessThan;

public class PredicatesDotStringifier extends PredicatesStringifier {

	@Override
	public void visit(Negation expression) {
		if (expression.predicate instanceof LessThan) {
			LessThan lt = (LessThan) expression.predicate;
			
			lt.a.accept(this);
			
			ret += " >= ";
			
			lt.b.accept(this);
		} else if (expression.predicate instanceof Equals) {
			Equals lt = (Equals) expression.predicate;
			
			lt.a.accept(this);
			
			ret += " != ";
			
			lt.b.accept(this);
		} else {
			super.visit(expression);
		}
	}

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
				PredicatesDotStringifier updatedPathStringifier = new PredicatesDotStringifier();
				PredicatesDotStringifier updatedExpressionStringifier = new PredicatesDotStringifier();
				
				path.accept(updatedPathStringifier);
				expression.accept(updatedExpressionStringifier);
				
				ret += "|";
				
				ret += updatedPathStringifier.getString();
				
				ret += " := ";
				
				ret += updatedExpressionStringifier.getString();
				
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
			PredicatesDotStringifier updatedPathStringifier = new PredicatesDotStringifier();
			PredicatesDotStringifier updatedExpressionStringifier = new PredicatesDotStringifier();
			
			path.accept(updatedPathStringifier);
			expression.accept(updatedExpressionStringifier);
			
			ret += "|";
			
			ret += updatedPathStringifier.getString();
			
			ret += " := ";
			
			ret += updatedExpressionStringifier.getString();
			
			ret += "|";
		}
		
		if (element.getNext() == null) return;
		
		element.getNext().accept(this);
	}

	@Override
	public void visit(AnonymousArray expression) {
		PredicatesDotStringifier lengthStringifier = new PredicatesDotStringifier();
		
		expression.length.accept(lengthStringifier);
		
		ret += "[ 0 .. ";
		
		ret += lengthStringifier.getString();

		ret += " ]";
	}

	@Override
	public void visit(AnonymousObject expression) {
		ret += "{";
		
		ret += expression.ei.getObjectRef();

		ret += "}";
	}

}
