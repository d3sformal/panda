package gov.nasa.jpf.abstraction.predicate.state.symbols;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;

public class LocalVariable extends Value {
	private Root localVariable;
	
	public LocalVariable(Root localVariable) {
		this.localVariable = localVariable;
	}
	
	public AccessExpression getAccessExpression() {
		return localVariable;
	}
	
	@Override
	public String toString() {
		return localVariable.getName();
	}
}
