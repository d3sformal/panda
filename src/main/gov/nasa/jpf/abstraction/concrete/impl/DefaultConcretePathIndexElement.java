package gov.nasa.jpf.abstraction.concrete.impl;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.AccessPathIndexElement;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.impl.DefaultAccessPathIndexElement;
import gov.nasa.jpf.abstraction.concrete.ArrayElementID;
import gov.nasa.jpf.abstraction.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.concrete.ConcretePathElement;
import gov.nasa.jpf.abstraction.concrete.ConcretePathIndexElement;
import gov.nasa.jpf.abstraction.concrete.PartialVariableID;
import gov.nasa.jpf.abstraction.concrete.VariableID;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public class DefaultConcretePathIndexElement extends DefaultAccessPathIndexElement implements ConcretePathIndexElement {
	
	public DefaultConcretePathIndexElement(Expression index) {
		super(index);
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
			
			if (ei.getClassInfo().isArray()) {
				for (int i = 0; i < ei.getArrayFields().arrayLength(); ++i) {
					AccessPath clone = path.clone();
					
					clone.appendIndexElement(Constant.create(i));

					if (ei.getClassInfo().isReferenceArray()) {
						ElementInfo info = ti.getElementInfo(ei.getArrayFields().getReferenceValue(i));
						
						if (info != null) {
							ret.put(clone, new PartialVariableID(info));
						}
					} else {
						ret.put(clone, new ArrayElementID(ei.getObjectRef(), i));
					}
				}
			}
		}
		
		return ret;
	}
	
	/*
	@Override
	public boolean equals(Object o) {
		return o instanceof AccessPathIndexElement;
	}
	*/
	
	@Override
	public DefaultConcretePathIndexElement clone() {
		DefaultConcretePathIndexElement clone = new DefaultConcretePathIndexElement(getIndex().clone());
		
		if (getNext() != null) {
			clone.setNext(getNext().clone());
			clone.getNext().setPrevious(clone);
		}
		
		return clone;
	}

}
