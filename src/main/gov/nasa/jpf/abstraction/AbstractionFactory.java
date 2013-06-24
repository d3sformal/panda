package gov.nasa.jpf.abstraction;

import java.util.List;

import gov.nasa.jpf.abstraction.numeric.Abstraction;

public abstract class AbstractionFactory {
	public abstract void tryAppendNew(List<Abstraction> abs_list, String[] args);
}
