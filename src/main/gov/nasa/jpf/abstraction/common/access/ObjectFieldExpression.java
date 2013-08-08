package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.access.meta.Field;

public interface ObjectFieldExpression extends ObjectAccessExpression {
	public Field getField();
}
