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

import java.util.HashSet;
import java.util.Set;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.impl.NullExpression;
import gov.nasa.jpf.abstraction.concrete.AnonymousExpression;
import gov.nasa.jpf.abstraction.state.MethodFrameSymbolTable;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.state.universe.Universe;
import gov.nasa.jpf.abstraction.state.universe.UniverseIdentifier;

/**
 * Branch if int comparison with NULL value succeeds
 * ..., value => ...
 */
public class IFNULL extends gov.nasa.jpf.jvm.bytecode.IFNULL implements UnaryAbstractBranching {

    Constant secondOperand = NullExpression.create();
    UnaryIfInstructionExecutor executor = new UnaryIfInstructionExecutor(secondOperand);
    Predicate last;

    public IFNULL(int targetPc) {
        super(targetPc);
    }

    @Override
    public Expression getSecondOperand() {
        return secondOperand;
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        return executor.execute(this, ti);
    }

    @Override
    public Instruction executeConcrete(ThreadInfo ti) {
        return super.execute(ti);
    }

    @Override
    public Instruction getSelf() {
        return this;
    }

    @Override
    public boolean getConcreteBranchValue(int v1, int v2) {
        return v1 == MJIEnv.NULL;
    }

    @Override
    public Predicate createPredicate(Expression expr1, Expression expr2) {
        last = createPredicateHelper(expr1, expr2);
        return last;
    }

    public Predicate createPredicateHelper(Expression expr1, Expression expr2) {
        if (expr1 instanceof NullExpression) {
            return Tautology.create();
        } else if (expr1 instanceof AnonymousExpression) {
            return Contradiction.create();
        } else if (expr1 instanceof AccessExpression) {
            AccessExpression access = (AccessExpression) expr1;

            MethodFrameSymbolTable symbolTable = PredicateAbstraction.getInstance().getSymbolTable().get(0);

            Set<UniverseIdentifier> values = new HashSet<UniverseIdentifier>();

            symbolTable.lookupValues(access, values);

            if (values.size() == 1) {
                if (values.contains(Universe.nullReference)) {
                    return Tautology.create();
                } else {
                    return Contradiction.create();
                }
            }
        }

        return Equals.create(expr1, expr2);
    }

    @Override
    public Predicate getLastPredicate() {
        return last;
    }

    @Override
    public Instruction getDefaultTarget() {
        return getTarget();
    }

    @Override
    public Instruction getTarget(ThreadInfo ti, int num) {
        if (num == 0) {
            return getNext(ti);
        }

        return getTarget();
    }

}
