package gov.nasa.jpf.abstraction.predicate.state.symbols;

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
