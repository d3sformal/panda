/*
 * Copyright (C) 2015, Charles University in Prague.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.nasa.jpf.abstraction.util;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.bytecode.StaticFieldInstruction;
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
import gov.nasa.jpf.abstraction.state.universe.StructuredValueSlot;
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
    public void vmInitialized(VM vm) {
        if (PandaConfig.getInstance().monitorEntireUniverse()) {
            inspectAll(PredicateAbstraction.getInstance());
        } else {
            inspectLocal(PredicateAbstraction.getInstance());
        }
    }

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
        PredicateAbstraction predicate = (PredicateAbstraction) abs;
        Universe universe = predicate.getSymbolTable().getUniverse();
        Set<StructuredValueIdentifier> values = universe.getStructuredValues();

        System.out.println("--UNIVERSE " + universe.hashCode() +  "--");

        inspect(abs, universe, values);

        System.out.println("--------------");
        System.out.flush();
    }

    private void inspectLocal(Abstraction abs) {
        PredicateAbstraction predicate = (PredicateAbstraction) abs;
        Universe universe = predicate.getSymbolTable().getUniverse();

        System.out.println("--UNIVERSE " + universe.hashCode() +  "--");

        Set<UniverseIdentifier> reachable = new HashSet<UniverseIdentifier>();
        Set<StructuredValueIdentifier> values = new HashSet<StructuredValueIdentifier>();

        ThreadInfo ti = ThreadInfo.getCurrentThread();
        StackFrame sf = ti.getTopFrame();

        if (sf != null) {
            for (Instruction i : sf.getMethodInfo().getInstructions()) {
                // Monitor those statics that are referred to from the current method and are already in Universe
                // Warning: directly querying the class info of the instruction would cause the class to be loaded and put into universe (which we do not want happening as the VM would not load the class otherwise)
                if (i instanceof StaticFieldInstruction) {
                    StaticFieldInstruction sfi = (StaticFieldInstruction) i;
                    String className = sfi.getClassName();

                    for (StructuredValueIdentifier id : universe.getStructuredValues()) {
                        if (id instanceof ClassName) {
                            ClassName cls = (ClassName) id;

                            if (cls.getClassName().equals(className)) {
                                reachable.add(id);
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < sf.getLocalVariableCount(); ++i) {
                LocalVarInfo var = sf.getLocalVarInfo(i);

                if (var != null) {
                    reachable.add(new Reference(ti.getElementInfo(sf.getLocalVariable(var.getSlotIndex()))));
                }
            }

            reachable = universe.computeReachable(reachable);

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
        boolean printPrimitive = false;

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
                    if (printPrimitive || a.getField(f) instanceof StructuredValueSlot) {
                        System.out.println("\t" + f.getName() + ": " + a.getField(f).getPossibleValues());
                    }
                }
            } else if (universe.get(v) instanceof Indexed) {
                Indexed i = (Indexed) universe.get(v);

                System.out.println(v + " @ " + ((Reference) v).getElementInfo().getClassInfo().getName());

                for (ElementIndex j : i.getElements().keySet()) {
                    if (printPrimitive || i.getElement(j) instanceof StructuredValueSlot) {
                        System.out.println("\t" + j.getIndex() + ": " + i.getElement(j).getPossibleValues());
                    }
                }
            }
        }
    }
}
