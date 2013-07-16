package gov.nasa.jpf.abstraction.common.impl;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.AccessPathIndexElement;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

public class DefaultAccessPathIndexElement extends DefaultAccessPathMiddleElement implements AccessPathIndexElement {
	private Expression index;
	
	public DefaultAccessPathIndexElement(Expression index) {
		this.index = index;
	}

	@Override
	public Expression getIndex() {
		return index;
	}

    @Override
    public boolean equals(Object o) {
        if (o instanceof DefaultAccessPathIndexElement) {
            DefaultAccessPathIndexElement i = (DefaultAccessPathIndexElement) o;

            return index.equals(i.index);
        }

        return false;
    }
	
	@Override
	public DefaultAccessPathIndexElement clone() {
		DefaultAccessPathIndexElement clone = new DefaultAccessPathIndexElement(index);
		
		if (getNext() != null) {
			clone.setNext(getNext().clone());
			clone.getNext().setPrevious(clone);
		}
		
		return clone;
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public DefaultAccessPathIndexElement replace(AccessPath formerPath, Expression expression) {
		DefaultAccessPathIndexElement replaced = new DefaultAccessPathIndexElement(index.replace(formerPath, expression));
		
		if (getNext() != null) {
			replaced.setNext(getNext().replace(formerPath, expression));
			replaced.getNext().setPrevious(replaced);
		}
		
		return replaced;
	}

}
