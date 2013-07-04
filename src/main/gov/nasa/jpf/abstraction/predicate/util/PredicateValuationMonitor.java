package gov.nasa.jpf.abstraction.predicate.util;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.PredicateValuation;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class PredicateValuationMonitor extends ListenerAdapter {
	@Override
	public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {
		for (PredicateValuation val : PredicateAbstraction.getPredicateValuations()) {
			System.out.println("--PREDICATES--");
			System.out.print(val.toString());
		}
		System.out.println("--------------");
	}
}