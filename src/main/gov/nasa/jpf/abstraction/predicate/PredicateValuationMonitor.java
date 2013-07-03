package gov.nasa.jpf.abstraction.predicate;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.abstraction.predicate.state.ScopedPredicateValuation;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class PredicateValuationMonitor extends ListenerAdapter {
	@Override
	public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {
		System.out.println(ScopedPredicateValuation.getInstance().toString());
	}
}