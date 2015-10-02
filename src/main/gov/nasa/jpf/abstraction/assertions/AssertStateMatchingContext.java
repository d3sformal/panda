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
package gov.nasa.jpf.abstraction.assertions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.vm.Instruction;

public class AssertStateMatchingContext {
    public static Map<Instruction, LocationAssertion> assertions = new HashMap<Instruction, LocationAssertion>();

    public static <T extends LocationAssertion> T getAssertion(Instruction pc, Class<T> assertionClass) {
        if (!assertions.containsKey(pc)) {
            try {
                assertions.put(pc, assertionClass.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return cast(assertions.get(pc), assertionClass);
    }

    private static <T> T cast(Object instance, Class<T> cls) {
        if (cls.isAssignableFrom(instance.getClass())) {

            @SuppressWarnings("unchecked")
            T tInstance = (T) instance;

            return tInstance;
        }

        return null;
    }

    public static Set<Instruction> getLocations() {
        return assertions.keySet();
    }

    public static LocationAssertion get(Instruction insn) {
        return assertions.get(insn);
    }

    public static void reset() {
        assertions.clear();
    }
}
