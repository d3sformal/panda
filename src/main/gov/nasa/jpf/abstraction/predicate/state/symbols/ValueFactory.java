package gov.nasa.jpf.abstraction.predicate.state.symbols;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.StaticElementInfo;

/**
 * A factory which ensures that no object is create twice in the universe and that the most recent view of it is always returned
 * 
 * if obj1 does not exist:
 *   return new obj1
 *   
 * if obj1 exists and has its fields defined (obj1.f = {obj2, obj3})
 *   return obj1 in its current state
 */
public class ValueFactory {
	private Universe universe;
	
	public ValueFactory(Universe universe) {
		this.universe = universe;
	}
	
	public HeapObject createObject(Integer reference, ElementInfo elementInfo) {
		if (!universe.contains(reference)) {
			universe.add(new HeapObject(universe, reference, elementInfo));
		}
		
		return (HeapObject) universe.get(reference);
	}
	
	public HeapArray createArray(Integer reference, ElementInfo elementInfo) {
		if (!universe.contains(reference)) {
			universe.add(new HeapArray(universe, reference, elementInfo));
		}
		
		return (HeapArray) universe.get(reference);
	}

	public ClassStatics createClass(StaticElementInfo elementInfo) {
		if (!universe.contains(elementInfo.getClassInfo().getName())) {
			universe.add(new ClassStatics(universe, elementInfo));
		}
		
		return (ClassStatics) universe.get(elementInfo.getClassInfo().getName());
	}

	public Null createNull() {
		if (!universe.contains(Universe.NULL)) {
			universe.add(new Null(universe));
		}
		
		return (Null) universe.get(Universe.NULL);
	}
}
