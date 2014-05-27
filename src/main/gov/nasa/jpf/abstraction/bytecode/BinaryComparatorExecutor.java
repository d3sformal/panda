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

import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.numeric.SignsAbstraction;
import gov.nasa.jpf.abstraction.numeric.SignsValue;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.BranchingConditionValuation;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.choice.IntChoiceFromList;

import gov.nasa.jpf.abstraction.Attribute;

/**
 * An implementation of common behaviour of all the comparison expressions
 */
public abstract class BinaryComparatorExecutor<T> {

    final public Instruction execute(AbstractBinaryOperator<T> cmp, ThreadInfo ti) {

        String name = cmp.getClass().getSimpleName();

        SystemState ss = ti.getVM().getSystemState();
        StackFrame sf = ti.getModifiableTopFrame();

        Attribute attr1 = getLeftAttribute(sf);
        Attribute attr2 = getRightAttribute(sf);

        AbstractValue abs_v1 = Attribute.getAbstractValue(attr1);
        AbstractValue abs_v2 = Attribute.getAbstractValue(attr2);
        Expression expr1 = Attribute.getExpression(attr1);
        Expression expr2 = Attribute.getExpression(attr2);

        Attribute result = null;

        /**
         * First we check whether there is no choice generator present
         * If not we perform the comparison
         * Otherwise we inspect all the choices
         */
        if (!ti.isFirstStepInsn()) { // first time around
            /**
             * If there is enough information (symbolic expressions) to decide the condition we ask abstractions to provide the truth value
             * Only predicate abstraction is designed to respond with a valid value (TRUE, FALSE, UNKNOWN).
             * No other abstraction can do that, the rest of them returns UNDEFINED.
             */
            if (expr1 != null && expr2 != null && RunDetector.isRunning()) {
                TruthValue lt = (TruthValue) GlobalAbstraction.getInstance().processBranchingCondition(LessThan.create(expr1, expr2));
                TruthValue eq = (TruthValue) GlobalAbstraction.getInstance().processBranchingCondition(Equals.create(expr1, expr2));
                TruthValue gt = null;

                if (TruthValue.and(lt, eq) == TruthValue.UNDEFINED) {
                    gt = TruthValue.UNDEFINED;
                } else if (TruthValue.and(lt, eq) == TruthValue.FALSE) {
                    gt = TruthValue.TRUE;
                } else if (lt == TruthValue.TRUE || eq == TruthValue.TRUE) {
                    gt = TruthValue.FALSE;
                } else {
                    gt = TruthValue.UNKNOWN;
                }

                // UNDEFINED MEANS THERE WAS NO ABSTRACTION TO DECIDE THE VALIDITY OF THE PREDICATE
                if (gt != TruthValue.UNDEFINED) {
                    SignsValue absValue = SignsAbstraction.getInstance().create(lt != TruthValue.FALSE, eq != TruthValue.FALSE, gt != TruthValue.FALSE);

                    result = new Attribute(absValue, Constant.create(absValue.getKey() - 1));
                }
            }

            /**
             * When there was no predicate abstraction we try to follow other abstractions or default to concrete execution when the operands are non abstract values
             */
            if (result == null) {
                T v1 = getLeftOperand(sf);
                T v2 = getRightOperand(sf);

                if (abs_v1 == null && abs_v2 == null) {
                    Instruction ret = cmp.executeConcrete(ti);

                    sf.setOperandAttr(new Attribute(null, Constant.create(sf.peek())));

                    return ret;
                }

                result = cmp.getResult(v1, attr1, v2, attr2);
            }

            /**
             * If the result of the comparison is not deterministic we create a choice generator and let JPF to reexecute this instruction
             */
            if (Attribute.getAbstractValue(result).isComposite()) {
                int size = Attribute.getAbstractValue(result).getTokensNumber();
                int i = 0;
                int[] choices = new int[size];

                for (AbstractValue choice : Attribute.getAbstractValue(result).getTokens()) {
                    choices[i] = choice.getKey();

                    ++i;
                }

                ChoiceGenerator<?> cg = new IntChoiceFromList("abstractComparisonAll", choices);
                ss.setNextChoiceGenerator(cg);

                return cmp.getSelf();
            }
        } else { // this is what really returns results
            /**
             * Exploration of all possible outcomes of the comparison
             */
            ChoiceGenerator<?> cg = ss.getChoiceGenerator();

            assert (cg instanceof IntChoiceFromList);

            int key = (Integer) cg.getNextChoice();

            SignsValue custom = new SignsValue(key);
            SignsValue absValue = SignsAbstraction.getInstance().create(custom.can_be_NEG(), custom.can_be_ZERO(), custom.can_be_POS());

            result = new Attribute(absValue, Constant.create(absValue.getKey() - 1));

            if (expr1 != null && expr2 != null) {
                Predicate predicate = Equals.create(expr1, expr2);

                if (absValue == SignsAbstraction.NEG) {
                    predicate = LessThan.create(expr1, expr2);
                }
                if (absValue == SignsAbstraction.POS) {
                    predicate = LessThan.create(expr2, expr1);
                }

                GlobalAbstraction.getInstance().informAboutBranchingDecision(new BranchingConditionValuation(predicate, TruthValue.TRUE));
            }
        }

        storeResult(result, sf);

        return cmp.getNext(ti);
    }

    protected Attribute getAttribute(StackFrame sf, int index) {
        return (Attribute)sf.getOperandAttr(index);
    }

    abstract protected Attribute getLeftAttribute(StackFrame sf);
    abstract protected Attribute getRightAttribute(StackFrame sf);
    abstract protected T getLeftOperand(StackFrame sf);
    abstract protected T getRightOperand(StackFrame sf);
    abstract protected void storeAttribute(Attribute result, StackFrame sf);
    abstract protected void storeResult(Attribute result, StackFrame sf);
}
