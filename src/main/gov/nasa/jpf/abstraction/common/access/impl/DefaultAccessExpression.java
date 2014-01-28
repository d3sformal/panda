package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.HashMap;
import java.util.Set;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectAccessExpression;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;
import gov.nasa.jpf.abstraction.common.access.ReturnValue;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.impl.DefaultObjectExpression;
import gov.nasa.jpf.abstraction.predicate.smt.PredicatesSMTStringifier;

/**
 * Implements common behaviour of most of the access expression elements
 */
public abstract class DefaultAccessExpression extends DefaultObjectExpression implements AccessExpression {

	/**
	 * Retrieves all access expressions present in this expression
	 * 
	 * that is:
	 * 1) this expression itself
	 * 2) any access expression present in an array index
	 * 3) any access expression present in an update expression (fwrite, awrite, alengthupdate ...)
	 */
	@Override
	public final void addAccessExpressionsToSet(Set<AccessExpression> out) {
		addAccessSubExpressionsToSet(out);
		out.add(this);
	}

	@Override
	public final void addAllPrefixesToSet(Set<AccessExpression> out) {
		AccessExpression prefix = this;

		while (!(prefix instanceof Root)) {
			out.add(prefix);
			prefix = prefix.cutTail();
		}

		out.add(prefix);
	}

	/**
	 * Replaces occurances of access expressions by other expressions 
	 */
	@Override
	public final Expression replace(Map<AccessExpression, Expression> replacements) {
		for (AccessExpression expression : replacements.keySet()) {
			Expression newExpression = replacements.get(expression);

			if (equals(expression)) {
				return newExpression;
			}
		}
		
		AccessExpression path = this;
		
		for (AccessExpression expression : replacements.keySet()) {
			Expression newExpression = replacements.get(expression);

			if (expression.isPrefixOf(path) && newExpression instanceof AccessExpression) {
				AccessExpression newPrefix = (AccessExpression) newExpression;

				path = path.reRoot(expression, newPrefix);

				break; // Do not chain the replacements
			}
		}
		
		return path.replaceSubExpressions(replacements);
	}
	
	/**
	 * Transforms the access expression in such a way that it reflects all updates (fwrite, awrite...) in affected subexpressions (fread, aread).
	 * 
	 * update(fread(f, o), o.f, 3) = fread(fwrite(f, o, 3), o)
	 * update(fread(f, o), p.f, 3) = fread(fwrite(f, p, 3), o)
	 * ...
	 */
	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
		return this;
	}

    @Override
    public abstract DefaultAccessExpression createShallowCopy();

	/**
	 * Is this expression referring (entirely) to the java keyword this
	 */
	@Override
	public final boolean isThis() {
		if (this instanceof Root) {
			Root r = (Root) this;
			
			return r.getName().equals("this");
		}
		
		return false;
	}
	
	/**
	 * Is the expression accessing a variable through a static context
	 */
	@Override
	public final boolean isStatic() {
		return getRoot() instanceof PackageAndClass;
	}
	
	/**
	 * Is the expression accessing a local variable
	 */
	@Override
	public final boolean isLocalVariable() {
		return this instanceof Root && !isStatic() && !isReturnValue();
	}

	/**
	 * Is the expression referring to the keyword return
	 */
	@Override
	public final boolean isReturnValue() {
		return this instanceof ReturnValue;
	}
	
	/**
	 * Is this expression a prefix of the supplied expression
	 * 
	 * a.b.c is a prefix of a.b.c, a.b.c.d ...
	 * a.b.c is not a prefix of a.b, a.b.d ...
	 */
	@Override
	public boolean isPrefixOf(AccessExpression path) {
		if (getLength() > path.getLength()) {
			return false;
		}
		
		return equals(path.get(getLength()));
	}
	
	/**
	 * Is the expression a prefix (up to different array indices) of the supplied expression
	 * 
	 * a[i] is similar to a prefix of a[k], a[k].b, a[i][d]
	 * a[i] is not similar to a prefix of b[i], a.b
	 */
	@Override
	public boolean isSimilarToPrefixOf(AccessExpression path) {
		if (getLength() > path.getLength()) {
			return false;
		}
		
		return isSimilarTo(path.get(getLength()));
	}
	
	/**
	 * Is the expression a prefix of the supplied expression and is its length strictly lower
	 * 
	 * a.b is a proper prefix of a.b.c but not a proper prefix of a.b
	 */
	@Override
	public final boolean isProperPrefixOf(AccessExpression expression) {
		return getLength() < expression.getLength() && isPrefixOf(expression);
	}
	
	/**
	 * Changes the prefix of this expression from oldPrefix to newPrefix
	 * 
	 * reRoot(a.b.c.d, a.b, x.y) = x.y.c.d
	 */
	@Override
	public final AccessExpression reRoot(AccessExpression oldPrefix, AccessExpression newPrefix) {
		if (oldPrefix.isPrefixOf(this)) {
            AccessExpression oldPrefixTail = get(oldPrefix.getLength());
            AccessExpression result = newPrefix;
			AccessExpression parent = this;
            ObjectAccessExpression parentCopy = null;
            ObjectAccessExpression previousParentCopy = null;
			
            while (parent != oldPrefixTail && parent instanceof ObjectAccessExpression) {
                parentCopy = (ObjectAccessExpression) parent.createShallowCopy();

                if (result == newPrefix) {
                    result = parentCopy;
                }

                if (previousParentCopy != null) {
                    previousParentCopy.setObject(parentCopy);
                }

                previousParentCopy = parentCopy;
                parent = ((ObjectAccessExpression)parent).getObject();
            }

            return result;
		}
		
		return null;
	}
	
}
