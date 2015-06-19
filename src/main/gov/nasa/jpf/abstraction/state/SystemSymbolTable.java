package gov.nasa.jpf.abstraction.state;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.jvm.bytecode.DLOAD;
import gov.nasa.jpf.jvm.bytecode.DSTORE;
import gov.nasa.jpf.jvm.bytecode.FLOAD;
import gov.nasa.jpf.jvm.bytecode.FSTORE;
import gov.nasa.jpf.jvm.bytecode.ILOAD;
import gov.nasa.jpf.jvm.bytecode.ISTORE;
import gov.nasa.jpf.jvm.bytecode.LLOAD;
import gov.nasa.jpf.jvm.bytecode.LSTORE;
import gov.nasa.jpf.jvm.bytecode.LocalVariableInstruction;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.KernelState;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.StaticElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadList;
import gov.nasa.jpf.vm.Types;
import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ExpressionUtil;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ReturnValue;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultReturnValue;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.concrete.AnonymousExpression;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.state.MethodFrameSymbolTable;
import gov.nasa.jpf.abstraction.state.SystemSymbolTable;
import gov.nasa.jpf.abstraction.state.universe.ClassName;
import gov.nasa.jpf.abstraction.state.universe.LocalVariable;
import gov.nasa.jpf.abstraction.state.universe.Reference;
import gov.nasa.jpf.abstraction.state.universe.StructuredValueIdentifier;
import gov.nasa.jpf.abstraction.state.universe.Universe;
import gov.nasa.jpf.abstraction.state.universe.UniverseIdentifier;
import gov.nasa.jpf.abstraction.util.RunDetector;

/**
 * Symbol table aware of method call scope changes
 */
public class SystemSymbolTable implements SymbolTable, Scoped {
    private Universe universe = new Universe();

    /**
     * Stacks of scopes (pushed by invoke, poped by return) separately for all threads
     */
    private Map<Integer, SymbolTableStack> scopes = new HashMap<Integer, SymbolTableStack>();

    private PredicateAbstraction abstraction;
    private Integer currentThreadID = 0;

    public SystemSymbolTable(PredicateAbstraction abstraction) {
        this.abstraction = abstraction;
    }

    /**
     * Create a scope for a given method
     */
    @Override
    public MethodFrameSymbolTable createDefaultScope(ThreadInfo threadInfo, MethodInfo method) {
        MethodFrameSymbolTable ret = new MethodFrameSymbolTable(scopes.get(currentThreadID).top());

        /**
         * Register new local variables
         */
        StackFrame sf = threadInfo.getModifiableTopFrame();

        /**
         * Handle main(String[] args)
         *
         * it is necessary to initialize the arguments
         */
        VM vm = threadInfo.getVM();
        String target = vm.getConfig().getTarget();
        String entry = vm.getConfig().getTargetEntry();

        if (entry == null) {
            entry = "main([Ljava/lang/String;)V";
        }

        if (method.getFullName().equals(target + "." + entry)) {
            sf.setFrameAttr(null);

            Expression[] arguments = new Expression[method.getNumberOfStackArguments()];

            for (int i = 0; i < method.getNumberOfStackArguments(); ++i) {
                ElementInfo ei = threadInfo.getElementInfo(sf.getLocalVariable(i));

                if (ei.isArray()) {
                    int length = ei.arrayLength();

                    arguments[i] = AnonymousArray.create(new Reference(ei), Constant.create(length));
                } else {
                    arguments[i] = AnonymousObject.create(new Reference(ei));
                }

                sf.setLocalAttr(i, arguments[i]);

                ret.addObject((AnonymousObject) arguments[i]);
            }

            sf.setFrameAttr(arguments);
        }

        return ret;
    }

    @Override
    public Set<AccessExpression> processPrimitiveStore(Expression from, AccessExpression to) {
        return scopes.get(currentThreadID).top().processPrimitiveStore(from, to);
    }

    @Override
    public Set<AccessExpression> processObjectStore(Expression from, AccessExpression to) {
        return scopes.get(currentThreadID).top().processObjectStore(from, to);
    }

