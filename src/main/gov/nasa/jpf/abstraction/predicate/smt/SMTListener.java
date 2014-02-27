package gov.nasa.jpf.abstraction.predicate.smt;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.abstraction.util.Pair;

import java.util.Map;
import java.util.Set;
import java.util.List;

/**
 * A special listener that JPF can instantiate as a common listener which also listens to SMT events
 */
public abstract class SMTListener extends ListenerAdapter {
	
	public SMTListener() {
		SMT.registerListener(this);
	}
	
	public void valuatePredicatesInvoked(Map<Predicate, PredicateValueDeterminingInfo> predicates) {}
	public void getModelInvoked(Expression expression, List<Pair<Predicate, TruthValue>> determinants) {}
	public void getModelInputGenerated(String input) {}
	public void getModelExecuted(Boolean satisfiability, Integer model) {}
	public void valuatePredicatesInvoked(Set<Predicate> predicates) {}
	public void valuatePredicatesInputGenerated(String input) {}
	public void valuatePredicatesExecuted(Map<Predicate, TruthValue> valuation) {}

}
