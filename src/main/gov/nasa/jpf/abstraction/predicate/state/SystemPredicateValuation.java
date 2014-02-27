package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ReturnValue;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultReturnValue;
import gov.nasa.jpf.abstraction.common.access.Root;
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
import gov.nasa.jpf.vm.Instruction;

import gov.nasa.jpf.jvm.bytecode.LocalVariableInstruction;
import gov.nasa.jpf.jvm.bytecode.IINC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A predicate valuation aware of method scope changes
 */
public class SystemPredicateValuation extends CallAnalyzer implements PredicateValuation, Scoped {

	/**
	 * Stacks of scopes (pushed by invoke, poped by return) separately for all threads
	 */
	private Map<Integer, PredicateValuationStack> scopes = new HashMap<Integer, PredicateValuationStack>();
    
	private Predicates predicateSet;
	private Map<Predicate, TruthValue> initialValuation;
    private SMT smt = new SMT();
    private Integer currentThreadID;
	
	public SystemPredicateValuation(PredicateAbstraction abstraction, Predicates predicateSet) {
		this.predicateSet = predicateSet;
		
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
	}

    public void close() {
        smt.close();
    }
	
	/**
	 * Collect predicates targeted at the given method and store them in the upcoming scope
	 */
	@Override
	public MethodFramePredicateValuation createDefaultScope(ThreadInfo threadInfo, MethodInfo method) {
		MethodFramePredicateValuation valuation = new MethodFramePredicateValuation(smt);
		
		if (method == null) return valuation;
		
        // Collect relevant contexts and predicates stored in them
        // Match context method with actual method
        // Match context object with actual object
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
		scopes.get(currentThreadID).top().put(predicate, value);
	}
	

	@Override
	public void putAll(Map<Predicate, TruthValue> values) {
		scopes.get(currentThreadID).top().putAll(values);
	}
	
	@Override
	public void remove(Predicate predicate) {
		scopes.get(currentThreadID).top().remove(predicate);
	}

	@Override
	public TruthValue get(Predicate predicate) {
		return scopes.get(currentThreadID).top().get(predicate);
	}
	
	@Override
	public void processMethodCall(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
		MethodInfo method = after.getMethodInfo();
		
		// Scope to be added as the callee scope
		MethodFramePredicateValuation calleeScope = createDefaultScope(threadInfo, method);
		
		RunDetector.detectRunning(VM.getVM(), after.getPC(), before.getPC());

		if (RunDetector.isRunning()) {
			// Copy of the current caller scope - to avoid modifications - may not be needed now, it is not different from .top() and it is not modified here
			MethodFramePredicateValuation callerScope = scopes.get(currentThreadID).top();
			
			Map<Predicate, Predicate> replaced = new HashMap<Predicate, Predicate>();
			
			/**
			 * Take predicates from the callee that describe formal parameters
			 * Replace formal parameters with the concrete assignment
			 * Reason about the value of the predicates using known values of predicates in the caller
			 */
            boolean[] slotInUse = new boolean[method.getNumberOfStackArguments()];

            getArgumentSlotUsage(method, slotInUse);

            // Each predicate to be initialised for the callee
            for (Predicate predicate : calleeScope.getPredicates()) {
                Map<AccessExpression, Expression> replacements = new HashMap<AccessExpression, Expression>();

                // Replace formal parameters with actual parameters
                for (int slotIndex = 0; slotIndex < method.getNumberOfStackArguments(); ++slotIndex) {
                    if (slotInUse[slotIndex]) {
                        // Actual symbolic parameter
                        Attribute attr = Attribute.ensureNotNull((Attribute) after.getSlotAttr(slotIndex));

                        LocalVarInfo arg = after.getLocalVarInfo(slotIndex);
                        String name = arg == null ? null : arg.getName();

                        replacements.put(DefaultRoot.create(name, slotIndex), attr.getExpression());
                    }
                }

                replaced.put(predicate.replace(replacements), predicate);
            }

            // Valuate predicates in the caller scope, and adopt the valuation for the callee predicates
            Map<Predicate, TruthValue> valuation = callerScope.evaluatePredicates(replaced.keySet());

            for (Predicate predicate : replaced.keySet()) {
                calleeScope.put(replaced.get(predicate), valuation.get(predicate));
            }
		}
		
		scopes.get(currentThreadID).push(method.getFullName(), calleeScope);
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
		RunDetector.detectRunning(VM.getVM(), after.getPC(), before.getPC());

		Attribute attr = Attribute.ensureNotNull((Attribute) after.getResultAttr());
		ReturnValue ret = DefaultReturnValue.create(after.getPC(), threadInfo.getTopFrameMethodInfo().isReferenceReturnType());
			
        if (RunDetector.isRunning()) {
			MethodFramePredicateValuation scope;
			
			scope = scopes.get(currentThreadID).top(1);
			
			Map<Predicate, Predicate> predicates = new HashMap<Predicate, Predicate>();
			Set<Predicate> determinants = new HashSet<Predicate>();
			
			/**
			 * Determine values of predicates over the return value based on the concrete symbolic expression being returned
			 */
			for (Predicate predicate : getPredicates()) {
				
				if (isPredicateOverReturn(predicate)) {
					Predicate determinant = predicate.replace(DefaultReturnValue.create(), attr.getExpression());

					predicates.put(determinant, predicate);
					determinants.add(determinant);
				}
			}
			
            // Valuate predicates over `return` access expression using the return expression
            // Predicate: return < 3
            // Statement: return 2
            //
            // return < 3 is determined by 2 < 3
			Map<Predicate, TruthValue> valuation = evaluatePredicates(determinants);
			
			for (Predicate predicate : valuation.keySet()) {
				scope.put(predicates.get(predicate).replace(DefaultReturnValue.create(), ret), valuation.get(predicate));
			}
        }
			
		after.setOperandAttr(new NonEmptyAttribute(attr.getAbstractValue(), ret));
		
        // The rest is the same as if no return happend
		processVoidMethodReturn(threadInfo, before, after);
	}
	
