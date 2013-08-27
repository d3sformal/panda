package gov.nasa.jpf.abstraction.concrete.access.impl;

import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.concrete.Reference;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteRoot;
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
	
	public static LocalVarRootedHeapObject create(String name, ThreadInfo threadInfo, ElementInfo elementInfo, LocalVarInfo localVarInfo) {
		if (name == null || threadInfo == null || elementInfo == null || localVarInfo == null) {
			return null;
		}
		
		return new LocalVarRootedHeapObject(name, threadInfo, elementInfo, localVarInfo);
	}
}
