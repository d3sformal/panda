package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.vm.JenkinsStateSet;
import gov.nasa.jpf.vm.SerializingStateSet;
import gov.nasa.jpf.vm.StateSerializer;
import gov.nasa.jpf.vm.VM;

public class ResetableStateSet extends SerializingStateSet {
    private JenkinsStateSet set;
    private VM vm;
    private int startingSize;

    public ResetableStateSet() {
        clear(0);
    }

    @Override
    public void attach(VM vm) {
        super.attach(vm);

        this.vm = vm;
        set.attach(vm);
    }

    @Override
    public int addCurrent() {
        return add(serializer.getStoringData());
    }

    @Override
    protected int add(int[] state) {
        return set.add(state) + startingSize;
    }

    public boolean isCurrentUnique() {
        int[] data = serializer.getStoringData();

        long hash = JenkinsStateSet.longLookup3Hash(data);
        int id = set.lookup(hash);

        return id == -1;
    }

    @Override
    public int size() {
        return set.size() + startingSize;
    }

    public void clear(int startingSize) {
        this.startingSize = startingSize;

        set = VM.getVM().getJPF().getConfig().getInstance("panda.storage.class", JenkinsStateSet.class);

        if (vm != null) {
            set.attach(vm);
        }
    }
}
