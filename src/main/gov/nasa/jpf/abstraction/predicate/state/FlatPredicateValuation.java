package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.UpdatedPredicate;
import gov.nasa.jpf.abstraction.predicate.smt.PredicateValueDeterminingInfo;
import gov.nasa.jpf.abstraction.predicate.smt.SMT;
import gov.nasa.jpf.vm.VM;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.Comparator;

/**
 * A predicate valuation for a single scope
 */
public class FlatPredicateValuation implements PredicateValuation, Scope {
	private HashMap<Predicate, TruthValue> valuations = new HashMap<Predicate, TruthValue>();
    private SMT smt;

    public FlatPredicateValuation(SMT smt) {
        this.smt = smt;
    }

	@SuppressWarnings("unchecked")
	@Override
	public FlatPredicateValuation clone() {
		FlatPredicateValuation clone = new FlatPredicateValuation(smt);
		
		clone.valuations = (HashMap<Predicate, TruthValue>)valuations.clone();
		
		return clone;
	}
	
    /**
     * @param universe Universe of all predicates that may or may not determine the value of this predicate
     * @return A selection of those predicates from the universe that may directly determine the value of this predicate
     */
    public static Set<Predicate> selectDeterminants(Predicate predicate, Set<Predicate> universe) {
    	Set<Predicate> ret = new HashSet<Predicate>();
    	
	    for (AccessExpression path : predicate.getPaths()) {
    		for (Predicate candidate : universe) {
			    List<AccessExpression> candidatePaths = candidate.getPaths();

			    for (AccessExpression candidatePath : candidatePaths) {
				    for (AccessExpression candidateSubPath : candidatePath.getAllPrefixes()) {
					    if (candidateSubPath.isSimilarToPrefixOf(path)) {
						    ret.add(candidate);
					    }
				    }
			    }
		    }
	    }
    	
    	return ret;
    }
    
    /**
     * Finds a transitive closure of all predicates that may infer the value of this one.
     * 
     * @param universe Universe of all predicates that may or may not determine the value of this predicate
     * @return A selection of those predicates from the universe that may determine the value of this predicate
     */
	public static Set<Predicate> determinantClosure(Predicate predicate, Set<Predicate> universe) {
		Set<Predicate> cur;
		Set<Predicate> ret = selectDeterminants(predicate, universe);
		
		int prevSize = 0;
		
		while (prevSize != ret.size()) {
			prevSize = ret.size();

			cur = new HashSet<Predicate>();

			for (Predicate p : ret) {
				cur.addAll(selectDeterminants(p, universe));
			}
			
			ret = cur;
		}
		
		return ret;
	}
	
	/**
	 * Suppose valuation of given predicates change, update all affected predicates, too
	 * 
	 * Keep doing so until a fixpoint is reached
	 */
	private void cascadeReevaluation(Map<Predicate, TruthValue> updated) {
		Map<Predicate, PredicateValueDeterminingInfo> predicates = new HashMap<Predicate, PredicateValueDeterminingInfo>();
		
		int size = updated.size();
		
		for (Predicate affectedCandidate : valuations.keySet()) {
			Set<Predicate> updatedDeterminants = selectDeterminants(affectedCandidate, updated.keySet());
			
			if (!updatedDeterminants.isEmpty()) {
				Predicate positiveWeakestPrecondition = affectedCandidate;
				Predicate negativeWeakestPrecondition = Negation.create(affectedCandidate);
				
				Map<Predicate, TruthValue> determinants = new HashMap<Predicate, TruthValue>();
					
				for (Predicate determinant : determinantClosure(positiveWeakestPrecondition, valuations.keySet())) {
					determinants.put(determinant, valuations.get(determinant));
				}
				for (Predicate determinant : determinantClosure(negativeWeakestPrecondition, valuations.keySet())) {
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
			cascadeReevaluation(updated);
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
		
		if (config.containsKey(key) && config.getBoolean(key)) {
			cascadeReevaluation(newValuations);
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

		int padding = 0;
		
		for (Predicate p : valuations.keySet()) {
			String predicate = p.toString(Notation.DOT_NOTATION);

			padding = padding < predicate.length() ? predicate.length() : padding;
		}

        Set<Predicate> sorted = new TreeSet<Predicate>(new Comparator<Predicate>() {
            @Override
            public int compare(Predicate p1, Predicate p2) {
                return p1.toString(Notation.DOT_NOTATION).compareTo(p2.toString(Notation.DOT_NOTATION));
            }
        });

        sorted.addAll(valuations.keySet());
		
		padding += 4;
		
		for (Predicate p : sorted) {
			String predicate = p.toString(Notation.DOT_NOTATION);
			StringBuilder pad = new StringBuilder();
			
			for (int i = 0; i < padding - predicate.length(); ++i) {
				pad.append(" ");
			}

			ret.append(predicate);
			ret.append(pad);
			ret.append(valuations.get(p));
			ret.append("\n");
		}
		
		return ret.toString();
	}

	@Override
	public void reevaluate(AccessExpression affected, Set<AccessExpression> resolvedAffected, Expression expression) {
		Map<Predicate, PredicateValueDeterminingInfo> predicates = new HashMap<Predicate, PredicateValueDeterminingInfo>();

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
			for (AccessExpression path1 : resolvedAffected) {
				for (AccessExpression path2 : predicate.getPaths()) {
					affects = affects || path1.isSimilarToPrefixOf(path2);
				}
			}

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
				
				for (Predicate determinant : determinantClosure(positiveWeakestPrecondition, valuations.keySet())) {
					determinants.put(determinant, valuations.get(determinant));
				}
				for (Predicate determinant : determinantClosure(negativeWeakestPrecondition, valuations.keySet())) {
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
		if (predicates.isEmpty()) return new HashMap<Predicate, TruthValue>();
		
		Map<Predicate, PredicateValueDeterminingInfo> input = new HashMap<Predicate, PredicateValueDeterminingInfo>();
		
		for (Predicate predicate : predicates) {
			Predicate positiveWeakestPrecondition = predicate;
			Predicate negativeWeakestPrecondition = Negation.create(predicate);
	
			Map<Predicate, TruthValue> determinants = new HashMap<Predicate, TruthValue>();
				
			for (Predicate determinant : determinantClosure(positiveWeakestPrecondition, valuations.keySet())) {
				determinants.put(determinant, valuations.get(determinant));
			}
			for (Predicate determinant : determinantClosure(negativeWeakestPrecondition, valuations.keySet())) {
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
	
}
