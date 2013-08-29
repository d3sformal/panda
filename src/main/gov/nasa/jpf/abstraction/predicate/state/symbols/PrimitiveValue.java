package gov.nasa.jpf.abstraction.predicate.state.symbols;

public class PrimitiveValue extends Value {
	
	//TODO identifier of the current value
	// 1) method m modifies a primitive value reachable from parent scope
	// 2) return
	// 3) need to detect the modification - comparison of the universes - need to distinguish the primitive values
	
	@Override
	public String toString() {
		return "primitive";
	}
}
