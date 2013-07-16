package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.concrete.EmptyExpression;
import gov.nasa.jpf.abstraction.predicate.common.Contradiction;
import gov.nasa.jpf.abstraction.predicate.common.Equals;
import gov.nasa.jpf.abstraction.predicate.common.LessThan;
import gov.nasa.jpf.abstraction.predicate.common.MethodContext;
import gov.nasa.jpf.abstraction.predicate.common.ObjectContext;
import gov.nasa.jpf.abstraction.predicate.common.Predicates;
import gov.nasa.jpf.abstraction.predicate.common.StaticContext;
import gov.nasa.jpf.abstraction.predicate.common.Tautology;

public interface PredicatesVisitor {
	public void visit(Predicates predicates);
	public void visit(ObjectContext context);
	public void visit(MethodContext context);
	public void visit(StaticContext context);
	public void visit(Negation predicate);
	public void visit(LessThan predicate);
	public void visit(Equals predicate);
	public void visit(Tautology predicate);
	public void visit(Contradiction predicate);
	public void visit(Conjunction predicate);
	public void visit(Disjunction predicate);
	public void visit(Implication predicate);
	public void visit(EmptyExpression expression);
	public void visit(Add expression);
	public void visit(Subtract expression);
	public void visit(Multiply expression);
	public void visit(Divide expression);
	public void visit(Modulo expression);
	public void visit(AccessPath expression);
	public void visit(AccessPathRootElement element);
	public void visit(AccessPathSubElement element);
	public void visit(AccessPathIndexElement element);
	public void visit(Constant expression);
}
