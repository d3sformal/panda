package gov.nasa.jpf.abstraction.util;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ClassInfo;

public class ClassInitializedMonitor extends ListenerAdapter {
    @Override
    public void classLoaded(VM vm, ClassInfo classInfo) {
        System.out.println("Class loaded: " + classInfo.getName());
    }
}