package gov.nasa.jpf.abstraction.predicate.smt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ArrayElementWrite;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthRead;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthWrite;
import gov.nasa.jpf.abstraction.common.access.Fresh;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Method;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrayLengths;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrays;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultField;
import gov.nasa.jpf.abstraction.common.impl.NullExpression;
import gov.nasa.jpf.abstraction.common.Add;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Divide;
import gov.nasa.jpf.abstraction.common.Modulo;
import gov.nasa.jpf.abstraction.common.Multiply;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.PredicatesVisitable;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.Subtract;
import gov.nasa.jpf.abstraction.common.Undefined;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.concrete.EmptyExpression;
import gov.nasa.jpf.abstraction.predicate.common.Conjunction;
import gov.nasa.jpf.abstraction.predicate.common.Context;
import gov.nasa.jpf.abstraction.predicate.common.Contradiction;
import gov.nasa.jpf.abstraction.predicate.common.Disjunction;
import gov.nasa.jpf.abstraction.predicate.common.Equals;
import gov.nasa.jpf.abstraction.predicate.common.Implication;
import gov.nasa.jpf.abstraction.predicate.common.LessThan;
import gov.nasa.jpf.abstraction.predicate.common.MethodContext;
import gov.nasa.jpf.abstraction.predicate.common.ObjectContext;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.common.Predicates;
import gov.nasa.jpf.abstraction.predicate.common.StaticContext;
import gov.nasa.jpf.abstraction.predicate.common.Tautology;
import gov.nasa.jpf.abstraction.predicate.common.UpdatedPredicate;

public class PredicatesSMTInfoCollector implements PredicatesVisitor {
	private Set<String> classes = new HashSet<String>();
	private Set<String> vars = new HashSet<String>();
	private Set<String> fields = new HashSet<String>();
	private Set<Integer> fresh = new HashSet<Integer>();
	
	private PredicatesVisitable currentCollectable = null;
	private Map<PredicatesVisitable, Set<Predicate>> additionalPredicates = new HashMap<PredicatesVisitable, Set<Predicate>>();
	
	private Set<AccessExpression> objects = new HashSet<AccessExpression>();

	@Override
	public void visit(Predicates predicates) {
		for (Context context : predicates.contexts) {
			context.accept(this);
		}
	}

	@Override
	public void visit(ObjectContext context) {
		for (Predicate predicate : context.predicates) {
			predicate.accept(this);
		}
	}

	@Override
	public void visit(MethodContext context) {
		for (Predicate predicate : context.predicates) {
			predicate.accept(this);
		}
	}

	@Override
	public void visit(StaticContext context) {
		for (Predicate predicate : context.predicates) {
			predicate.accept(this);
		}
	}

	@Override
	public void visit(Negation predicate) {		
		predicate.predicate.accept(this);
	}

	@Override
	public void visit(LessThan predicate) {		
		predicate.a.accept(this);
		predicate.b.accept(this);
	}

	@Override
	public void visit(Equals predicate) {		
		predicate.a.accept(this);
		predicate.b.accept(this);
	}

	@Override
	public void visit(Tautology predicate) {
	}

	@Override
	public void visit(Contradiction predicate) {
	}

	@Override
	public void visit(Conjunction predicate) {		
		predicate.a.accept(this);
		predicate.b.accept(this);
	}

	@Override
	public void visit(Disjunction predicate) {		
		predicate.a.accept(this);
		predicate.b.accept(this);
	}

	@Override
	public void visit(Implication predicate) {		
		predicate.a.accept(this);
		predicate.b.accept(this);
	}

	@Override
	public void visit(Add expression) {
		expression.a.accept(this);
		expression.b.accept(this);
	}

	@Override
	public void visit(Subtract expression) {
		expression.a.accept(this);
		expression.b.accept(this);
	}

	@Override
	public void visit(Multiply expression) {
		expression.a.accept(this);
		expression.b.accept(this);
	}

	@Override
	public void visit(Divide expression) {
		expression.a.accept(this);
		expression.b.accept(this);
	}

