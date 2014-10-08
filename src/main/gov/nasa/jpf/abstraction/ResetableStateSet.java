package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.vm.JenkinsStateSet;
import gov.nasa.jpf.vm.StateSerializer;
import gov.nasa.jpf.vm.StateSet;
import gov.nasa.jpf.vm.VM;

public class ResetableStateSet implements StateSet {
    private JenkinsStateSet set;
    private VM vm;
    private int startingSize;
    private StateSerializer serializer;

    public ResetableStateSet() {
        clear(0);
    }

    @Override
    public void attach(VM vm) {
        this.vm = vm;
        this.serializer = vm.getSerializer();
        set.attach(vm);
    }

    @Override
    public int addCurrent() {
        return set.addCurrent() + startingSize;
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

        set = (JenkinsStateSet) VM.getVM().getJPF().getConfig().getInstance("panda.storage.class", StateSet.class);

        if (vm != null) {
            set.attach(vm);
        }
    }
}
