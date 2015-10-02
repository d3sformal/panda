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

import gov.nasa.jpf.jvm.bytecode.InstructionVisitor;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Access jump table by key match and jump
 * ..., key => ...
 */
public class LOOKUPSWITCH extends SwitchInstruction implements
        gov.nasa.jpf.vm.LookupSwitchInstruction {

    public LOOKUPSWITCH(int defaultTarget, int numberOfTargets) {
        super(defaultTarget, numberOfTargets);
    }

    @Override
    public void setTarget(int index, int match, int target) {
        targets[index] = target;
        matches[index] = match;
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        /**
         * Implementation is in SwitchInstruction, because now LOOKUPSWITCH's
         * implementation is used for TABLESWITCH as well
         */
        return super.execute(ti);
    }

    @Override
    public int getLength() {
        return 10 + 2 * (matches.length);
    }

    @Override
    public int getByteCode() {
        return 0xAB;
    }

    @Override
    public void accept(InstructionVisitor insVisitor) {
        insVisitor.visit(this);
    }

}
