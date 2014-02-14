package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.impl.NonEmptyAttribute;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ReturnValue;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultReturnValue;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.predicate.state.universe.Universe;
import gov.nasa.jpf.abstraction.predicate.state.universe.Reference;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Types;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.Instruction;

import gov.nasa.jpf.jvm.bytecode.DLOAD;
import gov.nasa.jpf.jvm.bytecode.FLOAD;
import gov.nasa.jpf.jvm.bytecode.ILOAD;
import gov.nasa.jpf.jvm.bytecode.LLOAD;
import gov.nasa.jpf.jvm.bytecode.DSTORE;
import gov.nasa.jpf.jvm.bytecode.FSTORE;
import gov.nasa.jpf.jvm.bytecode.ISTORE;
import gov.nasa.jpf.jvm.bytecode.LSTORE;
import gov.nasa.jpf.jvm.bytecode.LocalVariableInstruction;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * Symbol table aware of method call scope changes
 */
public class ScopedSymbolTable extends CallAnalyzer implements SymbolTable, Scoped {
	/**
	 * Stack of scopes (pushed by invoke, poped by return)
	 */
    private Universe universe = new Universe();
	private Map<Integer, SymbolTableStack> scopes = new HashMap<Integer, SymbolTableStack>();
	private PredicateAbstraction abstraction;
    private int currentThread = 0;
	
	public ScopedSymbolTable(PredicateAbstraction abstraction) {
		this.abstraction = abstraction;
		
		// Scope for passing what has been statically initialised
		// without this all static initialisations return and remove their scope without writing it anywhere else
		SymbolTableStack mainThreadStack = new SymbolTableStack();
        mainThreadStack.push("-- Dummy stop scope --", new FlatSymbolTable(universe, abstraction));
        scopes.put(currentThread, mainThreadStack);
	}

	/**
	 * Create a scope for a given method
	 */
	@Override
	public FlatSymbolTable createDefaultScope(ThreadInfo threadInfo, MethodInfo method) {
		FlatSymbolTable ret = new FlatSymbolTable(scopes.get(currentThread).top());

		/**
		 * Register new local variables
		 */
        StackFrame sf = threadInfo.getTopFrame();

		/**
		 * Handle main(String[] args)
         *
         * it is necessary to initialize the arguments
		 */
		VM vm = threadInfo.getVM();
		String target = vm.getConfig().getTarget();
		
		if (method.getFullName().equals(target + ".main([Ljava/lang/String;)V")) {
			ElementInfo ei = threadInfo.getElementInfo(sf.getLocalVariable(0));
			int length = ei.arrayLength();
			
			LocalVarInfo args = method.getArgumentLocalVars()[0];
            Expression argsExpr = AnonymousArray.create(new Reference(ei), Constant.create(length));
            Attribute attr = new NonEmptyAttribute(null, argsExpr);

            sf.setLocalAttr(0, attr);
            method.addAttr(attr);

			ret.processObjectStore(argsExpr, DefaultRoot.create(args.getName(), 0));
		}
		
		return ret;
	}
	
	@Override
	public Set<AccessExpression> processPrimitiveStore(Expression from, AccessExpression to) {
		return scopes.get(currentThread).top().processPrimitiveStore(from, to);
	}
	
	@Override
	public Set<AccessExpression> processObjectStore(Expression from, AccessExpression to) {
		return scopes.get(currentThread).top().processObjectStore(from, to);
	}
	
	/**
	 * At a method call it is necessary to change the scope correspondingly
	 */
	@Override
	public void processMethodCall(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
		MethodInfo method = after.getMethodInfo();

		FlatSymbolTable originalScope = scopes.get(currentThread).top();
		FlatSymbolTable newScope = createDefaultScope(threadInfo, method);

		scopes.get(currentThread).push(method.getFullName(), newScope);

		/**
		 * Assign values to the formal parameters according to the actual parameters
		 */
        boolean[] slotInUse = new boolean[method.getNumberOfStackArguments()];
        boolean[] localVarIsPrimitive = new boolean[method.getNumberOfStackArguments()];

        getArgumentSlotUsage(method, slotInUse);
        getArgumentSlotType(method, localVarIsPrimitive);

        for (int slotIndex = 0; slotIndex < method.getNumberOfStackArguments(); ++slotIndex) {
            if (slotInUse[slotIndex]) {
    			Attribute attr = Attribute.ensureNotNull((Attribute) after.getSlotAttr(slotIndex));

                LocalVarInfo arg = after.getLocalVarInfo(slotIndex);
                String name = arg == null ? null : arg.getName();

                if (localVarIsPrimitive[slotIndex]) {
	    			newScope.addPrimitiveLocalVariable(DefaultRoot.create(name, slotIndex));
		    		newScope.processPrimitiveStore(attr.getExpression(), originalScope, DefaultRoot.create(name, slotIndex));
                } else {
			    	newScope.addStructuredLocalVariable(DefaultRoot.create(name, slotIndex));
    				newScope.processObjectStore(attr.getExpression(), originalScope, DefaultRoot.create(name, slotIndex));
                }
            }
        }
	}
	
