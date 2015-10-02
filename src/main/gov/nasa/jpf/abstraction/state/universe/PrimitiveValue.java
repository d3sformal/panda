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

public class PrimitiveValue extends UniverseValue {
    private PrimitiveValueIdentifier identifier = new PrimitiveValueIdentifier();

    @Override
    public PrimitiveValue createShallowCopy() {
        return this; // No need to copy primitive values, they do not change nor get transferred
    }

    @Override
    public PrimitiveValueIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public int hashCode() {
        return getIdentifier().hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof PrimitiveValue) {
            return getIdentifier().equals(((PrimitiveValue) object).getIdentifier());
        }

        return false;
    }
}
