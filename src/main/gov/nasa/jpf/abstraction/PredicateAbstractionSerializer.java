package gov.nasa.jpf.abstraction;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.state.MethodFramePredicateValuation;
import gov.nasa.jpf.abstraction.state.MethodFrameSymbolTable;
import gov.nasa.jpf.abstraction.state.SymbolTable;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.state.universe.ElementIndex;
import gov.nasa.jpf.abstraction.state.universe.FieldName;
import gov.nasa.jpf.abstraction.state.universe.LocalVariable;
import gov.nasa.jpf.abstraction.state.universe.PrimitiveValue;
import gov.nasa.jpf.abstraction.state.universe.Reference;
import gov.nasa.jpf.abstraction.state.universe.StructuredValue;
import gov.nasa.jpf.abstraction.state.universe.StructuredValueIdentifier;
import gov.nasa.jpf.abstraction.state.universe.StructuredValueSlot;
import gov.nasa.jpf.abstraction.state.universe.Universe;
import gov.nasa.jpf.abstraction.state.universe.UniverseArray;
import gov.nasa.jpf.abstraction.state.universe.UniverseClass;
import gov.nasa.jpf.abstraction.state.universe.UniverseIdentifier;
import gov.nasa.jpf.abstraction.state.universe.UniverseObject;
import gov.nasa.jpf.abstraction.state.universe.UniverseSlot;
import gov.nasa.jpf.abstraction.state.universe.UniverseValue;
import gov.nasa.jpf.util.FinalBitSet;
import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Fields;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.StaticElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadList;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.serialize.FilteringSerializer;

/**
 * a serializer that uses Abstract values stored in attributes
 * to obtain the values to hash.
 */
public class PredicateAbstractionSerializer extends FilteringSerializer {

    static JPFLogger logger = JPF.getLogger("gov.nasa.jpf.abstraction.PredicateAbstractionSerializer");
    protected int depth = 0;
    protected int thread = 0;
    protected PredicateAbstraction pabs;
    protected Universe universe;
    protected Map<StructuredValueIdentifier, Integer> canonical = new HashMap<StructuredValueIdentifier, Integer>();

    public PredicateAbstractionSerializer(Config conf) {
    }

    protected int canonicalId (StructuredValueIdentifier value) {
        if (canonical.containsKey(value)) {
            return canonical.get(value);
        }

        throw new RuntimeException("Could not canonicalize: " + value);
    }

    protected SortedSet<StructuredValueIdentifier> sortStructuredValues(Set<StructuredValueIdentifier> values) {
        SortedSet<StructuredValueIdentifier> sorted = new TreeSet<StructuredValueIdentifier>();

        sorted.addAll(values);

        return sorted;
    }

    protected void serializeHeapValue(StructuredValueIdentifier value) {
        serializeStructuredValue(value);
    }

