//
// Copyright (C) 2007 United States Government as represented by the
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

import gov.nasa.jpf.Config;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.abstraction.bytecode.*;
import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.abstraction.numeric.Signs;

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

		abs = new Signs(); // for now we only have one abstraction but in the
							// future this should be customized based on user
							// config
	}
	
	@Override
	public void setClassInfoContext(ClassInfo ci) {
		this.ci = ci;
	}
	
	// bytecodes

	@Override
	public Instruction d2f() {
		return (filter.isInstrumentedClass(ci) ? new D2F() : super.d2f());
	}

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

}