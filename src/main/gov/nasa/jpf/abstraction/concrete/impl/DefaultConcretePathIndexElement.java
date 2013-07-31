package gov.nasa.jpf.abstraction.concrete.impl;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.impl.DefaultAccessPathIndexElement;
import gov.nasa.jpf.abstraction.concrete.ArrayElementID;
import gov.nasa.jpf.abstraction.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.concrete.ConcretePathElement;
import gov.nasa.jpf.abstraction.concrete.ConcretePathIndexElement;
import gov.nasa.jpf.abstraction.concrete.PartialVariableID;
import gov.nasa.jpf.abstraction.concrete.Reference;
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
	public PathResolution getVariableIDs(ThreadInfo ti) {
		ConcretePathElement previous = getPrevious();
		PathResolution resolution = previous.getVariableIDs(ti);
		Map<AccessPath, VariableID> vars = resolution.current;
		Map<AccessPath, VariableID> ret = new HashMap<AccessPath, VariableID>();
		
		for (AccessPath path : vars.keySet()) {
			VariableID var = vars.get(path);
		
			if (var instanceof CompleteVariableID) continue;
			
			Reference ref = ((PartialVariableID)var).getRef();
			
			if (ref.getElementInfo().getClassInfo().isArray()) {
				for (int i = 0; i < ref.getElementInfo().getArrayFields().arrayLength(); ++i) {
					AccessPath clone = path.clone();
					
					clone.appendIndexElement(Constant.create(i));

					if (ref.getElementInfo().getClassInfo().isReferenceArray()) {
						ElementInfo info = ti.getElementInfo(ref.getElementInfo().getArrayFields().getReferenceValue(i));
						
						if (info != null) {
							ret.put(clone, new PartialVariableID(DefaultConcretePathElement.createArrayElementReference(ti, i, ref.getElementInfo())));
						}
					} else {
						ret.put(clone, new ArrayElementID(ref.getObjectRef(), i));
					}
				}
			}
		}
		
		resolution.processed.putAll(resolution.current);
		resolution.current = ret;
		
		return resolution;
	}
	
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
