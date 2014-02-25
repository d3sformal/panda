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

import gov.nasa.jpf.abstraction.common.access.SpecialVariable;

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
	
	protected StringBuilder ret = new StringBuilder();
	
	public String getString() {
		return ret.toString();
	}

	@Override
	public void visit(Predicates predicates) {		
		for (Context c : predicates.contexts) {
			c.accept(this);
			ret.append("\n");
		}
	}

	@Override
	public void visit(ObjectContext context) {
		ret.append("[object ");
		
		context.getPackageAndClass().accept(this);
		
		ret.append("]\n");

		for (Predicate p : context.predicates) {
			p.accept(this);
			ret.append("\n");
		}
	}

	@Override
	public void visit(MethodContext context) {
		ret.append("[method ");
		
		context.getMethod().accept(this);
		
		ret.append("]\n");

		for (Predicate p : context.predicates) {
			p.accept(this);
			ret.append("\n");
		}
	}

	@Override
	public void visit(StaticContext context) {
		ret.append("[static]\n");

		for (Predicate p : context.predicates) {
			p.accept(this);
			ret.append("\n");
		}
	}

	@Override
	public void visit(Negation predicate) {
		ret.append("not(");
		
		predicate.predicate.accept(this);
		
		ret.append(")");
	}

	@Override
	public void visit(LessThan predicate) {
		predicate.a.accept(this);
		
		ret.append(" < ");
		
		predicate.b.accept(this);
	}

	@Override
	public void visit(Equals predicate) {
		predicate.a.accept(this);
		
		ret.append(" = ");
		
		predicate.b.accept(this);
	}
	
	@Override
	public void visit(Tautology predicate) {
		ret.append("true");
	}
	
	@Override
	public void visit(Contradiction predicate) {
		ret.append("false");
	}
	
	@Override
	public void visit(Conjunction predicate) {
		ret.append("(");
		
		predicate.a.accept(this);

		ret.append(" and ");
		
		predicate.b.accept(this);
		
		ret.append(")");
	}
	
	@Override
	public void visit(Disjunction predicate) {
		ret.append("(");
		
		predicate.a.accept(this);

		ret.append(" or ");
		
		predicate.b.accept(this);
		
		ret.append(")");
	}
	
	@Override
	public void visit(Implication predicate) {
		ret.append("(");
		
		predicate.a.accept(this);

		ret.append(" => ");
		
		predicate.b.accept(this);
		
		ret.append(")");
	}

	@Override
	public void visit(EmptyExpression expression) {
		ret.append(" ? ");
	}
	
	@Override
	public void visit(NullExpression expression) {
		ret.append("null");
	}

	@Override
	public void visit(Add expression) {
		ret.append("(");
		
		expression.a.accept(this);
		
		ret.append(" + ");
		
		expression.b.accept(this);
		
		ret.append(")");
	}

	@Override
	public void visit(Subtract expression) {
		ret.append("(");
		
		expression.a.accept(this);
		
		ret.append(" - ");
		
		expression.b.accept(this);
		
		ret.append(")");
	}

	@Override
	public void visit(Multiply expression) {
		ret.append("(");
		
		expression.a.accept(this);
		
		ret.append(" * ");
		
		expression.b.accept(this);
		
		ret.append(")");
	}

	@Override
	public void visit(Divide expression) {
		ret.append("(");
		
		expression.a.accept(this);
		
		ret.append(" / ");
		
		expression.b.accept(this);
		
		ret.append(")");
	}

	@Override
	public void visit(Modulo expression) {
		ret.append("(");
		
		expression.a.accept(this);
		
		ret.append(" % ");
		
		expression.b.accept(this);
		
		ret.append(")");
	}

	@Override
	public void visit(Constant expression) {
		ret.append(expression.value);
	}
	
	@Override
	public void visit(DefaultArrayLengths meta) {
		ret.append("arrlen");
	}
	
	@Override
	public void visit(Undefined expression) {
		ret.append("<<UNDEFINED>>");
	}
	
	@Override
	public void visit(UpdatedPredicate predicate) {
		predicate.apply().accept(this);
	}
	
	@Override
	public void visit(PackageAndClass packageAndClass) {
		ret.append(packageAndClass.getName());
	}
	
	@Override
	public void visit(Method method) {
		method.getPackageAndClass().accept(this);
		
		ret.append(".");
		
		ret.append(method.getName());
	}

    @Override
    public void visit(SpecialVariable expression) {
        ret.append(expression.getName());
    }

}
