package gov.nasa.jpf.abstraction.concrete.access.impl;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.concrete.VariableID;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteRoot;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.util.Map;

public class LocalVar extends DefaultRoot implements ConcreteRoot {

	private ThreadInfo ti;
	private LocalVarInfo li;
	
	protected LocalVar(String name, ThreadInfo ti, LocalVarInfo li) {
		super(name);
		
		this.ti = ti;
		this.li = li;
	}
	
	@Override
	public Map<AccessExpression, VariableID> partialResolve() {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}

	@Override
	public Map<AccessExpression, VariableID> partialExhaustiveResolve() {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}

	@Override
	public Map<AccessExpression, CompleteVariableID> resolve() {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}
	
	public static LocalVar create(String name, ThreadInfo threadInfo, LocalVarInfo localVarInfo) {
		if (name == null || threadInfo == null || localVarInfo == null) {
			return null;
		}
		
		return new LocalVar(name, threadInfo, localVarInfo);
	}
}
