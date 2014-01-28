package gov.nasa.jpf.abstraction.common.access;

/**
 * A grammar element used to specify a target of a [object ...] context
 * 
 * @see gov.nasa.jpf.abstraction.common.ObjectContext
 */
public interface PackageAndClass extends Root {
	public String getName();
	
    @Override
    public PackageAndClass createShallowCopy();
}
