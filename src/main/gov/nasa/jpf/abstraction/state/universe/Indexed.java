package gov.nasa.jpf.abstraction.state.universe;

import java.util.Map;

public interface Indexed {
    public UniverseSlot getElement(ElementIndex index);
    public Map<ElementIndex, UniverseSlot> getElements();
    public PrimitiveValueSlot getLengthSlot();
    public Integer getLength();
}
