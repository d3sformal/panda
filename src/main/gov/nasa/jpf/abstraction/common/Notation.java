package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.impl.PredicatesDotStringifier;
import gov.nasa.jpf.abstraction.common.impl.PredicatesFunctionStringifier;

public enum Notation {
	DOT_NOTATION,
	FUNCTION_NOTATION;
	
	public static Notation policy = Notation.FUNCTION_NOTATION;

	public static PredicatesStringifier getStringifier(Notation policy) {
		switch (policy) {
		case DOT_NOTATION:
			return new PredicatesDotStringifier();
		case FUNCTION_NOTATION:
			return new PredicatesFunctionStringifier();
		}
		
		return null;
	}
	
	public static PredicatesStringifier getDefaultStringifier() {
		return getStringifier(policy);
	}
	
	public static String convertToString(PredicatesVisitable visitable) {
		return convertToString(visitable, policy);
	}
	
	public static String convertToString(PredicatesVisitable visitable, Notation policy) {
		return convertToString(visitable, getStringifier(policy));
	}
	
	public static String convertToString(PredicatesVisitable visitable, PredicatesStringifier stringifier) {		
		visitable.accept(stringifier);
		
		return stringifier.getString();
	}
}
