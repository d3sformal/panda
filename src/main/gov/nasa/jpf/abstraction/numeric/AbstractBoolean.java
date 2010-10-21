package gov.nasa.jpf.abstraction.numeric;

public class AbstractBoolean {
	public static AbstractBoolean TRUE = new AbstractBoolean();
	public static AbstractBoolean FALSE = new AbstractBoolean();
	public static AbstractBoolean TOP = new AbstractBoolean();

	public AbstractBoolean () {
	}

	AbstractBoolean abstract_map(boolean v) {
		return (v ? AbstractBoolean.TRUE : AbstractBoolean.FALSE);
	}
}
