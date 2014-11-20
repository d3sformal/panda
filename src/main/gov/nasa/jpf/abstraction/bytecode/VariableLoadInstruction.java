package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.common.access.Root;

public interface VariableLoadInstruction {
    public Root getVariable();
}
