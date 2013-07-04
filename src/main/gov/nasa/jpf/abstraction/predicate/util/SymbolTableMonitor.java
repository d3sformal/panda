package gov.nasa.jpf.abstraction.predicate.util;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.SymbolTable;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class SymbolTableMonitor extends ListenerAdapter {
	@Override
	public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {
		for (SymbolTable tab : PredicateAbstraction.getSymbolTables()) {
			System.out.println("--SYMBOLS--");
			System.out.print(tab.toString());
		}
		System.out.println("-----------");
	}
}
