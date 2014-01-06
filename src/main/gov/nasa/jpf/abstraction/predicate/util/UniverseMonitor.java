package gov.nasa.jpf.abstraction.predicate.util;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.numeric.ContainerAbstraction;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.symbols.ClassStatics;
import gov.nasa.jpf.abstraction.predicate.state.symbols.ClassStaticsReference;
import gov.nasa.jpf.abstraction.predicate.state.symbols.HeapObject;
import gov.nasa.jpf.abstraction.predicate.state.symbols.HeapObjectReference;
import gov.nasa.jpf.abstraction.predicate.state.symbols.StructuredArray;
import gov.nasa.jpf.abstraction.predicate.state.symbols.StructuredObject;
import gov.nasa.jpf.abstraction.predicate.state.symbols.StructuredValue;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Universe;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

/**
 * Prints the current abstract heap after each instruction in the target program
 */
public class UniverseMonitor extends ListenerAdapter {
	
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
			Universe universe = predicate.getSymbolTable().getUniverse();
			System.out.println("--Universe (" + universe.getID() + ")--");
			
			Set<StructuredValue> ordered = new TreeSet<StructuredValue>(new Comparator<StructuredValue>() {
				@Override
				public int compare(StructuredValue o1, StructuredValue o2) {
					if (o1 instanceof ClassStatics && o2 instanceof ClassStatics) {
						ClassStaticsReference r1 = (ClassStaticsReference) o1.getReference();
						ClassStaticsReference r2 = (ClassStaticsReference) o2.getReference();
						
						return r1.getClassName().compareTo(r2.getClassName());
					}
					
					if (o1 instanceof ClassStatics) return -1;
					if (o2 instanceof ClassStatics) return +1;
					
					HeapObjectReference r1 = (HeapObjectReference) o1.getReference();
					HeapObjectReference r2 = (HeapObjectReference) o2.getReference();
					
					return r1.getReference().compareTo(r2.getReference());
				}
			});
			
			ordered.addAll(universe.getStructuredValues());
			
			for (StructuredValue v : ordered) {
				if (v instanceof StructuredObject) {
					StructuredObject so = (StructuredObject) v;
					
					System.out.print(so + "::");
					
					for (String f : so.getFields().keySet()) {
						System.out.print(" " + f + ": " + so.getField(f).getPossibleValues());
					}
					
					System.out.println();
				}
				
				if (v instanceof StructuredArray) {
					StructuredArray sa = (StructuredArray) v;
					
					System.out.print(sa + "::");
					
					for (Integer i : sa.getElements().keySet()) {
						System.out.print(" " + i + ": " + sa.getElement(i).getPossibleValues());
					}
					
					System.out.println();
				}
			}

			System.out.println("--------------");
			System.out.flush();
		}		
	}
}
