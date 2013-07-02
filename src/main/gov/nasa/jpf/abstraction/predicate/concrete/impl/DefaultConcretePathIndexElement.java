package gov.nasa.jpf.abstraction.predicate.concrete.impl;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.abstraction.predicate.concrete.ArrayElementID;
import gov.nasa.jpf.abstraction.predicate.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.predicate.concrete.ConcretePath;
import gov.nasa.jpf.abstraction.predicate.concrete.ConcretePathElement;
import gov.nasa.jpf.abstraction.predicate.concrete.ConcretePathIndexElement;
import gov.nasa.jpf.abstraction.predicate.concrete.PartialVariableID;
import gov.nasa.jpf.abstraction.predicate.concrete.VariableID;
import gov.nasa.jpf.abstraction.predicate.grammar.AccessPath;
import gov.nasa.jpf.abstraction.predicate.grammar.AccessPathMiddleElement;
import gov.nasa.jpf.abstraction.predicate.grammar.Constant;
import gov.nasa.jpf.abstraction.predicate.grammar.impl.DefaultAccessPathIndexElement;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public class DefaultConcretePathIndexElement extends DefaultAccessPathIndexElement implements ConcretePathIndexElement {
	
	public DefaultConcretePathIndexElement() {
		super(null);
	}
	
	@Override
	public ConcretePathElement getPrevious() {
		return (ConcretePathElement)super.getPrevious();
	}

	@Override
	public Map<AccessPath, VariableID> getVariableID(ThreadInfo ti) {
		ConcretePathElement previous = getPrevious();
		Map<AccessPath, VariableID> vars = previous.getVariableID(ti);
		Map<AccessPath, VariableID> ret = new HashMap<AccessPath, VariableID>();
		
		for (AccessPath path : vars.keySet()) {
			VariableID var = vars.get(path);
		
			if (var instanceof CompleteVariableID) continue;
			
			ElementInfo ei = ((PartialVariableID)var).getInfo();
			
			if (ei.getClassInfo().isArray()) {
				for (int i = 0; i < ei.getArrayFields().arrayLength(); ++i) {
					AccessPath clone = (AccessPath) path.clone();
					
					clone.appendIndexElement(new Constant(i));

					if (ei.getClassInfo().isReferenceArray()) {
						ret.put(clone, new PartialVariableID(ti.getElementInfo(ei.getArrayFields().getReferenceValue(i))));
					} else {
						ret.put(clone, new ArrayElementID(ei.getObjectRef(), i));
					}
				}
			}
		}
		
		return ret;
	}
	
	@Override
	public Object clone() {
		DefaultConcretePathIndexElement clone = new DefaultConcretePathIndexElement();
		
		if (getNext() != null) {
			clone.setNext((AccessPathMiddleElement) getNext().clone());
		}
		
		return clone;
	}

}