    /**
     * At a method call it is necessary to change the scope correspondingly
     */
    @Override
    public void processMethodCall(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
        MethodInfo method = after.getMethodInfo();

        MethodFrameSymbolTable originalScope = scopes.get(currentThreadID).top();
        MethodFrameSymbolTable newScope = createDefaultScope(threadInfo, method);

        scopes.get(currentThreadID).push(method.getFullName(), newScope);

        if (!method.isNative() && !method.isMJI()) {
            /**
             * Assign values to the formal parameters according to the actual parameters
             */
            byte[] argTypes = new byte[method.getNumberOfStackArguments()];

            int i = 0;

            if (!method.isStatic()) {
                argTypes[i++] = Types.T_REFERENCE;
            }

            for (byte argType : method.getArgumentTypes()) {
                argTypes[i++] = argType;
            }

            for (int argIndex = 0, slotIndex = 0; argIndex < method.getNumberOfStackArguments(); ++argIndex) {
                Expression expr = ExpressionUtil.getExpression(after.getLocalAttr(slotIndex));

                LocalVarInfo arg = after.getLocalVarInfo(slotIndex);
                String name = arg == null ? null : arg.getName();

                if (argTypes[argIndex] == Types.T_REFERENCE || argTypes[argIndex] == Types.T_ARRAY) {
                    newScope.addStructuredLocalVariable(DefaultRoot.create(name, slotIndex));
                    newScope.processObjectStore(expr, originalScope, DefaultRoot.create(name, slotIndex));
                } else {
                    newScope.addPrimitiveLocalVariable(DefaultRoot.create(name, slotIndex));
                    newScope.processPrimitiveStore(expr, originalScope, DefaultRoot.create(name, slotIndex));
                }

                switch (argTypes[argIndex]) {
                    case Types.T_LONG:
                    case Types.T_DOUBLE:
                        slotIndex += 2;
                        break;

                    default:
                        slotIndex += 1;
                        break;
                }
            }
        }
    }

    @Override
    public void processMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
        ReturnValue calleeReturnValue = DefaultReturnValue.create();
        ReturnValue callerReturnValue = DefaultReturnValue.create(threadInfo.getPC());

        Expression returnExpression = ExpressionUtil.getExpression(after.getOperandAttr());
        //after.setOperandAttr(callerReturnValue); // This is performed by the predicate valuation after it uses the original expression

        /**
         * Register the return value
         *
         * top()  ... before return
         * top(1) ... after return (previous stack frame)
         *
         * 1) create a new return container in callee
         * 2) write the actual return value into the container (still in callee)
         * 3) create a specific unique return container in the caller
         * 4) write callee return into caller return
         */
        if (before.getMethodInfo().isReferenceReturnType()) {
            scopes.get(currentThreadID).top().addStructuredReturn(calleeReturnValue);
            scopes.get(currentThreadID).top().processObjectStore(returnExpression, calleeReturnValue);
            scopes.get(currentThreadID).top(1).addStructuredReturn(callerReturnValue);
            scopes.get(currentThreadID).top(1).processObjectStore(calleeReturnValue, scopes.get(currentThreadID).top(), callerReturnValue);
        } else {
            scopes.get(currentThreadID).top().addPrimitiveReturn(calleeReturnValue);
            scopes.get(currentThreadID).top().processPrimitiveStore(returnExpression, calleeReturnValue);
            scopes.get(currentThreadID).top(1).addPrimitiveReturn(callerReturnValue);
            scopes.get(currentThreadID).top(1).processPrimitiveStore(calleeReturnValue, scopes.get(currentThreadID).top(), callerReturnValue);
        }

