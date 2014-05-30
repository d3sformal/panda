package gov.nasa.jpf.abstraction.common.access;

/**
 * Expressions fread(f, o) ~ o.f
 */
public interface ObjectFieldRead extends ObjectFieldExpression {
    @Override
    public ObjectFieldRead createShallowCopy();
}
