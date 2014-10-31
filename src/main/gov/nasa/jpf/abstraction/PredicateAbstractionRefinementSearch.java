package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.Property;
import gov.nasa.jpf.search.DFSearch;
import gov.nasa.jpf.vm.Path;
import gov.nasa.jpf.vm.StateSet;
import gov.nasa.jpf.vm.ThreadList;
import gov.nasa.jpf.vm.VM;

public class PredicateAbstractionRefinementSearch extends DFSearch {
    private Integer backtrackLevel = null;

    public PredicateAbstractionRefinementSearch(Config config, VM vm) {
        super(config, vm);
    }

    @Override
    public void error(Property property, Path path, ThreadList threadList) {
        error(property, path, threadList, false);
    }

    public void error(Property property, Path path, ThreadList threadList, boolean disableRefinement) {
        if (disableRefinement) {
            backtrackLevel = null;
        } else {
            backtrackLevel = PredicateAbstraction.getInstance().error();
        }

        if (backtrackLevel == null) {
            super.error(property, path, threadList);
        } else {
            VM.getVM().getSystemState().setIgnored(true);
        }
    }

    @Override
    public void search() {
        boolean depthLimitReached = false;

        depth = 0;

        notifySearchStarted();

        while (!done) {
            boolean b = checkAndResetBacktrackRequest() || !isNewState() || isEndState() || isIgnoredState() || depthLimitReached || (backtrackLevel != null && backtrackLevel < depth);
            boolean bNotPossible = false;

            while (b) {
                if (!backtrack()) { // backtrack not possible, done
                    bNotPossible = true;
                    break;
                }

                depthLimitReached = false;
                depth--;
                notifyStateBacktracked();

                b = backtrackLevel != null && backtrackLevel < depth;
            }

            if (bNotPossible) {
                break;
            }

            if (backtrackLevel != null) {
                StateSet stateSet = VM.getVM().getStateSet();

                if (stateSet instanceof ResetableStateSet) {
                    ((ResetableStateSet)stateSet).clear(VM.getVM().getStateId() + 1);
                } else if (stateSet != null) {
                    throw new RuntimeException("Cannot restart execution at refinement: invalid state set.");
                }

                backtrackLevel = null;

                VM.getVM().getChoiceGenerator().reset();
            }

            if (forward()) {
                depth++;
                notifyStateAdvanced();

                if (currentError != null){
                    notifyPropertyViolated();

                    if (hasPropertyTermination()) {
                        break;
                    }
                    // for search.multiple_errors we go on and treat this as a new state
                    // but hasPropertyTermination() will issue a backtrack request
                }

                if (depth >= depthLimit) {
                    depthLimitReached = true;
                    notifySearchConstraintHit("depth limit reached: " + depthLimit);
                    continue;
                }

                if (!checkStateSpaceLimit()) {
                    notifySearchConstraintHit("memory limit reached: " + minFreeMemory);
                    // can't go on, we exhausted our memory
                    break;
                }
            } else { // forward did not execute any instructions
                notifyStateProcessed();
            }
        }

        notifySearchFinished();

    }
}
