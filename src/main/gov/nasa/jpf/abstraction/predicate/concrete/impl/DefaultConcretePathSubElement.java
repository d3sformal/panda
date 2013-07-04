package gov.nasa.jpf.abstraction.predicate.concrete.impl;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.abstraction.predicate.common.AccessPath;
import gov.nasa.jpf.abstraction.predicate.common.impl.DefaultAccessPathSubElement;
import gov.nasa.jpf.abstraction.predicate.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.predicate.concrete.ConcretePath;
import gov.nasa.jpf.abstraction.predicate.concrete.ConcretePathElement;
import gov.nasa.jpf.abstraction.predicate.concrete.ConcretePathRootElement;
import gov.nasa.jpf.abstraction.predicate.concrete.ConcretePathSubElement;
import gov.nasa.jpf.abstraction.predicate.concrete.ObjectFieldID;
import gov.nasa.jpf.abstraction.predicate.concrete.PartialVariableID;
import gov.nasa.jpf.abstraction.predicate.concrete.StaticFieldID;
import gov.nasa.jpf.abstraction.predicate.concrete.VariableID;
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

			ElementInfo ei = ((PartialVariableID)var).getInfo();
		
			Object object = ei.getFieldValueObject(getName());

			path.appendSubElement(getName());

			if (previous instanceof ConcretePathRootElement) {
				ConcretePathRootElement root = (ConcretePathRootElement) previous;
			
				if (root.getType() == ConcretePath.Type.STATIC) {
					if (!(object instanceof ElementInfo)) {
						// STATIC PRIMITIVE FIELD
						ret.put(path, new StaticFieldID(ei.getClassInfo().getName(), getName()));
						continue;
					}
				}
			}
			
			if (object instanceof ElementInfo) {
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
