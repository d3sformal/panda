package gov.nasa.jpf.abstraction.util;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.PandaConfig;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.state.universe.Associative;
import gov.nasa.jpf.abstraction.state.universe.ClassName;
import gov.nasa.jpf.abstraction.state.universe.ElementIndex;
import gov.nasa.jpf.abstraction.state.universe.FieldName;
import gov.nasa.jpf.abstraction.state.universe.Indexed;
import gov.nasa.jpf.abstraction.state.universe.Reference;
import gov.nasa.jpf.abstraction.state.universe.StructuredValueIdentifier;
import gov.nasa.jpf.abstraction.state.universe.Universe;
import gov.nasa.jpf.abstraction.state.universe.UniverseArray;
import gov.nasa.jpf.abstraction.state.universe.UniverseClass;
import gov.nasa.jpf.abstraction.state.universe.UniverseIdentifier;
import gov.nasa.jpf.abstraction.state.universe.UniverseNull;
import gov.nasa.jpf.abstraction.state.universe.UniverseObject;
import gov.nasa.jpf.abstraction.util.RunDetector;

/**
 * Prints the current abstract heap after each instruction in the target program
 */
public class UniverseMonitor extends ListenerAdapter {

    @Override
    public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {
        if (RunDetector.isRunning()) {
            if (PandaConfig.getInstance().monitorEntireUniverse()) {
                inspectAll(PredicateAbstraction.getInstance());
            } else {
                inspectLocal(PredicateAbstraction.getInstance());
            }
        }
    }

    private void inspectAll(Abstraction abs) {
        System.out.println("--UNIVERSE--");

        PredicateAbstraction predicate = (PredicateAbstraction) abs;
        Universe universe = predicate.getSymbolTable().getUniverse();
        Set<StructuredValueIdentifier> values = universe.getStructuredValues();

        inspect(abs, universe, values);

        System.out.println("--------------");
        System.out.flush();
    }

    private void inspectLocal(Abstraction abs) {
        System.out.println("--UNIVERSE--");

        PredicateAbstraction predicate = (PredicateAbstraction) abs;
        Universe universe = predicate.getSymbolTable().getUniverse();
        Set<StructuredValueIdentifier> values = new HashSet<StructuredValueIdentifier>();
        Set<UniverseIdentifier> reachable = new HashSet<UniverseIdentifier>();

        ThreadInfo ti = ThreadInfo.getCurrentThread();
        StackFrame sf = ti.getTopFrame();

        if (sf != null) {
            for (int i = 0; i < sf.getLocalVariableCount(); ++i) {
                LocalVarInfo var = sf.getLocalVarInfo(i);

                if (var != null) {
                    values.add(new Reference(ti.getElementInfo(sf.getLocalVariable(var.getSlotIndex()))));
                }
            }

            reachable = universe.computeReachable(values);

            for (UniverseIdentifier id : reachable) {
                if (id instanceof StructuredValueIdentifier) {
                    StructuredValueIdentifier sid = (StructuredValueIdentifier) id;

                    values.add(sid);
                }
            }

            inspect(abs, universe, values);
        }

        System.out.println("--------------");
        System.out.flush();
    }

    private void inspect(Abstraction abs, Universe universe, Set<StructuredValueIdentifier> values) {
        Set<StructuredValueIdentifier> ordered = new TreeSet<StructuredValueIdentifier>(new Comparator<StructuredValueIdentifier>() {
            @Override
            public int compare(StructuredValueIdentifier o1, StructuredValueIdentifier o2) {
                if (o1 instanceof ClassName && o2 instanceof ClassName) {
                    ClassName r1 = (ClassName) o1;
                    ClassName r2 = (ClassName) o2;

                    return r1.getClassName().compareTo(r2.getClassName());
                }

                if (o1 instanceof ClassName) return -1;
                if (o2 instanceof ClassName) return +1;

                Reference r1 = (Reference) o1;
                Reference r2 = (Reference) o2;

                return r1.getReferenceNumber().compareTo(r2.getReferenceNumber());
            }
        });

        ordered.addAll(values);

        for (StructuredValueIdentifier v : ordered) {
            if (universe.get(v) instanceof Associative) {
                Associative a = (Associative) universe.get(v);

                // nothing printed for "null"
                if (v instanceof Reference) {
                    if ( ! (a instanceof UniverseNull) ) {
                        System.out.println(v + " @ " + ((Reference) v).getElementInfo().getClassInfo().getName());
                    }
                } else {
                    System.out.println(v);
                }

                for (FieldName f : a.getFields().keySet()) {
                    System.out.println("\t" + f.getName() + ": " + a.getField(f).getPossibleValues());
                }
            } else if (universe.get(v) instanceof Indexed) {
                Indexed i = (Indexed) universe.get(v);

                System.out.println(v + " @ " + ((Reference) v).getElementInfo().getClassInfo().getName());

                for (ElementIndex j : i.getElements().keySet()) {
                    System.out.println("\t" + j.getIndex() + ": " + i.getElement(j).getPossibleValues());
                }
            }
        }
    }
}
