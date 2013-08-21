package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.NotationPolicy;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.common.UpdatedPredicate;
import gov.nasa.jpf.abstraction.predicate.smt.PredicateDeterminant;
import gov.nasa.jpf.abstraction.predicate.smt.SMT;
import gov.nasa.jpf.vm.VM;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class FlatPredicateValuation implements PredicateValuation, Scope {
	private HashMap<Predicate, TruthValue> valuations = new HashMap<Predicate, TruthValue>();
	
	@SuppressWarnings("unchecked")
	@Override
	public FlatPredicateValuation clone() {
		FlatPredicateValuation clone = new FlatPredicateValuation();
		
		clone.valuations = (HashMap<Predicate, TruthValue>)valuations.clone();
		
		return clone;
	}
	
	private void cascadeReevaluation(Map<Predicate, TruthValue> updated) {
		Map<Predicate, PredicateDeterminant> predicates = new HashMap<Predicate, PredicateDeterminant>();
		
		int size = updated.size();
		
		for (Predicate affectedCandidate : valuations.keySet()) {
			Set<Predicate> updatedDeterminants = affectedCandidate.selectDeterminants(updated.keySet());
			
			if (!updatedDeterminants.isEmpty()) {
				Predicate positiveWeakestPrecondition = affectedCandidate;
				Predicate negativeWeakestPrecondition = Negation.create(affectedCandidate);
				
				Map<Predicate, TruthValue> determinants = new HashMap<Predicate, TruthValue>();
					
				for (Predicate determinant : positiveWeakestPrecondition.determinantClosure(valuations.keySet())) {
					determinants.put(determinant, valuations.get(determinant));
				}
				for (Predicate determinant : negativeWeakestPrecondition.determinantClosure(valuations.keySet())) {
					determinants.put(determinant, valuations.get(determinant));
				}
				for (Predicate determinant : updatedDeterminants) {
					determinants.put(determinant, updated.get(determinant));
				}
				
				predicates.put(affectedCandidate, new PredicateDeterminant(positiveWeakestPrecondition, negativeWeakestPrecondition, determinants));
			}
		}
		
		updated.putAll(new SMT().valuatePredicates(predicates));
		
		if (size != updated.size()) {
			cascadeReevaluation(updated);
		}
	}

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

	@Override
	public TruthValue get(Predicate predicate) {
		return valuations.get(predicate);
	}

	@Override
	public Iterator<Entry<Predicate, TruthValue>> iterator() {
		return valuations.entrySet().iterator();
	}

	@Override
	public String toString() {	
		StringBuilder ret = new StringBuilder();

		int padding = 0;
		
		for (Predicate p : valuations.keySet()) {
			String predicate = p.toString(NotationPolicy.DOT_NOTATION);

			padding = padding < predicate.length() ? predicate.length() : padding;
		}
		
		padding += 4;
		
		for (Predicate p : valuations.keySet()) {
			String predicate = p.toString(NotationPolicy.DOT_NOTATION);
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
		Map<Predicate, PredicateDeterminant> predicates = new HashMap<Predicate, PredicateDeterminant>();

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

			if (affects) {
				Predicate positiveWeakestPrecondition = predicate;
				Predicate negativeWeakestPrecondition = Negation.create(predicate);
					
				if (expression != null) {
					positiveWeakestPrecondition = UpdatedPredicate.create(positiveWeakestPrecondition, affected, expression);
					negativeWeakestPrecondition = UpdatedPredicate.create(negativeWeakestPrecondition, affected, expression);
				}
				
				Map<Predicate, TruthValue> determinants = new HashMap<Predicate, TruthValue>();
				
				for (Predicate determinant : positiveWeakestPrecondition.determinantClosure(valuations.keySet())) {
					determinants.put(determinant, valuations.get(determinant));
				}
				for (Predicate determinant : negativeWeakestPrecondition.determinantClosure(valuations.keySet())) {
					determinants.put(determinant, valuations.get(determinant));
				}
				
				predicates.put(predicate, new PredicateDeterminant(positiveWeakestPrecondition, negativeWeakestPrecondition, determinants));
			}
		}
		
		if (predicates.isEmpty()) return;
		
		Map<Predicate, TruthValue> newValuations = new SMT().valuatePredicates(predicates);
			
		valuations.putAll(newValuations);
	}
	
	@Override
	public TruthValue evaluatePredicate(Predicate predicate) {
		Predicate positiveWeakestPrecondition = predicate;
		Predicate negativeWeakestPrecondition = Negation.create(predicate);

		Map<Predicate, PredicateDeterminant> predicates = new HashMap<Predicate, PredicateDeterminant>();
		Map<Predicate, TruthValue> determinants = new HashMap<Predicate, TruthValue>();
			
		for (Predicate determinant : positiveWeakestPrecondition.determinantClosure(valuations.keySet())) {
			determinants.put(determinant, valuations.get(determinant));
		}
		for (Predicate determinant : negativeWeakestPrecondition.determinantClosure(valuations.keySet())) {
			determinants.put(determinant, valuations.get(determinant));
		}
		
		predicates.put(predicate, new PredicateDeterminant(positiveWeakestPrecondition, negativeWeakestPrecondition, determinants));

		Map<Predicate, TruthValue> valuation = new SMT().valuatePredicates(predicates);
		
		return valuation.get(predicate);
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
