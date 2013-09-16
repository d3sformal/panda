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
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrays;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultField;
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
				
		expression.getField().accept(this);
		
		ret += " ";
		
		expression.getObject().accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(ObjectFieldWrite expression) {
		ret += "(store ";
		
		expression.getField().accept(this);
		
		ret += " ";
		
		expression.getObject().accept(this);
		
		ret += " ";
		
		expression.getNewValue().accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(ArrayElementRead expression) {
		ret += "(select ";
		
		ret += "(select ";
		
		expression.getArrays().accept(this);
		
		ret += " ";
		
		expression.getArray().accept(this);
		
		ret += ")";
		
		ret += " ";
		
		expression.getIndex().accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(ArrayElementWrite expression) {
		ret += "(store "; // Store the updated array into arrays
		
		expression.getArrays().accept(this);
		
		ret += " ";
		
		expression.getArray().accept(this);

		ret += " ";

		ret += "(store "; // Update the array

		ret += "(select "; // Select the concrete array

		expression.getArrays().accept(this);

		ret += " ";
		
		expression.getArray().accept(this);

		ret += ")";
		
		ret += " ";
		
		expression.getIndex().accept(this);
		
		ret += " ";
		
		expression.getNewValue().accept(this);
		
		ret += ")";
		
		ret += ")";
	}

	@Override
	public void visit(ArrayLengthRead expression) {
		ret += "(select ";
			
		expression.getArrayLengths().accept(this);
		
		ret += " ";
		
		expression.getArray().accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(ArrayLengthWrite expression) {
		ret += "(store ";
		
		expression.getArrayLengths().accept(this);
		
		ret += " ";
		
		expression.getArray().accept(this);
		
		ret += " ";
		
		expression.getNewValue().accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(DefaultArrays meta) {
		ret += "arr";
	}

	@Override
	public void visit(DefaultField meta) {
		ret += "field_" + meta.getName();
	}
	
	@Override
	public void visit(PackageAndClass packageAndClass) {
		ret += "class_" + packageAndClass.getName().replace("_", "__").replace('.', '_');
	}

}