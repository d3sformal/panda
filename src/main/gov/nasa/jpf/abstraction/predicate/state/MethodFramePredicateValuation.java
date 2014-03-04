package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.abstraction.util.Pair;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.UpdatedPredicate;
import gov.nasa.jpf.abstraction.common.impl.NullExpression;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.predicate.smt.PredicateValueDeterminingInfo;
import gov.nasa.jpf.abstraction.predicate.smt.SMT;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.VM;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.Collections;

/**
 * A predicate valuation for a single scope
 */
public class MethodFramePredicateValuation implements PredicateValuation, Scope {
	private HashMap<Predicate, TruthValue> valuations = new HashMap<Predicate, TruthValue>();
    private HashMap<Predicate, Set<Predicate>> determinantCache = new HashMap<Predicate, Set<Predicate>>();
    private SMT smt;

    public MethodFramePredicateValuation(SMT smt) {
        this.smt = smt;
    }

    public void addObject(AnonymousObject object) {
        // add anonymous object predicates (through all its constructors)
        
        // 1)
        // take all predicates over fields and no local variables
        // take all paths that are rooted in the new object
        // assume their initial (default) value to be 0 / null
        // ask SMT for the predicate values
        
        // or
        
        // 2)
        // add an artificial predicate for each field = 0 / null as TRUE

        ElementInfo elementInfo = object.getReference().getElementInfo();
        ClassInfo classInfo = elementInfo.getClassInfo();

        if (object instanceof AnonymousArray) {
            // predicates over elements maybe
        } else {
            // initialize all fields across all super classes
            while (classInfo != null) {
                for (FieldInfo field : classInfo.getInstanceFields()) {
                    if (field.isReference()) {
                        put(Equals.create(DefaultObjectFieldRead.create(object, field.getName()), NullExpression.create()), TruthValue.TRUE);
                    } else {
                        put(Equals.create(DefaultObjectFieldRead.create(object, field.getName()), Constant.create(0)), TruthValue.TRUE);
                    }
                }

                classInfo = classInfo.getSuperClass();
            }
        }
    }

	@SuppressWarnings("unchecked")
	@Override
	public MethodFramePredicateValuation clone() {
		MethodFramePredicateValuation clone = new MethodFramePredicateValuation(smt);
		
		clone.valuations = (HashMap<Predicate, TruthValue>)valuations.clone();

        /**
         * No need to memorize
         */
		clone.determinantCache = new HashMap<Predicate, Set<Predicate>>();
		
		return clone;
	}
	
    /**
     * @param universe Universe of all predicates that may or may not determine the value of this predicate
     * @return A selection of those predicates from the universe that may directly determine the value of this predicate
     */
    private static void selectDeterminants(Predicate predicate, Set<Predicate> universe, Set<Predicate> outDeterminants) {
        Set<AccessExpression> paths = new HashSet<AccessExpression>();
		Set<AccessExpression> candidatePaths = new HashSet<AccessExpression>();
        Set<AccessExpression> prefixes = new HashSet<AccessExpression>();

        predicate.addAccessExpressionsToSet(paths);
    	
        // filter all determinants from all the candidates
    	for (Predicate candidate : universe) {
            boolean alreadyAdded = false;

            candidate.addAccessExpressionsToSet(candidatePaths);

	        for (AccessExpression path : paths) {
                // check shared access expressions between the candidate and the input predicate
                // stop at first match
			    for (AccessExpression candidatePath : candidatePaths) {
                    candidatePath.addAllPrefixesToSet(prefixes);

				    for (AccessExpression candidateSubPath : prefixes) {
					    if (candidateSubPath.isSimilarToPrefixOf(path)) {
						    outDeterminants.add(candidate);

                            alreadyAdded = true;
                            break;
					    }
				    }

                    prefixes.clear();

                    if (alreadyAdded) {
                        break;
                    }
			    }

                if (alreadyAdded) {
                    break;
                }
		    }

            candidatePaths.clear();
	    }
    }
    
