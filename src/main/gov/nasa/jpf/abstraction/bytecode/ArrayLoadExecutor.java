package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayLengthRead;
import gov.nasa.jpf.abstraction.impl.NonEmptyAttribute;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.MethodFrameSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseIdentifier;
import gov.nasa.jpf.abstraction.predicate.state.universe.Reference;
import gov.nasa.jpf.abstraction.predicate.state.universe.Indexed;
import gov.nasa.jpf.abstraction.predicate.state.universe.Universe;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.choice.IntIntervalGenerator;
import gov.nasa.jpf.vm.choice.IntChoiceFromList;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ArrayIndexOutOfBoundsExecutiveException;

import java.util.Set;
import java.util.HashSet;

public class ArrayLoadExecutor {
    private AccessExpression array;
    private Expression index;
    private AccessExpression path;

    private IndexSelector indexSelector = new IndexSelector();
    private static final String ARRAY_INDEX_OUT_OF_BOUNDS = "java.lang.ArrayIndexOutOfBoundsException";

    public Instruction execute(ArrayLoadInstruction load, ThreadInfo ti) {
        StackFrame sf = ti.getModifiableTopFrame();

        Attribute arrayAttr = (Attribute) sf.getOperandAttr(1);
        Attribute indexAttr = (Attribute) sf.getOperandAttr(0);

        arrayAttr = Attribute.ensureNotNull(arrayAttr);
        indexAttr = Attribute.ensureNotNull(indexAttr);

        array = (AccessExpression) arrayAttr.getExpression();
        index = indexAttr.getExpression();
        path = DefaultArrayElementRead.create(array, index);

        Instruction expectedNextInsn = JPFInstructionAdaptor.getStandardNextInstruction(load.getSelf(), ti);

        SystemState ss = ti.getVM().getSystemState();

        if (RunDetector.isRunning()) {
            PredicateAbstraction abs = ((PredicateAbstraction) GlobalAbstraction.getInstance().get());
            MethodFrameSymbolTable sym = abs.getSymbolTable().get(0);

            if (indexSelector.selectIndex(ti, ss, abs, sym, array, index)) {
                return load.getSelf();
            }

            sf.pop();
            sf.push(indexSelector.getIndex());
        }

        Instruction actualNextInsn = load.executeConcrete(ti);

        if (JPFInstructionAdaptor.testArrayElementInstructionAbort(load.getSelf(), ti, expectedNextInsn, actualNextInsn)) {
            return actualNextInsn;
        }

        Attribute attribute = new NonEmptyAttribute(null, path);

        sf.setOperandAttr(attribute);

        return actualNextInsn;
    }

    public void push(ArrayLoadInstruction load, StackFrame sf, ElementInfo ei, int someIndex) throws ArrayIndexOutOfBoundsExecutiveException {
        if (RunDetector.isRunning()) {
            // Upper bound on the index (= array length)
            Expression upperBound;

            // Builtin static (constant-length) arrays
            // such as SWITCHMAP for enum types
            if (array instanceof ObjectFieldRead && ((ObjectFieldRead) array).getField().getName().startsWith("$SwitchMap")) {
                upperBound = Constant.create(ei.arrayLength());
            } else {
                upperBound = DefaultArrayLengthRead.create(array);
            }

            // i >= 0 && i < a.length
            Predicate inBounds = Conjunction.create(
                Negation.create(LessThan.create(index, Constant.create(0))),
                LessThan.create(index, upperBound)
            );

            TruthValue value = (TruthValue) GlobalAbstraction.getInstance().processBranchingCondition(inBounds);

            if (value != TruthValue.TRUE) {
                throw new ArrayIndexOutOfBoundsExecutiveException(ThreadInfo.getCurrentThread().createAndThrowException(ARRAY_INDEX_OUT_OF_BOUNDS, "Cannot ensure: " + inBounds));
            }
        }

        load.pushConcrete(sf, ei, someIndex);
    }
}