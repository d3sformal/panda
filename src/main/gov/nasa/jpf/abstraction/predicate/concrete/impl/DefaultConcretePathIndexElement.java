package gov.nasa.jpf.abstraction.predicate.concrete.impl;

import gov.nasa.jpf.abstraction.predicate.concrete.ArrayElementID;
import gov.nasa.jpf.abstraction.predicate.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.predicate.concrete.ConcretePath;
import gov.nasa.jpf.abstraction.predicate.concrete.ConcretePathElement;
import gov.nasa.jpf.abstraction.predicate.concrete.ConcretePathIndexElement;
import gov.nasa.jpf.abstraction.predicate.concrete.PartialVariableID;
import gov.nasa.jpf.abstraction.predicate.concrete.VariableID;
import gov.nasa.jpf.abstraction.predicate.grammar.AccessPathMiddleElement;
import gov.nasa.jpf.abstraction.predicate.grammar.Constant;
import gov.nasa.jpf.abstraction.predicate.grammar.Expression;
import gov.nasa.jpf.abstraction.predicate.grammar.impl.DefaultAccessPathIndexElement;
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
	public VariableID getVariableID(ThreadInfo ti) {
		ConcretePathElement previous = getPrevious();
		VariableID var = previous.getVariableID(ti);
		
		if (var instanceof PartialVariableID) {
				ElementInfo ei = ((PartialVariableID)var).getInfo();
				
				if (ei.getClassInfo().isArray()) {
					ConcretePath indexPath = (ConcretePath) getIndex();
					CompleteVariableID index = indexPath.resolve();

					if (ei.getClassInfo().isReferenceArray()) {
						try {
							return new PartialVariableID(ti.getElementInfo(ei.getReferenceElement(index.getInteger())));
						} catch (Exception e) {
							System.err.println("Cannot continue resolving path");
							return null; //TODO: unless we know the precise index we cannot continue along the path 
						}
					}
					
					return new ArrayElementID(ei.getObjectRef(), index);
				}
		}
		
		return null;
	}
	
	@Override
	public Object clone() {
		DefaultConcretePathIndexElement clone = new DefaultConcretePathIndexElement(getIndex());
		
		if (getNext() != null) {
			clone.setNext((AccessPathMiddleElement) getNext().clone());
		}
		
		return clone;
	}

}