	@Override
	public void visit(Modulo expression) {
		expression.a.accept(this);
		expression.b.accept(this);
	}

	private void addAdditionalPredicate(Predicate predicate) {		
		if (!additionalPredicates.containsKey(currentCollectable)) {
			additionalPredicates.put(currentCollectable, new HashSet<Predicate>());
		}
		
		additionalPredicates.get(currentCollectable).add(predicate);
	}

	private void addObject(AccessExpression expression) {
		if (!(expression.getRoot() instanceof Fresh)) {
			objects.add(expression);
		}
	}

	@Override
	public void visit(Constant expression) {
	}

	@Override
	public void visit(AnonymousObject expression) {
		fresh.add(expression.getReference().getObjectRef());
	}

	@Override
	public void visit(AnonymousArray expression) {
		fresh.add(expression.getReference().getObjectRef());
		
		expression.getArrayLength().accept(this);
	}
	
	public Set<Predicate> getAdditionalPredicates(PredicatesVisitable collectable) {
		if (!additionalPredicates.containsKey(collectable)) {
			return new HashSet<Predicate>();
		}
		
		return additionalPredicates.get(collectable);
	}
	
	public Set<String> getVars() {
		return vars;
	}
	
	public Set<String> getFields() {
		return fields;
	}
	
	public Set<String> getClasses() {
		return classes;
	}
	
	public Set<Integer> getFresh() {
		return fresh;
	}
	
	public Set<AccessExpression> getObjects() {
		return objects;
	}

	@Override
	public void visit(Root expression) {
		vars.add(expression.getName());
		
		addObject(expression);
	}

	@Override
	public void visit(Fresh expression) {	
	}

	@Override
	public void visit(ObjectFieldRead expression) {
		expression.getObject().accept(this);
		expression.getField().accept(this);
		
		addObject(expression);
	}

	@Override
	public void visit(ObjectFieldWrite expression) {
		expression.getObject().accept(this);
		expression.getField().accept(this);
		expression.getNewValue().accept(this);
	}

	@Override
	public void visit(ArrayElementRead expression) {	
		expression.getArray().accept(this);
		expression.getArrays().accept(this);
		expression.getIndex().accept(this);
		
		addObject(expression);
	}

	@Override
	public void visit(ArrayElementWrite expression) {
		expression.getArray().accept(this);
		expression.getArrays().accept(this);
		expression.getIndex().accept(this);
		expression.getNewValue().accept(this);
	}

	@Override
	public void visit(ArrayLengthRead expression) {
		Predicate predicate = Negation.create(LessThan.create(expression, Constant.create(0)));
		
		addAdditionalPredicate(predicate);
				
		expression.getArray().accept(this);
		expression.getArrayLengths().accept(this);
		
		addObject(expression);
	}

	@Override
	public void visit(ArrayLengthWrite expression) {
		expression.getArray().accept(this);
		expression.getArrayLengths().accept(this);
		expression.getNewValue().accept(this);
	}

	@Override
	public void visit(DefaultArrays meta) {
	}

	@Override
	public void visit(DefaultArrayLengths meta) {
	}

	@Override
	public void visit(DefaultField meta) {
		fields.add(meta.getName());
	}

	@Override
	public void visit(Undefined expression) {
		throw new SMTException("UNDEFINED IN THE INPUT");
	}
	
	@Override
	public void visit(NullExpression expression) {
	}
	
	@Override
	public void visit(EmptyExpression expression) {
		throw new SMTException("EMPTY EXPRESSION IN THE INPUT");
	}

	@Override
	public void visit(UpdatedPredicate predicate) {		
		predicate.apply().accept(this);
	}
	
	public void collect(PredicatesVisitable collectable) {
		if (collectable instanceof UpdatedPredicate) {
			UpdatedPredicate updated = (UpdatedPredicate) collectable;
			
			currentCollectable = updated.getPredicate();
		} else {
			currentCollectable = collectable;
		}
		
		collectable.accept(this);
	}

	@Override
	public void visit(PackageAndClass expression) {
		classes.add(expression.getName());
	}

	@Override
	public void visit(Method expression) {
	}

}
