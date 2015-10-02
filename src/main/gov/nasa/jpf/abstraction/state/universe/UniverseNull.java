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

import java.util.Collections;
import java.util.Map;

import gov.nasa.jpf.abstraction.util.Pair;

public class UniverseNull extends HeapValue implements Associative, Indexed {

    public static Reference nullReference = new Reference(null);

    public UniverseNull() {
        super(nullReference);
    }

    @Override
    public UniverseNull createShallowCopy() {
        UniverseNull copy = new UniverseNull();

        for (Pair<Identifier, UniverseSlotKey> parentSlot : getParentSlots()) {
            copy.addParentSlot(parentSlot.getFirst(), parentSlot.getSecond());
        }

        return copy;
    }

    @Override
    public UniverseSlot getSlot(UniverseSlotKey key) {
        throw new RuntimeException("Trying to access a slot of a NULL object");
    }

    @Override
    public Map<? extends UniverseSlotKey, UniverseSlot> getSlots() {
        return Collections.emptyMap();
    }

    @Override
    public UniverseSlot getField(FieldName name) {
        throw new RuntimeException("Trying to access a field of a NULL object");
    }

    @Override
    public Map<FieldName, UniverseSlot> getFields() {
        return Collections.emptyMap();
    }

    @Override
    public UniverseSlot getElement(ElementIndex index) {
        throw new RuntimeException("Trying to access an element of a NULL array");
    }

    @Override
    public PrimitiveValueSlot getLengthSlot() {
        throw new RuntimeException("Trying to access the length of a NULL array");
    }

    @Override
    public Map<ElementIndex, UniverseSlot> getElements() {
        return Collections.emptyMap();
    }

    @Override
    public void addSlot(UniverseSlotKey slotKey, UniverseSlot slot) {
        throw new RuntimeException("Trying to add a slot to a NULL object");
    }

    @Override
    public void removeSlot(UniverseSlotKey slotKey) {
        throw new RuntimeException("Trying to remove a slot from a NULL object");
    }

    @Override
    public Integer getLength() {
        throw new RuntimeException("Trying to access the length of a NULL array");
    }
}
