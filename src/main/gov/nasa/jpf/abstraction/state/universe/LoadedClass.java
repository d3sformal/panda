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

import gov.nasa.jpf.abstraction.common.access.PackageAndClass;

public class LoadedClass extends StructuredValueSlot implements Identifier {
    public static class SlotKey implements UniverseSlotKey {
    }

    private PackageAndClass accessExpression;
    public static SlotKey slotKey = new SlotKey();

    public LoadedClass(PackageAndClass accessExpression) {
        this.accessExpression = accessExpression;

        setParent(this);
        setSlotKey(slotKey);
    }

    @Override
    public LoadedClass createShallowCopy() {
        LoadedClass copy = new LoadedClass(getAccessExpression());

        for (StructuredValueIdentifier value : getPossibleStructuredValues()) {
            copy.addPossibleStructuredValue(value);
        }

        return copy;
    }

    public PackageAndClass getAccessExpression() {
        return accessExpression;
    }

    @Override
    public int compareTo(Identifier id) {
        if (id instanceof LoadedClass) {
            LoadedClass lcls = (LoadedClass) id;

            return getAccessExpression().getName().compareTo(lcls.getAccessExpression().getName());
        }

        return Identifier.Ordering.compare(this, id);
    }
}
