package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.HashMap;
import java.util.Map;

public class Null extends StructuredValue implements StructuredObject, StructuredArray {

	protected Null(Universe universe) {
		super(universe, new HeapObjectReference(Universe.NULL));
	}

	@Override
	public void setElement(Integer index, StructuredValue... values) {
		throw new RuntimeException("NullPointer accessed");
	}

	@Override
	public void setElement(Integer index, PrimitiveValue... values) {
		throw new RuntimeException("NullPointer accessed");
	}

	@Override
	public Slot getElement(Integer index) {
		throw new RuntimeException("NullPointer accessed");
	}

	@Override
	public Map<Integer, Slot> getElements() {
		return new HashMap<Integer, Slot>();
	}

	@Override
	public Integer getLength() {
		return 0;
	}

	@Override
	public void setField(String name, StructuredValue... values) {
		throw new RuntimeException("NullPointer accessed");
	}

	@Override
	public void setField(String name, PrimitiveValue... values) {
		throw new RuntimeException("NullPointer accessed");
	}

	@Override
	public Slot getField(String name) {
		throw new RuntimeException("NullPointer accessed");
	}

	@Override
	public Map<String, Slot> getFields() {
		return new HashMap<String, Slot>();
	}

	@Override
	public Null cloneInto(Universe universe, Slot slot) {		
		Null clone = cloneInto(universe);
		
		clone.addSlot(slot);
		
		return clone;
	}
	
	@Override
	public Null cloneInto(Universe universe) {	
		return universe.getFactory().createNull();
	}

}
