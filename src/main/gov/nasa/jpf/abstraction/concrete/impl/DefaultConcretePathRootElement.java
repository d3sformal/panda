package gov.nasa.jpf.abstraction.concrete.impl;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.AccessPathIndexElement;
import gov.nasa.jpf.abstraction.common.AccessPathMiddleElement;
import gov.nasa.jpf.abstraction.common.impl.DefaultAccessPathRootElement;
import gov.nasa.jpf.abstraction.concrete.ArrayReference;
import gov.nasa.jpf.abstraction.concrete.ObjectReference;
import gov.nasa.jpf.abstraction.concrete.PartialClassID;
import gov.nasa.jpf.abstraction.concrete.ConcretePath;
import gov.nasa.jpf.abstraction.concrete.ConcretePathRootElement;
import gov.nasa.jpf.abstraction.concrete.LocalVariableID;
import gov.nasa.jpf.abstraction.concrete.PartialVariableID;
import gov.nasa.jpf.abstraction.concrete.Reference;
import gov.nasa.jpf.abstraction.concrete.VariableID;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public class DefaultConcretePathRootElement extends DefaultAccessPathRootElement implements ConcretePathRootElement {

	private ElementInfo ei;
	private LocalVarInfo info;
	private ConcretePath.Type type;

	public DefaultConcretePathRootElement(String name, ElementInfo ei, LocalVarInfo info, ConcretePath.Type type) {
		super(name);

		this.ei = ei;
		this.info = info;
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
		
		if (ei == null && info == null) return new PathResolution();
				
		switch (type) {
		case LOCAL:
			// LOCAL VARIABLE
			ret.put(new AccessPath(getName()), new LocalVariableID(info.getName(), info.getSlotIndex()));
			break;
		case STATIC:
			// STATIC PATH
			ret.put(new AccessPath(getName()), new PartialClassID(new ObjectReference(ei), getName()));
			break;
		case HEAP:
			// INCOMPLETE PATH
			ret.put(new AccessPath(getName()), new PartialVariableID(DefaultConcretePathElement.createLocalVarReference(ti, ei, info)));
			break;
		}
		
		return new PathResolution(ret);
	}
	
	@Override
	public DefaultConcretePathRootElement clone() {
		DefaultConcretePathRootElement clone = new DefaultConcretePathRootElement(getName(), ei, info, type);
		
		if (getNext() != null) {
			clone.setNext(getNext().clone());
			clone.getNext().setPrevious(clone);
		}
		
		return clone;
	}

}
