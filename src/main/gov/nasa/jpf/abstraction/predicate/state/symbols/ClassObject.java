package gov.nasa.jpf.abstraction.predicate.state.symbols;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;

public class ClassObject extends Value {
	private PackageAndClass classObject;
	
	public ClassObject(PackageAndClass classObject) {
		this.classObject = classObject;
	}
	
	public AccessExpression getAccessExpression() {
		return classObject;
	}
	
	@Override
	public String toString() {
		return classObject.getName();
	}
}
