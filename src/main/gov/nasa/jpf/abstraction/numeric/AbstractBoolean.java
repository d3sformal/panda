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
	
	public AbstractBoolean not() {
		return create(this != TRUE, this != FALSE);
	}
	
	public AbstractBoolean and(AbstractBoolean right) {
		boolean t = (this != FALSE && right != FALSE);
		boolean f = (this != TRUE || right != TRUE);
		return create(t, f);
	}
	
	public AbstractBoolean or(AbstractBoolean right) {
		boolean t = (this != FALSE || right != FALSE);
		boolean f = (this != TRUE && right != TRUE);
		return create(t, f);
	}
	
	public AbstractBoolean xor(AbstractBoolean right) {
		boolean t = (this != FALSE && right != TRUE) || (this != TRUE && right != FALSE);
		boolean f = (this != FALSE && right != FALSE) || (this != TRUE && right != TRUE);
		return create(t, f);
	}	
	
	private AbstractBoolean create(boolean t, boolean f) {
		if (t)
			if (f)
				return TOP;
			else
				return TRUE;
		else if (f)
			return FALSE;
		throw new RuntimeException("### Error: AbstractBoolean out of range");
	}
	
}