    /**
     * Finds a transitive closure of all predicates that may infer the value of this one.
     * 
     * @param universe Universe of all predicates that may or may not determine the value of this predicate
     * @return A selection of those predicates from the universe that may determine the value of this predicate
     */
	private Set<Predicate> computeDeterminantClosure(Predicate predicate, Set<Predicate> universe) {
        if (!determinantCache.containsKey(predicate)) {
    		Set<Predicate> cur;
	    	Set<Predicate> ret = new HashSet<Predicate>();
            
            selectDeterminants(predicate, universe, ret);
		
    		int prevSize = 0;
		
	    	while (prevSize != ret.size()) {
		    	prevSize = ret.size();

			    cur = new HashSet<Predicate>();

    			for (Predicate p : ret) {
	    			selectDeterminants(p, universe, cur);
		    	}
			
                // each `p` in `ret` is contained in `selectDet(p)` and therefore also in `cur`
                // thus the following statement avoids unnecessary merge of the sets
		    	ret = cur;
    		}

            determinantCache.put(predicate, ret);
        }

		return determinantCache.get(predicate);
	}
	
	/**
	 * Suppose valuation of given predicates change, update all affected predicates, too
	 * 
	 * Keep doing so until a fixpoint is reached
	 */
	private void performCascadeReevaluation(Map<Predicate, TruthValue> updated) {
		Map<Predicate, PredicateValueDeterminingInfo> predicates = new HashMap<Predicate, PredicateValueDeterminingInfo>();
		
		int size = updated.size();
		
		for (Predicate affectedCandidate : valuations.keySet()) {
			Set<Predicate> updatedDeterminants = new HashSet<Predicate>();
            
            selectDeterminants(affectedCandidate, updated.keySet(), updatedDeterminants);
			
			if (!updatedDeterminants.isEmpty()) {
				Predicate positiveWeakestPrecondition = affectedCandidate;
				Predicate negativeWeakestPrecondition = Negation.create(affectedCandidate);
				
				Map<Predicate, TruthValue> determinants = new HashMap<Predicate, TruthValue>();
					
				for (Predicate determinant : computeDeterminantClosure(positiveWeakestPrecondition, valuations.keySet())) {
					determinants.put(determinant, valuations.get(determinant));
				}
				for (Predicate determinant : computeDeterminantClosure(negativeWeakestPrecondition, valuations.keySet())) {
					determinants.put(determinant, valuations.get(determinant));
				}
				for (Predicate determinant : updatedDeterminants) {
					determinants.put(determinant, updated.get(determinant));
				}
				
				predicates.put(affectedCandidate, new PredicateValueDeterminingInfo(positiveWeakestPrecondition, negativeWeakestPrecondition, determinants));
			}
		}
		
		updated.putAll(smt.valuatePredicates(predicates));
		
		if (size != updated.size()) {
			performCascadeReevaluation(updated);
		}
	}

	/**
	 * Force (create/overwrite) valuation of the given predicate to the given value
	 */
	@Override
	public void put(Predicate predicate, TruthValue value) {
		Map<Predicate, TruthValue> newValuations = new HashMap<Predicate, TruthValue>();
		
		newValuations.put(predicate, value);
		
		Config config = VM.getVM().getJPF().getConfig();
		String key = "abstract.branch.reevaluate_predicates";

        // Change of the set of predicates -> need to recompute determinant sets
        if (!valuations.containsKey(predicate)) {
            determinantCache.clear();
        }
		
		if (config.containsKey(key) && config.getBoolean(key)) {
			performCascadeReevaluation(newValuations);
		}
		
		valuations.putAll(newValuations);
	}
	
	/**
	 * Same as the above (for more predicates at once)
	 */
	@Override
	public void putAll(Map<Predicate, TruthValue> values) {
		for (Predicate predicate : values.keySet()) {
			put(predicate, values.get(predicate));
		}
	}
	
