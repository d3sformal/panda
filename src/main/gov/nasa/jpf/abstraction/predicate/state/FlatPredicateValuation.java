package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.predicate.common.AccessPath;
import gov.nasa.jpf.abstraction.predicate.common.Expression;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.smt.SMT;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
	public void reevaluate(Set<AccessPath> affected, Expression expression) {
		List<Predicate> affectedPredicates = new LinkedList<Predicate>();

		if (affected.isEmpty()) return;

		System.err.println("SMT: ");
		
		System.err.println("\tREACTION TO:");
		for (AccessPath path : affected) {
			System.err.println("\t\t" + path.toString(AccessPath.NotationPolicy.DOT_NOTATION));
		}
		
		System.err.println("\tAFFECTS:");
		for (Predicate predicate : valuations.keySet()) {
			boolean affects = false;
			
			Predicate weakestPrecondition = predicate;

			for (AccessPath path : affected) {
				affects = affects || predicate.getPaths().contains(path);
				
				//TODO cope with arrays whose ambiguity makes it not work properly
				System.err.println("\t\t" + path + " / " + expression + "\t [REPLACE]");
				weakestPrecondition = weakestPrecondition.replace(path, expression);
			}
			
			if (affects) {
				affectedPredicates.add(predicate);

				System.err.println("\t\t" + predicate + " " + weakestPrecondition);
			}
		}
		
		try {
			Map<Predicate, TruthValue> newValuations = new SMT().valuatePredicates(affectedPredicates);
		
			for (Predicate predicate : newValuations.keySet()) {
				valuations.put(predicate, newValuations.get(predicate));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
