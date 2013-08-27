package gov.nasa.jpf.abstraction.concrete.access.impl;

import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteRoot;
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
	
	public static LocalVar create(String name, ThreadInfo threadInfo, LocalVarInfo localVarInfo) {
		if (name == null || threadInfo == null || localVarInfo == null) {
			return null;
		}
		
		return new LocalVar(name, threadInfo, localVarInfo);
	}
}
