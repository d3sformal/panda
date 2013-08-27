package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteAccessExpression;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class FlatSymbolTable implements SymbolTable, Scope {

	@Override
	public int count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<AccessExpression> processPrimitiveStore(
			ConcreteAccessExpression to) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<AccessExpression> processObjectStore(Expression from,
			ConcreteAccessExpression to) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isArray(ConcreteAccessExpression path) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isObject(ConcreteAccessExpression path) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPrimitive(ConcreteAccessExpression path) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public FlatSymbolTable clone() {
		return this;
	}

}
