package gov.nasa.jpf.abstraction;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.vm.DebugStateSerializer;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.state.MethodFramePredicateValuation;
import gov.nasa.jpf.abstraction.state.MethodFrameSymbolTable;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.state.universe.ClassName;
import gov.nasa.jpf.abstraction.state.universe.LocalVariable;
import gov.nasa.jpf.abstraction.state.universe.Reference;
import gov.nasa.jpf.abstraction.state.universe.StructuredValue;
import gov.nasa.jpf.abstraction.state.universe.StructuredValueIdentifier;
import gov.nasa.jpf.abstraction.state.universe.StructuredValueSlot;
import gov.nasa.jpf.abstraction.state.universe.UniverseSlot;
import gov.nasa.jpf.abstraction.state.universe.UniverseSlotKey;

public class DebugPredicateAbstractionSerializer extends PredicateAbstractionSerializer implements DebugStateSerializer {
    PrintWriter out = new PrintWriter(System.out);

    public DebugPredicateAbstractionSerializer(Config config) {
        super(config);
    }

    @Override
    public void setOutputStream(OutputStream out) {
        this.out = new PrintWriter(out);
    }

    protected void imitateSerializeHeapValue(StructuredValueIdentifier value) {
        out.print("\t");

        if (value instanceof ClassName) {
            out.print(value);
        } else if (value instanceof Reference) {
            Reference ref = (Reference) value;

            out.print(canonicalId(ref));

            if (ref.getElementInfo() == null) {
                out.print(" NULL");
            } else {
                out.print(" (" + ref.getElementInfo().getClassInfo().getName() + ")");
            }
        }

        out.println();

        StructuredValue object = universe.get(value);

        for (UniverseSlotKey key : new TreeSet<UniverseSlotKey>(object.getSlots().keySet())) {
            UniverseSlot slot = object.getSlot(key);

            if (slot instanceof StructuredValueSlot) {
                out.print("\t\t" + key + ": ");

                boolean first = true;

                for (StructuredValueIdentifier id : ((StructuredValueSlot) slot).getPossibleStructuredValues()) {
                    if (!first) {
                        out.print(" | ");
                    }

                    out.print(canonicalId(id));

                    first = false;
                }
                out.println();
            }
        }
    }

    @Override
    protected void serializeHeap() {
        super.serializeHeap();

        Set<Reference> references = new TreeSet<Reference>();
        Set<ClassName> classes = new TreeSet<ClassName>(new Comparator<ClassName>() {
            @Override
            public int compare(ClassName c1, ClassName c2) {
                String n1 = c1.getClassName();
                String n2 = c2.getClassName();

                String[] pkg1 = n1.split("\\.");
                String[] pkg2 = n2.split("\\.");

                // builtin (no package)
                if (pkg1.length == 1 && pkg2.length > 1) return -1;
                if (pkg2.length == 1 && pkg1.length > 1) return +1;

                // arrays after elements
                if (n1.startsWith("[") && !n2.startsWith("[")) return +1;
                if (n2.startsWith("[") && !n1.startsWith("[")) return -1;

                // package ordering
                for (int i = 0; i < pkg1.length - 1 && i < pkg2.length - 1; ++i) {
                    int pkgComparison = pkg1[i].compareTo(pkg2[i]);

                    if (pkgComparison != 0) return pkgComparison;
                }

                // subpackages
                int lengthComparison = Integer.valueOf(pkg1.length).compareTo(Integer.valueOf(pkg2.length));

                if (lengthComparison != 0) return lengthComparison;

                // classes
                return pkg1[pkg1.length - 1].compareTo(pkg2[pkg2.length - 1]);
            }
        });

        for (StructuredValueIdentifier id : sortStructuredValues(universe.getStructuredValues())) {
            if (id instanceof Reference) {
                references.add((Reference) id);
            } else if (id instanceof ClassName) {
                classes.add((ClassName) id);
            } else {
                throw new RuntimeException("Cannot dump a value of an unknown type.");
            }
        }

        out.println("======== Classes ========");

        for (ClassName cls : classes) {
            imitateSerializeHeapValue(cls);
        }


        out.println();
        out.println("======== Heap ========");

        for (Reference ref : references) {
            imitateSerializeHeapValue(ref);
        }
    }

    @Override
    protected void serializeLocalVariable(Root localVariable, Set<StructuredValueIdentifier> values) {
        out.print("\t\t\t" + localVariable + ": ");

        boolean first = true;

        for (StructuredValueIdentifier id : values) {
            if (!first) {
                out.print(" | ");
            }

            out.print(canonicalId(id));

            first = false;
        }
        out.println();

        super.serializeLocalVariable(localVariable, values);
    }

    @Override
    protected void serializeLocalVariables(MethodFrameSymbolTable currentScope) {
        out.println("\t\t" + "local variables:");

        super.serializeLocalVariables(currentScope);
    }

    private int predicateLength;
    private int predicatePadding = 4;

    @Override
    protected void serializePredicate(Predicate p, TruthValue value) {
        out.print("\t\t\t" + p.toString(Notation.DOT_NOTATION));

        int length = p.toString(Notation.DOT_NOTATION).length();

        for (int i = 0; i < predicateLength - length + predicatePadding; ++i) {
            out.print(" ");
        }

        out.println(value);

        super.serializePredicate(p, value);
    }

    @Override
    protected void serializePredicates(MethodFramePredicateValuation currentScope) {
        out.println("\t\t" + "predicates:");

        predicateLength = 0;

        for (Predicate p : currentScope.getPredicates()) {
            int length = p.toString(Notation.DOT_NOTATION).length();

            if (predicateLength < length) {
                predicateLength = length;
            }
        }

        super.serializePredicates(currentScope);
    }

    @Override
    protected void serializeFrame(StackFrame frame) {
        if ( ! frame.isSynthetic() ) {
            out.println("\t" + "frame [depth = " + depth + "]");

            Instruction pcInsn = frame.getPC();
            String pcSourceLine = (pcInsn.getSourceLine() != null) ? pcInsn.getSourceLine().trim() : "";
            out.println("\t\t" + "pc: " + frame.getMethodInfo().getFullName() + "[" + pcInsn.getPosition() + "]");
            out.println("\t\t\t" + "source line: " + pcSourceLine);
            out.println("\t\t\t" + "instruction: " + pcInsn.getMnemonic());
        }

        super.serializeFrame(frame);
    }

    @Override
    protected void serializeStackFrames(ThreadInfo threadInfo) {
        out.println();
        out.println("======== Thread ========");
        out.println("\t" + "id: " + threadInfo.getId());
        out.println("\t" + "state: " + threadInfo.getState());

        out.println("\t" + "waiting for lock: " + canonicalId(new Reference(threadInfo.getLockObject())));

        Set<Integer> lockedObjects = new TreeSet<Integer>();

        for (ElementInfo locked : threadInfo.getLockedObjects()) {
            lockedObjects.add(canonicalId(new Reference(locked)));
        }

        out.println("\t" + "locked objects: " + lockedObjects);

        super.serializeStackFrames(threadInfo);
    }

    @Override
    protected int[] computeStoringData() {
        int[] ret = super.computeStoringData();

        out.flush();

        return ret;
    }
}
