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
import gov.nasa.jpf.abstraction.concrete.AnonymousExpression;
import gov.nasa.jpf.abstraction.common.impl.NullExpression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MJIEnv;

import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.MethodFrameSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.universe.Universe;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseIdentifier;

import java.util.Set;
import java.util.HashSet;

/**
 * Branch if int comparison with NULL value succeeds
 * ..., value => ...
 */
public class IFNULL extends gov.nasa.jpf.jvm.bytecode.IFNULL implements AbstractBranching {
	
	UnaryIfInstructionExecutor executor = new UnaryIfInstructionExecutor(NullExpression.create());

	public IFNULL(int targetPc) {
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
		return Abstraction._eq(v1, abs_v1, MJIEnv.NULL, null);
	}

	@Override
	public Predicate createPredicate(Expression expr1, Expression expr2) {
        if (expr1 instanceof NullExpression) {
            return Tautology.create();
        } else if (expr1 instanceof AnonymousExpression) {
            return Contradiction.create();
        } else if (expr1 instanceof AccessExpression) {
            AccessExpression access = (AccessExpression) expr1;

            MethodFrameSymbolTable symbolTable = ((PredicateAbstraction) GlobalAbstraction.getInstance().get()).getSymbolTable().get(0);

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

}
