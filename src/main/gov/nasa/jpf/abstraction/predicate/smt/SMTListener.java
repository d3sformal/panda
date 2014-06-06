package gov.nasa.jpf.abstraction.predicate.smt;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.abstraction.util.Pair;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A special listener that JPF can instantiate as a common listener which also listens to SMT events
 */
public abstract class SMTListener extends ListenerAdapter {

    protected List<SMTCache> caches = new LinkedList<SMTCache>();

    public SMTListener() {
        SMT.registerListener(this);
    }

    public void registerCache(SMTCache cache) {
        caches.add(cache);
    }

    public void isSatisfiableInvoked(List<Predicate> formulas) {}
    public void valuatePredicatesInvoked(Map<Predicate, PredicateValueDeterminingInfo> predicates) {}
    public void valuatePredicatesInvoked(Set<Predicate> predicates) {}
    public void getModelInvoked(Expression expression, List<Pair<Predicate, TruthValue>> determinants) {}

    public void isSatisfiableInputGenerated(String input) {}
    public void valuatePredicatesInputGenerated(String input) {}
    public void getModelInputGenerated(String input) {}

    public void isSatisfiableExecuted(List<Predicate> formulas, boolean[] satisfiable) {}
    public void valuatePredicatesExecuted(Map<Predicate, TruthValue> valuation) {}
    public void getModelExecuted(Boolean satisfiability, Integer model) {}

}
