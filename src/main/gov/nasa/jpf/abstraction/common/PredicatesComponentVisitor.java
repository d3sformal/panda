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
import gov.nasa.jpf.abstraction.common.access.SpecialVariable;
import gov.nasa.jpf.abstraction.common.access.impl.Select;
import gov.nasa.jpf.abstraction.common.access.impl.Store;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrayLengths;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrays;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultField;
import gov.nasa.jpf.abstraction.common.impl.ArraysAssign;
import gov.nasa.jpf.abstraction.common.impl.FieldAssign;
import gov.nasa.jpf.abstraction.common.impl.New;
import gov.nasa.jpf.abstraction.common.impl.NullExpression;
import gov.nasa.jpf.abstraction.common.impl.VariableAssign;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.concrete.EmptyExpression;

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
    public void visit(Expressions expressions);
    public void visit(ObjectPredicateContext context);
    public void visit(MethodPredicateContext context);
    public void visit(MethodAssumePrePredicateContext context);
    public void visit(MethodAssumePostPredicateContext context);
    public void visit(StaticPredicateContext context);
    public void visit(ObjectExpressionContext context);
    public void visit(MethodExpressionContext context);
    public void visit(StaticExpressionContext context);
    public void visit(Negation predicate);
    public void visit(LessThan predicate);
    public void visit(Equals predicate);
    public void visit(Tautology predicate);
    public void visit(Contradiction predicate);
    public void visit(Conjunction predicate);
    public void visit(Disjunction predicate);
    public void visit(Implication predicate);
    public void visit(UpdatedPredicate predicate);
    public void visit(VariableAssign predicate);
    public void visit(FieldAssign predicate);
    public void visit(ArraysAssign predicate);
    public void visit(New predicate);
    public void visit(EmptyExpression expression);
    public void visit(NullExpression expression);
    public void visit(Add expression);
    public void visit(Subtract expression);
    public void visit(Multiply expression);
    public void visit(Divide expression);
    public void visit(Modulo expression);
    public void visit(UninterpretedShiftLeft expression);
    public void visit(UninterpretedShiftRight expression);
    public void visit(IfThenElse expression);
    public void visit(DefaultArrays meta);
    public void visit(DefaultArrayLengths meta);
    public void visit(DefaultField meta);
    public void visit(Root expression);
    public void visit(SpecialVariable expression);
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
    public void visit(Select select);
    public void visit(Store store);
}
