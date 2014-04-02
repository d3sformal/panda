package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ElementInfo;

import java.util.Set;
import java.util.HashSet;

import gov.nasa.jpf.abstraction.GlobalAbstraction;
import gov.nasa.jpf.abstraction.common.PredicatesFactory;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseIdentifier;

public class AliasingDumper extends ExecuteInstructionHandler {
    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn) {
        StackFrame sf = curTh.getModifiableTopFrame();
        ElementInfo ei = curTh.getElementInfo(sf.pop());

        AccessExpression ae = PredicatesFactory.createAccessExpressionFromString(ei.asString());

        Set<UniverseIdentifier> ids = new HashSet<UniverseIdentifier>();

        ((PredicateAbstraction) GlobalAbstraction.getInstance().get()).getSymbolTable().get(0).lookupValues(ae, ids);

        System.out.println("[DEBUG] " + ae.toString(Notation.DOT_NOTATION) + ": " + ids + " in " + sf.getMethodInfo().getFullName());
    }
}
