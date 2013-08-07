package gov.nasa.jpf.abstraction.predicate.smt;

import gov.nasa.jpf.abstraction.common.Add;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Divide;
import gov.nasa.jpf.abstraction.common.Modulo;
import gov.nasa.jpf.abstraction.common.Multiply;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.PredicatesStringifier;
import gov.nasa.jpf.abstraction.common.Subtract;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ArrayElementWrite;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthRead;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthWrite;
import gov.nasa.jpf.abstraction.common.access.Fresh;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.Root;
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
	public void visit(AnonymousArray expression) {
		ret += "fresh";
	}

	@Override
	public void visit(AnonymousObject expression) {
		ret += "fresh";
	}

	@Override
	public void visit(Root expression) {
		ret += "var_" + expression.getName();
	}

	@Override
	public void visit(Fresh expression) {
		ret += "fresh";
	}

	@Override
	public void visit(ObjectFieldRead expression) {
		ret += "(select ";
				
		ret += "field_" + expression.getName();
		
		ret += " ";
		
		expression.getObject().accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(ObjectFieldWrite expression) {
		ret += "(store ";
		
		ret += "field_" + expression.getName();
		
		ret += " ";
		
		expression.getObject().accept(this);
		
		ret += " ";
		
		expression.getNewValue().accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(ArrayElementRead expression) {
		ret += "(select ";
		
		if (expression.getArray() instanceof ArrayElementWrite) {
			ArrayElementWrite write = (ArrayElementWrite) expression.getArray();
			
			expression.getArray().accept(this);
			
			ret += " ";
			
			write.getArray().accept(this);
			
		} else {
			ret += "arr";
			
			ret += " ";
			
			expression.getArray().accept(this);
		}
		
		ret += " ";
		
		expression.getIndex().accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(ArrayElementWrite expression) {
		ret += "(store ";
		
		ret += "arr";
		
		ret += " ";
		
		expression.getArray().accept(this);
		
		ret += " ";
		
		expression.getIndex().accept(this);
		
		ret += " ";
		
		expression.getNewValue().accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(ArrayLengthRead expression) {
		ret += "(select ";
		
		if (expression.getArray() instanceof ArrayLengthWrite) {
			ArrayLengthWrite write = (ArrayLengthWrite) expression.getArray();
			
			expression.getArray().accept(this);
			
			ret += " ";
			
			write.getArray().accept(this);
			
		} else {
			ret += "arrlen";
			
			ret += " ";
			
			expression.getArray().accept(this);
		}
		
		ret += ")";
	}

	@Override
	public void visit(ArrayLengthWrite expression) {
		ret += "(store ";
		
		ret += "arrlen";
		
		ret += " ";
		
		expression.getArray().accept(this);
		
		ret += " ";
		
		expression.getNewValue().accept(this);
		
		ret += ")";
	}

}