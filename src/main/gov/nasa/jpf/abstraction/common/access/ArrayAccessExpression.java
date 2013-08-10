package gov.nasa.jpf.abstraction.common.access;

public interface ArrayAccessExpression extends ObjectAccessExpression {
	public AccessExpression getArray();
	
	@Override
	public ArrayAccessExpression clone();
}
