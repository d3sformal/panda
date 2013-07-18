package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

import java.util.LinkedList;
import java.util.List;

public class UpdatedPredicate extends Predicate {
	
	public Predicate predicate;
	public AccessPath path;
	public Expression expression;
	
	protected UpdatedPredicate(Predicate predicate, AccessPath path, Expression expression) {
		this.predicate = predicate;
		this.path = path;
		this.expression = expression;
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public List<AccessPath> getPaths() {
		List<AccessPath> ret = new LinkedList<AccessPath>();
		
		ret.addAll(predicate.getPaths());
		ret.addAll(expression.getPaths());
		
		// ONLY variable paths (single element - varname) get replaced, other paths stay in the expressions
		if (path.getRoot() != path.getTail()) {
			ret.add(path);
		}
		
		return ret;
	}

	@Override
	public Predicate replace(AccessPath formerPath, Expression expression) {
		return create(predicate.replace(formerPath, expression), this.path, this.expression);
	}
	
	public static Predicate create(Predicate predicate, AccessPath path, Expression expression) {
		if (predicate == null) return null;
		if (path == null || expression == null) return null;
		
		return new UpdatedPredicate(predicate, path, expression);
	}

}
