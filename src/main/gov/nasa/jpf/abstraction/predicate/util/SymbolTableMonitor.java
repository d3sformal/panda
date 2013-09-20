package gov.nasa.jpf.abstraction.predicate.util;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.numeric.ContainerAbstraction;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

/**
 * Prints the current symbol table after each instruction in the target program
 */
public class SymbolTableMonitor extends ListenerAdapter {
	
	@Override
	public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {		
		if (RunDetector.isRunning()) {
			inspect(GlobalAbstraction.getInstance().get());
		}
	}

	private void inspect(Abstraction abs) {
		if (abs instanceof ContainerAbstraction) {
			ContainerAbstraction container = (ContainerAbstraction) abs;
			
			for (Abstraction subAbs : container.getAbstractionsList()) {
				inspect(subAbs);
			}
		} else if (abs instanceof PredicateAbstraction) {
			PredicateAbstraction predicate = (PredicateAbstraction) abs;
			String table = predicate.getSymbolTable().toString();
			System.out.println(
				"--SYMBOLS " + predicate.getSymbolTable().count() + " --\n" +
				table +
				"--------------"
			);
			System.out.flush();
		}		
	}
}
