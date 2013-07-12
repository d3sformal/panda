package gov.nasa.jpf.abstraction.predicate.util;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.abstraction.AbstractInstructionFactory;
import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.numeric.ContainerAbstraction;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.SymbolTable;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class SymbolTableMonitor extends ListenerAdapter {
	@Override
	public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {
		inspect(AbstractInstructionFactory.abs);
	}

	private void inspect(Abstraction abs) {
		if (abs instanceof ContainerAbstraction) {
			ContainerAbstraction container = (ContainerAbstraction) abs;
			
			for (Abstraction subAbs : container.getAbstractionsList()) {
				inspect(subAbs);
			}
		} else if (abs instanceof PredicateAbstraction) {
			PredicateAbstraction predicate = (PredicateAbstraction) abs;
			System.out.println("--SYMBOLS--");
			System.out.print(predicate.getSymbolTable().toString());
			System.out.println("--------------");
		}		
	}
}
