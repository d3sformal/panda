package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ReturnValue;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultReturnValue;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.concrete.AnonymousExpression;
import gov.nasa.jpf.abstraction.impl.NonEmptyAttribute;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Comparison;
import gov.nasa.jpf.abstraction.common.Context;
import gov.nasa.jpf.abstraction.common.MethodContext;
import gov.nasa.jpf.abstraction.common.ObjectContext;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Predicates;
import gov.nasa.jpf.abstraction.common.StaticContext;
import gov.nasa.jpf.abstraction.predicate.smt.SMT;
import gov.nasa.jpf.abstraction.predicate.smt.SMTException;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A predicate valuation aware of method scope changes
 */
public class ScopedPredicateValuation implements PredicateValuation, Scoped {
	private PredicateValuationStack scopes = new PredicateValuationStack();
	private Predicates predicateSet;
	private Map<Predicate, TruthValue> initialValuation;
    private SMT smt = new SMT();
	
	public ScopedPredicateValuation(PredicateAbstraction abstraction, Predicates predicateSet) {
		this.predicateSet = predicateSet;
		
		scopes.push(new FlatPredicateValuation(smt));
		
		Set<Predicate> predicates = new HashSet<Predicate>();

		for (Context context : predicateSet.contexts) {
			predicates.addAll(context.predicates);
		}
		
		initialValuation = new HashMap<Predicate, TruthValue>();

		/**
		 * Detect initial valuations of Tautologies and Contradictions
		 * 
		 * This is called once and increases comprehensibility of the valuation
		 * It could confuse the user to see that a=a is UNKNOWN
		 */
		if (!predicates.isEmpty()) {
			try {
				initialValuation = smt.valuatePredicates(predicates);
			
				for (Predicate predicate : predicates) {
					// IF NOT A TAUTOLOGY OR CONTRADICTION
					if (initialValuation.get(predicate) == TruthValue.UNKNOWN) {
						initialValuation.put(predicate, TruthValue.UNDEFINED);
					} else {
						initialValuation.put(predicate, initialValuation.get(predicate));
					}
				}
			} catch (SMTException e) {
				e.printStackTrace();
				
				throw e;
			}
		}
		
		if (initialValuation.isEmpty()) {
			for (Predicate predicate : predicates) {
				initialValuation.put(predicate, TruthValue.UNDEFINED);
			}
		}
		
		// Monitor static predicates in the special starting scope
		// This allows to pass static predicates throw clinit/main...
		for (Context context : predicateSet.contexts) {
			if (context instanceof StaticContext) {
				for (Predicate predicate : context.predicates) {
					scopes.top().put(predicate, initialValuation.get(predicate));
				}
			}
		}
	}

    public void close() {
        smt.close();
    }
	
