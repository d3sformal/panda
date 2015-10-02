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

public class ElementIndex implements UniverseSlotKey, Comparable<ElementIndex> {
    private int index;

    public ElementIndex(int index) {
        this.index = index;
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ElementIndex) {
            return index == ((ElementIndex) object).index;
        }

        return false;
    }

    public Integer getIndex() {
        return index;
    }

    @Override
    public int compareTo(ElementIndex i) {
        return getIndex().compareTo(i.getIndex());
    }

    @Override
    public String toString() {
        return getIndex().toString();
    }
}
