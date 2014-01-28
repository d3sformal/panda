package gov.nasa.jpf.abstraction.common.access;

/**
 * A special value representing a completely new object
 */
public interface Fresh extends Root {
    @Override
    public Fresh createShallowCopy();
}
