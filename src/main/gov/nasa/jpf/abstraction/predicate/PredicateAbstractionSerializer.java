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
import gov.nasa.jpf.abstraction.predicate.state.symbols.Universe;
import gov.nasa.jpf.abstraction.predicate.state.symbols.LocalVariable;
import gov.nasa.jpf.abstraction.predicate.state.symbols.StructuredValue;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Slot;
import gov.nasa.jpf.abstraction.predicate.state.symbols.StructuredValueSlot;
import gov.nasa.jpf.abstraction.predicate.state.symbols.UniverseIdentifier;
import gov.nasa.jpf.abstraction.predicate.state.symbols.HeapObject;
import gov.nasa.jpf.abstraction.predicate.state.symbols.HeapArray;
import gov.nasa.jpf.abstraction.predicate.state.symbols.HeapObjectReference;
import gov.nasa.jpf.abstraction.predicate.state.symbols.ClassStatics;
import gov.nasa.jpf.abstraction.predicate.state.symbols.ClassStaticsReference;

/**
 * a serializer that uses Abstract values stored in attributes 
 * to obtain the values to hash. 
 */
public class PredicateAbstractionSerializer extends FilteringSerializer {

	static JPFLogger logger = JPF.getLogger("gov.nasa.jpf.abstraction.PredicateAbstractionSerializer");
    private int depth = 0;
    private PredicateAbstraction pabs;
    private Universe universe;
    private Map<UniverseIdentifier, Integer> canonical = new HashMap<UniverseIdentifier, Integer>();

	public PredicateAbstractionSerializer(Config conf) {
	}

    private Integer canonicalId (StructuredValue value) {
        return canonical.get(value.getReference());
    }

    private int currentType (StructuredValue value) {
        return universe.get(value.getReference()).getElementInfo().getClassInfo().getName().hashCode();
    }

    protected Set<StructuredValue> sortStructuredValues(Set<StructuredValue> values) {
    	Set<StructuredValue> order = new TreeSet<StructuredValue>();

        order.addAll(values);

        return order;
    }

    protected void serializeHeap() {    
    	Set<StructuredValue> sorted = sortStructuredValues(collectReachableHeap());
	    int i = 0;
	
    	canonical.clear();
	
	    for (StructuredValue value : sorted) {
	        canonical.put(value.getReference(), i);
    	    ++i;
	    }

        for (StructuredValue value : sorted) {
            serializeStructuredValue(value);
        }
    }

    protected void serializeSlot(Slot slot) {
        if (slot instanceof StructuredValueSlot) {
            StructuredValueSlot svs = (StructuredValueSlot) slot;

            buf.add(svs.getPossibleHeapValues().size());

            for (StructuredValue p : sortStructuredValues(svs.getPossibleHeapValues())) {
                buf.add(canonicalId(p));
            }
        }
    }

    protected void serializeStructuredValue(StructuredValue value) {
        if (value instanceof HeapArray) {
            HeapArray a = (HeapArray) value;
            buf.add(HeapArray.class.hashCode());
            buf.add(a.getLength());

            for (Integer index : new TreeSet<Integer>(a.getElements().keySet())) {
                serializeSlot(a.getElement(index));
            }
        } else if (value instanceof HeapObject) {
            HeapObject o = (HeapObject) value;
            buf.add(HeapObject.class.hashCode());
            buf.add(o.getFields().size());

            for (String field : new TreeSet<String>(o.getFields().keySet())) {
            	serializeSlot(o.getField(field));
            }
        } else if (value instanceof ClassStatics) {
            ClassStatics s = (ClassStatics) value;
            buf.add(ClassStatics.class.hashCode());
            buf.add(((ClassStaticsReference)s.getReference()).getClassName().hashCode());

            for (String field : new TreeSet<String>(s.getFields().keySet())) {
            	serializeSlot(s.getField(field));
            }
        }
    }

    protected SortedSet<StructuredValue> collectReachableHeap() {
        TreeSet<StructuredValue> heap = new TreeSet<StructuredValue>();

        ThreadList tl = ks.getThreadList();
        // Collect all objects stored in live variable

        for (ThreadInfo ti : tl) {
            heap.add(universe.get(ti.getThreadObjectRef()));

            if (ti.isAlive()) {
                for (int depth = 0; depth < pabs.getSymbolTable().depth(); ++depth) {
                    FlatSymbolTable sym = pabs.getSymbolTable().get(depth);

                    for (Root lv : sym.getLocalVariables()) {
                        if (sym.getLocal(lv).getSlot() instanceof StructuredValueSlot) {
                            StructuredValueSlot svs = (StructuredValueSlot) sym.getLocal(lv).getSlot();

                            for (StructuredValue possibility : svs.getPossibleHeapValues()) {
                                if (universe.contains(possibility.getReference())) {
                                    StructuredValue object = universe.get(possibility.getReference());

                                    heap.add(object);
                                } else {
                                    throw new RuntimeException();
                                }
                            }
                        }
                    }
                }
            }
        }

        // Collect all statics (modelled within abstract heap as distinguished objects)

        for (ClassLoaderInfo cl : ks.classLoaders) {
            if(cl.isAlive()) {
                for (StaticElementInfo sei : cl.getStatics().liveStatics()) {
                    String ref = sei.getClassInfo().getName();

                    if (universe.contains(ref)) {
                        StructuredValue object = universe.get(ref);

                        heap.add(object);
                    } else {
                        throw new RuntimeException();
                    }
                }
            }
        }

        // Construct reachable closure

        Set<StructuredValue> open = new HashSet<StructuredValue>();

        open.addAll(heap);

        while (!open.isEmpty()) {
            Set<StructuredValue> nextGen = new HashSet<StructuredValue>();

            for (StructuredValue object : open) {
                Collection<Slot> slots = new HashSet<Slot>();

                if (object instanceof HeapObject) {
                    HeapObject ho = (HeapObject) object;

                    slots = ho.getFields().values();
                }

                if (object instanceof HeapArray) {
                    HeapArray ha = (HeapArray) object;

                    slots = ha.getElements().values();
                }

                if (object instanceof ClassStatics) {
                    ClassStatics cs = (ClassStatics) object;

                    slots = cs.getFields().values();
                }

                for (Slot slot : slots) {
                    if (slot instanceof StructuredValueSlot) {
                        StructuredValueSlot s = (StructuredValueSlot) slot;

                        for (StructuredValue child : s.getPossibleHeapValues()) {
                            if (!heap.contains(child)) {
                                nextGen.add(child);
                            } else {
                                throw new RuntimeException();
                            }
                        }
                    }
                }
            }

            heap.addAll(open);
            open.clear();
            open.addAll(nextGen);
        }

        return heap;
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
        buf.add(canonicalId(universe.get(objRef)));
    }

    @Override
    protected int getSerializedReferenceValue (ElementInfo ei){
        return canonicalId(universe.get(ei.getObjectRef()));
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
            buf.add(pabs.getPredicateValuation().get(depth).get(p).ordinal());
        }

        for (Root local : currentScope.getLocalVariables()) {
            LocalVariable v = currentScope.getLocal(local);

            if (v.getSlot() instanceof StructuredValueSlot) {
                StructuredValueSlot svs = (StructuredValueSlot)v.getSlot();
                Set<StructuredValue> possibilities = svs.getPossibleHeapValues();

                buf.add(possibilities.size());

                Set<StructuredValue> possibilitiesOrder = sortStructuredValues(possibilities);

                for (StructuredValue p : possibilitiesOrder) {
                    buf.add(currentType(p));
                    buf.add(canonicalId(p));
                }
            }
        }

        ++depth;
	}

}
