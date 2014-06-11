package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.vm.IntChoiceGenerator;
import gov.nasa.jpf.vm.choice.IntIntervalGenerator;


public class AbstractChoiceGenerator extends IntIntervalGenerator {

    boolean isReverseOrder;

    // assume we always have 2 choices: used only for bools in coditions
    public AbstractChoiceGenerator() {
        super("abstract",0,1,1);
        isReverseOrder = false;
    }

    /*
     * If reverseOrder is true, the ChoiceGenerator
     * explores paths in the opposite order used by
     * the default constructor. If reverseOrder is false
     * the usual behavior is used.
     */
    public AbstractChoiceGenerator(boolean reverseOrder) {
        super("abstract",0, 1, reverseOrder ? -1 : 1);
        isReverseOrder = reverseOrder;
    }



    public IntChoiceGenerator randomize() {
        return new AbstractChoiceGenerator(random.nextBoolean());
    }

    public void setNextChoice(int nextChoice){
        super.next = nextChoice;
    }
}
