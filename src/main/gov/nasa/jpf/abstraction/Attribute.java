package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.abstraction.predicate.concrete.ConcretePath;

public class Attribute {
	public AbstractValue abstractValue;
	public ConcretePath accessPath;
	
	public Attribute(AbstractValue value, ConcretePath path) {
		abstractValue = value;
		accessPath = path;
	}
}
