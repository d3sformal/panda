package gov.nasa.jpf.abstraction.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.search.Search;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.smt.PredicateValueDeterminingInfo;
import gov.nasa.jpf.abstraction.smt.SMT;
import gov.nasa.jpf.abstraction.smt.SMTCache;
import gov.nasa.jpf.abstraction.smt.SMTListener;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.util.Pair;

/**
 * Prints calls to the SMT
 */
public class SMTMonitor extends SMTListener {

    @Override
    public void isSatisfiableInvoked(List<Predicate> formulas) {
        System.out.println("SMT Is satisfiable:");

        for (Predicate f : formulas) {
            System.out.println("\t" + f.toString(Notation.DOT_NOTATION));
        }

        System.out.println();

    }

    @Override
    public void isSatisfiableInputGenerated(String input) {
        System.out.println("SMT Input: ");

        System.out.println(input);

        System.out.println();
    }

    @Override
    public void isSatisfiableExecuted(List<Predicate> formulas, boolean[] satisfiable) {
        System.out.println("SMT Returned:");

        int i = 0;

        for (Predicate f : formulas) {
            System.out.println("\t" + f.toString(Notation.DOT_NOTATION) + ": " + (satisfiable[i++] ? "SAT" : "UNSAT"));
        }

        System.out.println();
    }

    @Override
    public void valuatePredicatesInvoked(Map<Predicate, PredicateValueDeterminingInfo> predicates) {
        System.out.println("SMT Valuate predicates:");

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
    public void valuatePredicatesInputGenerated(String input) {
        System.out.println("SMT Input: ");

        System.out.println(input);

        System.out.println();
    }

    @Override
    public void valuatePredicatesExecuted(Map<Predicate, TruthValue> valuation) {
        System.out.println("SMT Returned:");

        for (Predicate p : valuation.keySet()) {
            System.out.println("\t" + p.toString(Notation.DOT_NOTATION) + ": " + valuation.get(p));
        }

        System.out.println();
    }

    @Override
    public void getModelInvoked(Expression expression, List<Pair<Predicate, TruthValue>> determinants) {
        System.out.println("SMT Get model:");

        System.out.println("\t" + expression);

        System.out.println("\tDET:");
        for (Pair<Predicate, TruthValue> pair : determinants) {
            System.out.println("\t\t" + pair.getFirst().toString(Notation.DOT_NOTATION) + " " + pair.getSecond());
        }

        System.out.println();
    }

    @Override
    public void getModelInputGenerated(String input) {
        System.out.println("SMT Input: ");

        System.out.println(input);

        System.out.println();
    }

    @Override
    public void getModelExecuted(Boolean satisfiability, Integer model) {
        System.out.println("SMT Returned: " + (satisfiability ? "sat " + model : "unsat"));

        System.out.println();
    }

    @Override
    public void searchFinished(Search search) {
        System.out.println("SMT Number of queries: " + SMT.getQueryCountTotal() + " (New: " + SMT.getQueryCount() + ", Cached: " + SMT.getCacheHitCount() + ")");
        for (SMTCache cache : caches) {
            System.out.println();
            System.out.println("SMT Cache:");
            for (String query : cache.getQueries()) {
                System.out.println("\t" + query + " " + cache.get(query));
            }
        }
    }

}
