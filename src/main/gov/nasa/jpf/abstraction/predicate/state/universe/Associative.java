package gov.nasa.jpf.abstraction.predicate.state.universe;

import java.util.Map;

public interface Associative {
    public UniverseSlot getField(FieldName name);
    public Map<FieldName, UniverseSlot> getFields();
    public void setField(FieldName name, UniverseIdentifier value);
}
