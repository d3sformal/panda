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

public abstract class HeapValue extends StructuredValue {
    private Reference identifier;

    public HeapValue(Reference identifier) {
        this.identifier = identifier;
    }

    @Override
    public abstract HeapValue createShallowCopy();

    @Override
    public StructuredValueIdentifier getIdentifier() {
        return identifier;
    }

    public Reference getReference() {
        return identifier;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof HeapValue) {
            return identifier.equals(((HeapValue) object).identifier);
        }

        return false;
    }
}
