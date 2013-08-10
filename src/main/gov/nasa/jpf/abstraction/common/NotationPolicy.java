package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.impl.PredicatesDotStringifier;
import gov.nasa.jpf.abstraction.common.impl.PredicatesFunctionStringifier;

public enum NotationPolicy {
	DOT_NOTATION,
	FUNCTION_NOTATION;
	
	public static NotationPolicy policy = NotationPolicy.FUNCTION_NOTATION;

	public static PredicatesStringifier getStringifier(NotationPolicy policy) {
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
	
	public static String convertToString(PredicatesVisitable visitable, NotationPolicy policy) {
		PredicatesStringifier stringifier = getStringifier(policy);
		
		visitable.accept(stringifier);
		
		return stringifier.getString();
	}
}
