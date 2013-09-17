package gov.nasa.jpf.abstraction.common.access;

public interface ReturnValue extends Root {
	public boolean isReference();

	@Override
	public ReturnValue clone();
}
