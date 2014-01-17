package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ArrayElementWrite;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthRead;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthWrite;
import gov.nasa.jpf.abstraction.common.access.Fresh;
import gov.nasa.jpf.abstraction.common.access.Method;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrayLengths;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrays;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultField;
import gov.nasa.jpf.abstraction.common.impl.NullExpression;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.concrete.EmptyExpression;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Disjunction;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Implication;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.MethodContext;
import gov.nasa.jpf.abstraction.common.ObjectContext;
import gov.nasa.jpf.abstraction.common.Predicates;
import gov.nasa.jpf.abstraction.common.StaticContext;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.UpdatedPredicate;

/**
 * An interface for visitors of the hierarchy:
 * 
 * predicates
 *   -> context
 *     -> predicate
 *       -> expression
 * 
 * @see gov.nasa.jpf.abstraction.common.PredicatesStringifier, gov.nasa.jpf.abstraction.common.impl.PredicatesDotStringifier
 */
public interface PredicatesComponentVisitor {
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
	public void visit(UpdatedPredicate predicate);
	public void visit(EmptyExpression expression);
	public void visit(NullExpression expression);
	public void visit(Add expression);
	public void visit(Subtract expression);
	public void visit(Multiply expression);
	public void visit(Divide expression);
	public void visit(Modulo expression);
	public void visit(DefaultArrays meta);
	public void visit(DefaultArrayLengths meta);
	public void visit(DefaultField meta);
	public void visit(Root expression);
	public void visit(Fresh expression);
	public void visit(PackageAndClass expression);
	public void visit(Method expression);
	public void visit(ObjectFieldRead expression);
	public void visit(ObjectFieldWrite expression);
	public void visit(ArrayElementRead expression);
	public void visit(ArrayElementWrite expression);
	public void visit(ArrayLengthRead expression);
	public void visit(ArrayLengthWrite expression);
	public void visit(Constant expression);
	public void visit(AnonymousObject expression);
	public void visit(AnonymousArray expression);
	public void visit(Undefined expression);
}