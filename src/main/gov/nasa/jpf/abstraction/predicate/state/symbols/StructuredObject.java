package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.Map;

public interface StructuredObject {
	public void setField(String name, StructuredValue... values);
	public void setField(String name, PrimitiveValue... values);
	public Slot getField(String name);
	public Map<String, Slot> getFields();
}
