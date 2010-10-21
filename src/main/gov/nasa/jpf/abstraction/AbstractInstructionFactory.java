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
import gov.nasa.jpf.JPFException;
import gov.nasa.jpf.jvm.DefaultInstructionFactory;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.util.InstructionFactoryFilter;


public class AbstractInstructionFactory extends DefaultInstructionFactory {
	 static Class<? extends Instruction>[] insnClass;

	  static {
	    insnClass = createInsnClassArray(260);

//	    insnClass[ALOAD_0]         = gov.nasa.jpf.abstraction.bytecode.ALOAD.class;
//	    insnClass[ALOAD_1]         = gov.nasa.jpf.abstraction.bytecode.ALOAD.class;
//	    insnClass[ALOAD_2]         = gov.nasa.jpf.abstraction.bytecode.ALOAD.class;
//	    insnClass[ALOAD_3]         = gov.nasa.jpf.abstraction.bytecode.ALOAD.class;
	    insnClass[IADD] = gov.nasa.jpf.abstraction.bytecode.IADD.class;
//	    insnClass[IAND] = gov.nasa.jpf.abstraction.bytecode.IAND.class;
//	    insnClass[IINC] = gov.nasa.jpf.abstraction.bytecode.IINC.class;
	    insnClass[ISUB] = gov.nasa.jpf.abstraction.bytecode.ISUB.class;
//	    insnClass[IMUL] = gov.nasa.jpf.abstraction.bytecode.IMUL.class;
//	    insnClass[INEG] = gov.nasa.jpf.abstraction.bytecode.INEG.class;
	    insnClass[IFLE] = gov.nasa.jpf.abstraction.bytecode.IFLE.class;
	    insnClass[IFLT] = gov.nasa.jpf.abstraction.bytecode.IFLT.class;
	    insnClass[IFGE] = gov.nasa.jpf.abstraction.bytecode.IFGE.class;
	    insnClass[IFGT] = gov.nasa.jpf.abstraction.bytecode.IFGT.class;
//	    insnClass[IFEQ] = gov.nasa.jpf.abstraction.bytecode.IFEQ.class;
//	    insnClass[IFNE] = gov.nasa.jpf.abstraction.bytecode.IFNE.class;
//	    insnClass[INVOKESTATIC] = gov.nasa.jpf.abstraction.bytecode.INVOKESTATIC.class;
//	    insnClass[INVOKEVIRTUAL] = gov.nasa.jpf.abstraction.bytecode.INVOKEVIRTUAL.class;
//	    insnClass[IF_ICMPGE] = gov.nasa.jpf.abstraction.bytecode.IF_ICMPGE.class;
//	    insnClass[IF_ICMPGT] = gov.nasa.jpf.abstraction.bytecode.IF_ICMPGT.class;
//	    insnClass[IF_ICMPLE] = gov.nasa.jpf.abstraction.bytecode.IF_ICMPLE.class;
//	    insnClass[IF_ICMPLT] = gov.nasa.jpf.abstraction.bytecode.IF_ICMPLT.class;
//	    insnClass[IDIV] = gov.nasa.jpf.abstraction.bytecode.IDIV.class;
//	    insnClass[ISHL] = gov.nasa.jpf.abstraction.bytecode.ISHL.class;
//	    insnClass[ISHR] = gov.nasa.jpf.abstraction.bytecode.ISHR.class;
//	    insnClass[IUSHR] = gov.nasa.jpf.abstraction.bytecode.IUSHR.class;
//	    insnClass[IXOR] = gov.nasa.jpf.abstraction.bytecode.IXOR.class;
//	    insnClass[IOR] = gov.nasa.jpf.abstraction.bytecode.IOR.class;
//	    insnClass[IREM] = gov.nasa.jpf.abstraction.bytecode.IREM.class;
//	    insnClass[IF_ICMPEQ] = gov.nasa.jpf.abstraction.bytecode.IF_ICMPEQ.class;
//	    insnClass[IF_ICMPNE] = gov.nasa.jpf.abstraction.bytecode.IF_ICMPNE.class;
//	    insnClass[INVOKESPECIAL] = gov.nasa.jpf.abstraction.bytecode.INVOKESPECIAL.class;
//	    insnClass[FADD] = gov.nasa.jpf.abstraction.bytecode.FADD.class;
//	    insnClass[FDIV] = gov.nasa.jpf.abstraction.bytecode.FDIV.class;
//	    insnClass[FMUL] = gov.nasa.jpf.abstraction.bytecode.FMUL.class;
//	    insnClass[FNEG] = gov.nasa.jpf.abstraction.bytecode.FNEG.class;
//	    insnClass[FREM] = gov.nasa.jpf.abstraction.bytecode.FREM.class;
//	    insnClass[FSUB] = gov.nasa.jpf.abstraction.bytecode.FSUB.class;
//	    insnClass[FCMPG] = gov.nasa.jpf.abstraction.bytecode.FCMPG.class;
//	    insnClass[FCMPL] = gov.nasa.jpf.abstraction.bytecode.FCMPL.class;
//	    insnClass[DADD] = gov.nasa.jpf.abstraction.bytecode.DADD.class;
//	    insnClass[DCMPG] = gov.nasa.jpf.abstraction.bytecode.DCMPG.class;
//	    insnClass[DCMPL] = gov.nasa.jpf.abstraction.bytecode.DCMPL.class;
//	    insnClass[DDIV] = gov.nasa.jpf.abstraction.bytecode.DDIV.class;
//	    insnClass[DMUL] = gov.nasa.jpf.abstraction.bytecode.DMUL.class;
//	    insnClass[DNEG] = gov.nasa.jpf.abstraction.bytecode.DNEG.class;
//	    insnClass[DREM] = gov.nasa.jpf.abstraction.bytecode.DREM.class;
//	    insnClass[DSUB] = gov.nasa.jpf.abstraction.bytecode.DSUB.class;
//	    insnClass[LADD] = gov.nasa.jpf.abstraction.bytecode.LADD.class;
//	    insnClass[LAND] = gov.nasa.jpf.abstraction.bytecode.LAND.class;
//	    insnClass[LCMP] = gov.nasa.jpf.abstraction.bytecode.LCMP.class;
//	    insnClass[LDIV] = gov.nasa.jpf.abstraction.bytecode.LDIV.class;
//	    insnClass[LMUL] = gov.nasa.jpf.abstraction.bytecode.LMUL.class;
//	    insnClass[LNEG] = gov.nasa.jpf.abstraction.bytecode.LNEG.class;
//	    insnClass[LOR] = gov.nasa.jpf.abstraction.bytecode.LOR.class;
//	    insnClass[LREM] = gov.nasa.jpf.abstraction.bytecode.LREM.class;
//	    insnClass[LSHL] = gov.nasa.jpf.abstraction.bytecode.LSHL.class;
//	    insnClass[LSHR] = gov.nasa.jpf.abstraction.bytecode.LSHR.class;
//	    insnClass[LSUB] = gov.nasa.jpf.abstraction.bytecode.LSUB.class;
//	    insnClass[LUSHR] = gov.nasa.jpf.abstraction.bytecode.LUSHR.class;
//	    insnClass[LXOR] = gov.nasa.jpf.abstraction.bytecode.LXOR.class;
//		insnClass[I2D] = gov.nasa.jpf.abstraction.bytecode.I2D.class;
//		insnClass[D2I] = gov.nasa.jpf.abstraction.bytecode.D2I.class;
//		insnClass[D2L] = gov.nasa.jpf.abstraction.bytecode.D2L.class;
//		insnClass[I2F] = gov.nasa.jpf.abstraction.bytecode.I2F.class;
//		insnClass[L2D] = gov.nasa.jpf.abstraction.bytecode.L2D.class;
//		insnClass[L2F] = gov.nasa.jpf.abstraction.bytecode.L2F.class;
//		insnClass[F2L] = gov.nasa.jpf.abstraction.bytecode.F2L.class;
//		insnClass[F2I] = gov.nasa.jpf.abstraction.bytecode.F2I.class;
//		insnClass[LOOKUPSWITCH] = gov.nasa.jpf.abstraction.bytecode.LOOKUPSWITCH.class;
//		insnClass[TABLESWITCH] = gov.nasa.jpf.abstraction.bytecode.TABLESWITCH.class;
//		insnClass[D2F] = gov.nasa.jpf.abstraction.bytecode.D2F.class;
//		insnClass[F2D] = gov.nasa.jpf.abstraction.bytecode.F2D.class;
//		insnClass[I2B] = gov.nasa.jpf.abstraction.bytecode.I2B.class;
//		insnClass[I2C] = gov.nasa.jpf.abstraction.bytecode.I2C.class;
//		insnClass[I2S] = gov.nasa.jpf.abstraction.bytecode.I2S.class;
//		insnClass[I2L] = gov.nasa.jpf.abstraction.bytecode.I2L.class;
//		insnClass[L2I] = gov.nasa.jpf.abstraction.bytecode.IADD.class;
//		insnClass[GETFIELD] = gov.nasa.jpf.abstraction.bytecode.GETFIELD.class;
//		insnClass[GETSTATIC] = gov.nasa.jpf.abstraction.bytecode.GETSTATIC.class;
//		insnClass[NEW] = gov.nasa.jpf.abstraction.bytecode.NEW.class;
//		insnClass[IFNULL] = gov.nasa.jpf.abstraction.bytecode.IFNULL.class;
//		insnClass[IFNONNULL] = gov.nasa.jpf.abstraction.bytecode.IFNONNULL.class;

		// IMPORTANT: if any new bytecodes are added make sure to update the
		// length of the array which is at the top of the function
	  };

	static public String[] dp;
	static public String[] string_dp;

	/*
	 * This is intended to serve as a catchall debug flag.
	 * If there's some debug printing/outputing, conditionally print using
	 * this flag.
	 */
	static public boolean debugMode;




	InstructionFactoryFilter filter = new InstructionFactoryFilter(null, new String[] {"java.*", "javax.*" },
			null, null);


	public  AbstractInstructionFactory (Config conf){
		System.out.println("Running Abstract PathFinder ...");
	}

	public Instruction create(ClassInfo ciMth, int opCode) {

	    if (opCode < insnClass.length){
	      Class<?> cls = insnClass[opCode];
	      if (cls != null && filter.isInstrumentedClass(ciMth)) {
	        try {
	          Instruction insn = (Instruction) cls.newInstance();
	          return insn;

	        } catch (Throwable e) {
	          throw new JPFException("creation of abstract Instruction object for opCode "
	                  + opCode + " failed: " + e);
	        }
	      }
	    }

	    // use default instruction classes
	    return super.create(ciMth, opCode);
	  }
}
