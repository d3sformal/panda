package gov.nasa.jpf.abstraction.common.access;

/**
 * Read of an array element aread(arr, a, i) ~ a[i]
 */
public interface ArrayElementRead extends ArrayElementExpression {
    @Override
    public ArrayElementRead createShallowCopy();
}
