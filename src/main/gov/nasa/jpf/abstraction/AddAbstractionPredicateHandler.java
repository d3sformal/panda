package gov.nasa.jpf.abstraction;

import java.util.LinkedList;

import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.bytecode.AnonymousExpressionTracker;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.abstraction.common.MethodPredicateContext;
import gov.nasa.jpf.abstraction.common.ObjectPredicateContext;
import gov.nasa.jpf.abstraction.common.PredicatesFactory;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Predicates;
import gov.nasa.jpf.abstraction.common.PredicateContext;
import gov.nasa.jpf.abstraction.common.StaticPredicateContext;
import gov.nasa.jpf.abstraction.common.access.Method;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultMethod;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultPackageAndClass;

public abstract class AddAbstractionPredicateHandler extends ExecuteInstructionHandler {
    @Override
    public void executeInstruction(VM vm, ThreadInfo curTh, Instruction nextInsn) {
        StackFrame sf = curTh.getModifiableTopFrame();
        MethodInfo mi = sf.getMethodInfo();
        ClassInfo ci = sf.getClassInfo();

        AnonymousExpressionTracker.notifyPopped(ExpressionUtil.getExpression(sf.getOperandAttr()), 1);

        String pDef = curTh.getEnv().getStringObject(sf.pop());
        Predicate p = PredicatesFactory.createPredicateFromString(pDef);
        Method m = DefaultMethod.create(DefaultPackageAndClass.create(ci.getName()), mi.getName());

        addPredicate(p, m);
    }

    public static MethodPredicateContext getContext(Method m) {
        Predicates pSet = PredicateAbstraction.getInstance().getPredicateValuation().getPredicateSet();

        for (PredicateContext ctx : pSet.contexts) {
            if (ctx instanceof MethodPredicateContext) {
                MethodPredicateContext mCtx = (MethodPredicateContext) ctx;

                if (mCtx.getMethod().equals(m)) {
                    return mCtx;
                }
            }
        }

        MethodPredicateContext mCtx = new MethodPredicateContext(m, new LinkedList<Predicate>());

        pSet.contexts.add(mCtx);

        return mCtx;
    }

    public static ObjectPredicateContext getContext(PackageAndClass p) {
        Predicates pSet = PredicateAbstraction.getInstance().getPredicateValuation().getPredicateSet();

        for (PredicateContext ctx : pSet.contexts) {
            if (ctx instanceof ObjectPredicateContext) {
                ObjectPredicateContext oCtx = (ObjectPredicateContext) ctx;

                if (oCtx.getPackageAndClass().equals(p)) {
                    return oCtx;
                }
            }
        }

        ObjectPredicateContext oCtx = new ObjectPredicateContext(p, new LinkedList<Predicate>());

        pSet.contexts.add(oCtx);

        return oCtx;
    }

    public static StaticPredicateContext getContext() {
        Predicates pSet = PredicateAbstraction.getInstance().getPredicateValuation().getPredicateSet();

        for (PredicateContext ctx : pSet.contexts) {
            if (ctx instanceof StaticPredicateContext) {
                return (StaticPredicateContext) ctx;
            }
        }

        StaticPredicateContext ctx = new StaticPredicateContext(new LinkedList<Predicate>());

        pSet.contexts.add(ctx);

        return ctx;
    }

    public abstract void addPredicate(Predicate p, Method m);
}
