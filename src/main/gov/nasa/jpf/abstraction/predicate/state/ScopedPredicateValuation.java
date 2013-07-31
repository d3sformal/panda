package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.predicate.common.Context;
import gov.nasa.jpf.abstraction.predicate.common.MethodContext;
import gov.nasa.jpf.abstraction.predicate.common.ObjectContext;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.common.Predicates;
import gov.nasa.jpf.abstraction.predicate.smt.SMT;
import gov.nasa.jpf.abstraction.predicate.smt.SMTException;
import gov.nasa.jpf.vm.MethodInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ScopedPredicateValuation implements PredicateValuation, Scoped {
	private PredicateValuationStack scopes = new PredicateValuationStack();
	private Predicates predicateSet;
	
	public ScopedPredicateValuation(Predicates predicateSet) {
		this.predicateSet = predicateSet;
	}
	
	@Override
	public FlatPredicateValuation createDefaultScope(MethodInfo method) {
		FlatPredicateValuation valuation = new FlatPredicateValuation();
		
		if (method == null) return valuation;
		
		Set<Predicate> predicates = new HashSet<Predicate>();

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

			predicates.addAll(context.predicates);
		}
		
		try {
			Map<Predicate, TruthValue> initialValuation = new SMT().valuatePredicates(predicates);
			
			for (Predicate predicate : predicates) {
				// IF NOT A TAUTOLOGY OR CONTRADICTION
				if (initialValuation.get(predicate) == TruthValue.UNKNOWN) {
					valuation.put(predicate, TruthValue.UNDEFINED);
				} else {
					valuation.put(predicate, initialValuation.get(predicate));
				}
			}
		} catch (SMTException e) {			
			for (Predicate predicate : predicates) {
				valuation.put(predicate, TruthValue.UNDEFINED);
			}
			
			e.printStackTrace();
		}
		
		return valuation;
	}

	@Override
	public void put(Predicate predicate, TruthValue value) {
		scopes.top().put(predicate, value);
	}

	@Override
	public TruthValue get(Predicate predicate) {
		return scopes.top().get(predicate);
	}

	@Override
	public Iterator<Entry<Predicate, TruthValue>> iterator() {
		return scopes.top().iterator();
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
	public void restore(Scopes scopes) {
		if (scopes instanceof PredicateValuationStack) {
			this.scopes = (PredicateValuationStack) scopes;
		} else {
			throw new RuntimeException("Invalid scopes type being restored!");
		}
	}
	
	@Override
	public PredicateValuationStack memorize() {
		return scopes.clone();
	}
	
	@Override
	public String toString() {
		return scopes.count() > 0 ? scopes.top().toString() : "";
	}

	@Override
	public void reevaluate(AccessPath affected, Set<AccessPath> resolvedAffected, Expression expression) {
		scopes.top().reevaluate(affected, resolvedAffected, expression);
	}
	
	@Override
	public TruthValue evaluatePredicate(Predicate predicate) {
		return scopes.top().evaluatePredicate(predicate);
	}
	
	@Override
	public int count() {
		return scopes.count() > 0 ? scopes.top().count() : 0;
	}

}
