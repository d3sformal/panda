package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.predicate.concrete.EmptyExpression;

public interface PredicatesVisitor {
	public void visit(Predicates predicates);
	public void visit(ObjectContext context);
	public void visit(MethodContext context);
	public void visit(StaticContext context);
	public void visit(Negation predicate);
	public void visit(LessThan predicate);
	public void visit(Equals predicate);
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
