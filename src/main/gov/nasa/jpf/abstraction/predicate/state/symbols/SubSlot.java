package gov.nasa.jpf.abstraction.predicate.state.symbols;

public abstract class SubSlot extends Slot {
	private Value parent;
	
	public SubSlot(Value parent) {
		this.parent = parent;
	}
	
	public final Value getParent() {
		return this.parent;
	}
}
