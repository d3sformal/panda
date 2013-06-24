//
// Copyright (C) 2012 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

package gov.nasa.jpf.abstraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.abstraction.bytecode.*;
import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.abstraction.numeric.EvennessAbstractionFactory;
import gov.nasa.jpf.abstraction.numeric.IntervalAbstractionFactory;
import gov.nasa.jpf.abstraction.numeric.RangeAbstractionFactory;
import gov.nasa.jpf.abstraction.numeric.SignsAbstractionFactory;
import gov.nasa.jpf.abstraction.numeric.ContainerAbstraction;

import gov.nasa.jpf.util.InstructionFactoryFilter;

public class AbstractInstructionFactory extends
		gov.nasa.jpf.jvm.bytecode.InstructionFactory {

	ClassInfo ci;

	InstructionFactoryFilter filter;

	public static Abstraction abs;

	public AbstractInstructionFactory(Config conf) {

		System.out.println("Running Abstract PathFinder ...");

		filter = new InstructionFactoryFilter(null,
				new String[] {/* "java.*", */"javax.*" }, null, null);

		Map<String, AbstractionFactory> abs_factory = new HashMap<String, AbstractionFactory>();
		
		abs_factory.put("signs", 	new SignsAbstractionFactory());
		abs_factory.put("evenness",	new EvennessAbstractionFactory());
		abs_factory.put("interval",	new IntervalAbstractionFactory());
		abs_factory.put("range",	new RangeAbstractionFactory());
		
		List<Abstraction> abs_list = new ArrayList<Abstraction>();

		String[] abs_str = conf.getStringArray("abstract.domain");

		for (String s : abs_str) {
			String[] args = s.split(" ");
			String abs_name = args[0].toLowerCase();
			
			AbstractionFactory factory = abs_factory.get(abs_name);
			
			if (factory == null) {
				System.out.println("### jpf-abstraction: " + s
						+ " is unknown abstraction");
			} else {
				Abstraction abs = factory.create(args);
				
				if (abs != null) {
					abs_list.add(abs);
				}
			}
		}

		if (abs_list.size() == 0) {
			abs = null;
		} else if (abs_list.size() == 1) {
			abs = abs_list.get(0);
		} else {
			abs = new ContainerAbstraction(abs_list);
			System.out
					.println("### jpf-abstraction: CONTAINER abstraction turned on");
		}
	}

	@Override
	public Instruction d2f() {
		return (filter.isInstrumentedClass(ci) ? new D2F() : super.d2f());
	}

	// bytecodes

	@Override
	public Instruction d2i() {
		return (filter.isInstrumentedClass(ci) ? new D2I() : super.d2i());
	}

	@Override
	public Instruction d2l() {
		return (filter.isInstrumentedClass(ci) ? new D2L() : super.d2l());
	}

	@Override
	public Instruction dadd() {
		return (filter.isInstrumentedClass(ci) ? new DADD() : super.dadd());
	}

	@Override
	public Instruction dcmpg() {
		return (filter.isInstrumentedClass(ci) ? new DCMPG() : super.dcmpg());
	}

	@Override
	public Instruction dcmpl() {
		return (filter.isInstrumentedClass(ci) ? new DCMPL() : super.dcmpl());
	}

	@Override
	public Instruction ddiv() {
		return (filter.isInstrumentedClass(ci) ? new DDIV() : super.ddiv());
	}

	@Override
	public Instruction dmul() {
		return (filter.isInstrumentedClass(ci) ? new DMUL() : super.dmul());
	}

	@Override
	public Instruction dneg() {
		return (filter.isInstrumentedClass(ci) ? new DNEG() : super.dneg());
	}

	@Override
	public Instruction drem() {
		return (filter.isInstrumentedClass(ci) ? new DREM() : super.drem());
	}

	@Override
	public Instruction dsub() {
		return (filter.isInstrumentedClass(ci) ? new DSUB() : super.dsub());
	}

	@Override
	public Instruction f2d() {
		return (filter.isInstrumentedClass(ci) ? new F2D() : super.f2d());
	}

	@Override
	public Instruction f2i() {
		return (filter.isInstrumentedClass(ci) ? new F2I() : super.f2i());
	}

	@Override
	public Instruction f2l() {
		return (filter.isInstrumentedClass(ci) ? new F2L() : super.f2l());
	}

	@Override
	public Instruction fadd() {
		return (filter.isInstrumentedClass(ci) ? new FADD() : super.fadd());
	}

	@Override
	public Instruction fcmpg() {
		return (filter.isInstrumentedClass(ci) ? new FCMPG() : super.fcmpg());
	}

	@Override
	public Instruction fcmpl() {
		return (filter.isInstrumentedClass(ci) ? new FCMPL() : super.fcmpl());
	}

	@Override
	public Instruction fdiv() {
		return (filter.isInstrumentedClass(ci) ? new FDIV() : super.fdiv());
	}

	@Override
	public Instruction fmul() {
		return (filter.isInstrumentedClass(ci) ? new FMUL() : super.fmul());
	}

	@Override
	public Instruction fneg() {
		return (filter.isInstrumentedClass(ci) ? new FNEG() : super.fneg());
	}

	@Override
	public Instruction frem() {
		return (filter.isInstrumentedClass(ci) ? new FREM() : super.frem());
	}

	@Override
	public Instruction fsub() {
		return (filter.isInstrumentedClass(ci) ? new FSUB() : super.fsub());
	}

	@Override
	public Instruction i2d() {
		return (filter.isInstrumentedClass(ci) ? new I2D() : super.i2d());
	}

	@Override
	public Instruction i2f() {
		return (filter.isInstrumentedClass(ci) ? new I2F() : super.i2f());
	}

	@Override
	public Instruction i2l() {
		return (filter.isInstrumentedClass(ci) ? new I2L() : super.i2l());
	}

	@Override
	public Instruction i2s() {
		return (filter.isInstrumentedClass(ci) ? new I2S() : super.i2s());
	}

	@Override
	public Instruction iadd() {
		return (filter.isInstrumentedClass(ci) ? new IADD() : super.iadd());
	}

	@Override
	public Instruction iand() {
		return (filter.isInstrumentedClass(ci) ? new IAND() : super.iand());
	}

	@Override
	public Instruction idiv() {
		return (filter.isInstrumentedClass(ci) ? new IDIV() : super.idiv());
	}

	@Override
	public Instruction if_icmpeq(int targetPc) {
		return (filter.isInstrumentedClass(ci) ? new IF_ICMPEQ(targetPc)
				: super.if_icmpeq(targetPc));
	}

	@Override
	public Instruction if_icmpge(int targetPc) {
		return (filter.isInstrumentedClass(ci) ? new IF_ICMPGE(targetPc)
				: super.if_icmpge(targetPc));
	}

	@Override
	public Instruction if_icmpgt(int targetPc) {
		return (filter.isInstrumentedClass(ci) ? new IF_ICMPGT(targetPc)
				: super.if_icmpgt(targetPc));
	}

	@Override
	public Instruction if_icmple(int targetPc) {
		return (filter.isInstrumentedClass(ci) ? new IF_ICMPLE(targetPc)
				: super.if_icmple(targetPc));
	}

	@Override
	public Instruction if_icmplt(int targetPc) {
		return (filter.isInstrumentedClass(ci) ? new IF_ICMPLT(targetPc)
				: super.if_icmplt(targetPc));
	}

	@Override
	public Instruction if_icmpne(int targetPc) {
		return (filter.isInstrumentedClass(ci) ? new IF_ICMPNE(targetPc)
				: super.if_icmpne(targetPc));
	}

	@Override
	public Instruction ifeq(int targetPc) {
		return (filter.isInstrumentedClass(ci) ? new IFEQ(targetPc) : super
				.ifeq(targetPc));
	}

	@Override
	public Instruction ifge(int targetPc) {
		return (filter.isInstrumentedClass(ci) ? new IFGE(targetPc) : super
				.ifge(targetPc));
	}

	@Override
	public Instruction ifgt(int targetPc) {
		return (filter.isInstrumentedClass(ci) ? new IFGT(targetPc) : super
				.ifgt(targetPc));
	}

	@Override
	public Instruction ifle(int targetPc) {
		return (filter.isInstrumentedClass(ci) ? new IFLE(targetPc) : super
				.ifle(targetPc));
	}

	@Override
	public Instruction iflt(int targetPc) {
		return (filter.isInstrumentedClass(ci) ? new IFLT(targetPc) : super
				.iflt(targetPc));
	}

	@Override
	public Instruction ifne(int targetPc) {
		return (filter.isInstrumentedClass(ci) ? new IFNE(targetPc) : super
				.ifne(targetPc));
	}

	@Override
	public Instruction iinc(int localVarIndex, int incConstant) {
		return (filter.isInstrumentedClass(ci) ? new IINC(localVarIndex,
				incConstant) : super.iinc(localVarIndex, incConstant));
	}

	@Override
	public Instruction imul() {
		return (filter.isInstrumentedClass(ci) ? new IMUL() : super.imul());
	}

	@Override
	public Instruction ineg() {
		return (filter.isInstrumentedClass(ci) ? new INEG() : super.ineg());
	}

	@Override
	public Instruction ior() {
		return (filter.isInstrumentedClass(ci) ? new IOR() : super.ior());
	}

	@Override
	public Instruction irem() {
		return (filter.isInstrumentedClass(ci) ? new IREM() : super.irem());
	}

	@Override
	public Instruction ishl() {
		return (filter.isInstrumentedClass(ci) ? new ISHL() : super.ishl());
	}

	@Override
	public Instruction ishr() {
		return (filter.isInstrumentedClass(ci) ? new ISHR() : super.ishr());
	}

	@Override
	public Instruction isub() {
		return (filter.isInstrumentedClass(ci) ? new ISUB() : super.isub());
	}

	@Override
	public Instruction iushr() {
		return (filter.isInstrumentedClass(ci) ? new IUSHR() : super.iushr());
	}

	@Override
	public Instruction ixor() {
		return (filter.isInstrumentedClass(ci) ? new IXOR() : super.ixor());
	}

	@Override
	public Instruction l2d() {
		return (filter.isInstrumentedClass(ci) ? new L2D() : super.l2d());
	}

	@Override
	public Instruction l2f() {
		return (filter.isInstrumentedClass(ci) ? new L2F() : super.l2f());
	}

	@Override
	public Instruction l2i() {
		return (filter.isInstrumentedClass(ci) ? new L2I() : super.l2i());
	}

	@Override
	public Instruction ladd() {
		return (filter.isInstrumentedClass(ci) ? new LADD() : super.ladd());
	}

	@Override
	public Instruction land() {
		return (filter.isInstrumentedClass(ci) ? new LAND() : super.land());
	}

	@Override
	public Instruction ldiv() {
		return (filter.isInstrumentedClass(ci) ? new LDIV() : super.ldiv());
	}

	@Override
	public Instruction lmul() {
		return (filter.isInstrumentedClass(ci) ? new LMUL() : super.lmul());
	}

	@Override
	public Instruction lneg() {
		return (filter.isInstrumentedClass(ci) ? new LNEG() : super.lneg());
	}

	@Override
	public Instruction lookupswitch(int defaultTargetPc, int nEntries) {
		return filter.isInstrumentedClass(ci) 
				? new LOOKUPSWITCH(defaultTargetPc, nEntries) 
				: super.lookupswitch(defaultTargetPc, nEntries);
	}

	@Override
	public Instruction lor() {
		return (filter.isInstrumentedClass(ci) ? new LOR() : super.lor());
	}

	@Override
	public Instruction lrem() {
		return (filter.isInstrumentedClass(ci) ? new LREM() : super.lrem());
	}

	@Override
	public Instruction lshl() {
		return (filter.isInstrumentedClass(ci) ? new LSHL() : super.lshl());
	}

	@Override
	public Instruction lshr() {
		return (filter.isInstrumentedClass(ci) ? new LSHR() : super.lshr());
	}

	@Override
	public Instruction lsub() {
		return (filter.isInstrumentedClass(ci) ? new LSUB() : super.lsub());
	}

	@Override
	public Instruction lushr() {
		return (filter.isInstrumentedClass(ci) ? new LUSHR() : super.lushr());
	}

	@Override
	public Instruction lxor() {
		return (filter.isInstrumentedClass(ci) ? new LXOR() : super.lxor());
	}

	@Override
	public Instruction tableswitch(int defaultTargetPc, int low, int high) {
		return filter.isInstrumentedClass(ci) 
				? new TABLESWITCH(defaultTargetPc, low, high) 
				: super.tableswitch(defaultTargetPc, low, high);
	}
}