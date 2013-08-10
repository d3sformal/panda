package gov.nasa.jpf.abstraction.concrete.access.impl;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.concrete.ObjectReference;
import gov.nasa.jpf.abstraction.concrete.PartialClassID;
import gov.nasa.jpf.abstraction.concrete.VariableID;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteRoot;
import gov.nasa.jpf.abstraction.concrete.impl.PathResolution;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public class Class extends DefaultRoot implements ConcreteRoot {
	
	private ThreadInfo ti;
	private ElementInfo ei;
	
	protected Class(String name, ThreadInfo ti, ElementInfo ei) {
		super(name);
		
		this.ti = ti;
		this.ei = ei;
	}
	
	private PathResolution resolveClass() {
		Map<AccessExpression, VariableID> processed = new HashMap<AccessExpression, VariableID>();
		
		processed.put(DefaultRoot.create(getName()), new PartialClassID(new ObjectReference(ei), getName()));
		
		return new PathResolution(ti, processed);
	}
	
	@Override
	public PathResolution partialResolve() {
		return resolveClass();
	}

	@Override
	public PathResolution partialExhaustiveResolve() {
		return resolveClass();
	}

	@Override
	public PathResolution resolve() {
		return resolveClass();
	}
	
	public static Class create(String name, ThreadInfo threadInfo, ElementInfo elementInfo) {
		if (name == null || threadInfo == null || elementInfo == null) {
			return null;
		}
		
		return new Class(name, threadInfo, elementInfo);
	}
}
