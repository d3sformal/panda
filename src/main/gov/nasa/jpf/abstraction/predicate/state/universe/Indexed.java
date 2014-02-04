package gov.nasa.jpf.abstraction.predicate.state.universe;

import java.util.Map;

public interface Indexed {
    public UniverseSlot getElement(ElementIndex index);
    public Map<ElementIndex, UniverseSlot> getElements();
    public Integer getLength();
}
