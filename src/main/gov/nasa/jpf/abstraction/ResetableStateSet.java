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
package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.vm.JenkinsStateSet;
import gov.nasa.jpf.vm.SerializingStateSet;
import gov.nasa.jpf.vm.StateSerializer;
import gov.nasa.jpf.vm.VM;

public class ResetableStateSet extends SerializingStateSet {
    private JenkinsStateSet set;
    private VM vm;
    private int startingSize;

    public ResetableStateSet() {
        clear(0);
    }

    @Override
    public void attach(VM vm) {
        super.attach(vm);

        this.vm = vm;
        set.attach(vm);
    }

    @Override
    public int addCurrent() {
        return set.addCurrent() + startingSize;
    }

    @Override
    protected int add(int[] state) {
        return set.add(state) + startingSize;
    }

    public boolean isCurrentUnique() {
        int[] data = serializer.getStoringData();

        long hash = JenkinsStateSet.longLookup3Hash(data);
        int id = set.lookup(hash);

        return id == -1;
    }

    @Override
    public int size() {
        return set.size() + startingSize;
    }

    public void clear(int startingSize) {
        this.startingSize = startingSize;

        set = PandaConfig.getInstance().getStorageInstance();

        if (vm != null) {
            set.attach(vm);
        }
    }
}
