package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.Method;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrayLengths;
import gov.nasa.jpf.abstraction.common.impl.NullExpression;
import gov.nasa.jpf.abstraction.concrete.EmptyExpression;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Context;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Disjunction;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Implication;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.MethodContext;
import gov.nasa.jpf.abstraction.common.ObjectContext;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Predicates;
import gov.nasa.jpf.abstraction.common.StaticContext;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.UpdatedPredicate;

/**
 * A special visitor of the hierarchy:
 * 
 * predicates
 *   -> context
 *     -> predicate
 *       -> expression
 *       
 * to be used to produce a string representation of the captured hierarchy.
 */
public abstract class PredicatesStringifier implements PredicatesComponentVisitor {
	
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
		ret += "[object ";
		
		context.getPackageAndClass().accept(this);
		
		ret += "]\n";

		for (Predicate p : context.predicates) {
			p.accept(this);
			ret += "\n";
		}
	}

	@Override
	public void visit(MethodContext context) {
		ret += "[method ";
		
		context.getMethod().accept(this);
		
		ret += "]\n";

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
	public void visit(EmptyExpression expression) {
		ret += " ? ";
	}
	
	@Override
	public void visit(NullExpression expression) {
		ret += "null";
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
	public void visit(Constant expression) {
		ret += expression.value;
	}
	
	@Override
	public void visit(DefaultArrayLengths meta) {
		ret += "arrlen";
	}
	
	@Override
	public void visit(Undefined expression) {
		ret += "<<UNDEFINED>>";
	}
	
	@Override
	public void visit(UpdatedPredicate predicate) {
		predicate.apply().accept(this);
	}
	
	@Override
	public void visit(PackageAndClass packageAndClass) {
		ret += packageAndClass.getName();
	}
	
	@Override
	public void visit(Method method) {
		method.getPackageAndClass().accept(this);
		
		ret += ".";
		
		ret += method.getName();
	}

}
