package gov.nasa.jpf.abstraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.VM;

public class ChoiceHistory implements Cloneable {
    private class Choices<T> {
        public ChoiceGenerator<T> generator = null;
        public List<T> choices = new ArrayList<T>();
        public T last = null;

        public Choices(ChoiceGenerator<T> generator) {
            this.generator = generator;
        }

        public void add() {
            if (last != null) {
                choices.add(last);
            }

            replace();
        }

        public void replace() {
            last = generator.getNextChoice();
        }

        public boolean isEmpty() {
            return choices.isEmpty() && last == null;
        }

        public boolean isAlreadyProcessed() {
            return choices.contains(generator.getNextChoice());
        }

        public boolean isCurrent() {
            return last.equals(generator.getNextChoice());
        }

        @Override
        public Choices<T> clone() {
            Choices<T> clone = new Choices<T>(generator);

            for (T choice : choices) {
                clone.choices.add(choice);
            }

            clone.last = last;

            return clone;
        }

        @Override
        public String toString() {
            StringBuilder ret = new StringBuilder();

            ret.append('[');

            if (!isEmpty()) {
                for (T choice : choices) {
                    ret.append(String.valueOf(choice));
                    ret.append(',');
                    ret.append(' ');
                }
                ret.append('>');
                ret.append(String.valueOf(last));
            }

            ret.append(']');

            return ret.toString();
        }
    }

    private Stack<Choices<?>> history = new Stack<Choices<?>>();
    private int level = 0;

    public <T> void rememberChoiceGenerator(ChoiceGenerator<T> cg) {
        if (level == history.size()) { // Not predicting at the moment (no look-ahead)
            history.push(new Choices<T>(cg));
        } else {
            if (history.get(level - 1).generator.getClass().equals(cg.getClass())) { // Advancing along predicted choice generators
                // Nothing
            } else { // A non-matching prediction (maybe the predicted choice generator has been left out)
                while (history.size() > level && history.get(level).generator.getClass().equals(cg.getClass())) { // Drop obsolete predictions
                    history.remove(level);
                }

                if (level == history.size()) { // If there was no matching prediction, start building history again
                    history.push(new Choices<T>(cg));
                }
            }
        }

        ++level;
    }

    public void rememberChoice() {
        Choices<?> choices = history.get(level - 1);

        if (choices.isEmpty()) {
            choices.add();
        } else {
            while (choices.isAlreadyProcessed()) { // Already entirely processed
                if (choices.generator.hasMoreChoices()) {
                    choices.generator.advance();
                } else {
                    forgetChoiceGenerator();

                    return;
                }
            }

            if (!choices.isCurrent()) { // Completely new because it is not being currently processed
                if (level == history.size()) {
                    choices.add();
                } else {
                    while (history.size() > level) {
                        history.pop();
                    }

                    history.peek().replace();
                }
            }
        }

        if (PandaConfig.getInstance().enabledVerbose(this.getClass())) {
            System.out.println("Advance: " + choices.generator.getNextChoice());
            System.out.println(this.toString());
        }
    }

    public void forgetChoiceGenerator() {
        while (level - 1 < history.size()) {
            history.pop();
        }

        --level;

        if (PandaConfig.getInstance().enabledVerbose(this.getClass())) {
            System.out.println("Forget:");
            System.out.println(this.toString());
        }
    }

    public void backtrack() {
    }

    @Override
    public ChoiceHistory clone() {
        ChoiceHistory clone = new ChoiceHistory();

        for (Choices<?> choices : history) {
            clone.history.add(choices.clone());
        }

        clone.level = level;

        return clone;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();

        int level = 1;

        for (Choices<?> choices : history) {
            if (level == this.level) {
                ret.append("v----------------vVv----------------v\n");
            }
            ret.append(choices.toString() + " " + choices.generator.getClass().getSimpleName() + " " + System.identityHashCode(choices.generator));
            ret.append('\n');
            ++level;
        }

        return ret.toString();
    }
}
