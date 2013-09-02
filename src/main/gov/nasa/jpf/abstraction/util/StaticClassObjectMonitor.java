package gov.nasa.jpf.abstraction.util;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.ClassLoaderList;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class StaticClassObjectMonitor extends ListenerAdapter {
	@Override
	public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {		
		ClassLoaderList clis = vm.getClassLoaderList();
		
		String head = ">>> " + execInsn.getMnemonic() + " <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<";
		String foot = head.replaceAll(".", "=");
		
		System.out.println(head);
		for (ClassLoaderInfo cli : clis) {			
			for (ElementInfo ei : cli.getStatics()) {
				if (ei.getClassInfo().isInitialized()) {
					StaticClassObjectTracker.dumpElementInfo(curTh, ei);
				}
			}
		}
		
		System.out.println(foot);
	}
}
