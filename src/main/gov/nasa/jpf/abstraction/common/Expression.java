package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;

import java.util.ArrayList;
import java.util.List;

public abstract class Expression implements PredicatesVisitable, Cloneable {
	protected List<AccessExpression> accessExpressions = new ArrayList<AccessExpression>();
	
	public abstract List<AccessExpression> getAccessExpressions();
	public abstract Expression replace(AccessExpression formerPath, Expression expression);
	
    public String toString() {
    	return toString(AccessExpression.policy);
    }
    public String toString(AccessExpression.NotationPolicy policy) {
    	PredicatesStringifier stringifier = AccessExpression.getStringifier(policy);
		
		accept(stringifier);
		
		return stringifier.getString();
	}
    
    @Override
    public abstract Expression clone();
}