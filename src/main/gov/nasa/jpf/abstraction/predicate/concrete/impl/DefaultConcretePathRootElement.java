package gov.nasa.jpf.abstraction.predicate.concrete.impl;

import gov.nasa.jpf.abstraction.predicate.concrete.ConcretePath;
import gov.nasa.jpf.abstraction.predicate.concrete.ConcretePathRootElement;
import gov.nasa.jpf.abstraction.predicate.concrete.LocalVariableID;
import gov.nasa.jpf.abstraction.predicate.concrete.PartialVariableID;
import gov.nasa.jpf.abstraction.predicate.concrete.VariableID;
import gov.nasa.jpf.abstraction.predicate.grammar.AccessPathIndexElement;
import gov.nasa.jpf.abstraction.predicate.grammar.AccessPathMiddleElement;
import gov.nasa.jpf.abstraction.predicate.grammar.impl.DefaultAccessPathRootElement;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public class DefaultConcretePathRootElement extends DefaultAccessPathRootElement implements ConcretePathRootElement {

	private Object rootObject;
	private ConcretePath.Type type;

	public DefaultConcretePathRootElement(String name, Object rootObject, ConcretePath.Type type) {
		super(name);

		this.rootObject = rootObject;
		this.type = type;
	}

	@Override
	public ConcretePath.Type getType() {
		return type;
	}
	
	@Override
	public void setNext(AccessPathMiddleElement element) {
		switch (type) {
		case LOCAL:
			throw new RuntimeException("Cannot access structure of a primitive type.");

		case STATIC:
			if (element instanceof AccessPathIndexElement) {
				throw new RuntimeException("Cannot access a class as an array.");
			}
			super.setNext(element);
            break;

		default:
			super.setNext(element);
		}
	}

	@Override
	public VariableID getVariableID(ThreadInfo ti) {
		/**
		 * If the path is of a primitive form (one element)
		 * it necessarily refers to a primitive local variable.
		 */		
		switch (type) {
		case LOCAL:
			LocalVarInfo info = (LocalVarInfo) rootObject;

			return new LocalVariableID(info.getName(), info.getSlotIndex());
		}
		
		if (rootObject instanceof ElementInfo) {
			return new PartialVariableID((ElementInfo)rootObject);
		}

		return null;
	}
	
	@Override
	public Object clone() {
		DefaultConcretePathRootElement clone = new DefaultConcretePathRootElement(getName(), rootObject, type);
		
		if (getNext() != null) {
			clone.setNext((AccessPathMiddleElement) getNext().clone());
		}
		
		return clone;
	}

}