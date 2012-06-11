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

	public Instruction i2l() {
		return (filter.isInstrumentedClass(ci) ? new I2L() : super.i2l());
	}

	public Instruction iand() {
		return (filter.isInstrumentedClass(ci) ? new IAND() : super.iand());
	}

	public Instruction ior() {
		return (filter.isInstrumentedClass(ci) ? new IOR() : super.ior());
	}

	public Instruction ixor() {
		return (filter.isInstrumentedClass(ci) ? new IXOR() : super.ixor());
	}

	public Instruction ishl() {
		return (filter.isInstrumentedClass(ci) ? new ISHL() : super.ishl());
	}

	public Instruction ishr() {
		return (filter.isInstrumentedClass(ci) ? new ISHR() : super.ishr());
	}

	public Instruction iushr() {
		return (filter.isInstrumentedClass(ci) ? new IUSHR() : super.iushr());
	}

	public Instruction iadd() {
		return (filter.isInstrumentedClass(ci) ? new IADD() : super.iadd());
	}

	public Instruction isub() {
		return (filter.isInstrumentedClass(ci) ? new ISUB() : super.isub());
	}

	public Instruction imul() {
		return (filter.isInstrumentedClass(ci) ? new IMUL() : super.imul());
	}

	public Instruction idiv() {
		return (filter.isInstrumentedClass(ci) ? new IDIV() : super.idiv());
	}

	public Instruction irem() {
		return (filter.isInstrumentedClass(ci) ? new IREM() : super.irem());
	}

	public Instruction ineg() {
		return (filter.isInstrumentedClass(ci) ? new INEG() : super.ineg());
	}

	public Instruction iinc(int localVarIndex, int incConstant) {
		return (filter.isInstrumentedClass(ci) ? new IINC(localVarIndex,
				incConstant) : super.iinc(localVarIndex, incConstant));
	}

	public Instruction ifle(int targetPc) {
		return (filter.isInstrumentedClass(ci) ? new IFLE(targetPc) : super
				.ifle(targetPc));
	}

	public Instruction iflt(int targetPc) {
		return (filter.isInstrumentedClass(ci) ? new IFLT(targetPc) : super
				.iflt(targetPc));
	}

	public Instruction ifge(int targetPc) {
		return (filter.isInstrumentedClass(ci) ? new IFGE(targetPc) : super
				.ifge(targetPc));
	}

	public Instruction ifgt(int targetPc) {
		return (filter.isInstrumentedClass(ci) ? new IFGT(targetPc) : super
				.ifgt(targetPc));
	}

	public Instruction l2i() {
		return (filter.isInstrumentedClass(ci) ? new L2I() : super.l2i());
	}

	public Instruction ladd() {
		return (filter.isInstrumentedClass(ci) ? new LADD() : super.ladd());
	}

	public Instruction land() {
		return (filter.isInstrumentedClass(ci) ? new LAND() : super.land());
	}

	public Instruction ldiv() {
		return (filter.isInstrumentedClass(ci) ? new LDIV() : super.ldiv());
	}

	public Instruction lmul() {
		return (filter.isInstrumentedClass(ci) ? new LMUL() : super.lmul());
	}

	public Instruction lneg() {
		return (filter.isInstrumentedClass(ci) ? new LNEG() : super.lneg());
	}

	public Instruction lor() {
		return (filter.isInstrumentedClass(ci) ? new LOR() : super.lor());
	}

	public Instruction lrem() {
		return (filter.isInstrumentedClass(ci) ? new LREM() : super.lrem());
	}

	public Instruction lshl() {
		return (filter.isInstrumentedClass(ci) ? new LSHL() : super.lshl());
	}

	public Instruction lshr() {
		return (filter.isInstrumentedClass(ci) ? new LSHR() : super.lshr());
	}

	public Instruction lsub() {
		return (filter.isInstrumentedClass(ci) ? new LSUB() : super.lsub());
	}

	public Instruction lushr() {
		return (filter.isInstrumentedClass(ci) ? new LUSHR() : super.lushr());
	}

	public Instruction lxor() {
		return (filter.isInstrumentedClass(ci) ? new LXOR() : super.lxor());
	}

	ClassInfo ci;
	InstructionFactoryFilter filter;

	@Override
	public void setClassInfoContext(ClassInfo ci) {
		this.ci = ci;
	}

	public static Abstraction abs;

	public AbstractInstructionFactory(Config conf) {

		System.out.println("Running Abstract PathFinder ...");

		filter = new InstructionFactoryFilter(null,
				new String[] {/* "java.*", */"javax.*" }, null, null);

		abs = new Signs(); // for now we only have one abstraction but in the
							// future this should be customized based on user
							// config
	}

}