package gov.nasa.jpf.abstraction.common;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

public class PredicateTest {
    /**
     * Test of basic operations
     */

    @Test
    public void testPredicates() {
        Notation.policy = Notation.DOT_NOTATION;

        Predicate p = PredicatesFactory.createPredicateFromString("a + b = 2");

        AccessExpression a = PredicatesFactory.createAccessExpressionFromString("a");
        Expression aplusb = PredicatesFactory.createExpressionFromString("a + b");
        AccessExpression b = PredicatesFactory.createAccessExpressionFromString("b");
        AccessExpression c = PredicatesFactory.createAccessExpressionFromString("c");

        p = p.replace(a, aplusb);
        p = p.replace(b, c);

        Predicate q = PredicatesFactory.createPredicateFromString("(a + c) + c = 2");

        assertEquals(p, q);
    }
}
