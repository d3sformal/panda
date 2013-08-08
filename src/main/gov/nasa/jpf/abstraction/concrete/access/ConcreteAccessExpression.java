package gov.nasa.jpf.abstraction.concrete.access;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.concrete.VariableID;

import java.util.Map;

public interface ConcreteAccessExpression extends AccessExpression {
	public Map<AccessExpression, VariableID> partialResolve();
	public Map<AccessExpression, VariableID> partialExhaustiveResolve();
	public Map<AccessExpression, CompleteVariableID> resolve();
}
