package gov.nasa.jpf.abstraction.common.access;

/**
 * A grammar element representing a keyword 'return' used in predicates over return values of methods
 */
public interface ReturnValue extends Root {
	public boolean isReference();
    public boolean isReturnFromCurrentScope();

    @Override
    public ReturnValue createShallowCopy();
}
