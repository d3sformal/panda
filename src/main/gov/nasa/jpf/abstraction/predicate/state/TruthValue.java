package gov.nasa.jpf.abstraction.predicate.state;

public enum TruthValue {
	UNDEFINED,
	TRUE,
	FALSE,
	UNKNOWN;	
	
	private static TruthValue create(int i) {
		switch (i) {
		case 0:
			return UNDEFINED;
		case 1:
			return TRUE;
		case 2:
			return FALSE;
		}
		
		return UNKNOWN;
	}
	
	public static TruthValue create(boolean isTrue, boolean isFalse) {
		if (isTrue && isFalse) {
			return UNKNOWN;
		}
		
		if (isTrue) return TRUE;
		if (isFalse) return FALSE;
		
		return UNDEFINED;
	}
	
	public static TruthValue create(boolean isTrue) {
		return create(isTrue, !isTrue);
	}
	
	public static TruthValue and(TruthValue a, TruthValue b) {
		return create(a.ordinal() & b.ordinal());
	}
	
	public static TruthValue or(TruthValue a, TruthValue b) {
		return create(a.ordinal() | b.ordinal());
	}
}
