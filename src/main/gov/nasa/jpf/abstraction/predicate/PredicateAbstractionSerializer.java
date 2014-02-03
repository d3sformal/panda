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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Collection;
import java.util.Comparator;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Fields;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.StaticElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.serialize.FilteringSerializer;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadList;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.util.FinalBitSet;
import gov.nasa.jpf.util.JPFLogger;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.abstraction.predicate.state.SymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.FlatSymbolTable;
import gov.nasa.jpf.abstraction.predicate.state.universe.Universe;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseStructuredValue;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseIdentifier;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseSlot;
import gov.nasa.jpf.abstraction.predicate.state.universe.StructuredValueSlot;
import gov.nasa.jpf.abstraction.predicate.state.universe.FieldName;
import gov.nasa.jpf.abstraction.predicate.state.universe.ElementIndex;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseObject;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseArray;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseClass;
import gov.nasa.jpf.abstraction.predicate.state.universe.StructuredValueIdentifier;
import gov.nasa.jpf.abstraction.predicate.state.universe.Reference;
import gov.nasa.jpf.abstraction.predicate.state.universe.LocalVariable;

/**
 * a serializer that uses Abstract values stored in attributes 
 * to obtain the values to hash. 
 */
public class PredicateAbstractionSerializer extends FilteringSerializer {

	static JPFLogger logger = JPF.getLogger("gov.nasa.jpf.abstraction.PredicateAbstractionSerializer");
    private int depth = 0;
    private PredicateAbstraction pabs;
    private Universe universe;
    private Map<StructuredValueIdentifier, Integer> canonical = new HashMap<StructuredValueIdentifier, Integer>();

	public PredicateAbstractionSerializer(Config conf) {
	}

    private Integer canonicalId (StructuredValueIdentifier value) {
        return canonical.get(value);
    }

    private int getValueType(Reference ref) {
        return ref.getElementInfo().getClassInfo().getName().hashCode();
    }

    private SortedSet<StructuredValueIdentifier> sortStructuredValues(Set<StructuredValueIdentifier> values) {
        throw new RuntimeException("IMPLEMENT");
    }

    protected void serializeHeap() {    
    	Set<StructuredValueIdentifier> sorted = sortStructuredValues(collectReachableHeap());
	    int i = 0;
	
    	canonical.clear();
	
        // define canonical ids for all heap objects
	    for (StructuredValueIdentifier value : sorted) {
	        canonical.put(value, i);
    	    ++i;
	    }

        for (StructuredValueIdentifier value : sorted) {
            serializeStructuredValue(value);
        }
    }

    protected void serializeSlot(UniverseSlot slot) {
        if (slot instanceof StructuredValueSlot) {
            StructuredValueSlot svs = (StructuredValueSlot) slot;

            buf.add(svs.getPossibleStructuredValues().size());

            for (StructuredValueIdentifier p : sortStructuredValues(svs.getPossibleStructuredValues())) {
                buf.add(canonicalId(p));
            }
        }
    }

    protected void serializeStructuredValue(StructuredValueIdentifier id) {
        UniverseStructuredValue value = universe.get(id);

        if (value instanceof UniverseArray) {
            UniverseArray a = (UniverseArray) value;
            buf.add(UniverseArray.class.hashCode());
            buf.add(a.getLength());

            for (ElementIndex index : new TreeSet<ElementIndex>(a.getElements().keySet())) {
                serializeSlot(a.getElement(index));
            }
        } else if (value instanceof UniverseObject) {
            UniverseObject o = (UniverseObject) value;
            buf.add(UniverseObject.class.hashCode());
            buf.add(o.getFields().size());

            for (FieldName field : new TreeSet<FieldName>(o.getFields().keySet())) {
            	serializeSlot(o.getField(field));
            }
        } else if (value instanceof UniverseClass) {
            UniverseClass c = (UniverseClass) value;
            buf.add(UniverseClass.class.hashCode());
            buf.add(c.getClassName().getClassName().hashCode());

            for (FieldName field : new TreeSet<FieldName>(c.getFields().keySet())) {
            	serializeSlot(c.getField(field));
            }
        }
    }

    protected Set<StructuredValueIdentifier> collectReachableHeap() {
        throw new RuntimeException("MISSING IMPLEMENTATION"); //return current universe.keySet()
    }

    @Override
    protected int[] computeStoringData() {
        buf.clear();

        pabs = (PredicateAbstraction) GlobalAbstraction.getInstance().get();
        universe = pabs.getSymbolTable().getUniverse();

        serializeHeap();
        serializeStackFrames();
        serializeThreadStates();

        return buf.toArray();
    }

    @Override
    public void processReference(int objRef) {
        VM vm = VM.getVM();
        ThreadInfo ti = vm.getCurrentThread();
        ElementInfo ei = ti.getElementInfo(objRef);

        buf.add(canonicalId(new Reference(ei, ti)));
    }

    @Override
    protected int getSerializedReferenceValue(ElementInfo ei){
        VM vm = VM.getVM();
        ThreadInfo ti = vm.getCurrentThread();

        return canonicalId(new Reference(ei, ti));
    }

    @Override
	protected void serializeStackFrames(ThreadInfo ti){
        depth = 0;

        super.serializeStackFrames(ti);
	}

    @Override
	protected void serializeFrame(StackFrame frame){
		buf.add(frame.getMethodInfo().getGlobalId());

        FlatSymbolTable currentScope = pabs.getSymbolTable().get(depth);

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

        // sort predicates
        Set<Predicate> order = new TreeSet<Predicate>(new Comparator<Predicate>() {
            public int compare(Predicate p1, Predicate p2) {
                int h1 = p1.hashCode();
                int h2 = p2.hashCode();

                return h1 - h2;
            }
        });

        order.addAll(pabs.getPredicateValuation().getPredicates(depth));

        // store all predicate valuations in the current scope in a predefined order
        for (Predicate p : order) {
            buf.add(p.hashCode());
            buf.add(pabs.getPredicateValuation().get(depth).get(p).ordinal());
        }

        // store all local variables of a reference type
        // use canonical ids of referred objects
        for (Root local : currentScope.getLocalVariables()) {
            LocalVariable v = currentScope.getLocal(local);

            if (v instanceof StructuredValueSlot) {
                StructuredValueSlot svs = (StructuredValueSlot)v;
                Set<StructuredValueIdentifier> possibilities = svs.getPossibleStructuredValues();

                buf.add(possibilities.size());

                Set<StructuredValueIdentifier> possibilitiesOrder = sortStructuredValues(possibilities);

                for (StructuredValueIdentifier p : possibilitiesOrder) {
                    buf.add(getValueType((Reference) p));
                    buf.add(canonicalId(p));
                }
            }
        }

        ++depth;
	}

}
