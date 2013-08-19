package gov.nasa.jpf.abstraction.concrete.access.impl;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.concrete.PartialVariableID;
import gov.nasa.jpf.abstraction.concrete.VariableID;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteRoot;
import gov.nasa.jpf.abstraction.concrete.impl.PathResolution;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public class LocalVarRootedHeapObject extends DefaultRoot implements ConcreteRoot {
	
	private ThreadInfo ti;
	private ElementInfo ei;
	private LocalVarInfo li;
	
	protected LocalVarRootedHeapObject(String name, ThreadInfo ti, ElementInfo ei, LocalVarInfo li) {
		super(name);
		
		this.ti = ti;
		this.ei = ei;
		this.li = li;
	}
	
	private PathResolution resolveVar() {
		Map<AccessExpression, VariableID> processed = new HashMap<AccessExpression, VariableID>();
		
		processed.put(DefaultRoot.create(getName()), new PartialVariableID(DefaultConcreteAccessExpression.createLocalVarReference(ti, ei, li)));
		
		return new PathResolution(ti, processed, processed);
	}
	
	@Override
	public PathResolution partialResolve() {
		return resolveVar();
	}

	@Override
	public PathResolution partialExhaustiveResolve() {
		return resolveVar();
	}

	@Override
	public PathResolution resolve() {
		return resolveVar();
	}
	
	public static LocalVarRootedHeapObject create(String name, ThreadInfo threadInfo, ElementInfo elementInfo, LocalVarInfo localVarInfo) {
		if (name == null || threadInfo == null || elementInfo == null || localVarInfo == null) {
			return null;
		}
		
		return new LocalVarRootedHeapObject(name, threadInfo, elementInfo, localVarInfo);
	}
}
