package gov.nasa.jpf.abstraction.common.access;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.PredicatesFactory;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultFresh;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.smt.PredicatesSMTStringifier;

public class AccessExpressionTest {

    @Test
    public void testAccessExpressions() {
        Notation.policy = Notation.DOT_NOTATION;

        AccessExpression p = PredicatesFactory.createAccessExpressionFromString("a[a.length - 1]");

        AccessExpression a = PredicatesFactory.createAccessExpressionFromString("a");
        AccessExpression c = PredicatesFactory.createAccessExpressionFromString("c");

        Expression e = p.replace(a, c);

        AccessExpression x = PredicatesFactory.createAccessExpressionFromString("x.x");
        AccessExpression y = PredicatesFactory.createAccessExpressionFromString("x");
        AccessExpression z = PredicatesFactory.createAccessExpressionFromString("y");
        x = x.reRoot(y, z);

        AccessExpression e1;
        AccessExpression e2;

        e1 = DefaultObjectFieldRead.create(DefaultArrayElementRead.create(DefaultObjectFieldRead.create(DefaultRoot.create("this"), DefaultObjectFieldWrite.create(DefaultRoot.create("this"), "rectangles", DefaultFresh.create())), Constant.create(0)), "bottom");
        e2 = DefaultObjectFieldRead.create(DefaultArrayElementRead.create(DefaultObjectFieldRead.create(DefaultRoot.create("this"), DefaultObjectFieldWrite.create(DefaultRoot.create("this"), "rectangles", DefaultFresh.create())), Constant.create(0)), "bottom");

        e1 = DefaultArrayElementRead.create(DefaultObjectFieldRead.create(DefaultRoot.create("this"), DefaultObjectFieldWrite.create(DefaultRoot.create("this"), "rectangles", DefaultFresh.create())), Constant.create(0));
        e2 = DefaultArrayElementRead.create(DefaultObjectFieldRead.create(DefaultRoot.create("this"), DefaultObjectFieldWrite.create(DefaultRoot.create("this"), "rectangles", DefaultFresh.create())), Constant.create(0));

        e1 = DefaultObjectFieldRead.create(DefaultRoot.create("this"), DefaultObjectFieldWrite.create(DefaultRoot.create("this"), "rectangles", DefaultFresh.create()));
        e2 = DefaultObjectFieldRead.create(DefaultRoot.create("this"), DefaultObjectFieldWrite.create(DefaultRoot.create("this"), "rectangles", DefaultFresh.create()));

        assertFalse(e1.equals(e2));
    }

}
