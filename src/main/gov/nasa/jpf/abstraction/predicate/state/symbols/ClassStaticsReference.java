package gov.nasa.jpf.abstraction.predicate.state.symbols;

public class ClassStaticsReference implements UniverseIdentifier {
	private String className;
	
	public ClassStaticsReference(String className) {
		this.className = className;
	}
	
	public String getClassName() {
		return className;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ClassStaticsReference) {
			ClassStaticsReference r = (ClassStaticsReference) o;
			
			return className.equals(r.className);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return className.hashCode();
	}
	
	@Override
	public String toString() {
		return className;
	}
}
