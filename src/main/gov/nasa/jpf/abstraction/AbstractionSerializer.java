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

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.jvm.FieldInfo;
import gov.nasa.jpf.jvm.Fields;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.serialize.FilteringSerializer;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.util.FinalBitSet;
import gov.nasa.jpf.util.JPFLogger;

/**
 * a serializer that uses Abstract values stored in attributes 
 * to obtain the values to hash. 
 */
public class AbstractionSerializer extends FilteringSerializer {

  static JPFLogger logger = JPF.getLogger("gov.nasa.jpf.abstraction.AbstractionSerializer");

  public AbstractionSerializer(Config conf) {

  }
 

  protected void processField(Fields fields, int[] slotValues, FieldInfo fi, FinalBitSet filtered) {
    int off = fi.getStorageOffset();
    if (!filtered.get(off)) {
      Abstraction a = fields.getFieldAttr(fi.getFieldIndex(), Abstraction.class);
      if (a != null) {
        if (fi.is1SlotField()) {
          // abstraction cannot exist for references -> we can ignore them here

          // storing abstract representation of the concrete value (as in DynamicAbstractionSerializer) is not correct
            // the concrete value is always zero if we use the abstraction for the field 
	
          // this should work for one-slot fields (int, float) and also for two-slot fields (long, double)
          buf.add(a.get_key());
        }
      } else { // no abstraction, fall back to concrete values
        if (fi.is1SlotField()) {
          if (fi.isReference()) {
            int ref = slotValues[off];
            buf.add(ref);
            processReference(ref);

          } else {
            buf.add(slotValues[off]);
          }

        } else { // double or long
          buf.add(slotValues[off]);
          buf.add(slotValues[off + 1]);
        }
      }
    }
  }

  protected void processNamedFields(ClassInfo ci, Fields fields) {
    FinalBitSet filtered = getInstanceFilterMask(ci);
    int nFields = ci.getNumberOfInstanceFields();
    int[] slotValues = fields.asFieldSlots(); // for non-attributed fields

    for (int i = 0; i < nFields; i++) {
      FieldInfo fi = ci.getInstanceField(i);
      processField(fields, slotValues, fi, filtered);
    }
  } 
 
  // we must also store the abstract values that are in attributes for locals and method params (stack frame)
  protected void serializeFrame(StackFrame frame){
    buf.add(frame.getMethodInfo().getGlobalId());

    // there can be (rare) cases where a listener sets a null nextPc in
    // a frame that is still on the stack
    Instruction pc = frame.getPC();
    if (pc != null){
      buf.add(pc.getInstructionIndex());
    } else {
      buf.add(-1);
    }

    int len = frame.getTopPos()+1;
    buf.add(len);

    int[] slots = frame.getSlots();

    for (int i = 0; i < len; i++) {
      // store either the abstract value (attribute) or the concrete value 
      // we should not exceed "len"

      // we must give the offset as method parameter value
	  // we need attribute for the current slot
      Abstraction a = frame.getOperandAttr(frame.getTopPos()-i, Abstraction.class);

      if (a != null) buf.add(a.get_key());
      else buf.add(slots[i]);
    }

    frame.visitReferenceSlots(this);
  }
 
}
