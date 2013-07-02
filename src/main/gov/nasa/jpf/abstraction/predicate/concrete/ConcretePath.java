package gov.nasa.jpf.abstraction.predicate.concrete;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.abstraction.predicate.concrete.impl.DefaultConcretePathIndexElement;
import gov.nasa.jpf.abstraction.predicate.concrete.impl.DefaultConcretePathRootElement;
import gov.nasa.jpf.abstraction.predicate.concrete.impl.DefaultConcretePathSubElement;
import gov.nasa.jpf.abstraction.predicate.grammar.AccessPath;
import gov.nasa.jpf.abstraction.predicate.grammar.AccessPathElement;
import gov.nasa.jpf.abstraction.predicate.grammar.AccessPathRootElement;
import gov.nasa.jpf.abstraction.predicate.grammar.Expression;
import gov.nasa.jpf.vm.ThreadInfo;

public class ConcretePath extends AccessPath {
	
	public enum Type {
		LOCAL,
		HEAP,
		STATIC
	}
	
	public Type type;
	public Object rootObject;
	public ThreadInfo ti;
	
	protected ConcretePath() {
	}

	public ConcretePath(String name, ThreadInfo ti, Object rootObject, Type type) {
		this.rootObject = rootObject;
		this.type = type;
		this.ti = ti;
		
		root = createRootElement(name);
		tail = root;
	}
	
	@Override
	public ConcretePathRootElement getRoot() {
		return (ConcretePathRootElement) super.getRoot();
	}
	
	@Override
	public ConcretePathElement getTail() {
		return (ConcretePathElement) super.getTail();
	}
	
	@Override
	protected AccessPathRootElement createRootElement(String name) {
		return new DefaultConcretePathRootElement(name, rootObject, type);
	}
	
	@Override
	public void appendSubElement(String name) {
		appendElement(new DefaultConcretePathSubElement(name));
	}
	
	@Override
	public void appendIndexElement(Expression index) {
		appendElement(new DefaultConcretePathIndexElement());
	}
	
	public Map<AccessPath, CompleteVariableID> resolve() {
		ConcretePathElement element = (ConcretePathElement) tail;
		
		Map<AccessPath, VariableID> vars = element.getVariableID(ti);
		Map<AccessPath, CompleteVariableID> ret = new HashMap<AccessPath, CompleteVariableID>();
		
		for (AccessPath path : vars.keySet()) {
			VariableID var = vars.get(path);

			if (var instanceof CompleteVariableID) {
				CompleteVariableID completeVar = (CompleteVariableID) var;

				ret.put(path, completeVar);
			}
		}
		
		return ret;
	}
	
	@Override
	public Object clone() {
		ConcretePath path = new ConcretePath();
		
		path.rootObject = rootObject;
		path.type = type;
		path.ti = ti;
		
		path.root = (AccessPathRootElement) root.clone();
		path.tail = path.root;
		
		AccessPathElement next = path.root;
		
		while (next != null) {
			path.tail = next;
			next = next.getNext();
		}
		
		return path;
	}

}