	@Override
	public void remove(Predicate predicate) {
		valuations.remove(predicate);
	}

	@Override
	public TruthValue get(Predicate predicate) {
		return valuations.get(predicate);
	}

	@Override
	public String toString() {	
		StringBuilder ret = new StringBuilder();

        Set<Predicate> sorted = new TreeSet<Predicate>(new Comparator<Predicate>() {
            @Override
            public int compare(Predicate p1, Predicate p2) {
                return p1.toString(Notation.DOT_NOTATION).compareTo(p2.toString(Notation.DOT_NOTATION));
            }
        });

        sorted.addAll(valuations.keySet());
		
		for (Predicate p : sorted) {
			String predicate = p.toString(Notation.DOT_NOTATION);
			
			ret.append(predicate);
		    ret.append(" : ");
			ret.append(valuations.get(p));
			ret.append("\n");
		}
		
		return ret.toString();
	}

	@Override
	public void reevaluate(AccessExpression affected, Set<AccessExpression> resolvedAffected, Expression expression) {
		Map<Predicate, PredicateValueDeterminingInfo> predicates = new HashMap<Predicate, PredicateValueDeterminingInfo>();

        Set<AccessExpression> paths = new HashSet<AccessExpression>();

		for (Predicate predicate : valuations.keySet()) {
			boolean affects = false;

			/**
			 * Affected may contain completely different paths:
			 * 
			 * assume a == b.c == d.e.f[ ? ]
			 * then
			 * 
			 * a.x := 3
			 * 
			 * causes
			 * 
			 * a.x, b.c.x, d.e.f[ ? ].x
			 * 
			 * be affected paths, hence
			 * 
			 * a.x = 3
			 * b.c.x + 2 = g + h
			 * d.e.f[0].x / 3 = 4
			 * d.e.f[1].x / 3 = 4
			 * d.e.f[k + l * m].x / 3 = 4
			 * 
			 * are affected predicates
			 */

            predicate.addAccessExpressionsToSet(paths);

            // Do not cycle through the collection if the other is empty
            if (!paths.isEmpty()) {
    			for (AccessExpression path1 : resolvedAffected) {
	    			for (AccessExpression path2 : paths) {
		    			affects = affects || path1.isSimilarToPrefixOf(path2);
			    	}
			    }
            }

			paths.clear();

			/**
			 * If the predicate may be affected, compute preconditions and determinants and schedule the predicate for reevaluation
			 */
			if (affects) {
				Predicate positiveWeakestPrecondition = predicate;
				Predicate negativeWeakestPrecondition = Negation.create(predicate);
					
				if (expression != null) {
					positiveWeakestPrecondition = UpdatedPredicate.create(positiveWeakestPrecondition, affected, expression);
					negativeWeakestPrecondition = UpdatedPredicate.create(negativeWeakestPrecondition, affected, expression);
				}
				
				Map<Predicate, TruthValue> determinants = new HashMap<Predicate, TruthValue>();
				
				for (Predicate determinant : computeDeterminantClosure(positiveWeakestPrecondition, valuations.keySet())) {
					determinants.put(determinant, valuations.get(determinant));
				}
				for (Predicate determinant : computeDeterminantClosure(negativeWeakestPrecondition, valuations.keySet())) {
					determinants.put(determinant, valuations.get(determinant));
				}
				
				predicates.put(predicate, new PredicateValueDeterminingInfo(positiveWeakestPrecondition, negativeWeakestPrecondition, determinants));
			}
		}
		
		/**
		 * Save a void call to SMT
		 */
		if (predicates.isEmpty()) return;
		
		/**
		 * Collective reevaluation
		 */
		Map<Predicate, TruthValue> newValuations = smt.valuatePredicates(predicates);
			
		valuations.putAll(newValuations);
	}