	@Override
	public void processMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
		ReturnValue calleeReturnValue = DefaultReturnValue.create();
		ReturnValue callerReturnValue = DefaultReturnValue.create(threadInfo.getPC(), before.getMethodInfo().isReferenceReturnType());

		Attribute attr = Attribute.ensureNotNull((Attribute) after.getOperandAttr());
		Expression returnExpression = attr.getExpression();
		//after.setOperandAttr(new NonEmptyAttribute(null, callerReturnValue)); // This is performed by the predicate valuation after it uses the original expression

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
			scopes.get(currentThread).top().addStructuredReturn(calleeReturnValue);
			scopes.get(currentThread).top().processObjectStore(returnExpression, calleeReturnValue);
			scopes.get(currentThread).top(1).addStructuredReturn(callerReturnValue);
			scopes.get(currentThread).top(1).processObjectStore(calleeReturnValue, scopes.get(currentThread).top(), callerReturnValue);
		} else {
			scopes.get(currentThread).top().addPrimitiveReturn(calleeReturnValue);
			scopes.get(currentThread).top().processPrimitiveStore(returnExpression, calleeReturnValue);
			scopes.get(currentThread).top(1).addPrimitiveReturn(callerReturnValue);
			scopes.get(currentThread).top(1).processPrimitiveStore(calleeReturnValue, scopes.get(currentThread).top(), callerReturnValue);
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
		scopes.get(currentThread).pop();
	}
	
	@Override
	public void restore(Map<Integer, ? extends Scopes> scopes) {
        this.scopes.clear();

        for (Integer threadId : scopes.keySet()) {
            Scopes threadScopes = scopes.get(threadId);

		    if (threadScopes instanceof SymbolTableStack) {
                SymbolTableStack threadSymbolTableScopes = (SymbolTableStack) threadScopes;

                this.scopes.put(threadId, threadSymbolTableScopes.clone());

                if (threadSymbolTableScopes.count() > 0) {
                    this.universe = threadSymbolTableScopes.top().getUniverse();
                }
		    } else {
        		throw new RuntimeException("Invalid scopes type being restored!");
		    }
        }
	}
	
	@Override
	public Map<Integer, SymbolTableStack> memorize() {
        Map<Integer, SymbolTableStack> scopesClone = new HashMap<Integer, SymbolTableStack>();

        for (Integer threadId : scopes.keySet()) {
            scopesClone.put(threadId, scopes.get(threadId).clone());
        }

		return scopesClone;
	}
	
	@Override
	public String toString() {
		return scopes.get(currentThread).count() > 0 ? scopes.get(currentThread).top().toString() : "";
	}

	@Override
	public boolean isArray(AccessExpression path) {
		return scopes.get(currentThread).top().isArray(path);
	}

	@Override
	public boolean isObject(AccessExpression path) {
		return scopes.get(currentThread).top().isObject(path);
	}

	@Override
	public boolean isPrimitive(AccessExpression path) {
		return scopes.get(currentThread).top().isPrimitive(path);
	}

	@Override
	public Universe getUniverse() {
		return scopes.get(currentThread).top().getUniverse();
	}

	@Override
	public int count() {
		return scopes.get(currentThread).count() > 0 ? scopes.get(currentThread).top().count() : 0;
	}

	@Override
	public int depth() {
        return scopes.get(currentThread).count();
	}

    @Override
    public FlatSymbolTable get(int depth) {
        return scopes.get(currentThread).top(depth);
    }

    @Override
    public void print() {
        scopes.get(currentThread).print();
    }
	
}
