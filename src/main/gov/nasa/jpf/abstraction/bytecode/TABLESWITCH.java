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

import java.util.ArrayList;

import gov.nasa.jpf.JPFException;
import gov.nasa.jpf.jvm.bytecode.InstructionVisitor;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Access jump table by index and jump
 * ..., index  => ...
 * WARNING: it actually duplicates LOOKUPSWITCH behavior
 */
public class TABLESWITCH extends SwitchInstruction implements gov.nasa.jpf.vm.TableSwitchInstruction {

    int min, max;

    public TABLESWITCH(int defaultTarget, int min, int max) {
        super(defaultTarget, (max - min + 1));
        this.min = min;
        this.max = max;
    }

    @Override
    public void setTarget(int value, int target) {
        int i = value - min;

        if (i >= 0 && i < targets.length) {
            targets[i] = target;
        } else {
            throw new JPFException("illegal tableswitch target: " + value);
        }
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        /*
         * TABLESWITCH is not implemented properly because it loses sense with
         * abstractions. Instead LOOKUPSWITCH behavior is used.
         */
        return super.execute(ti);
    }

    @Override
    public int getLength() {
        return 13 + 2 * (matches.length);
    }

    @Override
    public int getByteCode() {
        return 0xAA;
    }

    @Override
    public void accept(InstructionVisitor insVisitor) {
        insVisitor.visit(this);
    }

    @Override
    public int[] getMatches() {
        int[] matches = new int[max - min + 1];

        for (int i = 0; i < matches.length; ++i) {
            matches[i] = i;
        }

        return matches;
    }

}
