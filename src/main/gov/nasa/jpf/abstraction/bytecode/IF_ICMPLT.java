//
// Copyright (C) 2012 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.AbstractBoolean;
import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;

/**
 * Branch if int comparison succeeds
 * ..., value1, value2 => ...
 */
public class IF_ICMPLT extends gov.nasa.jpf.jvm.bytecode.IF_ICMPLT implements AbstractBranching {

    /**
     * Share implementation with all the other binary if instructions.
     */
    BinaryIfInstructionExecutor executor = new BinaryIfInstructionExecutor();

    public IF_ICMPLT(int targetPc) {
        super(targetPc);
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
    public AbstractBoolean getCondition(int v1, AbstractValue abs_v1, int v2, AbstractValue abs_v2) {
        return Abstraction._lt(v1, abs_v1, v2, abs_v2);
    }

    @Override
    public TruthValue getConcreteBranchValue(int v1, int v2) {
        return TruthValue.create(v1 < v2);
    }

    @Override
    public Predicate createPredicate(Expression expr1, Expression expr2) {
        return LessThan.create(expr1, expr2);
    }

}
