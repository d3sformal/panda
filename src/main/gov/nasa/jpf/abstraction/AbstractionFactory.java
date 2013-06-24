package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.abstraction.numeric.Abstraction;

public abstract class AbstractionFactory {
	public abstract Abstraction create(String[] args);
}
