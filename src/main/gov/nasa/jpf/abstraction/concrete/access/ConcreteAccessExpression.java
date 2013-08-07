package gov.nasa.jpf.abstraction.concrete.access;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.concrete.VariableID;

import java.util.Map;

public class ConcreteAccessExpression {
	public Map<AccessExpression, VariableID> partialResolve() {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}
	
	public Map<AccessExpression, VariableID> partialExhaustiveResolve() {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}
	
	public Map<AccessExpression, CompleteVariableID> resolve() {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}
}
