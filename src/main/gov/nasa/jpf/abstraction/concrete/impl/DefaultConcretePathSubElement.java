package gov.nasa.jpf.abstraction.concrete.impl;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.impl.DefaultAccessPathSubElement;
import gov.nasa.jpf.abstraction.concrete.PartialClassID;
import gov.nasa.jpf.abstraction.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.concrete.ConcretePathElement;
import gov.nasa.jpf.abstraction.concrete.ConcretePathSubElement;
import gov.nasa.jpf.abstraction.concrete.ObjectFieldID;
import gov.nasa.jpf.abstraction.concrete.PartialVariableID;
import gov.nasa.jpf.abstraction.concrete.StaticFieldID;
import gov.nasa.jpf.abstraction.concrete.VariableID;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public class DefaultConcretePathSubElement extends DefaultAccessPathSubElement implements ConcretePathSubElement {
	
	public DefaultConcretePathSubElement(String name) {
		super(name);
	}

	@Override
	public ConcretePathElement getPrevious() {
		return (ConcretePathElement)super.getPrevious();
	}

	@Override
	public Map<AccessPath, VariableID> getVariableIDs(ThreadInfo ti) {
		ConcretePathElement previous = getPrevious();
		Map<AccessPath, VariableID> vars = previous.getVariableIDs(ti);
		Map<AccessPath, VariableID> ret = new HashMap<AccessPath, VariableID>();
		
		for (AccessPath path : vars.keySet()) {
			VariableID var = vars.get(path);

			if (var instanceof CompleteVariableID) continue;
			
			path.appendSubElement(getName());
			
			ElementInfo ei = ((PartialVariableID)var).getInfo();
			Object object = ei.getFieldValueObject(getName());
			
			if (var instanceof PartialClassID) {
				// CLASS
				PartialClassID classID = (PartialClassID) var;
				
				if (classID.complete() && !(object instanceof ElementInfo)) {
					// STATIC PRIMITIVE FIELD

					ret.put(path, new StaticFieldID(ei.getClassInfo().getName(), getName()));
				} else {
					// NOT YET COMPLETE PATH package.package.Class
					classID.extend(getName());

					ret.put(path, var);
				}
			} else if (object instanceof ElementInfo) {
				// STRUCTURED FIELD (PATH NOT YET COMPLETE)
				
				ret.put(path, new PartialVariableID((ElementInfo)object));
			} else {
				// PRIMITIVE FIELD
				
				ret.put(path, new ObjectFieldID(ei.getObjectRef(), getName()));
			}
		}
		
		return ret;
	}
	
	@Override
	public DefaultConcretePathSubElement clone() {
		DefaultConcretePathSubElement clone = new DefaultConcretePathSubElement(getName());
		
		if (getNext() != null) {
			clone.setNext(getNext().clone());
			clone.getNext().setPrevious(clone);
		}
		
		return clone;
	}

}
