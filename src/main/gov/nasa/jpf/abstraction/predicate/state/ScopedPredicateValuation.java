package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ReturnValue;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultAccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultReturnValue;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.NotationPolicy;
import gov.nasa.jpf.abstraction.concrete.access.impl.DefaultConcreteReturnValue;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.abstraction.impl.NonEmptyAttribute;
import gov.nasa.jpf.abstraction.predicate.common.Comparison;
import gov.nasa.jpf.abstraction.predicate.common.Context;
import gov.nasa.jpf.abstraction.predicate.common.MethodContext;
import gov.nasa.jpf.abstraction.predicate.common.ObjectContext;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.common.Predicates;
import gov.nasa.jpf.abstraction.predicate.smt.SMT;
import gov.nasa.jpf.abstraction.predicate.smt.SMTException;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ScopedPredicateValuation implements PredicateValuation, Scoped {
	private PredicateValuationStack scopes = new PredicateValuationStack();
	private Predicates predicateSet;
	private Map<Predicate, TruthValue> initialValuation;
	
	public ScopedPredicateValuation(Predicates predicateSet) {
		this.predicateSet = predicateSet;
		
		Set<Predicate> predicates = new HashSet<Predicate>();

		for (Context context : predicateSet.contexts) {
			predicates.addAll(context.predicates);
		}
		
		if (!predicates.isEmpty()) {
			try {
				initialValuation = new SMT().valuatePredicates(predicates);
			
				for (Predicate predicate : predicates) {
					// IF NOT A TAUTOLOGY OR CONTRADICTION
					if (initialValuation.get(predicate) == TruthValue.UNKNOWN) {
						initialValuation.put(predicate, TruthValue.UNDEFINED);
					} else {
						initialValuation.put(predicate, initialValuation.get(predicate));
					}
				}
				
				return;
			} catch (SMTException e) {
				e.printStackTrace();
				
				throw e;
			}
		}
		
		initialValuation = new HashMap<Predicate, TruthValue>();
		
		for (Predicate predicate : predicates) {
			initialValuation.put(predicate, TruthValue.UNDEFINED);
		}
	}
	
	@Override
	public FlatPredicateValuation createDefaultScope(MethodInfo method) {
		FlatPredicateValuation valuation = new FlatPredicateValuation();
		
		if (method == null) return valuation;
		
		for (Context context : predicateSet.contexts) {
			if (context instanceof MethodContext) {
				MethodContext methodContext = (MethodContext) context;

				if (!methodContext.getMethod().toString(NotationPolicy.DOT_NOTATION).equals(method.getBaseName())) {
					continue;
				}
			} else if (context instanceof ObjectContext) {
				ObjectContext objectContext = (ObjectContext) context;
				
				if (!objectContext.getObject().toString(NotationPolicy.DOT_NOTATION).equals(method.getClassName())) {
					continue;
				}
			}

			for (Predicate predicate : context.predicates) {
				valuation.put(predicate, initialValuation.get(predicate));
			}
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
	public void processMethodCall(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
		MethodInfo method = after.getMethodInfo();
		
		FlatPredicateValuation transitionScope;
		FlatPredicateValuation finalScope = createDefaultScope(method);
		
		if (scopes.count() == 0) {
			transitionScope = createDefaultScope(method);
		} else {
			transitionScope = scopes.top().clone();
		}
		
		Object attrs[] = after.getArgumentAttrs(method);
		LocalVarInfo args[] = method.getArgumentLocalVars();
		
		if (args != null && attrs != null) {
			for (Map.Entry<Predicate, TruthValue> entry : finalScope) {
				Predicate replaced = entry.getKey();
				
				for (int i = 1; i < args.length; ++i) {
					Attribute attr = (Attribute) attrs[i];
					
					if (attr == null) attr = new EmptyAttribute();
					
					replaced = replaced.replace(DefaultAccessExpression.createFromString(args[i].getName()), attr.getExpression());
				}
				
				finalScope.put(entry.getKey(), transitionScope.evaluatePredicate(replaced));
			}
		}
		
		scopes.push(finalScope);
	}
	
	private static boolean isPredicateOverReturn(Predicate predicate) {
		if (predicate instanceof Negation) {
			Negation n = (Negation) predicate;
			
			return isPredicateOverReturn(n.predicate);
		}
		
		if (predicate instanceof Comparison) {
			Comparison c = (Comparison) predicate;
			
			return c.a instanceof ReturnValue || c.b instanceof ReturnValue;
		}
		
		return false;
	}
	
	@Override
	public void processMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {		
		Attribute attr = (Attribute) after.getResultAttr();
		ReturnValue ret = DefaultConcreteReturnValue.create(threadInfo, after.getPC());
		
		FlatPredicateValuation scope;
		
		if (scopes.count() == 1) {
			scope = new FlatPredicateValuation();
		} else {
			scope = scopes.top(1);
		}
		
		if (attr == null) attr = new EmptyAttribute();
		
		for (Map.Entry<Predicate, TruthValue> entry : this) {
			Predicate predicate = entry.getKey();
			
			if (isPredicateOverReturn(predicate)) {
				Predicate determinant = predicate.replace(DefaultReturnValue.create(), attr.getExpression());
				Predicate result = predicate.replace(DefaultReturnValue.create(), ret);
				
				scope.put(result, scopes.top().evaluatePredicate(determinant));
			}
		}
				
		after.setOperandAttr(new NonEmptyAttribute(attr.getAbstractValue(), ret));
		
		processVoidMethodReturn(threadInfo, before, after);
	}
	
	@Override
	public void processVoidMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
		FlatPredicateValuation scope;
		
		if (scopes.count() == 1) {
			scope = new FlatPredicateValuation();
		} else {
			scope = scopes.top(1);
		}
		
		boolean sameObject = before.getThis() == after.getThis();
		
		for (Map.Entry<Predicate, TruthValue> entry : scope) {
			Predicate predicate = entry.getKey();
			
			boolean canInferValuation = !predicate.determinantClosure(getPredicates()).isEmpty();
			boolean dependsOnShared = false;
			boolean hardToDecide = false;
			
			for (AccessExpression path : predicate.getPaths()) {
				dependsOnShared |= /* IS THIS             */ DefaultRoot.create("this").isPrefixOf(path) && sameObject;
				dependsOnShared |= /* IS STATIC FIELD     */ false;
				
				hardToDecide    |= /* PATH IS A LOCAL VAR */ path instanceof Root;
			}
			
			if (canInferValuation && dependsOnShared) {
				TruthValue value = evaluatePredicate(predicate);
				
				if (hardToDecide) {
					value = TruthValue.UNKNOWN;
				}
				
				scope.put(predicate, value);
			}
			
			if (!canInferValuation && dependsOnShared) {
				scope.put(predicate, TruthValue.UNKNOWN);
			}
		}
		
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
			this.scopes = (PredicateValuationStack) scopes.clone();
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
	public void reevaluate(AccessExpression affected, Set<AccessExpression> resolvedAffected, Expression expression) {
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

	@Override
	public boolean containsKey(Predicate predicate) {
		return scopes.top().containsKey(predicate);
	}

	@Override
	public Set<Predicate> getPredicates() {
		return scopes.top().getPredicates();
	}

}
