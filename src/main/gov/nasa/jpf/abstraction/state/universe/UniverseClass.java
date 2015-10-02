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

import gov.nasa.jpf.vm.StaticElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.util.Pair;

public class UniverseClass extends StructuredValue implements Associative {
    private ClassName identifier;
    private Map<FieldName, UniverseSlot> fields = new HashMap<FieldName, UniverseSlot>();

    public UniverseClass(StaticElementInfo elementInfo) {
        identifier = new ClassName(elementInfo);
    }

    protected UniverseClass(ClassName identifier) {
        this.identifier = identifier;
    }

    public ClassName getClassName() {
        return identifier;
    }

    @Override
    public UniverseClass createShallowCopy() {
        UniverseClass copy = new UniverseClass(identifier);

        for (Pair<Identifier, UniverseSlotKey> parentSlot : getParentSlots()) {
            copy.addParentSlot(parentSlot.getFirst(), parentSlot.getSecond());
        }

        copy.fields.putAll(fields);

        return copy;
    }

    @Override
    public StructuredValueIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public UniverseSlot getSlot(UniverseSlotKey key) {
        return fields.get((FieldName) key);
    }

    @Override
    public Map<? extends UniverseSlotKey, UniverseSlot> getSlots() {
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

    @Override
    public UniverseSlot getField(FieldName name) {
        return fields.get(name);
    }

    @Override
    public Map<FieldName, UniverseSlot> getFields() {
        return fields;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof UniverseClass) {
            return identifier.equals(((UniverseClass) object).identifier);
        }

        return false;
    }
}
