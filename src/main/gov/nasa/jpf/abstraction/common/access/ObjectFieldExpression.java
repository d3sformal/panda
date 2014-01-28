package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.access.meta.Field;

/**
 * Expressions accessing object fields: fread(f, o); fwrite(f, o, e);
 */
public interface ObjectFieldExpression extends ObjectAccessExpression {
	public Field getField();

    @Override
    public ObjectFieldExpression createShallowCopy();
}
