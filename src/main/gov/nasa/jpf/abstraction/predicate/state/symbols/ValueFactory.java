package gov.nasa.jpf.abstraction.predicate.state.symbols;

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
	
	public HeapObject createObject(Integer reference) {
		if (!universe.contains(reference)) {
			universe.add(new HeapObject(universe, reference));
		}
		
		return (HeapObject) universe.get(reference);
	}
	
	public HeapArray createArray(Integer reference, Integer length) {
		if (!universe.contains(reference)) {
			universe.add(new HeapArray(universe, reference, length));
		}
		
		return (HeapArray) universe.get(reference);
	}

	public ClassStatics createClass(String className) {
		if (!universe.contains(className)) {
			universe.add(new ClassStatics(universe, className));
		}
		
		return (ClassStatics) universe.get(className);
	}

	public Null createNull() {
		if (!universe.contains(Universe.NULL)) {
			universe.add(new Null(universe));
		}
		
		return (Null) universe.get(Universe.NULL);
	}
}
