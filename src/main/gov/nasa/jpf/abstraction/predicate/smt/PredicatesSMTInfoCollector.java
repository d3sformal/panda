package gov.nasa.jpf.abstraction.predicate.smt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.AccessPathIndexElement;
import gov.nasa.jpf.abstraction.common.AccessPathRootElement;
import gov.nasa.jpf.abstraction.common.AccessPathSubElement;
import gov.nasa.jpf.abstraction.common.Add;
import gov.nasa.jpf.abstraction.common.ArrayLength;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Divide;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Modulo;
import gov.nasa.jpf.abstraction.common.Multiply;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.Subtract;
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
	
	private AccessPath updatedPath = null;
	private Expression newExpression = null;
	
	private Set<String> vars = new HashSet<String>();
	private Set<String> fields = new HashSet<String>();
	
	private Predicate lastPredicate = null;
	private Map<Predicate, Set<Predicate>> additionalPredicates = new HashMap<Predicate, Set<Predicate>>();
	
	private Set<AccessPath> objects = new HashSet<AccessPath>();

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
		lastPredicate = predicate;
		
		predicate.predicate.accept(this);
	}

	@Override
	public void visit(LessThan predicate) {
		lastPredicate = predicate;
		
		predicate.a.accept(this);
		predicate.b.accept(this);
	}

	@Override
	public void visit(Equals predicate) {
		lastPredicate = predicate;
		
		predicate.a.accept(this);
		predicate.b.accept(this);
	}

	@Override
	public void visit(Tautology predicate) {
		lastPredicate = predicate;
	}

	@Override
	public void visit(Contradiction predicate) {
		lastPredicate = predicate;
	}

	@Override
	public void visit(Conjunction predicate) {
		lastPredicate = predicate;
		
		predicate.a.accept(this);
		predicate.b.accept(this);
	}

	@Override
	public void visit(Disjunction predicate) {
		lastPredicate = predicate;
		
		predicate.a.accept(this);
		predicate.b.accept(this);
	}

	@Override
	public void visit(Implication predicate) {
		lastPredicate = predicate;
		
		predicate.a.accept(this);
		predicate.b.accept(this);
	}

	@Override
	public void visit(UpdatedPredicate predicate) {
		lastPredicate = predicate;
		
		updatedPath = predicate.path;
		newExpression = predicate.expression;
		
		predicate.predicate.accept(this);
		predicate.path.accept(this);
		predicate.expression.accept(this);
	}

	@Override
	public void visit(EmptyExpression expression) {
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

	@Override
	public void visit(ArrayLength expression) {
		Predicate predicate = Negation.create(LessThan.create(expression, Constant.create(0)));
		
		if (updatedPath != null && newExpression != null) {
			predicate = UpdatedPredicate.create(predicate, updatedPath, newExpression);
		}
		
		addAdditionalPredicate(predicate);
		
		expression.path.accept(this);
	}

	private void addAdditionalPredicate(Predicate predicate) {
		if (!additionalPredicates.containsKey(lastPredicate)) {
			additionalPredicates.put(lastPredicate, new HashSet<Predicate>());
		}
		
		additionalPredicates.get(lastPredicate).add(predicate);
	}

	@Override
	public void visit(AccessPath expression) {
		addObjects(expression);
		
		expression.getRoot().accept(this);
	}

	private void addObjects(AccessPath expression) {
		AccessPath path = expression.clone();
		
		while (path.getLength() > 1) {
			objects.add(path);
			
			path = path.cutTail();
		}
			
		objects.add(path);
	}

	@Override
	public void visit(AccessPathRootElement element) {
		if (element.getNext() != null) {
			element.getNext().accept(this);
		}
		
		vars.add(element.getName());
	}

	@Override
	public void visit(AccessPathSubElement element) {
		if (element.getNext() != null) {
			element.getNext().accept(this);
		}
		
		fields.add(element.getName());
	}

	@Override
	public void visit(AccessPathIndexElement element) {
		element.getIndex().accept(this);
		
		if (element.getNext() != null) {
			element.getNext().accept(this);
		}
	}

	@Override
	public void visit(Constant expression) {
	}

	@Override
	public void visit(AnonymousObject expression) {
	}

	@Override
	public void visit(AnonymousArray expression) {
		expression.length.accept(this);
	}
	
	public Set<Predicate> getAdditionalPredicates(Predicate predicate) {
		if (!additionalPredicates.containsKey(predicate)) {
			return new HashSet<Predicate>();
		}
		
		return additionalPredicates.get(predicate);
	}
	
	public Set<String> getVars() {
		return vars;
	}
	
	public Set<String> getFields() {
		return fields;
	}
	
	public Set<AccessPath> getObjects() {
		return objects;
	}

}
