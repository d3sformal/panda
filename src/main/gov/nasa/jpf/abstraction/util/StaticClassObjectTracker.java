package gov.nasa.jpf.abstraction.util;

import java.util.HashSet;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.bytecode.INVOKECLINIT;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StaticElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

public class StaticClassObjectTracker extends ListenerAdapter {
	
	private static String indentationStep = "  ";

	@Override
	public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {		
		if (execInsn instanceof INVOKECLINIT) {
			INVOKECLINIT clinit = (INVOKECLINIT) execInsn;
			
			StaticElementInfo ei = clinit.getStaticElementInfo();
			
			String head = ">>>>>>>>>>>>>>>>>>>>>>>>>>" + ei.getClassInfo().getName() + "<<<<<<<<<<<<<<<<<<<<<<<<<<";
			String foot = head.replaceAll(".", "#");
			
			System.out.println(head);
			traverse(curTh, ei, "", new HashSet<ElementInfo>());
			System.out.println(foot);
		}
	}

	private void traverse(ThreadInfo ti, ElementInfo ei, String indentation, HashSet<ElementInfo> visited) {
		if (ei == null) {
			System.out.println(ei);
			return;
		}
		
		boolean isVisited = visited.contains(ei);
		
		visited.add(ei);
		
		if (ei.isArray()) {
			System.out.println("array(" + ei.getObjectRef() + ")");
		} else {
			System.out.println("object(" + ei.getObjectRef() + ")");
		}
		
		if (isVisited) return;
		
		indentation += indentationStep;
		
		if (ei.isArray()) {
			for (int i = 0; i < ei.arrayLength(); ++i) {
				if (ei.isReferenceArray()) {
					ElementInfo eei = ti.getElementInfo(ei.getReferenceElement(i));
					
					System.out.print(indentation + i + ": ");
					
					traverse(ti, eei, indentation, visited);
				} else {
					System.out.println(indentation + i + ": primitive");
				}
			}
		} else {
			for (int i = 0; i < ei.getNumberOfFields(); ++i) {
				FieldInfo fi = ei.getFieldInfo(i);
				String field = fi.getName();
				
				if (fi.isReference()) {
					ElementInfo fei = ti.getElementInfo(ei.getReferenceField(fi));
					
					System.out.print(indentation + field + ": ");
					
					traverse(ti, fei, indentation + indentationStep , visited);
				} else {
					System.out.println(indentation + field + ": primitive");
				}
			}
		}
	}

}
