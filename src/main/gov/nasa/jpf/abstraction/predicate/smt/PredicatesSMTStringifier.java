package gov.nasa.jpf.abstraction.predicate.smt;

import gov.nasa.jpf.abstraction.common.AccessPathIndexElement;
import gov.nasa.jpf.abstraction.common.AccessPathRootElement;
import gov.nasa.jpf.abstraction.common.AccessPathSubElement;
import gov.nasa.jpf.abstraction.common.Add;
import gov.nasa.jpf.abstraction.common.ArrayLength;
import gov.nasa.jpf.abstraction.common.ArrayPath;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Divide;
import gov.nasa.jpf.abstraction.common.Modulo;
import gov.nasa.jpf.abstraction.common.Multiply;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.PredicatesStringifier;
import gov.nasa.jpf.abstraction.common.Subtract;
import gov.nasa.jpf.abstraction.common.impl.FreshRootElement;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.predicate.common.Conjunction;
import gov.nasa.jpf.abstraction.predicate.common.Disjunction;
import gov.nasa.jpf.abstraction.predicate.common.Equals;
import gov.nasa.jpf.abstraction.predicate.common.Implication;
import gov.nasa.jpf.abstraction.predicate.common.LessThan;

public class PredicatesSMTStringifier extends PredicatesStringifier {
	
	@Override
	public void visit(Negation predicate) {
		ret += "(not ";
		
		predicate.predicate.accept(this);
		
		ret += ")";
	}
	
	@Override
	public void visit(Conjunction predicate) {
		ret += "(and ";
		
		predicate.a.accept(this);
		
		ret += " ";
		
		predicate.b.accept(this);
		
		ret += ")";
	}
	
	@Override
	public void visit(Disjunction predicate) {
		ret += "(or ";
		
		predicate.a.accept(this);
		
		ret += " ";
		
		predicate.b.accept(this);
		
		ret += ")";
	}
	
	@Override
	public void visit(Implication predicate) {
		ret += "(=> ";
		
		predicate.a.accept(this);
		
		ret += " ";
		
		predicate.b.accept(this);
		
		ret += ")";
	}
	
	@Override
	public void visit(LessThan predicate) {
		ret += "(< ";
		
		predicate.a.accept(this);
		
		ret += " ";
		
		predicate.b.accept(this);
		
		ret += ")";
	}
	
	@Override
	public void visit(Equals predicate) {
		ret += "(= ";
		
		predicate.a.accept(this);
		
		ret += " ";
		
		predicate.b.accept(this);
		
		ret += ")";
	}
	
	@Override
	public void visit(Add expression) {
		ret += "(+ ";
		
		expression.a.accept(this);
		
		ret += " ";
		
		expression.b.accept(this);
		
		ret += ")";
	}
	
	@Override
	public void visit(Subtract expression) {
		ret += "(- ";
		
		expression.a.accept(this);
		
		ret += " ";
		
		expression.b.accept(this);
		
		ret += ")";
	}
	
	@Override
	public void visit(Multiply expression) {
		ret += "(* ";
		
		expression.a.accept(this);
		
		ret += " ";
		
		expression.b.accept(this);
		
		ret += ")";
	}
	
	@Override
	public void visit(Divide expression) {
		ret += "(/ ";
		
		expression.a.accept(this);
		
		ret += " ";
		
		expression.b.accept(this);
		
		ret += ")";
	}
	
	@Override
	public void visit(Modulo expression) {
		visit((Subtract)expression);
	}
	
	@Override
	public void visit(Constant expression) {
		if (expression.value.doubleValue() < 0) {
			ret += "(- " + (-expression.value.doubleValue()) + ")";
		} else {
			ret += expression;
		}
	}
	
	@Override
	public void visit(ArrayLength expression) {
		ret += "(select ";
		
		if (updatedPath != null && (newExpression instanceof ArrayPath || newExpression instanceof AnonymousArray)) {
			ret += "(store arrlen ";
			
			updatedPath.accept(this);
			
			ret += " ";
			
			if (newExpression instanceof AnonymousArray) {
				AnonymousArray array = (AnonymousArray) newExpression;
				
				array.length.accept(this);
			} else {
				ret += "(select arrlen ";
				
				newExpression.accept(this);
				
				ret += ")";
			}
			
			ret += ")";
		} else {
			ret += "arrlen";
		}
		
		ret += " ";
		
		expression.path.accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(AccessPathRootElement element) {
		if (element.getNext() == null) {
			ret += "%s";
		} else {
			element.getNext().accept(this);
		}

		if (element instanceof FreshRootElement) {
			ret = String.format(ret, element.getName());
		} else {
			ret = String.format(ret, "var_" + element.getName());
		}
	}

	@Override
	public void visit(AccessPathSubElement element) {
		if (element.getNext() == null) {
			ret += "%s";
		} else {
			element.getNext().accept(this);
		}
		
		String field = "field_" + element.getName();
		
		if (updatedPath != null && updatedPath.getTail() instanceof AccessPathSubElement) {
			AccessPathSubElement updatedField = (AccessPathSubElement) updatedPath.getTail();
			
			if (updatedField.getName().equals(element.getName())) {
				PredicatesSMTStringifier updatePrefixVisitor = new PredicatesSMTStringifier();
				PredicatesSMTStringifier updateExpressionVisitor = new PredicatesSMTStringifier();
				
				updatedPath.cutTail().accept(updatePrefixVisitor);
				newExpression.accept(updateExpressionVisitor);
				
				field = "(store field_" + element.getName() + " " + updatePrefixVisitor.getString() + " " + updateExpressionVisitor.getString() + ")";
			}
		}
		
		ret = String.format(ret, "(select " + field + " %s)");
	}

	@Override
	public void visit(AccessPathIndexElement element) {
		if (element.getNext() == null) {
			ret += "%s";
		} else {
			element.getNext().accept(this);
		}
		
		PredicatesSMTStringifier indexVisitor = new PredicatesSMTStringifier();
		
		element.getIndex().accept(indexVisitor);
		
		String array = "arr";
		
		if (updatedPath != null && updatedPath.getTail() instanceof AccessPathIndexElement) {
			AccessPathIndexElement index = (AccessPathIndexElement) updatedPath.getTail();
			PredicatesSMTStringifier updatePrefixVisitor = new PredicatesSMTStringifier();
			PredicatesSMTStringifier updateIndexVisitor = new PredicatesSMTStringifier();
			PredicatesSMTStringifier updateExpressionVisitor = new PredicatesSMTStringifier();
				
			updatedPath.cutTail().accept(updatePrefixVisitor);
			index.getIndex().accept(updateIndexVisitor);
			newExpression.accept(updateExpressionVisitor);
			
			array = "(store arr " + updatePrefixVisitor.getString() + " (store (select arr " + updatePrefixVisitor.getString() + ") " + updateIndexVisitor.getString() + " " + updateExpressionVisitor.getString() + "))";
		}
		
		if (updatedPath != null && newExpression instanceof AnonymousArray) {
			PredicatesSMTStringifier updatePathVisitor = new PredicatesSMTStringifier();
			
			updatedPath.accept(updatePathVisitor);
			
			array = "(store arr " + updatePathVisitor.getString() + " (select arr fresh))";
		}
		
		ret = String.format(ret, "(select (select " + array + " %s) " + indexVisitor.getString() + ")");
	}

	@Override
	public void visit(AnonymousArray expression) {
		ret += "fresh";
	}

	@Override
	public void visit(AnonymousObject expression) {
		ret += "fresh";
	}

}