	/**
	 * Collect predicates targeted at the given method and store them in the upcoming scope
	 */
	@Override
	public FlatPredicateValuation createDefaultScope(ThreadInfo threadInfo, MethodInfo method) {
		FlatPredicateValuation valuation = new FlatPredicateValuation(smt);
		
		if (method == null) return valuation;
		
		for (Context context : predicateSet.contexts) {
			if (context instanceof MethodContext) {
				MethodContext methodContext = (MethodContext) context;

				if (!methodContext.getMethod().toString().equals(method.getBaseName())) {
					continue;
				}
			} else if (context instanceof ObjectContext) {
				ObjectContext objectContext = (ObjectContext) context;
				
				if (method.isStatic()) {
					continue;
				}
				
				if (!objectContext.getPackageAndClass().toString().equals(method.getClassName())) {
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
	public void putAll(Map<Predicate, TruthValue> values) {
		scopes.top().putAll(values);
	}
	
	@Override
	public void remove(Predicate predicate) {
		scopes.top().remove(predicate);
	}

	@Override
	public TruthValue get(Predicate predicate) {
		return scopes.top().get(predicate);
	}
	
	@Override
	public SideEffect processMethodCall(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
		MethodInfo method = after.getMethodInfo();
		
		FlatPredicateValuation finalScope = createDefaultScope(threadInfo, method);
		
		RunDetector.detectRunning(VM.getVM(), after.getPC(), before.getPC());

		if (RunDetector.isRunning()) {
			FlatPredicateValuation transitionScope;
			transitionScope = scopes.top().clone();
			
			/**
			 * Collect actual arguments
			 */
			ArrayList<Attribute> attrsList = new ArrayList<Attribute>();
			
			Iterator<Object> it = after.getMethodInfo().attrIterator();
			
			while (it.hasNext()) {
				Attribute attr = (Attribute) it.next();
				
				attrsList.add(attr);
			}
			
			Attribute[] attrs = attrsList.toArray(new Attribute[attrsList.size()]);
			LocalVarInfo args[] = method.getArgumentLocalVars();
			
			Map<Predicate, Predicate> replaced = new HashMap<Predicate, Predicate>();
			
			/**
			 * Take predicates from the callee that describe formal parameters
			 * Replace formal parameters with the concrete assignment
			 * Reason about the value of the predicates using known values of predicates in the caller
			 */
			if (args != null && attrs != null) {
				for (Predicate predicate : finalScope.getPredicates()) {

					Map<AccessExpression, Expression> replacements = new HashMap<AccessExpression, Expression>();
					
					for (int i = 0; i < args.length; ++i) {
						Attribute attr = attrs[i];
											
						if (args[i] != null && attr.getExpression() != null) {
							replacements.put(DefaultRoot.create(args[i].getName()), attr.getExpression());
						}
					}
					
					replaced.put(predicate.replace(replacements), predicate);
				}
				
				Map<Predicate, TruthValue> valuation = transitionScope.evaluatePredicates(replaced.keySet());
				
				for (Predicate predicate : replaced.keySet()) {
					finalScope.put(replaced.get(predicate), valuation.get(predicate));
				}
			}
		}
		
		scopes.push(finalScope);
		
		return null;
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
	public SideEffect processMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after, SideEffect sideEffect) {
		RunDetector.detectRunning(VM.getVM(), after.getPC(), before.getPC());

        if (RunDetector.isRunning()) {
			Attribute attr = (Attribute) after.getResultAttr();
			ReturnValue ret = DefaultReturnValue.create(after.getPC(), threadInfo.getTopFrameMethodInfo().isReferenceReturnType());
			
			FlatPredicateValuation scope;
			
			scope = scopes.top(1);
			
			attr = Attribute.ensureNotNull(attr);
			
			Map<Predicate, Predicate> predicates = new HashMap<Predicate, Predicate>();
			Set<Predicate> determinants = new HashSet<Predicate>();
			
			/**
			 * Determine values of predicates over the return value based on the concrete symbolic expression being returned
			 */
			for (Predicate predicate : getPredicates()) {
				
				if (isPredicateOverReturn(predicate)) {
					Map<AccessExpression, Expression> replacements = new HashMap<AccessExpression, Expression>();
					
					replacements.put(DefaultReturnValue.create(), attr.getExpression());

					Predicate determinant = predicate.replace(replacements);

					predicates.put(determinant, predicate);
					determinants.add(determinant);
				}
			}
			
			Map<Predicate, TruthValue> valuation = evaluatePredicates(determinants);
			
			for (Predicate predicate : valuation.keySet()) {
				Map<AccessExpression, Expression> replacements = new HashMap<AccessExpression, Expression>();

				replacements.put(DefaultReturnValue.create(), ret);

				scope.put(predicates.get(predicate).replace(replacements), valuation.get(predicate));
			}
					
			after.setOperandAttr(new NonEmptyAttribute(attr.getAbstractValue(), ret));
        }
		
		return processVoidMethodReturn(threadInfo, before, after, sideEffect);
	}
	
	@Override
	public SideEffect processVoidMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after, SideEffect sideEffect) {
        if (RunDetector.isRunning()) {
			AffectedAccessExpressions affected = (AffectedAccessExpressions) sideEffect; // MAY BE USED OR NOT ... OVERAPPROXIMATING THIS MAY SAVE A LOT WHEN DETERMINING THE sideEffect SET
			FlatPredicateValuation scope;
			
			scope = scopes.top(1);
			
			boolean sameObject = before.getThis() == after.getThis();
			
			ArrayList<Attribute> attrsList = new ArrayList<Attribute>();
			
			Iterator<Object> it = before.getMethodInfo().attrIterator();
			
			while (it.hasNext()) {
				Attribute attr = (Attribute) it.next();
				
				attrsList.add(attr);
			}
			
			Attribute[] attrs = attrsList.toArray(new Attribute[attrsList.size()]);
			LocalVarInfo[] args = before.getMethodInfo().getArgumentLocalVars() == null ? new LocalVarInfo[0] : before.getMethodInfo().getArgumentLocalVars();
			LocalVarInfo[] locals = before.getLocalVars() == null ? new LocalVarInfo[0] : before.getLocalVars();
			
			Set<LocalVarInfo> referenceArgs = new HashSet<LocalVarInfo>();
			Set<LocalVarInfo> notWantedLocalVariables = new HashSet<LocalVarInfo>();
			
			/**
			 * Determine what reference arguments were written to (they contain a different reference from the initial one)
			 * 
			 * those parameters and predicates over them cannot be used to argue the value of predicates over the initial value back in the caller
			 */
			for (int i = 0; i < args.length; ++i) {
				LocalVarInfo l = args[i];
				
				if (l != null) {
					
					if (!l.isNumeric() && !l.isBoolean()) {
						referenceArgs.add(l);
					}
				
					Attribute actualAttribute = (Attribute) before.getLocalAttr(l.getSlotIndex());
					
					actualAttribute = Attribute.ensureNotNull(actualAttribute);
					
					Expression originalExpr = attrs[i].getExpression();
					Expression actuaExpr = actualAttribute.getExpression();
					
					boolean different = false;
					
					different |= originalExpr == null && actuaExpr != null;
					different |= originalExpr != null && actuaExpr == null;
					different |= originalExpr != null && actuaExpr != null && !originalExpr.equals(actuaExpr);
					
					// Someone has changed the argument, we cannot use predicates about it to infer information about the original value supplied by the caller
					if (different) {
						notWantedLocalVariables.add(l);
					}
				}
			}
			
			/**
			 * No predicate containing a callee's local variable (not a parameter) can be used to infer value of predicates in the caller
			 */
			// Local variables are out of scope
			for (LocalVarInfo l : locals) {
				if (l != null) {
					notWantedLocalVariables.add(l);
				}
			}
			
			notWantedLocalVariables.removeAll(referenceArgs);
			
			FlatPredicateValuation relevant = new FlatPredicateValuation(smt);
			
			// Filter out predicates from the callee that cannot be used for propagation to the caller 
			for (Predicate predicate : getPredicates()) {
				TruthValue value = get(predicate);
				
				boolean isAnonymous = false;
				
				Map<AccessExpression, Expression> replacements = new HashMap<AccessExpression, Expression>();

				for (int i = 0; i < args.length; ++i) {
					if (args[i] != null && !args[i].isNumeric() && !args[i].isBoolean()) {
						replacements.put(DefaultRoot.create(args[i].getName()), attrs[i].getExpression());
						
						isAnonymous |= attrs[i].getExpression() instanceof AnonymousExpression;
					}
				}
				
				predicate = predicate.replace(replacements);

				boolean isUnwanted = false;
				
				for (LocalVarInfo l : notWantedLocalVariables) {
					for (AccessExpression path : predicate.getPaths()) {
						isUnwanted |= path.isLocalVariable() && path.getRoot().getName().equals(l.getName());
					}
				}
				
				if (!isUnwanted) {
					relevant.put(predicate, value);
					
					// Handling mainly constructor (object still anonymous) 
					if (isAnonymous) {
						scope.put(predicate, value);
					}
				}
			}
					
			Set<Predicate> toBeUpdated = new HashSet<Predicate>();
			
			for (Predicate predicate : scope.getPredicates()) {
				
				boolean canBeAffected = false;
				
				for (AccessExpression path : predicate.getPaths()) {
					canBeAffected |= path.getRoot().isThis() && sameObject;
					canBeAffected |= path.isStatic();
					
					for (AccessExpression affectedPath : affected) {
						canBeAffected |= affectedPath.isPrefixOf(path);
					}
				}
				
				if (canBeAffected) {
					toBeUpdated.add(predicate);
				}
			}
			
			Map<Predicate, TruthValue> valuation = relevant.evaluatePredicates(toBeUpdated);
			
			for (Predicate predicate : valuation.keySet()) {
				TruthValue value = valuation.get(predicate);
				
				scope.put(predicate, value);
			}
        }
		
		scopes.pop();
		
		return null;
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
	public Map<Predicate, TruthValue> evaluatePredicates(Set<Predicate> predicates) {
		return scopes.top().evaluatePredicates(predicates);
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
		return getPredicates(0);
	}
	
	public Set<Predicate> getPredicates(int i) {
		return scopes.top(i).getPredicates();
	}

    @Override
    public FlatPredicateValuation get(int depth) {
        return scopes.top(depth);
    }

}
