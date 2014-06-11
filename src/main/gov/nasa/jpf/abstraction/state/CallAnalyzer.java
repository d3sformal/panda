package gov.nasa.jpf.abstraction.state;

import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.Types;

public abstract class CallAnalyzer {
    private interface ArgumentDiscriminant {
        public boolean getDecision(byte type);
    }

    protected static void getArgumentSlotUsage(MethodInfo method, boolean[] slotInUse) {
        for (int i = 0; i < method.getNumberOfStackArguments(); ++i) {
            slotInUse[i] = false;
        }

        getArgumentSlotProperties(method, slotInUse, new ArgumentDiscriminant() {
            @Override
            public boolean getDecision(byte type) {
                return true;
            }
        });
    }

    protected static void getArgumentSlotType(MethodInfo method, boolean[] localVarIsPrimitive) {
        for (int i = 0; i < method.getNumberOfStackArguments(); ++i) {
            localVarIsPrimitive[i] = false;
        }

        getArgumentSlotProperties(method, localVarIsPrimitive, new ArgumentDiscriminant() {
            @Override
            public boolean getDecision(byte argType) {
                switch (argType) {
                    case Types.T_ARRAY:
                    case Types.T_REFERENCE:
                        return false;

                    default:
                        return true;
                }
            }
        });
    }

    private static void getArgumentSlotProperties(MethodInfo method, boolean[] properties, ArgumentDiscriminant discriminant) {
        int offset = 0;

        if (!method.isStatic()) {
            properties[offset] = discriminant.getDecision(Types.T_REFERENCE);

            ++offset;
        }

        for (byte argType : method.getArgumentTypes()) {
            properties[offset] = discriminant.getDecision(argType);

            switch (argType) {
                case Types.T_LONG:
                case Types.T_DOUBLE:
                    offset += 2;
                    break;

                default:
                    ++offset;
                    break;
            }
        }
    }
}
