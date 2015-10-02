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

public class UniverseObject extends HeapValue implements Associative {
    private Map<FieldName, UniverseSlot> fields = new HashMap<FieldName, UniverseSlot>();

    public UniverseObject(ElementInfo elementInfo) {
        super(new Reference(elementInfo));
    }

    protected UniverseObject(Reference identifier) {
        super(identifier);
    }

    @Override
    public UniverseObject createShallowCopy() {
        UniverseObject copy = new UniverseObject(getReference());

        for (Pair<Identifier, UniverseSlotKey> parentSlot : getParentSlots()) {
            copy.addParentSlot(parentSlot.getFirst(), parentSlot.getSecond());
        }

        copy.fields.putAll(fields);

        return copy;
    }

    @Override
    public UniverseSlot getSlot(UniverseSlotKey key) {
        return fields.get((FieldName) key);
    }

    @Override
    public Map<? extends FieldName, UniverseSlot> getSlots() {
        return fields;
    }

    @Override
    public UniverseSlot getField(FieldName name) {
        return fields.get(name);
    }

    @Override
    public Map<FieldName, UniverseSlot> getFields() {
        return fields;
    }

    @Override
    public void addSlot(UniverseSlotKey slotKey, UniverseSlot slot) {
        fields.put((FieldName) slotKey, slot);
    }

    @Override
    public void removeSlot(UniverseSlotKey slotKey) {
        fields.remove((FieldName) slotKey);
    }

}
