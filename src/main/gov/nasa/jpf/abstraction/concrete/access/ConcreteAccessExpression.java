package gov.nasa.jpf.abstraction.concrete.access;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.concrete.impl.PathResolution;

public interface ConcreteAccessExpression extends AccessExpression {
	public PathResolution partialResolve();
	public PathResolution partialExhaustiveResolve();
	public PathResolution resolve();
}
