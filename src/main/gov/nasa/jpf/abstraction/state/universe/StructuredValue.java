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

public abstract class StructuredValue extends UniverseValue {
    @Override
    public abstract StructuredValue createShallowCopy();

    @Override
    public abstract StructuredValueIdentifier getIdentifier();

    public abstract UniverseSlot getSlot(UniverseSlotKey key);

    public abstract Map<? extends UniverseSlotKey, ? extends UniverseSlot> getSlots();

    public abstract void addSlot(UniverseSlotKey slotKey, UniverseSlot slot);

    public abstract void removeSlot(UniverseSlotKey slotKey);

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object object);
}
