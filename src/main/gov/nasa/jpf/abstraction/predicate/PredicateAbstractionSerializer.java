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
package gov.nasa.jpf.abstraction.predicate;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Comparator;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Fields;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.serialize.FilteringSerializer;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.util.FinalBitSet;
import gov.nasa.jpf.util.JPFLogger;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;

/**
 * a serializer that uses Abstract values stored in attributes 
 * to obtain the values to hash. 
 */
public class PredicateAbstractionSerializer extends FilteringSerializer {

	static JPFLogger logger = JPF.getLogger("gov.nasa.jpf.abstraction.PredicateAbstractionSerializer");

	public PredicateAbstractionSerializer(Config conf) {
	}

    static int depth = 0;

    @Override
    protected int[] computeStoringData() {
        buf.clear();
        //heap = ks.getHeap();
        //initReferenceQueue();

        //--- serialize all live objects and loaded classes
        serializeStackFrames();
        //serializeClassLoaders();

        //--- now serialize the thread states (which might refer to live objects)
        // we do this last because threads contain some internal references
        // (locked objects etc) that should NOT set the canonical reference serialization
        // values (if they are encountered before their first explicit heap reference)
        //serializeThreadStates();

        return buf.toArray();
    }

    @Override
	protected void serializeStackFrames(ThreadInfo ti){
        depth = 0;

		for (StackFrame frame = ti.getTopFrame(); frame != null; frame = frame.getPrevious()){
			serializeFrame(frame);
		}
	}

    @Override
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

        Abstraction abs = GlobalAbstraction.getInstance().get();

        if (abs instanceof PredicateAbstraction) {
            PredicateAbstraction pabs = (PredicateAbstraction) abs;

            Set<Predicate> order = new TreeSet<Predicate>(new Comparator<Predicate>() {
                public int compare(Predicate p1, Predicate p2) {
                    int h1 = p1.hashCode();
                    int h2 = p2.hashCode();

                    return h1 - h2;
                }
            });

            order.addAll(pabs.getPredicateValuation().getPredicates(depth));

            for (Predicate p : order) {
                buf.add(p.hashCode());
                buf.add(pabs.getPredicateValuation().get(p).toInteger());
            }
        }

        ++depth;
	}

}
