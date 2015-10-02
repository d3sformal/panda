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
package gov.nasa.jpf.abstraction.heuristic;

import java.util.HashSet;
import java.util.Set;

import gov.nasa.jpf.jvm.bytecode.LocalVariableInstruction;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;

import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.state.SystemPredicateValuation;

public class VariableLifespanRefinementHeuristic extends RefinementHeuristic {
    public VariableLifespanRefinementHeuristic(SystemPredicateValuation predVal) {
        super(predVal);
    }

    @Override
    protected boolean refineAtomic(Predicate interpolant, MethodInfo m, int fromPC, int toPC) {
        Instruction first = m.getFirstInsn();
        Instruction last = m.getLastInsn();

        Set<AccessExpression> exprs = new HashSet<AccessExpression>();

        interpolant.addAccessExpressionsToSet(exprs);

        int start = fromPC;
        int end = toPC;

        for (Instruction i : m.getInstructions()) {
            if (i instanceof LocalVariableInstruction) {
                LocalVariableInstruction lvInsn = (LocalVariableInstruction) i;
                Root var = DefaultRoot.create(lvInsn.getLocalVariableName(), lvInsn.getLocalVariableIndex());
                
                if (exprs.contains(var)) {
                    int pos = i.getPosition();
                    
                    if (start > pos) {
                        start = pos;
                    }

                    pos += i.getLength();

                    if (end < pos) {
                        end = pos;
                    }
                }
            }
        }

        return super.refineAtomic(interpolant, m, start, end);
    }
}
