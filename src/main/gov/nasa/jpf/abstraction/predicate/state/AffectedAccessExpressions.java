package gov.nasa.jpf.abstraction.predicate.state;

import java.util.HashSet;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;

/**
 * Collection of access expressions that the symbol table (@see gov.nasa.jpf.abstraction.predicate.state.SymbolTable and descendants) marked as affected by an assignment or other instruction
 */
public class AffectedAccessExpressions extends HashSet<AccessExpression> implements SideEffect {
	private static final long serialVersionUID = 666L;
}
