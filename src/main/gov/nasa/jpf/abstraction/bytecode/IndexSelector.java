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
import gov.nasa.jpf.abstraction.state.MethodFrameSymbolTable;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.state.universe.Indexed;
import gov.nasa.jpf.abstraction.state.universe.Reference;
import gov.nasa.jpf.abstraction.state.universe.Universe;
import gov.nasa.jpf.abstraction.state.universe.UniverseIdentifier;
import gov.nasa.jpf.abstraction.util.RunDetector;

public class IndexSelector {
    private Integer selectedIndex = null;
    private static final String INDEX_CHOICE_ID = "abstractArrayElementLoadChooseIndex";

    public boolean makeChoices(ThreadInfo ti, SystemState ss, PredicateAbstraction abs, MethodFrameSymbolTable sym, AccessExpression array, Expression index) {
        return selectIndex(ti, ss, abs, sym, array, index);
    }

    public boolean selectIndex(ThreadInfo ti, SystemState ss, PredicateAbstraction abs, MethodFrameSymbolTable sym, AccessExpression array, Expression index) {
        if (!isIndexChoiceFirstStep(ti, ss)) {

            // There is an elementChoice but not the indexChoice
            // Leave the selectedIndex untouched, no need to recompute it
            if (!ti.isFirstStepInsn() || selectedIndex == null) {
                if (index instanceof Constant) {
                    selectedIndex = ((Constant) index).value.intValue();
                } else {
                    selectedIndex = abs.computePreciseExpressionValue(index);
                }

                if (selectedIndex == null) {
                    Universe universe = sym.getUniverse();

                    Set<UniverseIdentifier> values = new HashSet<UniverseIdentifier>();
                    sym.lookupValues(array, values);

                    Indexed arrayObject = (Indexed) universe.get(values.iterator().next());

                    ChoiceGenerator<?> indexChoice = new IntIntervalGenerator(INDEX_CHOICE_ID, 0, arrayObject.getLength() - 1);

                    if (ti.isFirstStepInsn()) {
                        ss.setForced(true);
                    }

                    ss.setNextChoiceGenerator(indexChoice);

                    return true;
                }
            }
        } else {
            ChoiceGenerator<?> indexChoice = ss.getCurrentChoiceGenerator(INDEX_CHOICE_ID, IntIntervalGenerator.class);

            selectedIndex = ((IntIntervalGenerator) indexChoice).getNextChoice();

            Predicate assumption = Equals.create(index, Constant.create(selectedIndex));

            // This is inefficient: many infeasible choices are created and then immediately pruned (when PANDA starts their exploration)
            // We are performing non-deterministic choice over all indices to an array
            // However, only some concrete indices satisfy constraints given in the current abstract state (predicates over index expression)
            // We prune the infeasible choices
            if (abs.getPredicateValuation().getPredicatesInconsistentWith(assumption, TruthValue.TRUE).isEmpty()) {
                abs.getPredicateValuation().force(assumption, TruthValue.TRUE);
            } else {
                ti.breakTransition("Ignore inconsistent state");
                ss.setIgnored(true);

                return true;
            }
        }

        return false;
    }

    private boolean isIndexChoiceFirstStep(ThreadInfo ti, SystemState ss) {
        ChoiceGenerator<?> prev = null;

        return ti.isFirstStepInsn() && ss.getCurrentChoiceGenerator(prev) != null && ss.getCurrentChoiceGenerator(prev).getId().equals(INDEX_CHOICE_ID);
    }

    public Integer getIndex() {
        return selectedIndex;
    }
}
