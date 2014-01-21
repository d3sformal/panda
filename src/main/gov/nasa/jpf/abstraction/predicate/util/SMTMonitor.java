package gov.nasa.jpf.abstraction.predicate.util;

import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.smt.PredicateValueDeterminingInfo;
import gov.nasa.jpf.abstraction.predicate.smt.SMTListener;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;

/**
 * Prints calls to the SMT
 */
public class SMTMonitor extends SMTListener {

	@Override
	public void valuatePredicatesInvoked(Map<Predicate, PredicateValueDeterminingInfo> predicates) {
		System.out.println("SMT:");
		
		for (Predicate p : predicates.keySet()) {
			System.out.println("\t" + p.toString(Notation.DOT_NOTATION) + " WP(+): " + predicates.get(p).positiveWeakestPrecondition.toString(Notation.DOT_NOTATION) + " WP(-): " + predicates.get(p).negativeWeakestPrecondition.toString(Notation.DOT_NOTATION));
			
			Map<Predicate, TruthValue> determinants = predicates.get(p).determinants;
			
			System.out.println("\tDET:");
			for (Predicate d : determinants.keySet()) {
				System.out.println("\t\t" + d.toString(Notation.DOT_NOTATION) + " " + determinants.get(d));
			}
		}
		
		System.out.println();
	}
	
	@Override
	public void valuatePredicatesInvoked(Set<Predicate> predicates) {
		System.out.println("SMT:");
		
		for (Predicate p : predicates) {
			System.out.println("\t" + p.toString(Notation.DOT_NOTATION));
		}
		
		System.out.println();
	}
	
	@Override
	public void valuatePredicatesInputGenerated(Set<Predicate> predicates, String input) {
        System.out.println("SMT Input for predicates: ");

        for (Predicate predicate : predicates) {
            System.out.println("\t" + predicate.toString(Notation.DOT_NOTATION));
        }

		System.out.println(input);
	}
	
	@Override
	public void valuatePredicatesExecuted(Map<Predicate, TruthValue> valuation) {
		System.out.println("SMT Returned:");
		
		for (Predicate p : valuation.keySet()) {
			System.out.println("\t" + p.toString(Notation.DOT_NOTATION) + ": " + valuation.get(p));
		}
		
		System.out.println();
	}
	
}
