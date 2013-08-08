package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.NotationPolicy;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectAccessExpression;
import gov.nasa.jpf.abstraction.common.impl.DefaultObjectExpression;
import gov.nasa.jpf.abstraction.predicate.parser.PredicatesLexer;
import gov.nasa.jpf.abstraction.predicate.parser.PredicatesParser;

public abstract class DefaultAccessExpression extends DefaultObjectExpression implements AccessExpression {

	@Override
	public final List<AccessExpression> getAccessExpressions() {
		List<AccessExpression> ret = getSubAccessExpressions();
		
		ret.add(this);
		
		return ret;
	}

	@Override
	public final Expression replace(AccessExpression expression, Expression newExpression) {
		if (equals(expression)) {
			return newExpression;
		}
		
		AccessExpression path = this;
		
		if (expression.isPrefixOf(this) && newExpression instanceof AccessExpression) {
			AccessExpression newPrefix = ((AccessExpression)newExpression).clone();
			
			path = path.reRoot(expression, newPrefix);
		}
		
		return path.replaceSubExpressions(expression, newExpression);
	}
	
	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
		return expression.update(expression, newExpression);
	}
	
	@Override
	public abstract DefaultAccessExpression clone();

	@Override
	public final AccessExpression getTail() {
		return this;
	}
	
	@Override
	public boolean isPrefixOf(AccessExpression path) {
		if (getLength() > path.getLength()) {
			return false;
		}
		
		return equals(path.get(getLength()));
	}
	
	@Override
	public boolean isSimilarToPrefixOf(AccessExpression path) {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}
	
	@Override
	public final boolean isProperPrefixOf(AccessExpression expression) {
		return getLength() < expression.getLength() && isPrefixOf(expression);
	}
	
	public static DefaultAccessExpression createFromString(String definition) {
		ANTLRInputStream chars = new ANTLRInputStream(definition);
		PredicatesLexer lexer = new PredicatesLexer(chars);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PredicatesParser parser = new PredicatesParser(tokens);
		
		return parser.standalonepath().val;
	}
	
	@Override
	public final AccessExpression reRoot(AccessExpression oldPrefix, AccessExpression newPrefix) {
		if (oldPrefix.isPrefixOf(this)) {
			AccessExpression clone = clone();
			ObjectAccessExpression parent = (ObjectAccessExpression) clone.get(oldPrefix.getLength() + 1);
			
			if (parent == null) {
				return newPrefix;
			}
									
			parent.setObject(parent.getObject().reRoot(newPrefix));
			
			return clone;
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		NotationPolicy.policy = NotationPolicy.DOT_NOTATION;
		
		AccessExpression p = createFromString("a[a.length - 1]");
		
		AccessExpression a = createFromString("a");
		AccessExpression c = createFromString("c");
		
		Expression e = p.replace(a, c);
		
		System.out.println(e /*+ " " + e.getPaths()*/);
		
		AccessExpression x = createFromString("x.x");		
		AccessExpression y = createFromString("x");
		AccessExpression z = createFromString("y");
		x = x.reRoot(y, z);
		
		System.out.println(x + " " + x.getTail().getClass().getSimpleName());
	}

}
