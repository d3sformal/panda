package gov.nasa.jpf.abstraction.predicate.util;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.numeric.ContainerAbstraction;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ReturnInstruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class PredicateValuationMonitor extends ListenerAdapter {
	private boolean running = false;
	
	@Override
	public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {
		if (execInsn instanceof InvokeInstruction) {			
			if (nextInsn.getMethodInfo().getName().equals("main")) {
				running = true;
			}
		}
		if (execInsn instanceof ReturnInstruction && execInsn.getMethodInfo().getName().equals("main")) {
			running = false;
		}
		
		if (running) {
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
			System.out.println("--PREDICATES--");
			System.out.print(predicate.getPredicateValuation().toString());
			System.out.println("--------------");
		}		
	}
}