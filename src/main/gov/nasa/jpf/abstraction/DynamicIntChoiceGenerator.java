package gov.nasa.jpf.abstraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.choice.IntChoiceFromList;

import gov.nasa.jpf.abstraction.common.access.Unknown;

public class DynamicIntChoiceGenerator extends IntChoiceFromList {
    public class Introduction {
        public int stateId;
        public int stateVer;

        public Introduction(int id, int ver) {
            stateId = id;
            stateVer = ver;
        }
    }

    private List<Introduction> introducedInState = new ArrayList<Introduction>();
    private List<TraceFormula> targetBranchings = new ArrayList<TraceFormula>();

    // Enable individual choices only when other unknowns have specific values
    // The map gives these constraints on other unknowns
    private List<Map<String, Integer>> otherUnknownsConditions = new ArrayList<Map<String, Integer>>();

    public DynamicIntChoiceGenerator(String name, int... values) {
        super(name, values);
    }

    public Integer getCurrentChoice() {
        return super.getNextChoice();
    }

    @Override
    public Integer getNextChoice() {
        Integer ret = getCurrentChoice();

        if (count > 0) {
            PredicateAbstraction.getInstance().forceStatesAlongTrace(targetBranchings.get(count - 1));
        }

        return ret;
    }

    public boolean isNextChoiceEnabled() {
        // Check that exact values (the current values during execution) of previous unknowns match the enabling constraints (the values discovered together with this model)
        //   value(u1) = v1
        //   value(u2) = u2
        //   ...

        boolean ret = true;

        if (count > 0) {
            Map<String, Integer> condition = otherUnknownsConditions.get(count - 1);

            for (String name : condition.keySet()) {
                Unknown unknown = PredicateAbstraction.getInstance().getUnknowns().get(name);

                int currentChoice = unknown.getChoiceGenerator().getNextChoice();
                int enabledChoice = condition.get(name);

                if (currentChoice != enabledChoice) {
                    ret = false;
                    break;
                }
            }
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

    public void add(int value, TraceFormula trace, Map<String, Integer> otherUnknownsCondition) {
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
        otherUnknownsConditions.add(otherUnknownsCondition);

        int stateId = VM.getVM().getSystemState().getId();
        int stateVer = PredicateAbstraction.getInstance().stateVer.get(stateId);

        introducedInState.add(new Introduction(stateId, stateVer));

        isDone = false;
    }

    public void add(int value, Map<String, Integer> condition) {
        add(value, PredicateAbstraction.getInstance().getTraceFormula(), condition);
    }

    public void add(int value) {
        add(value, new HashMap<String, Integer>());
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

    public List<Map<String, Integer>> getConditions() {
        return otherUnknownsConditions;
    }

    public int getIntroductionStateId() {
        if (count > 0) {
            return introducedInState.get(count - 1).stateId;
        }

        return 0;
    }

    public int getIntroductionStateVer() {
        if (count > 0) {
            return introducedInState.get(count - 1).stateVer;
        }

        return 0;
    }
}
