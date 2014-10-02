package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.vm.choice.IntChoiceFromList;

public class DynamicIntChoiceGenerator extends IntChoiceFromList {
    public DynamicIntChoiceGenerator(String name, int... values) {
        super(name, values);
    }

    public boolean hasProcessed(int value) {
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

    public void add(int value) {
        Integer[] newValues = new Integer[values.length + 1];

        for (int i = 0; i < values.length; ++i) {
            newValues[i] = values[i];
        }

        newValues[newValues.length - 1] = value;
        values = newValues;

        isDone = false;
    }

    public Integer[] getChoices() {
        return values;
    }
}
