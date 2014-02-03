package gov.nasa.jpf.abstraction.predicate.util;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.numeric.ContainerAbstraction;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseClass;
import gov.nasa.jpf.abstraction.predicate.state.universe.ClassName;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseObject;
import gov.nasa.jpf.abstraction.predicate.state.universe.Reference;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseArray;
import gov.nasa.jpf.abstraction.predicate.state.universe.Universe;
import gov.nasa.jpf.abstraction.predicate.state.universe.StructuredValueIdentifier;
import gov.nasa.jpf.abstraction.predicate.state.universe.Associative;
import gov.nasa.jpf.abstraction.predicate.state.universe.Indexed;
import gov.nasa.jpf.abstraction.predicate.state.universe.FieldName;
import gov.nasa.jpf.abstraction.predicate.state.universe.ElementIndex;
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
			System.out.println("--Universe--");
			
			Set<StructuredValueIdentifier> ordered = new TreeSet<StructuredValueIdentifier>(new Comparator<StructuredValueIdentifier>() {
				@Override
				public int compare(StructuredValueIdentifier o1, StructuredValueIdentifier o2) {
					if (o1 instanceof ClassName && o2 instanceof ClassName) {
						ClassName r1 = (ClassName) o1;
						ClassName r2 = (ClassName) o2;
						
						return r1.getClassName().compareTo(r2.getClassName());
					}
					
					if (o1 instanceof ClassName) return -1;
					if (o2 instanceof ClassName) return +1;
					
					Reference r1 = (Reference) o1;
					Reference r2 = (Reference) o2;
					
					return r1.getReference().compareTo(r2.getReference());
				}
			});
			
			ordered.addAll(universe.getStructuredValues());
			
			for (StructuredValueIdentifier v : ordered) {
				if (universe.get(v) instanceof Associative) {
					Associative a = (Associative) v;
					
					System.out.print(a + "::");
					
					for (FieldName f : a.getFields().keySet()) {
						System.out.print(" " + f.getName() + ": " + a.getField(f).getPossibleValues());
					}
					
					System.out.println();
				}
				
				if (v instanceof Indexed) {
					Indexed i = (Indexed) v;
					
					System.out.print(i + "::");
					
					for (ElementIndex j : i.getElements().keySet()) {
						System.out.print(" " + j.getIndex() + ": " + i.getElement(j).getPossibleValues());
					}
					
					System.out.println();
				}
			}

			System.out.println("--------------");
			System.out.flush();
		}		
	}
}
