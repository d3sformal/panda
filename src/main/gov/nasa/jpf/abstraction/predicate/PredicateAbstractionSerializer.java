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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Collection;
import java.util.Comparator;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
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

	public PredicateAbstractionSerializer(Config conf) {
	}

    protected Set<StructuredValue> sortStructuredValues(Set<StructuredValue> values) {
    	Set<StructuredValue> order = new TreeSet<StructuredValue>();
        /*
    	Set<StructuredValue> order = new TreeSet<StructuredValue>(new Comparator<StructuredValue>() {
        	public int compare(StructuredValue v1, StructuredValue v2) {
                if (v1 instanceof ClassStatics && v2 instanceof HeapObject  ) return -1;
                if (v1 instanceof HeapObject   && v2 instanceof ClassStatics) return +1;
                
                if (v1 instanceof HeapObject && v2 instanceof HeapObject) {
    	        	int r1 = ((HeapObjectReference)v1.getReference()).getReference();
                	int r2 = ((HeapObjectReference)v2.getReference()).getReference();

                    return r1 - r2;
                }

                if (v1 instanceof ClassStatics && v2 instanceof ClassStatics) {
    	        	String c1 = ((ClassStaticsReference)v1.getReference()).getClassName();
                	String c2 = ((ClassStaticsReference)v2.getReference()).getClassName();

                    return c1.compareTo(c2);
                }

            	return 0;
            }
        });
        */

        order.addAll(values);

        return order;
    }

    protected void serializeHeap(Set<StructuredValue> heap) {
        //buf.add(0x00000000);
        //buf.add(heap.size());
        //buf.add(0x00000000);
        
        //System.err.println("HEAP (R): " + heap);
        //System.err.println("HEAP (A): " + universe.getStructuredValues());

        for (StructuredValue value : sortStructuredValues(heap)) {
            serializeStructuredValue(value);
        }
    }

    protected void serializeSlot(Slot slot) {
        if (slot instanceof StructuredValueSlot) {
            StructuredValueSlot svs = (StructuredValueSlot) slot;

            buf.add(svs.getPossibleHeapValues().size());

            for (StructuredValue p : sortStructuredValues(svs.getPossibleHeapValues())) {
                int r = ((HeapObjectReference)p.getReference()).getReference();

                buf.add(r);
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
            if (ti.isAlive()) {
                for (StackFrame frame = ti.getTopFrame(); frame != null; frame = frame.getPrevious()) {
                    for (int i = 0; i < frame.getLocalVariableCount(); ++i) {
                        LocalVarInfo var = frame.getLocalVars()[i];

                        if (frame.isLocalVariableRef(var.getSlotIndex())) {
                            int ref = frame.getLocalVariable(var.getSlotIndex());

                            if (universe.contains(ref)) {
                                StructuredValue object = universe.get(ref);

                                heap.add(object);
                            /*
                            } else {
                                System.err.println("MISSING EXPECTED OBJECTS `" + ref + "` IN:\n" + universe.toString());
                                System.exit(0);
                            */
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
                    /*
                    } else {
                        System.err.println("MISSING EXPECTED OBJECT `" + ref +  "` IN:\n" + universe.toString());
                        System.exit(0);
                    */
                    }
                }
            }
        }

        // Construct reachable closure

        Set<StructuredValue> closed = new HashSet<StructuredValue>();
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
                            nextGen.add(child);
                        }
                    }
                }
            }

            closed.addAll(open);
            open = nextGen;
            heap.addAll(open);
        }

        return heap;
    }

    @Override
    protected int[] computeStoringData() {
        //System.out.println("-------------------------------------------- SERIALIZING --------------------------------------------"); 
        buf.clear();

        pabs = (PredicateAbstraction) GlobalAbstraction.getInstance().get();
        universe = pabs.getSymbolTable().getUniverse();

        serializeHeap(collectReachableHeap());

        //buf.add(0x00000000);
        //buf.add(0xDEADBEEF);
        //buf.add(0x00000000);

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
        //System.out.println("-------------------------------------------- SERIALIZED  --------------------------------------------"); 

        //System.out.print("Serialization: ");
        //for (int i = 0; i < buf.size() && i < 16; ++i) {
        //    System.out.printf("%08X", buf.get(i));
        //}
        //System.out.println("...");
        //System.out.println();

        return buf.toArray();
    }

    @Override
    public void processReference(int objRef) {
    }

    @Override
	protected void serializeStackFrames(ThreadInfo ti){
        depth = 0;

        super.serializeStackFrames(ti);
	}

    @Override
	protected void serializeFrame(StackFrame frame){
        //System.out.println("--- FRAME ---");
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
            //System.out.print("Predicate " + p + " " + p.hashCode() + " = ");
            buf.add(p.hashCode());
            buf.add(pabs.getPredicateValuation().get(depth).get(p).ordinal());
            //System.out.println(pabs.getPredicateValuation().get(depth).get(p).ordinal());
        }

        for (Root local : currentScope.getLocalVariables()) {
            LocalVariable v = currentScope.getLocal(local);

            if (v.getSlot() instanceof StructuredValueSlot) {
                //System.out.println("Reference type: " + v);
                StructuredValueSlot svs = (StructuredValueSlot)v.getSlot();
                Set<StructuredValue> possibilities = svs.getPossibleHeapValues();

                //System.out.print("\t" + possibilities.size() + ": ");
                buf.add(possibilities.size());

                Set<StructuredValue> possibilitiesOrder = sortStructuredValues(possibilities);

                for (StructuredValue p : possibilitiesOrder) {
                    int r = ((HeapObjectReference)p.getReference()).getReference(); 

                	//System.out.print("\t" + r);
                    buf.add(r);
                }
               	//System.out.println();
            } else {
                //System.out.println("Primitive type: " + v);
            }
        }

        ++depth;
	}

}
