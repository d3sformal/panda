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
import java.util.Set;

public class StructuredValueSlot extends AbstractUniverseSlot {
    private Set<StructuredValueIdentifier> possibleValues = new HashSet<StructuredValueIdentifier>();

    public StructuredValueSlot(Identifier parent, UniverseSlotKey slotKey) {
        super(parent, slotKey);
    }

    protected StructuredValueSlot() {
    }

    @Override
    public StructuredValueSlot createShallowCopy() {
        StructuredValueSlot copy = new StructuredValueSlot(getParent(), getSlotKey());

        copy.possibleValues.addAll(possibleValues);

        return copy;
    }

    @Override
    public Set<? extends UniverseIdentifier> getPossibleValues() {
        return possibleValues;
    }

    @Override
    public void clear() {
        possibleValues.clear();
    }

    public Set<StructuredValueIdentifier> getPossibleStructuredValues() {
        return possibleValues;
    }

    public void addPossibleStructuredValue(StructuredValueIdentifier value) {
        possibleValues.add(value);
    }
}