        /**
         * Handle the rest as if there were no return values
         */
        processVoidMethodReturn(threadInfo, before, after);
    }

    @Override
    public void processVoidMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
        /**
         * Drop callee scope
         */
        MethodFrameSymbolTable top = scopes.get(currentThreadID).top();
        scopes.get(currentThreadID).pop();
        MethodFrameSymbolTable bot = scopes.get(currentThreadID).top();

        bot.inheritClassesFrom(top);
    }

    @Override
    public void restore(Map<Integer, ? extends Scopes> scopes) {
        this.scopes.clear();

        if (!scopes.isEmpty()) {
            SymbolTableStack stack = (SymbolTableStack) scopes.get(scopes.keySet().iterator().next());

            if (stack.count() > 0) {
                this.universe = stack.top().getUniverse().clone();
            }
        }

        for (Integer threadId : scopes.keySet()) {
            Scopes threadScopes = scopes.get(threadId);

            if (threadScopes instanceof SymbolTableStack) {
                SymbolTableStack threadSymbolTableScopes = (SymbolTableStack) threadScopes;

                this.scopes.put(threadId, threadSymbolTableScopes.clone());

                for (MethodFrameSymbolTable scope : this.scopes.get(threadId)) {
                    scope.setUniverse(this.universe);
                }
            } else {
                throw new RuntimeException("Invalid scopes type being restored!");
            }
        }
    }

    @Override
    public Map<Integer, SymbolTableStack> memorize() {
        Map<Integer, SymbolTableStack> scopesClone = new HashMap<Integer, SymbolTableStack>();

        Universe universeClone = universe.clone();

        for (Integer threadId : scopes.keySet()) {
            scopesClone.put(threadId, scopes.get(threadId).clone());

            for (MethodFrameSymbolTable scope : scopesClone.get(threadId)) {
                scope.setUniverse(universeClone);
            }
        }

        return scopesClone;
    }

    @Override
    public String toString() {
        return scopes.get(currentThreadID).count() > 0 ? scopes.get(currentThreadID).top().toString() : "";
    }

    @Override
    public boolean isArray(AccessExpression path) {
        return scopes.get(currentThreadID).top().isArray(path);
    }

    @Override
    public boolean isObject(AccessExpression path) {
        return scopes.get(currentThreadID).top().isObject(path);
    }

    @Override
    public boolean isPrimitive(AccessExpression path) {
        return scopes.get(currentThreadID).top().isPrimitive(path);
    }

    @Override
    public Universe getUniverse() {
        return universe;
    }

    @Override
    public int count() {
        return scopes.get(currentThreadID).count() > 0 ? scopes.get(currentThreadID).top().count() : 0;
    }

    @Override
    public int depth() {
        return depth(currentThreadID);
    }

    public int depth(int threadID) {
        return scopes.get(threadID).count();
    }

    @Override
    public MethodFrameSymbolTable get(int depth) {
        return get(currentThreadID, depth);
    }

    public MethodFrameSymbolTable get(int threadID, int depth) {
        return scopes.get(threadID).top(depth);
    }

    @Override
    public void addThread(ThreadInfo threadInfo) {
        universe.add(threadInfo.getThreadObject(), threadInfo);

        SymbolTableStack threadStack = new SymbolTableStack();

        MethodFrameSymbolTable bottomScope;

        if (!scopes.isEmpty()) {
            bottomScope = new MethodFrameSymbolTable(scopes.get(currentThreadID).top());
        } else {
            bottomScope = new MethodFrameSymbolTable(universe, abstraction);
        }

        threadStack.push("-- Dummy stop scope --", bottomScope);

        scopes.put(threadInfo.getId(), threadStack);
    }

    @Override
    public void scheduleThread(ThreadInfo threadInfo) {
        scheduleThread(threadInfo.getId());
    }

    @Override
    public void scheduleThread(int threadID) {
        currentThreadID = threadID;
    }

    @Override
    public void print() {
        scopes.get(currentThreadID).print();
    }

    public void collectGarbage(VM vm, ThreadInfo ti) {
        KernelState ks = vm.getKernelState();

        ThreadList threads = ks.getThreadList();

        // Add roots
        Set<UniverseIdentifier> liveRoots = new HashSet<UniverseIdentifier>();

        // All classes
        for (ClassLoaderInfo cl : ks.classLoaders) {
            if(cl.isAlive()) {
                for (StaticElementInfo sei : cl.getStatics().liveStatics()) {
                    liveRoots.add(new ClassName(sei));
                    liveRoots.add(new Reference(ti.getElementInfo(sei.getClassObjectRef())));
                }
            }
        }

        for (StructuredValueIdentifier candidate : universe.getStructuredValues()) {
            if (candidate instanceof ClassName) {
                liveRoots.add(candidate);
            }
        }

        // All thread objects, lock objects, local variables
        for (ThreadInfo thread : threads) {
            liveRoots.add(new Reference(thread.getThreadObject()));
            liveRoots.add(new Reference(thread.getLockObject()));

            for (ElementInfo lock : thread.getLockedObjects()) {
                liveRoots.add(new Reference(lock));
            }

            if (thread.isAlive()) {
                for (int depth = 0; depth < depth(thread.getId()); ++depth) {
                    MethodFrameSymbolTable currentSymbolScope = get(thread.getId(), depth);

                    for (Root varName : currentSymbolScope.getLocalVariables()) {
                        LocalVariable var = currentSymbolScope.getLocal(varName);

                        liveRoots.addAll(var.getPossibleValues());
                    }

                    for (ReturnValue retName : currentSymbolScope.getReturnValues()) {
                        LocalVariable ret = currentSymbolScope.getReturnValue(retName);

                        liveRoots.addAll(ret.getPossibleValues());
                    }
                }

                // Anonymous objects on stacks are also live
                for (StackFrame frame = thread.getTopFrame(); frame != null; frame = frame.getPrevious()) {
                    for (int i = 0; i < frame.getTopPos() - frame.getLocalVariableCount(); ++i) {
                        Expression expr = ExpressionUtil.getExpression(frame.getOperandAttr(i));

                        if (expr instanceof AnonymousExpression) {
                            AnonymousExpression ae = (AnonymousExpression) expr;

                            liveRoots.add(ae.getReference());
                        }
                    }
                }
            }
        }

        universe.retainLiveValuesOnly(liveRoots);
    }
}
