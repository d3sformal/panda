package gov.nasa.jpf.abstraction.predicate.state.symbols;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;

import java.util.HashSet;
import java.util.Set;

public class ClassObject extends Value {
	private Set<Slot> slots = new HashSet<Slot>();
	
	public Set<Slot> getSlots() {
		return slots;
	}
	
	public AccessExpression getAccessExpression() {
		return null;
	}
}
