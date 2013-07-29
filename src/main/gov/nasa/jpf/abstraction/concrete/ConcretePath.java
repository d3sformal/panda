package gov.nasa.jpf.abstraction.concrete;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.AccessPathElement;
import gov.nasa.jpf.abstraction.common.AccessPathRootElement;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.impl.DefaultConcretePathIndexElement;
import gov.nasa.jpf.abstraction.concrete.impl.DefaultConcretePathRootElement;
import gov.nasa.jpf.abstraction.concrete.impl.DefaultConcretePathSubElement;
import gov.nasa.jpf.abstraction.concrete.impl.PathResolution;
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
		
		initialise(name);
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
		if (index == null) {
			index = EmptyExpression.create();
		}
		
		appendElement(new DefaultConcretePathIndexElement(index));
	}
	
	public Map<AccessPath, CompleteVariableID> resolve() {
		ConcretePathElement element = (ConcretePathElement) tail;
		
		Map<AccessPath, VariableID> vars = element.getVariableIDs(ti).current;
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
	
	public Map<AccessPath, VariableID> partialResolve() {
		ConcretePathElement element = (ConcretePathElement) tail;
		
		PathResolution resolution = element.getVariableIDs(ti);
		
		resolution.processed.putAll(resolution.current);
		
		return resolution.processed;
	}
	
	@Override
	public ConcretePath clone() {
		ConcretePath path = new ConcretePath();
		
		path.rootObject = rootObject;
		path.type = type;
		path.ti = ti;
		
		path.root = root.clone();
		path.tail = path.root;
		
		AccessPathElement next = path.root;
		
		while (next != null) {
			path.tail = next;
			next = next.getNext();
		}
		
		return path;
	}

}
