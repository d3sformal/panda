package gov.nasa.jpf.abstraction.bytecode;

import java.util.HashSet;
import java.util.Set;

import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.choice.IntChoiceFromList;
import gov.nasa.jpf.vm.choice.IntIntervalGenerator;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.state.MethodFrameSymbolTable;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.state.universe.Indexed;
import gov.nasa.jpf.abstraction.state.universe.Reference;
import gov.nasa.jpf.abstraction.state.universe.Universe;
import gov.nasa.jpf.abstraction.state.universe.UniverseIdentifier;
import gov.nasa.jpf.abstraction.util.RunDetector;

public class ElementSelector extends IndexSelector {
    private Integer selectedElementRef = null;
    private static final String ELEMENT_VALUE_CHOICE_ID = "abstractArrayElementLoadChooseElement";

    public static boolean isElementChoice(ChoiceGenerator<?> cg) {
        return cg.getId().equals(ELEMENT_VALUE_CHOICE_ID);
    }

    @Override
    public boolean makeChoices(ThreadInfo ti, SystemState ss, PredicateAbstraction abs, MethodFrameSymbolTable sym, AccessExpression array, Expression index) {
        return selectElementRef(ti, ss, abs, sym, array, index);
    }

    public boolean selectElementRef(ThreadInfo ti, SystemState ss, PredicateAbstraction abs, MethodFrameSymbolTable sym, AccessExpression array, Expression index) {
        if (selectIndex(ti, ss, abs, sym, array, index)) {
            return true;
        }

        if (!isElementChoiceFirstStep(ti, ss)) {
            Set<UniverseIdentifier> values = new HashSet<UniverseIdentifier>();

            sym.lookupValues(DefaultArrayElementRead.create(array, Constant.create(getIndex())), values);

            if (values.size() == 1) {
                selectedElementRef = ((Reference) values.iterator().next()).getReferenceNumber();
            } else {
                int[] references = new int[values.size()];

                int i = 0;

                for (UniverseIdentifier id : values) {
                    Reference ref = (Reference) id;

                    references[i] = ref.getReferenceNumber();

                    ++i;
                }

                ChoiceGenerator<?> elementValueChoice = new IntChoiceFromList(ELEMENT_VALUE_CHOICE_ID, references);

                if (ti.isFirstStepInsn()) {
                    ss.setForced(true);
                }

                ss.setNextChoiceGenerator(elementValueChoice);

                return true;
            }
        } else {
            ChoiceGenerator<?> elementValueChoice = ss.getCurrentChoiceGenerator(ELEMENT_VALUE_CHOICE_ID, IntChoiceFromList.class);

            selectedElementRef = ((IntChoiceFromList) elementValueChoice).getNextChoice();

            sym.restrictToSingleValue(array, getIndex(), new Reference(ti.getElementInfo(selectedElementRef)));
        }

        return false;
    }

    private boolean isElementChoiceFirstStep(ThreadInfo ti, SystemState ss) {
        ChoiceGenerator<?> prev = null;

        return ti.isFirstStepInsn() && ss.getCurrentChoiceGenerator(prev) != null && ss.getCurrentChoiceGenerator(prev).getId().equals(ELEMENT_VALUE_CHOICE_ID);
    }

    public Integer getElementRef() {
        return selectedElementRef;
    }
}
