/*
 * Copyright (C) 2015, Charles University in Prague.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.nasa.jpf.abstraction.state.universe;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.util.Pair;

public class UniverseArray extends HeapValue implements Indexed {
    private Map<ElementIndex, UniverseSlot> elements = new HashMap<ElementIndex, UniverseSlot>();
    private PrimitiveValueSlot lengthSlot;

    private Integer length;

    public UniverseArray(ElementInfo elementInfo) {
        this(new Reference(elementInfo), elementInfo.arrayLength(), new PrimitiveValueSlot(new Reference(elementInfo), LengthSlotKey.getInstance()));
    }

    protected UniverseArray(Reference identifier, Integer length, PrimitiveValueSlot lengthSlot) {
        super(identifier);

        this.length = length;
        this.lengthSlot = lengthSlot;
    }

    @Override
    public UniverseArray createShallowCopy() {
        UniverseArray copy = new UniverseArray(getReference(), getLength(), getLengthSlot());

        for (Pair<Identifier, UniverseSlotKey> parentSlot : getParentSlots()) {
            copy.addParentSlot(parentSlot.getFirst(), parentSlot.getSecond());
        }

        copy.elements.putAll(elements);

        return copy;
    }

    @Override
    public UniverseSlot getSlot(UniverseSlotKey key) {
        if (key instanceof LengthSlotKey) {
            return lengthSlot;
        }

        return elements.get((ElementIndex) key);
    }

    @Override
    public PrimitiveValueSlot getLengthSlot() {
        return lengthSlot;
    }

    @Override
    public Map<? extends UniverseSlotKey, UniverseSlot> getSlots() {
        return elements;
    }

    @Override
    public UniverseSlot getElement(ElementIndex index) {
        return elements.get(index);
    }

    @Override
    public Map<ElementIndex, UniverseSlot> getElements() {
        return elements;
    }

    @Override
    public void addSlot(UniverseSlotKey slotKey, UniverseSlot slot) {
        elements.put((ElementIndex) slotKey, slot);
    }

    @Override
    public void removeSlot(UniverseSlotKey slotKey) {
        elements.remove((ElementIndex) slotKey);
    }

    @Override
    public Integer getLength() {
        return length;
    }

}
