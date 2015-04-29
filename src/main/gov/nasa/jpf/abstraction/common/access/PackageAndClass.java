package gov.nasa.jpf.abstraction.common.access;

/**
 * A grammar element used to specify a target of a [object ...] context
 *
 * @see gov.nasa.jpf.abstraction.common.ObjectContext
 */
public interface PackageAndClass extends Root {
    @Override
    public PackageAndClass createShallowCopy();

    public boolean contains(PackageAndClass pc);
}
