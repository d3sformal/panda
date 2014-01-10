package gov.nasa.jpf.abstraction.common.access;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.PredicatesFactory;
import gov.nasa.jpf.abstraction.predicate.smt.PredicatesSMTStringifier;

import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultFresh;

public class AccessExpressionTest {

	public static void main(String[] args) {
		Notation.policy = Notation.DOT_NOTATION;
		
		AccessExpression p = PredicatesFactory.createAccessExpressionFromString("a[a.length - 1]");
		
		AccessExpression a = PredicatesFactory.createAccessExpressionFromString("a");
		AccessExpression c = PredicatesFactory.createAccessExpressionFromString("c");
		
		Expression e = p.replace(a, c);
		
		System.out.println(e /*+ " " + e.getPaths()*/);
		
		AccessExpression x = PredicatesFactory.createAccessExpressionFromString("x.x");		
		AccessExpression y = PredicatesFactory.createAccessExpressionFromString("x");
		AccessExpression z = PredicatesFactory.createAccessExpressionFromString("y");
		x = x.reRoot(y, z);
		
		System.out.println(x + " " + x.getClass().getSimpleName());
		
		AccessExpression e1;
		AccessExpression e2;
		
		e1 = DefaultObjectFieldRead.create(DefaultArrayElementRead.create(DefaultObjectFieldRead.create(DefaultRoot.create("this"), DefaultObjectFieldWrite.create(DefaultRoot.create("this"), "rectangles", DefaultFresh.create())), Constant.create(0)), "bottom");
		e2 = DefaultObjectFieldRead.create(DefaultArrayElementRead.create(DefaultObjectFieldRead.create(DefaultRoot.create("this"), DefaultObjectFieldWrite.create(DefaultRoot.create("this"), "rectangles", DefaultFresh.create())), Constant.create(0)), "bottom");
		
		e1 = DefaultArrayElementRead.create(DefaultObjectFieldRead.create(DefaultRoot.create("this"), DefaultObjectFieldWrite.create(DefaultRoot.create("this"), "rectangles", DefaultFresh.create())), Constant.create(0));
		e2 = DefaultArrayElementRead.create(DefaultObjectFieldRead.create(DefaultRoot.create("this"), DefaultObjectFieldWrite.create(DefaultRoot.create("this"), "rectangles", DefaultFresh.create())), Constant.create(0));
		
		e1 = DefaultObjectFieldRead.create(DefaultRoot.create("this"), DefaultObjectFieldWrite.create(DefaultRoot.create("this"), "rectangles", DefaultFresh.create()));
		e2 = DefaultObjectFieldRead.create(DefaultRoot.create("this"), DefaultObjectFieldWrite.create(DefaultRoot.create("this"), "rectangles", DefaultFresh.create()));
		
		System.out.println(Notation.convertToString(e1, new PredicatesSMTStringifier()));
		System.out.println(Notation.convertToString(e2, new PredicatesSMTStringifier()));
		System.out.println(e1.equals(e2) + " " + e1.hashCode() + " " + e2.hashCode());
	}

}