    @Override
    public void processVoidMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
        if (RunDetector.isRunning()) {
            MethodFramePredicateValuation callerScope;

            callerScope = scopes.get(currentThreadID).top(1);

            boolean sameObject = before.getThis() == after.getThis();

            // Collect original symbolic arguments of the method
            Set<Root> referenceArgs = new HashSet<Root>();
            Set<Root> notWantedLocalVariables = new HashSet<Root>();

            /**
             * Determine what reference arguments were written to (they contain a different reference from the initial one)
             *
             * those parameters and predicates over them cannot be used to argue about the value of predicates over the initial value back in the caller
             */
            MethodInfo method = before.getMethodInfo();

            boolean[] slotInUse = new boolean[method.getNumberOfStackArguments()];
            boolean[] argIsPrimitive = new boolean[method.getNumberOfStackArguments()];

            getArgumentSlotUsage(method, slotInUse);
            getArgumentSlotType(method, argIsPrimitive);

            Iterator<Object> originalArgumentAttributes = method.attrIterator();

            // Replace formal parameters with actual parameters
            for (int slotIndex = 0; slotIndex < method.getNumberOfStackArguments(); ++slotIndex) {
                if (slotInUse[slotIndex]) {
                    // Actual symbolic parameter
                    Attribute attr = Attribute.ensureNotNull((Attribute) before.getSlotAttr(slotIndex));

                    LocalVarInfo arg = method.getLocalVar(slotIndex, 0);
                    String name = arg == null ? null : arg.getName();

                    Root l = DefaultRoot.create(name, slotIndex);

                    // Determine type of the arguments
                    if (!argIsPrimitive[slotIndex]) {
                        referenceArgs.add(l);
                    }

                    Expression originalExpr = Attribute.ensureNotNull((Attribute) originalArgumentAttributes.next()).getExpression();
                    Expression actualExpr = Attribute.ensureNotNull((Attribute) before.getLocalAttr(slotIndex)).getExpression();

                    boolean different = false;

                    different |= originalExpr == null && actualExpr != null;
                    different |= originalExpr != null && actualExpr == null;
                    different |= originalExpr != null && actualExpr != null && !originalExpr.equals(actualExpr);

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
            for (Instruction instruction : method.getInstructions()) {
                if (instruction instanceof LocalVariableInstruction) {
                    LocalVariableInstruction lvInsn = (LocalVariableInstruction) instruction;

                    notWantedLocalVariables.add(DefaultRoot.create(lvInsn.getLocalVariableName(), lvInsn.getLocalVariableIndex()));
                } else if (instruction instanceof IINC) {
                    IINC iinc = (IINC) instruction;

                    LocalVarInfo lv = method.getLocalVar(iinc.getIndex(), iinc.getPosition() + iinc.getLength());
                    String name = lv == null ? null : lv.getName();

                    notWantedLocalVariables.add(DefaultRoot.create(name, iinc.getIndex()));
                }
            }

            // Arguments that are of a reference type are not bound to the callee scope and may be used to determine truth value of a predicate refering to it
            notWantedLocalVariables.removeAll(referenceArgs);

            // Collection of predicates in callee and caller that have additional value for update of the caller
            MethodFramePredicateValuation relevant = new MethodFramePredicateValuation(smt);

            Set<AccessExpression> temporaryPathsHolder = new HashSet<AccessExpression>();

            // Filter out predicates from the callee that cannot be used for propagation to the caller
            for (Predicate predicate : getPredicates()) {
                TruthValue value = get(predicate);

                boolean isAnonymous = false;

                Map<AccessExpression, Expression> replacements = new HashMap<AccessExpression, Expression>();

                // Replace formal parameters present in the predicate with actual expressions
                for (int slotIndex = 0; slotIndex < method.getNumberOfStackArguments(); ++slotIndex) {
                    if (slotInUse[slotIndex]) {
                        if (!argIsPrimitive[slotIndex]) {
                            LocalVarInfo arg = method.getLocalVar(slotIndex, 0);
                            String name = arg == null ? null : arg.getName();

                            Expression actualExpr = Attribute.ensureNotNull((Attribute) before.getLocalAttr(slotIndex)).getExpression();

                            replacements.put(DefaultRoot.create(name, slotIndex), actualExpr);

                            isAnonymous |= actualExpr instanceof AnonymousExpression;
                        }
                    }
                }

                predicate = predicate.replace(replacements);

                boolean isUnwanted = false;

                predicate.addAccessExpressionsToSet(temporaryPathsHolder);

                // If any of the symbols used in the predicate has changed
                for (Root l : notWantedLocalVariables) {
                    for (AccessExpression path : temporaryPathsHolder) {
                        isUnwanted |= path.isLocalVariable() && path.getRoot().getName().equals(l.getName());
                    }
                }

                // If the predicate uses only allowed symbols (those that do not lose their meaning by changing scope)
                if (!isUnwanted) {
                    relevant.put(predicate, value);

                    // Handling mainly constructor (object still anonymous)
                    if (isAnonymous) {
                        callerScope.put(predicate, value);
                    }
                }

                temporaryPathsHolder.clear();
            }

            // Usable predicates from callee with replaced occurences of formal parameters were collected
            // Select predicates that need to be updated (refer to an object that may have been modified by the callee: static, o.field, modified heap)

            Set<Predicate> toBeUpdated = new HashSet<Predicate>();

            for (Predicate predicate : callerScope.getPredicates()) {
                predicate.addAccessExpressionsToSet(temporaryPathsHolder);

                boolean canBeAffected = false;

                for (AccessExpression path : temporaryPathsHolder) {
                    canBeAffected |= path.getRoot().isThis() && sameObject;
                    canBeAffected |= path.isStatic();
                }

                for (AccessExpression path : temporaryPathsHolder) {
                    Iterator<Object> originalActualParameters = method.attrIterator();

                    for (int slotIndex = 0; slotIndex < method.getNumberOfStackArguments(); ++slotIndex) {
                        if (slotInUse[slotIndex]) {
                            Expression expr = Attribute.ensureNotNull((Attribute) originalActualParameters.next()).getExpression();

                            if (!argIsPrimitive[slotIndex]) {
                                AccessExpression actualParameter = (AccessExpression) expr;

                                // reference-passed objects may have been affected by the method
                                canBeAffected |= actualParameter.isPrefixOf(path);
                            }
                        }
                    }
                }

                // Predicates are either updated (when they were possibly affected) or can be used for value inference.
                // We take all predicates that are not to-be-updated as possibly relevant (for simplicity). Actual determining predicates are selected later.

                if (canBeAffected) {
                    toBeUpdated.add(predicate);
                } else {
                    relevant.put(predicate, callerScope.get(predicate));
                }

                temporaryPathsHolder.clear();
            }

            // Use the relevant predicates to valuate predicates that need to be updated
            Map<Predicate, TruthValue> valuation = relevant.evaluatePredicates(toBeUpdated);

            for (Predicate predicate : valuation.keySet()) {
                TruthValue value = valuation.get(predicate);

                callerScope.put(predicate, value);
            }
        }

        scopes.get(currentThreadID).pop();
    }
	
	@Override
    public void restore(Map<Integer, ? extends Scopes> scopes) {
        this.scopes.clear();
        for (Integer threadId : scopes.keySet()) {
            Scopes threadScopes = scopes.get(threadId);

            if (threadScopes instanceof PredicateValuationStack) {
                PredicateValuationStack threadPredicateValuationScopes = (PredicateValuationStack) threadScopes;

                this.scopes.put(threadId, threadPredicateValuationScopes.clone());
            } else {
                throw new RuntimeException("Invalid scopes type being restored!");
            }
        }
    }
    
    @Override
    public Map<Integer, PredicateValuationStack> memorize() {
        Map<Integer, PredicateValuationStack> scopesClone = new HashMap<Integer, PredicateValuationStack>();
        
        for (Integer threadId : scopes.keySet()) {
            scopesClone.put(threadId, scopes.get(threadId).clone());
        }

        return scopesClone;
    }
    
	@Override
	public String toString() {
		return scopes.get(currentThreadID).count() > 0 ? scopes.get(currentThreadID).top().toString() : "";
	}

	@Override
	public void reevaluate(AccessExpression affected, Set<AccessExpression> resolvedAffected, Expression expression) {
		scopes.get(currentThreadID).top().reevaluate(affected, resolvedAffected, expression);
	}

    @Override
    public void dropAllPredicatesIncidentWith(AccessExpression expr) {
        scopes.get(currentThreadID).top().dropAllPredicatesIncidentWith(expr);
    }
	
	@Override
	public TruthValue evaluatePredicate(Predicate predicate) {
		return scopes.get(currentThreadID).top().evaluatePredicate(predicate);
	}
	
	@Override
	public Map<Predicate, TruthValue> evaluatePredicates(Set<Predicate> predicates) {
		return scopes.get(currentThreadID).top().evaluatePredicates(predicates);
	}

    @Override
    public Integer evaluateExpression(Expression expression) {
        return scopes.get(currentThreadID).top().evaluateExpression(expression);
    }
	
	@Override
	public int count() {
		return scopes.get(currentThreadID).count() > 0 ? scopes.get(currentThreadID).top().count() : 0;
	}

	@Override
	public int depth() {
        return scopes.get(currentThreadID).count();
	}

	@Override
	public boolean containsKey(Predicate predicate) {
		return scopes.get(currentThreadID).top().containsKey(predicate);
	}

	@Override
	public Set<Predicate> getPredicates() {
		return getPredicates(0);
	}
	
	public Set<Predicate> getPredicates(int i) {
		return scopes.get(currentThreadID).top(i).getPredicates();
	}

    @Override
    public MethodFramePredicateValuation get(int depth) {
        return get(currentThreadID, depth);
    }

    public MethodFramePredicateValuation get(int threadID, int depth) {
        return scopes.get(threadID).top(depth);
    }

    @Override
    public void addThread(ThreadInfo threadInfo) {
		PredicateValuationStack threadStack = new PredicateValuationStack();
        threadStack.push("-- Dummy stop scope --", new MethodFramePredicateValuation(smt));
        scopes.put(threadInfo.getId(), threadStack);
		
		// Monitor static predicates in the special starting scope
		// This allows to pass static predicates throw clinit/main...
		for (Context context : predicateSet.contexts) {
			if (context instanceof StaticContext) {
				for (Predicate predicate : context.predicates) {
					scopes.get(threadInfo.getId()).top().put(predicate, initialValuation.get(predicate));
				}
			}
		}
    }

    @Override
    public void scheduleThread(ThreadInfo threadInfo) {
        scheduleThread(threadInfo.getId());
    }

    @Override
    public void scheduleThread(int threadID) {
        currentThreadID = threadID;
    }

    @Override
    public void print() {
        scopes.get(currentThreadID).print();
    }

}
