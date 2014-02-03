package gov.nasa.jpf.abstraction.predicate.state.universe;

import java.util.Map;

public interface Indexed {
    public UniverseSlot getElement(ElementIndex index);
    public Map<ElementIndex, UniverseSlot> getElements();
    public void setElement(ElementIndex index, UniverseIdentifier value);
    public Integer getLength();
}
