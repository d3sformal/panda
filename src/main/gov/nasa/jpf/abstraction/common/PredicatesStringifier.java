package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.concrete.EmptyExpression;
import gov.nasa.jpf.abstraction.predicate.common.Context;
import gov.nasa.jpf.abstraction.predicate.common.Equals;
import gov.nasa.jpf.abstraction.predicate.common.LessThan;
import gov.nasa.jpf.abstraction.predicate.common.MethodContext;
import gov.nasa.jpf.abstraction.predicate.common.ObjectContext;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.common.Predicates;
import gov.nasa.jpf.abstraction.predicate.common.StaticContext;

public abstract class PredicatesStringifier implements PredicatesVisitor {
	
	protected String ret = "";
	
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
