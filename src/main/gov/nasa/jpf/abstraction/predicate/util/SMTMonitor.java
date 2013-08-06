package gov.nasa.jpf.abstraction.predicate.util;

import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.smt.PredicateDeterminant;
import gov.nasa.jpf.abstraction.predicate.smt.SMTListener;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;

public class SMTMonitor extends SMTListener {

	@Override
	public void valuatePredicatesInvoked(Map<Predicate, PredicateDeterminant> predicates) {
		System.out.println("SMT:");
		
		for (Predicate p : predicates.keySet()) {
			System.out.println("\t" + p.toString(AccessPath.NotationPolicy.DOT_NOTATION) + " WP(+): " + predicates.get(p).positiveWeakestPrecondition.toString(AccessPath.NotationPolicy.DOT_NOTATION) + " WP(-): " + predicates.get(p).negativeWeakestPrecondition.toString(AccessPath.NotationPolicy.DOT_NOTATION));
			
			Map<Predicate, TruthValue> determinants = predicates.get(p).determinants;
			
			System.out.println("\tDET:");
			for (Predicate d : determinants.keySet()) {
				System.out.println("\t\t" + d.toString(AccessPath.NotationPolicy.DOT_NOTATION));
			}
		}
		
		System.out.println();
	}
	
	@Override
	public void valuatePredicatesInvoked(Set<Predicate> predicates) {
		System.out.println("SMT:");
		
		for (Predicate p : predicates) {
			System.out.println("\t" + p.toString(AccessPath.NotationPolicy.DOT_NOTATION));
		}
		
		System.out.println();
	}
	
	@Override
	public void valuatePredicatesInputGenerated(String input) {
		System.out.println(input);
	}
	
	@Override
	public void valuatePredicatesExecuted(Map<Predicate, TruthValue> valuation) {
		System.out.println("SMT Returned:");
		
		for (Predicate p : valuation.keySet()) {
			System.out.println("\t" + p.toString(AccessPath.NotationPolicy.DOT_NOTATION) + ": " + valuation.get(p));
		}
		
		System.out.println();
	}
	
}
