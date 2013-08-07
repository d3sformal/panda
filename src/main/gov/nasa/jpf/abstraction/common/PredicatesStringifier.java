package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.concrete.AnonymousExpression;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.concrete.EmptyExpression;
import gov.nasa.jpf.abstraction.predicate.common.Conjunction;
import gov.nasa.jpf.abstraction.predicate.common.Context;
import gov.nasa.jpf.abstraction.predicate.common.Contradiction;
import gov.nasa.jpf.abstraction.predicate.common.Disjunction;
import gov.nasa.jpf.abstraction.predicate.common.Equals;
import gov.nasa.jpf.abstraction.predicate.common.Implication;
import gov.nasa.jpf.abstraction.predicate.common.LessThan;
import gov.nasa.jpf.abstraction.predicate.common.MethodContext;
import gov.nasa.jpf.abstraction.predicate.common.ObjectContext;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.common.Predicates;
import gov.nasa.jpf.abstraction.predicate.common.StaticContext;
import gov.nasa.jpf.abstraction.predicate.common.Tautology;
import gov.nasa.jpf.abstraction.predicate.common.UpdatedPredicate;

public abstract class PredicatesStringifier implements PredicatesVisitor {
	
	protected String ret = "";
	protected AccessPath updatedPath = null;
	protected Expression newExpression = null;
	
	public String getString() {
		return ret;
	}

	@Override
	public void visit(Predicates predicates) {		
		for (Context c : predicates.contexts) {
			c.accept(this);
			ret += "\n";
		}
	}

	@Override
	public void visit(ObjectContext context) {
		ret += "[object " + context.getObject().toString(AccessPath.NotationPolicy.DOT_NOTATION) + "]\n";

		for (Predicate p : context.predicates) {
			p.accept(this);
			ret += "\n";
		}
	}

	@Override
	public void visit(MethodContext context) {
		ret += "[method " + context.getMethod().toString(AccessPath.NotationPolicy.DOT_NOTATION) + "]\n";

		for (Predicate p : context.predicates) {
			p.accept(this);
			ret += "\n";
		}
	}

	@Override
	public void visit(StaticContext context) {
		ret += "[static]\n";

		for (Predicate p : context.predicates) {
			p.accept(this);
			ret += "\n";
		}
	}

	@Override
	public void visit(Negation predicate) {
		ret += "not(";
		
		predicate.predicate.accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(LessThan predicate) {
		predicate.a.accept(this);
		
		ret += " < ";
		
		predicate.b.accept(this);
	}

	@Override
	public void visit(Equals predicate) {
		predicate.a.accept(this);
		
		ret += " = ";
		
		predicate.b.accept(this);
	}
	
	@Override
	public void visit(Tautology predicate) {
		ret += "true";
	}
	
	@Override
	public void visit(Contradiction predicate) {
		ret += "false";
	}
	
	@Override
	public void visit(Conjunction predicate) {
		ret += "(";
		
		predicate.a.accept(this);

		ret += " and ";
		
		predicate.b.accept(this);
		
		ret += ")";
	}
	
	@Override
	public void visit(Disjunction predicate) {
		ret += "(";
		
		predicate.a.accept(this);

		ret += " or ";
		
		predicate.b.accept(this);
		
		ret += ")";
	}
	
	@Override
	public void visit(Implication predicate) {
		ret += "(";
		
		predicate.a.accept(this);

		ret += " => ";
		
		predicate.b.accept(this);
		
		ret += ")";
	}
	
	@Override
	public void visit(UpdatedPredicate predicate) {		
		if (predicate.path.getRoot() == predicate.path.getTail()) {
			if (predicate.expression instanceof AnonymousObject) {
				predicate.expression = new Fresh();
			}
			
			predicate.predicate.replace(predicate.path, predicate.expression).accept(this);
							
			return;
		}
				
		updatedPath = predicate.path;
		newExpression = predicate.expression;
			
		predicate.predicate.accept(this);
	}

	@Override
	public void visit(EmptyExpression expression) {
		ret += " ? ";
	}

	@Override
	public void visit(Add expression) {
		ret += "(";
		
		expression.a.accept(this);
		
		ret += " + ";
		
		expression.b.accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(Subtract expression) {
		ret += "(";
		
		expression.a.accept(this);
		
		ret += " - ";
		
		expression.b.accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(Multiply expression) {
		ret += "(";
		
		expression.a.accept(this);
		
		ret += " * ";
		
		expression.b.accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(Divide expression) {
		ret += "(";
		
		expression.a.accept(this);
		
		ret += " / ";
		
		expression.b.accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(Modulo expression) {
		ret += "(";
		
		expression.a.accept(this);
		
		ret += " % ";
		
		expression.b.accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(AccessPath expression) {
		expression.getRoot().accept(this);
	}

	@Override
	public void visit(Constant expression) {
		ret += expression.value;
	}

}
