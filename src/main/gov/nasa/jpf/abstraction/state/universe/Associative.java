package gov.nasa.jpf.abstraction.state.universe;

import java.util.Map;

public interface Associative {
    public UniverseSlot getField(FieldName name);
    public Map<FieldName, UniverseSlot> getFields();
}
