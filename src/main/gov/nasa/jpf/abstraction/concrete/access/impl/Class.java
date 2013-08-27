package gov.nasa.jpf.abstraction.concrete.access.impl;

import gov.nasa.jpf.abstraction.common.access.impl.DefaultPackageAndClass;
import gov.nasa.jpf.abstraction.concrete.Reference;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteRoot;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public class Class extends DefaultPackageAndClass implements ConcreteRoot {
	
	private ThreadInfo ti;
	private ElementInfo ei;
	
	protected Class(String name, ThreadInfo ti, ElementInfo ei) {
		super(name);
		
		this.ti = ti;
		this.ei = ei;
	}
	
	public static Class create(String name, ThreadInfo threadInfo, ElementInfo elementInfo) {
		if (name == null || threadInfo == null || elementInfo == null) {
			return null;
		}
		
		return new Class(name, threadInfo, elementInfo);
	}
}
