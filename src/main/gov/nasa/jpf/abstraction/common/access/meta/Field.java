package gov.nasa.jpf.abstraction.common.access.meta;

import gov.nasa.jpf.abstraction.common.PredicatesVisitable;

public interface Field extends PredicatesVisitable, Cloneable {
	public String getName();
	public Field clone();
}
