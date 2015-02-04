package gov.nasa.jpf.abstraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gov.nasa.jpf.vm.choice.IntChoiceFromList;

public class DynamicIntChoiceGenerator extends IntChoiceFromList {
    private List<TraceFormula> targetBranchings = new ArrayList<TraceFormula>();

    public DynamicIntChoiceGenerator(String name, int... values) {
        super(name, values);
    }

    @Override
    public Integer getNextChoice() {
        Integer ret = super.getNextChoice();

        if (count > 0) {
            PredicateAbstraction.getInstance().forceStatesAlongTrace(targetBranchings.get(count - 1));
        }

        return ret;
    }

    public boolean hasProcessed(int value) {
        for (int i = count; i < values.length; ++i) {
            if (values[i] == value) {
                return false;
            }
        }

        for (int i = 0; i < count; ++i) {
            if (values[i] == value) {
                return true;
            }
        }

        return false;
    }

    public boolean has(int value) {
        for (int i = 0; i < values.length; ++i) {
            if (values[i] == value) {
                return true;
            }
        }

        return false;
    }

    public void add(int value, TraceFormula trace) {
        Integer[] newValues = new Integer[values.length + 1];

        for (int i = 0; i < values.length; ++i) {
            newValues[i] = values[i];
        }

        newValues[newValues.length - 1] = value;
        values = newValues;

        // Keep track of the branching that forced this choice to be added
        // This allows us to force-disable state matching and actually reach the branching
        // Not necessary to clone (because there is backtrack right after adding this choice)
        targetBranchings.add(trace);

        isDone = false;
    }

    public void add(int value) {
        add(value, PredicateAbstraction.getInstance().getTraceFormula());
    }

    public Integer[] getChoices() {
        return values;
    }

    public Integer[] getProcessedChoices() {
        return Arrays.copyOf(values, count);
    }

    public List<TraceFormula> getTraces() {
        return targetBranchings;
    }
}
