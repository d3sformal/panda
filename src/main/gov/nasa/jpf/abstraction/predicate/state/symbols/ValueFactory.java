package gov.nasa.jpf.abstraction.predicate.state.symbols;

public class ValueFactory {
	private Universe universe;
	
	public ValueFactory(Universe universe) {
		this.universe = universe;
	}
	
	public HeapObject createObject(Integer reference) {
		if (!universe.contains(reference)) {
			universe.add(new HeapObject(reference));
		}
		
		return (HeapObject) universe.get(reference);
	}
	
	public HeapArray createArray(Integer reference, Integer length) {
		if (!universe.contains(reference)) {
			universe.add(new HeapArray(reference, length));
		}
		
		return (HeapArray) universe.get(reference);
	}

	public ClassStatics createClass(String className) {
		if (!universe.contains(className)) {
			universe.add(new ClassStatics(className));
		}
		
		return (ClassStatics) universe.get(className);
	}
}
