package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.common.UpdatedPredicate;
import gov.nasa.jpf.abstraction.predicate.smt.PredicateDeterminant;
import gov.nasa.jpf.abstraction.predicate.smt.SMT;
import gov.nasa.jpf.abstraction.predicate.smt.SMTException;

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

	@Override
	public void put(Predicate predicate, TruthValue value) {
		valuations.put(predicate, value);
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
		String ret = "";

		int padding = 0;
		
		for (Predicate p : valuations.keySet()) {
			String predicate = p.toString(AccessPath.NotationPolicy.DOT_NOTATION);

			padding = padding < predicate.length() ? predicate.length() : padding;
		}
		
		padding += 4;
		
		for (Predicate p : valuations.keySet()) {
			String predicate = p.toString(AccessPath.NotationPolicy.DOT_NOTATION);
			String pad = "";
			
			for (int i = 0; i < padding - predicate.length(); ++i) {
				pad += " ";
			}

			ret += predicate + pad + valuations.get(p) + "\n";
		}
		
		return ret;
	}

	@Override
	public void reevaluate(AccessPath affected, Set<AccessPath> resolvedAffected, Expression expression) {
		Map<Predicate, PredicateDeterminant> predicates = new HashMap<Predicate, PredicateDeterminant>();

		if (affected == null) return;

		System.err.println("SMT: ");
		
		System.err.println("\tREACTION TO:");
		System.err.println("\t\t" + affected.toString(AccessPath.NotationPolicy.DOT_NOTATION));
		
		System.err.println("\tAFFECTS:");
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
			for (AccessPath path1 : resolvedAffected) {
				for (AccessPath path2 : predicate.getPaths()) {
					affects = affects || path1.similar(path2);
				}
			}
			
			Predicate positiveWeakestPrecondition = predicate;
			Predicate negativeWeakestPrecondition = new Negation(predicate);
				
			if (expression != null) {
				positiveWeakestPrecondition = new UpdatedPredicate(positiveWeakestPrecondition, affected, expression);
				negativeWeakestPrecondition = new UpdatedPredicate(negativeWeakestPrecondition, affected, expression);
			}

			if (affects) {
				Map<Predicate, TruthValue> determinants = new HashMap<Predicate, TruthValue>();
				
				for (Predicate determinant : positiveWeakestPrecondition.determinantClosure(valuations.keySet())) {
					determinants.put(determinant, valuations.get(determinant));
				}
				for (Predicate determinant : negativeWeakestPrecondition.determinantClosure(valuations.keySet())) {
					determinants.put(determinant, valuations.get(determinant));
				}
				
				predicates.put(predicate, new PredicateDeterminant(positiveWeakestPrecondition, negativeWeakestPrecondition, determinants));

				System.err.print("\t\t" + predicate + " [" + positiveWeakestPrecondition.toString(AccessPath.NotationPolicy.DOT_NOTATION) + ", " + negativeWeakestPrecondition.toString(AccessPath.NotationPolicy.DOT_NOTATION) + "] [");
				for (Predicate det : determinants.keySet()) {
					System.err.print(det.toString(AccessPath.NotationPolicy.DOT_NOTATION) + " :: " + determinants.get(det) + "; ");
				}
				System.err.println("]");
			}
		}
		
		try {
			@SuppressWarnings("unchecked")
			Map<Predicate, TruthValue> newValuations = new SMT().valuatePredicates(predicates);
		
			for (Predicate predicate : newValuations.keySet()) {
				valuations.put(predicate, newValuations.get(predicate));
			}
		} catch (SMTException e) {
			e.printStackTrace();
		}
	}
	
}
