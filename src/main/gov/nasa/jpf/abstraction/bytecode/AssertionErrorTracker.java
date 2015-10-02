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
package gov.nasa.jpf.abstraction.bytecode;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.MethodInfo;

import gov.nasa.jpf.abstraction.util.Pair;

public class AssertionErrorTracker {
    private static Map<Integer, Pair<MethodInfo, Integer>> errorAllocationSite = new HashMap<Integer, Pair<MethodInfo, Integer>>();

    public static void setAssertionErrorAllocationSite(ElementInfo error, MethodInfo m, int pc) {
        errorAllocationSite.put(error.getObjectRef(), new Pair<MethodInfo, Integer>(m, pc));
    }

    public static Pair<MethodInfo, Integer> getAllocationSite(ElementInfo error) {
        return errorAllocationSite.get(error.getObjectRef());
    }
}
