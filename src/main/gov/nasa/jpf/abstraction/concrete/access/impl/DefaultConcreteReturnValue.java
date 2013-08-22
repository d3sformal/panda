package gov.nasa.jpf.abstraction.concrete.access.impl;

import gov.nasa.jpf.abstraction.common.access.impl.DefaultReturnValue;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteRoot;
import gov.nasa.jpf.abstraction.concrete.impl.PathResolution;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

public class DefaultConcreteReturnValue extends DefaultReturnValue implements ConcreteRoot {
	
	private ThreadInfo threadInfo;
	private Instruction pc;
	
	protected DefaultConcreteReturnValue(ThreadInfo threadInfo, Instruction pc) {
		this.threadInfo = threadInfo;
		this.pc = pc;
	}

	@Override
	public PathResolution partialResolve() {
		return new PathResolution(threadInfo);
	}

	@Override
	public PathResolution partialExhaustiveResolve() {
		return new PathResolution(threadInfo);
	}

	@Override
	public PathResolution resolve() {
		return new PathResolution(threadInfo);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof DefaultConcreteReturnValue) {
			DefaultConcreteReturnValue ret = (DefaultConcreteReturnValue) o;
			
			return pc == ret.pc;
		}
		
		return false;
	}
	
	@Override
	public String getName() {
		return super.getName() + "_pc" + pc.getInstructionIndex();
	}
	
	@Override
	public int hashCode() {
		return ("return_" + pc.hashCode()).hashCode();
	}
	
	public static DefaultConcreteReturnValue create(ThreadInfo threadInfo, Instruction pc) {
		if (threadInfo == null || pc == null) {
			return null;
		}
		
		return new DefaultConcreteReturnValue(threadInfo, pc);
	}

}
