package gov.nasa.jpf.abstraction.predicate.concrete;

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
	protected AccessPathRootElement createRootElement(String name) {
		return new DefaultConcretePathRootElement(name, rootObject, type);
	}
	
	@Override
	public void appendSubElement(String name) {
		appendElement(new DefaultConcretePathSubElement(name));
	}
	
	@Override
	public void appendIndexElement(Expression index) {
		throw new RuntimeException("Concrete path cannot cope with expressions.");
	}
	
	public void appendIndexElement(int index) {
		appendElement(new DefaultConcretePathIndexElement(index));
	}
	
	public CompleteVariableID resolve() {
		ConcretePathElement element = (ConcretePathElement) tail;
		VariableID var = element.getVariableID(ti);
		
		if (var instanceof CompleteVariableID) {
			return (CompleteVariableID) var;
		}
		
		return null;
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
