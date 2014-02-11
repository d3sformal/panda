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

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayLengthRead;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.abstraction.impl.NonEmptyAttribute;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.FlatSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseIdentifier;
import gov.nasa.jpf.abstraction.predicate.state.universe.Reference;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.choice.IntChoiceFromList;
import gov.nasa.jpf.vm.ChoiceGenerator;

import java.util.Set;
import java.util.HashSet;

public class AALOAD extends gov.nasa.jpf.jvm.bytecode.AALOAD {

    private AccessExpression array;
    private Expression index;
    private AccessExpression path;

    private Integer selectedElementRef = null;

	@Override
	public Instruction execute(ThreadInfo ti) {
		StackFrame sf = ti.getTopFrame();
		Attribute arrayAttr = (Attribute) sf.getOperandAttr(1);
		Attribute indexAttr = (Attribute) sf.getOperandAttr(0);
		
		arrayAttr = Attribute.ensureNotNull(arrayAttr);
		indexAttr = Attribute.ensureNotNull(indexAttr);

		array = (AccessExpression) arrayAttr.getExpression();
        index = indexAttr.getExpression();
		path = DefaultArrayElementRead.create(array, index);
						
		Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(this, ti);

        SystemState ss = ti.getVM().getSystemState();
        ElementInfo ei = ti.getElementInfo(sf.peek(1));

        if (!ti.isFirstStepInsn()) {
            if (ei.arrayLength() > 1) {
                FlatSymbolTable symbolTable = ((PredicateAbstraction) GlobalAbstraction.getInstance().get()).getSymbolTable().get(0);

                Set<UniverseIdentifier> values = new HashSet<UniverseIdentifier>();
                symbolTable.lookupValues(path, values);

                int[] references = new int[values.size()];

                int i = 0;

                for (UniverseIdentifier id : values) {
                    Reference ref = (Reference) id;

                    references[i] = ref.getReferenceNumber();

                    ++i;
                }

                ChoiceGenerator<?> cg = new IntChoiceFromList("abstractArrayElementLoad", references);

                ss.setNextChoiceGenerator(cg);

                return this;
            }
        } else {
            if (ei.arrayLength() > 1) {
                ChoiceGenerator<?> cg = ss.getCurrentChoiceGenerator("abstractArrayElementLoad", IntChoiceFromList.class);

                selectedElementRef = ((IntChoiceFromList) cg).getNextChoice();
            }
        }

		Instruction actualNextInsn = super.execute(ti);

		if (JPFInstructionAdaptor.testArrayElementInstructionAbort(this, ti, expectedNextInsn, actualNextInsn)) {
			return actualNextInsn;
		}
		
		Attribute attribute = new NonEmptyAttribute(null, path);

		sf = ti.getModifiableTopFrame();
		sf.setOperandAttr(attribute);

		return actualNextInsn;
	}

    @Override
    public void push (StackFrame sf, ElementInfo ei, int someIndex) throws ArrayIndexOutOfBoundsException {
        // i >= 0 && i < a.length
        Predicate inBounds = Conjunction.create(
            Negation.create(LessThan.create(index, Constant.create(0))),
            LessThan.create(index, DefaultArrayLengthRead.create(array))
        );

        TruthValue value = (TruthValue) GlobalAbstraction.getInstance().processBranchingCondition(inBounds);

        if (value != TruthValue.TRUE) {
            throw new ArrayIndexOutOfBoundsException("Cannot ensure: " + inBounds);
        }

        if (ei.arrayLength() <= 1) {
            sf.push(ei.getReferenceElement(0));
        } else {
            sf.push(selectedElementRef.intValue());
        }
    }
}
