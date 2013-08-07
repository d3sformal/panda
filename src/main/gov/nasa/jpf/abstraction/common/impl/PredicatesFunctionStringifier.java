package gov.nasa.jpf.abstraction.common.impl;

import gov.nasa.jpf.abstraction.common.PredicatesStringifier;
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

public class PredicatesFunctionStringifier extends PredicatesStringifier {

	@Override
	public void visit(Root expression) {
		ret += expression.getName();
	}

	@Override
	public void visit(Fresh expression) {
		ret += expression.getName();
	}

	@Override
	public void visit(ObjectFieldRead expression) {
		ret += "fread(";
		
		ret += expression.getName();
		
		ret += ", ";
		
		expression.getObject().accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(ObjectFieldWrite expression) {
		ret += "fwrite(";
		
		ret += expression.getName();
		
		ret += ", ";
		
		expression.getObject().accept(this);
		
		ret += ", ";
		
		expression.getNewValue().accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(ArrayElementRead expression) {
		ret += "aread(";
		
		ret += "arr";
		
		ret += ", ";
		
		expression.getArray().accept(this);
		
		ret += ", ";
		
		expression.getIndex().accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(ArrayElementWrite expression) {
		ret += "awrite(";
		
		ret += "arr";
		
		ret += ", ";
		
		expression.getArray().accept(this);
		
		ret += ", ";
		
		expression.getIndex().accept(this);
		
		ret += ", ";
		
		expression.getNewValue().accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(ArrayLengthRead expression) {
		ret += "alength(";
		
		ret += "arrlen";
		
		ret += ", ";
		
		expression.getArray().accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(ArrayLengthWrite expression) {
		ret += "alengthupdate(";
		
		ret += "arrlen";
		
		ret += ", ";
		
		expression.getArray().accept(this);
		
		ret += ", ";
		
		expression.getNewValue().accept(this);
		
		ret += ")";
	}

	@Override
	public void visit(AnonymousObject expression) {
		ret += "object";
	}

	@Override
	public void visit(AnonymousArray expression) {
		ret += "array";
	}
	
}
