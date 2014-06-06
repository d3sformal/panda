package gov.nasa.jpf.abstraction.inspection;

import gov.nasa.jpf.abstraction.ExecuteInstructionHandler;
import gov.nasa.jpf.abstraction.bytecode.AnonymousExpressionTracker;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.PredicatesFactory;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.universe.UniverseIdentifier;
import gov.nasa.jpf.abstraction.util.ExpressionUtil;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import java.util.HashSet;
import java.util.Set;

public class AliasingDumper extends ExecuteInstructionHandler {
    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn) {
        StackFrame sf = curTh.getModifiableTopFrame();

        AnonymousExpressionTracker.notifyPopped(ExpressionUtil.getExpression(sf.getOperandAttr()), 1);

        ElementInfo ei = curTh.getElementInfo(sf.pop());

        AccessExpression ae = PredicatesFactory.createAccessExpressionFromString(ei.asString());

        Set<UniverseIdentifier> ids = new HashSet<UniverseIdentifier>();

        PredicateAbstraction.getInstance().getSymbolTable().get(0).lookupValues(ae, ids);

        System.out.println("[DEBUG] " + ae.toString(Notation.DOT_NOTATION) + ": " + ids + " in " + sf.getMethodInfo().getFullName());
    }
}
