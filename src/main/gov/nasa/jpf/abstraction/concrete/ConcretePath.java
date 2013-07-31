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
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public class ConcretePath extends AccessPath {
	
	public enum Type {
		LOCAL,
		HEAP,
		STATIC
	}
	
	public Type type;
	public ElementInfo ei;
	public LocalVarInfo info;
	public ThreadInfo ti;
	
	protected ConcretePath() {
	}
	
	protected ConcretePath(String name, ThreadInfo ti, LocalVarInfo info, Type type) {
		this(name, ti, null, info, type);
	}
	
	protected ConcretePath(String name, ThreadInfo ti, ElementInfo ei, Type type) {
		this(name, ti, ei, null, type);
	}

	protected ConcretePath(String name, ThreadInfo ti, ElementInfo ei, LocalVarInfo info, Type type) {
		this.ei = ei;
		this.info = info;
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
		return new DefaultConcretePathRootElement(name, ei, info, type);
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
	
	public Map<AccessPath, VariableID> partialExhaustiveResolve() {
		ConcretePathElement element = (ConcretePathElement) tail;
		
		return element.getVariableIDs(ti).current;
	}
	
	@Override
	public ConcretePath clone() {
		ConcretePath path = new ConcretePath();
		
		path.ei = ei;
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
	
	public static ConcretePath createStaticFieldPath(String root, ThreadInfo ti, ElementInfo ei) {
		return new ConcretePath(root, ti, ei, Type.STATIC);
	}
	
	public static ConcretePath createLocalVarPath(String root, ThreadInfo ti, LocalVarInfo info) {
		return new ConcretePath(root, ti, info, Type.LOCAL);
	}
	
	public static ConcretePath createLocalVarRootedHeapObjectPath(String root, ThreadInfo ti, ElementInfo ei, LocalVarInfo info) {
		return new ConcretePath(root, ti, ei, info, Type.HEAP);
	}

}
