package gov.nasa.jpf.abstraction.predicate;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class PredicateValuationMonitor extends ListenerAdapter {
	@Override
	public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {
		for (PredicateAbstraction abs : PredicateAbstraction.getInstances()) {
			System.out.println(abs.getPredicateValuation().toString());
		}
	}
}