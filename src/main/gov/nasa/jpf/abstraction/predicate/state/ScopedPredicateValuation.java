package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.predicate.common.Context;
import gov.nasa.jpf.abstraction.predicate.common.MethodContext;
import gov.nasa.jpf.abstraction.predicate.common.ObjectContext;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.common.Predicates;
import gov.nasa.jpf.vm.MethodInfo;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

public class ScopedPredicateValuation implements PredicateValuation, Scoped {
	private Stack<FlatPredicateValuation> scopes = new Stack<FlatPredicateValuation>();
	private Predicates predicateSet;
	
	public ScopedPredicateValuation(Predicates predicateSet) {
		this.predicateSet = predicateSet;
	}
	
	@Override
	public FlatPredicateValuation createDefaultScope(MethodInfo method) {
		FlatPredicateValuation valuation = new FlatPredicateValuation();
		
		if (method == null) return valuation;

		for (Context context : predicateSet.contexts) {
			if (context instanceof MethodContext) {
				MethodContext methodContext = (MethodContext) context;

				if (!methodContext.getMethod().toString(AccessPath.NotationPolicy.DOT_NOTATION).equals(method.getBaseName())) {
					continue;
				}
			} else if (context instanceof ObjectContext) {
				ObjectContext objectContext = (ObjectContext) context;
				
				if (!objectContext.getObject().toString(AccessPath.NotationPolicy.DOT_NOTATION).equals(method.getClassName())) {
					continue;
				}
			}

			for (Predicate predicate : context.predicates) {
				valuation.put(predicate, TruthValue.UNDEFINED);
			}
		}
		
		return valuation;
	}

	@Override
	public void put(Predicate predicate, TruthValue value) {
		scopes.lastElement().put(predicate, value);
	}

	@Override
	public TruthValue get(Predicate predicate) {
		return scopes.lastElement().get(predicate);
	}

	@Override
	public Iterator<Entry<Predicate, TruthValue>> iterator() {
		return scopes.lastElement().iterator();
	}
	
	@Override
	public void processMethodCall(MethodInfo method) {
		scopes.push(createDefaultScope(method));
	}

	@Override
	public void processMethodReturn() {
		scopes.pop();
	}
	
	@Override
	public void store(Scope scope) {
		if (scope instanceof FlatPredicateValuation) {
			scopes.push((FlatPredicateValuation) scope.clone());
		} else {
			throw new RuntimeException("Invalid scope type being pushed!");
		}
	}
	
	@Override
	public void restore(Scope scope) {
		if (scope instanceof FlatPredicateValuation) {
			scopes.pop();
			scopes.push((FlatPredicateValuation) scope.clone());
		} else {
			throw new RuntimeException("Invalid scope type being pushed!");
		}
	}
	
	@Override
	public FlatPredicateValuation memorize() {
		return scopes.lastElement().clone();
	}
	
	@Override
	public String toString() {
		return scopes.lastElement().toString();
	}

	@Override
	public void reevaluate(AccessPath affected, Expression expression) {
		scopes.lastElement().reevaluate(affected, expression);
	}
	
	@Override
	public int count() {
		return scopes.size();
	}

}
