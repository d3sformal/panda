package gov.nasa.jpf.abstraction.heuristic;

import gov.nasa.jpf.jvm.bytecode.BIPUSH;
import gov.nasa.jpf.jvm.bytecode.IfInstruction;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;

import gov.nasa.jpf.abstraction.bytecode.VariableLoadInstruction;
import gov.nasa.jpf.abstraction.common.BytecodeRange;
import gov.nasa.jpf.abstraction.common.BytecodeInterval;
import gov.nasa.jpf.abstraction.common.BytecodeIntervals;
import gov.nasa.jpf.abstraction.common.BytecodeUnlimitedRange;
import gov.nasa.jpf.abstraction.common.Comparison;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.state.SystemPredicateValuation;

public class WidenControlVariableDomainRefinementHeuristic extends RefinementHeuristic {
    public WidenControlVariableDomainRefinementHeuristic(SystemPredicateValuation predVal) {
        super(predVal);
    }

    @Override
    protected boolean refineAtomic(Predicate interpolant, MethodInfo m, int fromPC, int toPC) {
        boolean refined = false;

        refined |= super.refineAtomic(interpolant, m, fromPC, toPC);

        for (Predicate p : predVal.getPredicates()) {
            if (interpolant.equals(p)) {
                BytecodeRange scope = p.getScope();

                if (scope instanceof BytecodeUnlimitedRange) {
                    fromPC = 0;
                    toPC = m.getLastInsn().getPosition();
                } else if (scope instanceof BytecodeInterval) {
                    fromPC = ((BytecodeInterval)scope).getMin();
                    toPC = ((BytecodeInterval)scope).getMax();
                } else if (scope instanceof BytecodeIntervals) {
                    fromPC = ((BytecodeIntervals)scope).getMin();
                    toPC = ((BytecodeIntervals)scope).getMax();
                }
            }
        }

        if (interpolant instanceof Comparison) {
            Comparison cmp = (Comparison) interpolant;

            Root a = null;
            Constant c = null;

            if (cmp.a instanceof Root && cmp.b instanceof Constant) {
                a = (Root) cmp.a;
                c = (Constant) cmp.b;
            }

            if (cmp.b instanceof Root && cmp.a instanceof Constant) {
                a = (Root) cmp.b;
                c = (Constant) cmp.a;
            }

            if (a != null && c != null) {
                Instruction i0 = m.getInstructionAt(fromPC);

                while (i0 != null && i0.getPosition() <= toPC) {
                    if (i0 instanceof IfInstruction) {
                        boolean usesA = false;
                        Integer bc = null;

                        Instruction i1 = i0.getPrev();
                        Instruction i2 = i1.getPrev();

                        usesA |= isLoadOf(i1, a);
                        usesA |= isLoadOf(i2, a);

                        bc = cmpValue(i1, bc);
                        bc = cmpValue(i2, bc);

                        if (usesA && bc != null) {
                            int low = c.value.intValue();
                            int high = bc;

                            if (low > high) {
                                int tmp = low;
                                low = high;
                                high = tmp;
                            }

                            for (int k = low; k <= high; ++k) {
                                refined |= super.refineAtomic(LessThan.create(a, Constant.create(k)), m, fromPC, toPC);
                                refined |= super.refineAtomic(LessThan.create(Constant.create(k), a), m, fromPC, toPC);
                            }
                        }
                    }

                    i0 = i0.getNext();
                }
            }
        }

        return refined;
    }

    private boolean isLoadOf(Instruction i, Root v) {
        if (i instanceof VariableLoadInstruction) {
            VariableLoadInstruction vl = (VariableLoadInstruction) i;

            if (v.equals(vl.getVariable())) {
                return true;
            }
        }

        return false;
    }

    private Integer cmpValue(Instruction i, Integer fallback) {
        if (i instanceof BIPUSH) {
            return ((BIPUSH)i).getValue();
        }

        return fallback;
    }
}
