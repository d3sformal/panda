package gov.nasa.jpf.abstraction.predicate.state;

import java.util.HashSet;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;

public class AffectedAccessExpressions extends HashSet<AccessExpression> implements SideEffect {
	private static final long serialVersionUID = 666L;
}