    @Override
    public void dropAllPredicatesIncidentWith(AccessExpression expr) {
        Set<AccessExpression> paths = new HashSet<AccessExpression>();
        Set<Predicate> toBeRemoved = new HashSet<Predicate>();

        for (Predicate p : getPredicates()) {
            p.addAccessExpressionsToSet(paths);

            for (AccessExpression path : paths) {
                if (expr.isPrefixOf(path)) {
                    toBeRemoved.add(p);
                }
            }

            paths.clear();
        }

        for (Predicate p : toBeRemoved) {
            valuations.remove(p);
        }

        // Change of the set of predicates -> need to recompute determinant sets
        if (!toBeRemoved.isEmpty()) {
            determinantCache.clear();
        }
    }
	
    /**
     * Evaluate a single predicate regardless of a statement
     *
     * Used to detect tautologies in the initial phase.
     * Used to valuate branching conditions - a (possibly) new predicate whose value depends solely on current predicate valuation.
     */
	@Override
	public TruthValue evaluatePredicate(Predicate predicate) {
		Set<Predicate> predicates = new HashSet<Predicate>();
		
		predicates.add(predicate);
		
		return evaluatePredicates(predicates).get(predicate);
	}
	
	/**
	 * Evaluate predicates regardless of a statement
     *
     * Batch variant of evaluatePredicate
	 */
	@Override
	public Map<Predicate, TruthValue> evaluatePredicates(Set<Predicate> predicates) {
		if (predicates.isEmpty()) return Collections.emptyMap();

		Map<Predicate, PredicateValueDeterminingInfo> input = new HashMap<Predicate, PredicateValueDeterminingInfo>();

        // This takes long
		for (Predicate predicate : predicates) {
			Predicate positiveWeakestPrecondition = predicate;
			Predicate negativeWeakestPrecondition = Negation.create(predicate);
	
			Map<Predicate, TruthValue> determinants = new HashMap<Predicate, TruthValue>();
				
			for (Predicate determinant : computeDeterminantClosure(positiveWeakestPrecondition, valuations.keySet())) {
				determinants.put(determinant, valuations.get(determinant));
			}
			for (Predicate determinant : computeDeterminantClosure(negativeWeakestPrecondition, valuations.keySet())) {
				determinants.put(determinant, valuations.get(determinant));
			}
			
			input.put(predicate, new PredicateValueDeterminingInfo(positiveWeakestPrecondition, negativeWeakestPrecondition, determinants));
		}

		return smt.valuatePredicates(input);
	}
	
	@Override
	public int count() {
		return valuations.keySet().size();
	}

	@Override
	public boolean containsKey(Predicate predicate) {
		return valuations.containsKey(predicate);
	}

	@Override
	public Set<Predicate> getPredicates() {
		return valuations.keySet();
	}

    @Override
    public Integer evaluateExpression(Expression expression) {
        List<Pair<Predicate, TruthValue>> determinants = new LinkedList<Pair<Predicate, TruthValue>>();
                
        // An auxiliary predicate used to get all determining predicates
        // Determinants are defined only for predicates, not expressions
        Predicate determinantSelector = Equals.create(expression, Constant.create(0));

        // Collect determinant valuations
        for (Predicate predicate : computeDeterminantClosure(determinantSelector, getPredicates())) {
            determinants.add(new Pair<Predicate, TruthValue>(predicate, get(predicate)));
        }

        // Query any model
        Integer model1 = smt.getModel(expression, determinants);

        if (model1 != null) {
            // Forbid the first model, look for others
            determinants.add(new Pair<Predicate, TruthValue>(Equals.create(expression, Constant.create(model1)), TruthValue.FALSE));

            Integer model2 = smt.getModel(expression, determinants);

            // Only if there is a single satisfying value for the expression
            if (model2 == null) {
                return model1;
            }
        }

        return null;
    }
	
}
