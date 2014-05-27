package gov.nasa.jpf.abstraction.predicate.util;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ReturnInstruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.VM;

/**
 * Prints the current state of the symbolic stack after each instruction in the target program
 */
public class StackExpressionMonitor extends ListenerAdapter {

    @Override
    public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {
        //if (RunDetector.isRunning()) {
            StackFrame sf = curTh.getTopFrame();
            if (sf != null) {
                inspect(sf);
            }
        //}
    }

    public static void inspect(StackFrame sf) {
        System.out.println("--EXPRESSIONS --");
        for (int i = 0; i <= (sf.getTopPos() - sf.getLocalVariableCount()); i++)
        {
            Attribute attr = Attribute.getAttribute(sf.getOperandAttr(i));
            if ((attr != null) && (Attribute.getExpression(attr) != null)) System.out.println("["+i+"]: " + Attribute.getExpression(attr).toString());
            else System.out.println("["+i+"]: null");
        }
        System.out.println("--------------");
    }
}

