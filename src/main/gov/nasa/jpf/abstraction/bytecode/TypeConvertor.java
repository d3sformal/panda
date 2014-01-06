package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

interface TypeConvertor {
    Instruction execute(ThreadInfo ti);
    Instruction executeConcrete(ThreadInfo ti);
    Instruction getNext(ThreadInfo ti);
}
