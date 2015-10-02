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

public abstract class AbstractUniverseSlot implements UniverseSlot {
    protected boolean frozen = false;
    private Identifier parent;
    private UniverseSlotKey slotKey;

    public AbstractUniverseSlot(Identifier parent, UniverseSlotKey slotKey) {
        this.parent = parent;
        this.slotKey = slotKey;
    }

    protected AbstractUniverseSlot() {
    }

    @Override
    public void freeze() {
        frozen = true;
    }

    @Override
    public boolean isFrozen() {
        return frozen;
    }

    @Override
    public abstract AbstractUniverseSlot createShallowCopy();

    @Override
    public Identifier getParent() {
        return parent;
    }

    @Override
    public void setParent(Identifier parent) {
        this.parent = parent;
    }

    @Override
    public UniverseSlotKey getSlotKey() {
        return slotKey;
    }

    @Override
    public void setSlotKey(UniverseSlotKey slotKey) {
        this.slotKey = slotKey;
    }
}
