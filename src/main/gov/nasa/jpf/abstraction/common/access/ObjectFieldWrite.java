package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.UpdateExpression;
import gov.nasa.jpf.abstraction.common.access.meta.Field;

public interface ObjectFieldWrite extends ObjectFieldExpression, UpdateExpression, Field {
	@Override
	public ObjectFieldWrite clone();
}
