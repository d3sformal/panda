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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.util.Pair;

public abstract class UniverseValue implements Freezable {
    protected boolean frozen = false;

    protected Set<Pair<Identifier, UniverseSlotKey>> parentSlots = new HashSet<Pair<Identifier, UniverseSlotKey>>();

    @Override
    public void freeze() {
        frozen = true;
    }

    @Override
    public boolean isFrozen() {
        return frozen;
    }

    @Override
    public abstract UniverseValue createShallowCopy();

    public Set<Pair<Identifier, UniverseSlotKey>> getParentSlots() {
        return parentSlots;
    }

    public void addParentSlot(Identifier parent, UniverseSlotKey slotKey) {
        if (frozen) {
            throw new RuntimeException("Adding a parent slot to a frozen object");
        }

        parentSlots.add(new Pair<Identifier, UniverseSlotKey>(parent, slotKey));
    }

    public void removeParentSlot(Identifier parent, UniverseSlotKey slotKey) {
        if (frozen) {
            throw new RuntimeException("Removing a parent slot from a frozen object");
        }

        parentSlots.remove(new Pair<Identifier, UniverseSlotKey>(parent, slotKey));
    }

    public abstract UniverseIdentifier getIdentifier();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object object);
}
