package gov.nasa.jpf.abstraction.predicate.smt;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;

import java.util.Map;
import java.util.Set;

/**
 * A special listener that JPF can instantiate as a common listener which also listens to SMT events
 */
public abstract class SMTListener extends ListenerAdapter {
	
	public SMTListener() {
		SMT.registerListener(this);
	}
	
	public void valuatePredicatesInvoked(Map<Predicate, PredicateValueDeterminingInfo> predicates) {}
	public void valuatePredicatesInvoked(Set<Predicate> predicates) {}
	public void valuatePredicatesInputGenerated(String input) {}
	public void valuatePredicatesExecuted(Map<Predicate, TruthValue> valuation) {}

}