    protected void serializeHeap() {
        Set<StructuredValueIdentifier> sorted = sortStructuredValues(universe.getStructuredValues());
        int i = 0;

        canonical.clear();

        // define canonical ids for all heap objects
        for (StructuredValueIdentifier value : sorted) {
            canonical.put(value, i);
            ++i;
        }

        for (StructuredValueIdentifier value : sorted) {
            serializeHeapValue(value);
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
        StructuredValue value = universe.get(id);

        if (value instanceof UniverseArray) {
            UniverseArray a = (UniverseArray) value;
            buf.add(UniverseArray.class.hashCode());
            buf.add(a.getReference().getElementInfo().getClassInfo().getName().hashCode());
            buf.add(a.getLength());

            for (ElementIndex index : new TreeSet<ElementIndex>(a.getElements().keySet())) {
                serializeSlot(a.getElement(index));
            }
        } else if (value instanceof UniverseObject) {
            UniverseObject o = (UniverseObject) value;
            buf.add(UniverseObject.class.hashCode());
            buf.add(o.getReference().getElementInfo().getClassInfo().getName().hashCode());
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

    @Override
    protected int[] computeStoringData() {
        buf.clear();

        pabs = PredicateAbstraction.getInstance();
        universe = pabs.getSymbolTable().getUniverse();

        serializeHeap();
        serializeThreadStates();
        serializeStackFrames();

        return buf.toArray();
    }

    @Override
    public void processReference(int objRef) {
        VM vm = VM.getVM();
        ThreadInfo ti = vm.getCurrentThread();
        ElementInfo ei = ti.getElementInfo(objRef);

        Reference ref = new Reference(ei);
        int id = canonicalId(ref);

        buf.add(id);
    }

    @Override
    protected int getSerializedReferenceValue(ElementInfo ei){
        return canonicalId(new Reference(ei));
    }

    @Override
    protected void serializeStackFrames(ThreadInfo ti){
        // Set the context of subsequent serializations of individual frames
        depth = 0;
        thread = ti.getId();

        super.serializeStackFrames(ti);
    }

    protected void serializeLocalVariable(Root localVariable, Set<StructuredValueIdentifier> values) {
        buf.add(values.size());

        for (StructuredValueIdentifier p : values) {
            buf.add(canonicalId(p));
        }
    }

    protected void serializeLocalVariables(MethodFrameSymbolTable currentScope) {
        for (Root local : currentScope.getLocalVariables()) {
            LocalVariable v = currentScope.getLocal(local);

            if (v instanceof StructuredValueSlot) {
                StructuredValueSlot svs = (StructuredValueSlot)v;
                Set<StructuredValueIdentifier> possibilities = svs.getPossibleStructuredValues();
                Set<StructuredValueIdentifier> possibilitiesOrder = sortStructuredValues(possibilities);

                serializeLocalVariable(local, possibilitiesOrder);
            }
        }
    }

    protected void serializePredicate(Predicate p, TruthValue value) {
        buf.add(p.hashCode());
        buf.add(value.ordinal());
    }

    protected void serializePredicates(MethodFramePredicateValuation currentScope) {
        // sort predicates
        Set<Predicate> order = new TreeSet<Predicate>(new Comparator<Predicate>() {
            public int compare(Predicate p1, Predicate p2) {
                int h1 = p1.hashCode();
                int h2 = p2.hashCode();

                return Integer.valueOf(h1).compareTo(Integer.valueOf(h2));
            }
        });

        order.addAll(currentScope.getPredicates());

        // store all predicate valuations in the current scope in a predefined order
        for (Predicate p : order) {
            serializePredicate(p, currentScope.get(p));
        }
    }

    @Override
    protected void serializeFrame(StackFrame frame){
        if (frame.isSynthetic()) return;

        buf.add(frame.getMethodInfo().getGlobalId());

        // thread is the actual thread being serialized (set in serializeStackFrames)
        // depth is the current depth in the whole stack of the thread being currently processed
        MethodFrameSymbolTable currentSymbolScope = pabs.getSymbolTable().get(thread, depth);
        MethodFramePredicateValuation currentPredicateScope = pabs.getPredicateValuation().get(thread, depth);

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

        // store all local variables of a reference type
        // use canonical ids of referred objects
        serializeLocalVariables(currentSymbolScope);

        // store all predicate valuations
        serializePredicates(currentPredicateScope);

        ++depth;
    }

    @Override
    protected void serializeThreadState(ThreadInfo ti)
    {
        buf.add(ti.getId());

        // we serialize the same integer value for the following thread states: RUNNING, UNBLOCKED (they are semantically equivalent)
        if (ti.isRunnable()) buf.add(ThreadInfo.State.RUNNING.ordinal());
        else buf.add(ti.getState().ordinal());

        buf.add(ti.getStackDepth());

        // see the corresponding method in the superclass for additional comments

        // the object we are waiting for
        ElementInfo eiLock = ti.getLockObject();
        if (eiLock != null) buf.add(getSerializedReferenceValue(eiLock));

        // the objects we hold locks for
        serializeLockedObjects(ti.getLockedObjects());
    }
}
