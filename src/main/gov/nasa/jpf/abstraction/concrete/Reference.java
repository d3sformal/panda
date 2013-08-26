package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public class Reference {
	private ThreadInfo ti;
	private ElementInfo ei;
	private static int NULL = -1;
	
	public Reference(ThreadInfo ti, ElementInfo ei) {
		this.ti = ti;
		this.ei = ei;
	}
	
	public ThreadInfo getThreadInfo() {
		return ti;
	}
	
	public ElementInfo getElementInfo() {
		return ei;
	}
	
	public int getObjectRef() {
		return ei == null ? NULL : ei.getObjectRef();
	}
	
	@Override
	public String toString() {
		return ((Integer)getObjectRef()).toString();
	}
	
	@Override
	public final boolean equals(Object o) {
		if (o instanceof Reference) {
			return getObjectRef() == ((Reference) o).getObjectRef() && getObjectRef() != NULL;
		}
		
		return false;
	}
	
	@Override
	public final int hashCode() {
		return getObjectRef();
	}

}
