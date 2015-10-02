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
package gov.nasa.jpf.abstraction.util;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;


public class DebugMonitor extends ListenerAdapter {
    public DebugMonitor(Config cfg, JPF jpf) {
    }

    @Override
    public void stateAdvanced(Search search) {
        System.out.print("[MONITOR] state : ");

        if (search.isNewState()) {
            System.out.print("new");
        } else {
            System.out.print("visited");
        }

        System.out.println(" , id = " + search.getStateId());
    }

    @Override
    public void stateBacktracked(Search search) {
        System.out.println("[MONITOR] backtrack");
    }
}
