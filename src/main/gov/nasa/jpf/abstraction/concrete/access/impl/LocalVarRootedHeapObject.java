package gov.nasa.jpf.abstraction.concrete.access.impl;

import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.concrete.Reference;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteRoot;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Array;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Object;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Value;
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

	@Override
	public Value resolve() {		
		if (ei.isArray()) {
			return new Array(new Reference(ti, ei));
		} else {
			return new Object(new Reference(ti, ei));
		}
	}
	
	public static LocalVarRootedHeapObject create(String name, ThreadInfo threadInfo, ElementInfo elementInfo, LocalVarInfo localVarInfo) {
		if (name == null || threadInfo == null || elementInfo == null || localVarInfo == null) {
			return null;
		}
		
		return new LocalVarRootedHeapObject(name, threadInfo, elementInfo, localVarInfo);
	}
}
