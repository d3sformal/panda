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
package gov.nasa.jpf.abstraction.common.access;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;

public class SpecialVariable extends DefaultRoot {
    private static Map<String, SpecialVariable> instances = new HashMap<String, SpecialVariable>();

    protected SpecialVariable(String name) {
        super(name);
    }

    public static SpecialVariable create(String name) {
        if (!instances.containsKey(name)) {
            instances.put(name, new SpecialVariable(name));
        }

        return instances.get(name);
    }

    @Override
    public void accept(PredicatesComponentVisitor visitor) {
        visitor.visit(this);
    }
}
