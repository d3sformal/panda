package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

public class PredicateTest {
	/**
	 * Test of basic operations
	 */
	public static void main(String[] args) {
		Notation.policy = Notation.DOT_NOTATION;

		Predicate p = PredicatesFactory.createPredicateFromString("a + b = 2");

		AccessExpression a = PredicatesFactory.createAccessExpressionFromString("a");
		Expression aplusb = PredicatesFactory.createExpressionFromString("a + b");
		AccessExpression b = PredicatesFactory.createAccessExpressionFromString("b");
		AccessExpression c = PredicatesFactory.createAccessExpressionFromString("c");

        p = p.replace(a, aplusb);
        p = p.replace(b, c);

		System.out.println(p);
	}
}
