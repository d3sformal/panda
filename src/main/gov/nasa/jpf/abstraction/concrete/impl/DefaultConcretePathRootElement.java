package gov.nasa.jpf.abstraction.concrete.impl;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.AccessPathIndexElement;
import gov.nasa.jpf.abstraction.common.AccessPathMiddleElement;
import gov.nasa.jpf.abstraction.common.impl.DefaultAccessPathRootElement;
import gov.nasa.jpf.abstraction.concrete.PartialClassID;
import gov.nasa.jpf.abstraction.concrete.ConcretePath;
import gov.nasa.jpf.abstraction.concrete.ConcretePathRootElement;
import gov.nasa.jpf.abstraction.concrete.LocalVariableID;
import gov.nasa.jpf.abstraction.concrete.PartialVariableID;
import gov.nasa.jpf.abstraction.concrete.VariableID;
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
	public PathResolution getVariableIDs(ThreadInfo ti) {
		/**
		 * If the path is of a primitive form (one element)
		 * it necessarily refers to a primitive local variable.
		 */
		Map<AccessPath, VariableID> ret = new HashMap<AccessPath, VariableID>();
		
		if (rootObject == null) return new PathResolution();
		
		switch (type) {
		case LOCAL:
			// LOCAL VARIABLE
			LocalVarInfo info = (LocalVarInfo) rootObject;

			ret.put(new AccessPath(getName()), new LocalVariableID(info.getName(), info.getSlotIndex()));
			break;
		case STATIC:
			// STATIC PATH
			ret.put(new AccessPath(getName()), new PartialClassID((ElementInfo)rootObject, getName()));
			break;
		case HEAP:
			// INCOMPLETE PATH
			ret.put(new AccessPath(getName()), new PartialVariableID((ElementInfo)rootObject));
			break;
		}
		
		return new PathResolution(ret);
	}
	
	@Override
	public DefaultConcretePathRootElement clone() {
		DefaultConcretePathRootElement clone = new DefaultConcretePathRootElement(getName(), rootObject, type);
		
		if (getNext() != null) {
			clone.setNext(getNext().clone());
			clone.getNext().setPrevious(clone);
		}
		
		return clone;
	}

}
