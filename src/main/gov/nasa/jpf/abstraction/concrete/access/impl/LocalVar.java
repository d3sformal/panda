package gov.nasa.jpf.abstraction.concrete.access.impl;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.concrete.LocalVariableID;
import gov.nasa.jpf.abstraction.concrete.VariableID;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteRoot;
import gov.nasa.jpf.abstraction.concrete.impl.PathResolution;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public class LocalVar extends DefaultRoot implements ConcreteRoot {

	private ThreadInfo ti;
	private LocalVarInfo li;
	
	protected LocalVar(String name, ThreadInfo ti, LocalVarInfo li) {
		super(name);
		
		this.ti = ti;
		this.li = li;
	}
	
	private PathResolution resolveVar() {
		Map<AccessExpression, VariableID> processed = new HashMap<AccessExpression, VariableID>();
		
		processed.put(DefaultRoot.create(getName()), new LocalVariableID(getName(), li.getSlotIndex()));
		
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
	
	public static LocalVar create(String name, ThreadInfo threadInfo, LocalVarInfo localVarInfo) {
		if (name == null || threadInfo == null || localVarInfo == null) {
			return null;
		}
		
		return new LocalVar(name, threadInfo, localVarInfo);
	}
}
