package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.vm.StateSet;
import gov.nasa.jpf.vm.VM;

public class ResetableStateSet implements StateSet {
    private StateSet set;
    private VM vm;
    private int startingSize;

    public ResetableStateSet() {
        clear(0);
    }

    @Override
    public void attach(VM vm) {
        this.vm = vm;
        set.attach(vm);
    }

    @Override
    public int addCurrent() {
        return set.addCurrent() + startingSize;
    }

    @Override
    public int size() {
        return set.size() + startingSize;
    }

    public void clear(int startingSize) {
        this.startingSize = startingSize;

        set = VM.getVM().getJPF().getConfig().getInstance("panda.storage.class", StateSet.class);

        if (vm != null) {
            set.attach(vm);
        }
    }
}
