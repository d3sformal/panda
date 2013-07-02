package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.predicate.grammar.Predicate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class FlatPredicateValuation implements PredicateValuation, Cloneable {
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
		String ret = "--PREDICATES--------- (" + valuations.size() + ")\n";
		
		int padding = 0;
		
		for (Predicate p : valuations.keySet()) {
			String predicate = p.toString();

			padding = padding < predicate.length() ? predicate.length() : padding;
		}
		
		padding += 4;
		
		for (Predicate p : valuations.keySet()) {
			String predicate = p.toString();
			String pad = "";
			
			for (int i = 0; i < padding - predicate.length(); ++i) {
				pad += " ";
			}

			ret += predicate + pad + valuations.get(p) + "\n";
		}
		
		ret += "---------------------\n";
		
		return ret;
	}
	
}
