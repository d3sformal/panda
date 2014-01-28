package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;
import gov.nasa.jpf.abstraction.common.access.ReturnValue;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultPackageAndClass;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.concrete.AnonymousExpression;
import gov.nasa.jpf.abstraction.concrete.AnonymousObject;
import gov.nasa.jpf.abstraction.concrete.Reference;
import gov.nasa.jpf.abstraction.predicate.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.state.symbols.ClassObject;
import gov.nasa.jpf.abstraction.predicate.state.symbols.ClassStatics;
import gov.nasa.jpf.abstraction.predicate.state.symbols.HeapArray;
import gov.nasa.jpf.abstraction.predicate.state.symbols.HeapObject;
import gov.nasa.jpf.abstraction.predicate.state.symbols.StructuredArray;
import gov.nasa.jpf.abstraction.predicate.state.symbols.StructuredObject;
import gov.nasa.jpf.abstraction.predicate.state.symbols.StructuredValue;
import gov.nasa.jpf.abstraction.predicate.state.symbols.LocalVariable;
import gov.nasa.jpf.abstraction.predicate.state.symbols.PrimitiveValue;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Slot;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Universe;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Value;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A symbol table for a single scope
 */
public class FlatSymbolTable implements SymbolTable, Scope {
	
	private static String[] doNotMonitor = new String[] {
	};
	private static String[] doNotPrint = new String[] {
		"java",
		"boolean",
		"byte",
		"char",
		"double",
		"float",
		"int",
		"long",
		"short",
		"void",
		"[", // Statics of arrays
		"javax",
		"sun",
		"gov.nasa.jpf" // JPF
	};
	private static int GUARANTEED_LENGTH = 8;
	
	/**
	 * Abstract heap
	 */
	private Universe universe = new Universe();
	
	/**
	 * Entry points to the abstract heap
	 * 
	 * Local Variables:
	 */
	private Map<Root, LocalVariable> locals = new HashMap<Root, LocalVariable>();
	
	/**
	 * Return values (special "variables" with no backup in the program itself):
	 */
	private Map<ReturnValue, LocalVariable> returns = new HashMap<ReturnValue, LocalVariable>();
	
	/**
	 * Classes (holders for static fields)
	 */
	private Map<PackageAndClass, ClassObject> classes = new HashMap<PackageAndClass, ClassObject>();

	private PredicateAbstraction abstraction;
	
	public FlatSymbolTable(PredicateAbstraction abstraction) {
		this.abstraction = abstraction;
	}
	
	public Set<Root> getLocalVariables() {
		return locals.keySet();
	}
	
	public Set<ReturnValue> getReturnValues() {
		return returns.keySet();
	}

	public Set<PackageAndClass> getClasses() {
		return classes.keySet();
	}

	public LocalVariable getLocal(Root l) {
		return locals.get(l);
	}
	
	public LocalVariable getReturnValue(ReturnValue r) {
		return returns.get(r);
	}

	public ClassObject getClass(Root c) {
		return classes.get(c);
	}
	
	@Override
	public Universe getUniverse() {
		return universe;
	}
	
	public void addPrimitiveLocal(String name) {
		Root l = DefaultRoot.create(name);
		LocalVariable v = new LocalVariable(universe, l, new PrimitiveValue(universe));
				
		locals.put(l, v);
	}
	
	public void addHeapValueLocal(String name) {
		Root l = DefaultRoot.create(name);
		LocalVariable v = new LocalVariable(universe, l, universe.get(Universe.NULL));
				
		locals.put(l, v);
	}
	
	public void addPrimitiveReturn(ReturnValue r) {
		LocalVariable v = new LocalVariable(universe, r, new PrimitiveValue(universe));

		returns.put(r, v);
	}

	public void addHeapValueReturn(ReturnValue r) {
		LocalVariable ret = new LocalVariable(universe, r, universe.get(Universe.NULL));

		returns.put(r, ret);
	}

	public void addClass(String name, ThreadInfo threadInfo, ElementInfo elementInfo) {
		boolean excluded = false;
			
		for (String e : doNotMonitor) {
			excluded |= name.startsWith(e);
		}
			
		if (!excluded) {
			PackageAndClass c = DefaultPackageAndClass.create(name);
			ClassObject v = new ClassObject(universe, c, (ClassStatics) universe.add(threadInfo, elementInfo));
			
			classes.put(c, v);
		}
	}

    public void addThread(ThreadInfo threadInfo) {
        universe.add(threadInfo, threadInfo.getThreadObject());
    }
	
	/**
	 * Resolve a path to all values it may be pointing to (primitive/objects)
	 */
	private Set<Value> lookupValues(AccessExpression expression) {
		if (expression.getRoot() instanceof AnonymousExpression) {
			AnonymousExpression anonymous = (AnonymousExpression) expression.getRoot();
			Integer reference = anonymous.getReference().getObjectRef();
			
			if (universe.contains(reference)) {
				return universe.lookupValues(universe.get(reference), expression);
			}
		}
		
		if (expression.getRoot().isLocalVariable()) {
			if (locals.containsKey(expression.getRoot())) {
				return universe.lookupValues(locals.get(expression.getRoot()).getSlot().getPossibleValues(), expression);
			} else {
				return new HashSet<Value>(); // Not found
			}
		}
		
		if (expression.getRoot().isReturnValue()) {
			if (returns.containsKey(expression.getRoot())) {
				return universe.lookupValues(returns.get(expression.getRoot()).getSlot().getPossibleValues(), expression);
			} else {
				return new HashSet<Value>(); // Not found
			}
		}

		if (expression.getRoot().isStatic()) {
			if (classes.containsKey(expression.getRoot())) {
				return universe.lookupValues(classes.get(expression.getRoot()).getSlot().getPossibleValues(), expression);
			} else {
				return new HashSet<Value>(); // Not found
			}
		}
		
		throw new RuntimeException("Attempting to resolve access expression not rooted in a local variable nor a static field: " + expression);
	}
	
	/**
	 * Find all paths (up to a certain length) that can describe the given value (primitive/object)
	 */
	private Set<AccessExpression> valueToAccessExpressions(Value value, int maxLength) {
		Set<AccessExpression> ret = new HashSet<AccessExpression>();
		
		if (maxLength == 0) {
			return ret;
		}
		
        /**
         * Each object/array can be described by an anonymous object/array access expression
         */

        // Objects
		if (value instanceof HeapObject) {
			HeapObject ho = (HeapObject) value;
			VM vm = VM.getVM();
			ThreadInfo ti = vm.getCurrentThread();
			ElementInfo ei = ti.getElementInfo(ho.getReference().getReference());

			ret.add(AnonymousObject.create(new Reference(ti, ei)));
		}

        // Arrays
		if (value instanceof HeapArray) {
			HeapArray ha = (HeapArray) value;
			VM vm = VM.getVM();
			ThreadInfo ti = vm.getCurrentThread();
			ElementInfo ei = ti.getElementInfo(ha.getReference().getReference());

			ret.add(AnonymousArray.create(new Reference(ti, ei), Constant.create(ha.getLength())));
		}

        /**
         * It can as well be described as object field/array element of its parental object/array
         * or a local variable that contains it
         */
		for (Slot slot : value.getSlots()) {
			Value parent = slot.getParent();
			
			if (parent instanceof StructuredValue) {
				Set<AccessExpression> resolution = valueToAccessExpressions(parent,  maxLength - 1);

				for (AccessExpression prefix : resolution) {
					AccessExpression path = null;
					
					if (parent instanceof StructuredObject) {						
						path = DefaultObjectFieldRead.create(prefix, (String) slot.getSlotKey());
					}
					
					if (parent instanceof StructuredArray) {
						path = DefaultArrayElementRead.create(prefix, Constant.create((Integer) slot.getSlotKey()));
					}
					
					ret.add(path);
				}
			} else if (parent instanceof LocalVariable) {
				LocalVariable l = (LocalVariable) parent;
				
				ret.add(l.getAccessExpression());
			} else if (parent instanceof ClassObject) {
				ClassObject c = (ClassObject) parent;
				
				ret.add(c.getAccessExpression());
			}
		}
		
		return ret;
	}

	@Override
	public int count() {
		return getFilteredRelevantAccessExpressions().size();
	}
	
	/**
	 * Store values pointed to by one expression into the other
	 */
	@Override
	public Set<AccessExpression> processPrimitiveStore(Expression from, AccessExpression to) {
		return processPrimitiveStore(from, this, to);
	}

	/**
	 * Same as the other variant except it allows cross-universe writes (Necessary for method call boundaries)
     *
     * @return access path to all the affected objects
	 */
	public Set<AccessExpression> processPrimitiveStore(Expression from, FlatSymbolTable fromTable, AccessExpression to) {
		ensureAnonymousObjectExistance(from);
		ensureAnonymousObjectExistance(to);
		
		Set<AccessExpression> ret = new HashSet<AccessExpression>();
		Set<Value> destinations = lookupValues(to);

		boolean ambiguous = destinations.size() > 1;
		
		for (Value destination : destinations) {
			Value newValue = new PrimitiveValue(universe);
			
			for (Slot slot : destination.getSlots()) {
                /**
                 * If we know where we are storing things, we may overwrite the destination completely, otherwise we need to overapproximate and keep the original aliasing as well
                 */
				if (!ambiguous) {
					slot.clear();
					destination.removeSlot(slot);
				}
				
				Set<Value> values = new HashSet<Value>();
				values.add(newValue);
				slot.add(values);
				newValue.addSlot(slot);
			}
			
			ret.addAll(valueToAccessExpressions(newValue, getMaximalAccessExpressionLength()));
		}
		
		return ret;
	}
	
	/**
	 * Store values pointed to by one expression into the other
	 */
	@Override
	public Set<AccessExpression> processObjectStore(Expression from, AccessExpression to) {
		return processObjectStore(from, this, to);
	}
	
	/**
	 * Same as the other variant except it allows cross-universe writes (Necessary for method call boundaries)
	 */
	// The universes may differ instance-wise (different objects representing the same universe)
	// FromTable may have a different Locals/Statics sets
	public Set<AccessExpression> processObjectStore(Expression from, FlatSymbolTable fromTable, AccessExpression to) {
		fromTable.ensureAnonymousObjectExistance(from);
		
		ensureAnonymousObjectExistance(from);
		ensureAnonymousObjectExistance(to);
		
		Set<AccessExpression> ret = new HashSet<AccessExpression>();
		Set<Value> destinations = lookupValues(to.cutTail());
		Set<Value> sources = new HashSet<Value>();
		
        // First collect objects which may be referred to by the `from` access expression

		if (from instanceof AccessExpression) {
			Set<Value> rawSources = fromTable.lookupValues((AccessExpression) from);
			
			// ENSURE ALL SOURCE OBJECTS EXIST IN THE TARGET UNIVERSE
			for (Value foreign : rawSources) {
				sources.add(foreign.cloneInto(universe));
			}
		}
		
        // Special case is, when we store a constant MJIEnv.NULL which stands for NULL (by type of the target we know whether it is primitive MJIEnv.NULL or NULL)

		if (from instanceof Constant) {
			Constant referenceConstant = (Constant) from; // null, MJIEnv.NULL
			int reference = referenceConstant.value.intValue();
			
			if (universe.contains(reference)) {
				sources.add(universe.get(reference));
			}
		}
		
		boolean ambiguous = destinations.size() > 1;
		
        // For each new parent (object whose field/element is being set, or a local var ...)
        // Add the objects into the field/element or rewrite a local variable
		for (Value destination : destinations) {
			if (to instanceof ObjectFieldRead) {
				ObjectFieldRead read = (ObjectFieldRead) to;
				StructuredValue parent = (StructuredValue) destination;
				
				String field = read.getField().getName();
				
				for (AccessExpression prefix : valueToAccessExpressions(parent, getMaximalAccessExpressionLength())) {
					ret.add(DefaultObjectFieldRead.create(prefix, field));
				}
				
				if (!ambiguous) {
					((StructuredObject)parent).getField(field).clear();
				}
				
				((StructuredObject)parent).getField(field).add(sources);
			}
			
			if (to instanceof ArrayElementRead) {
				HeapArray parent = (HeapArray) destination;
				
				for (int i = 0; i < parent.getLength(); ++i) {
					for (AccessExpression prefix : valueToAccessExpressions(parent, getMaximalAccessExpressionLength())) {
						ret.add(DefaultArrayElementRead.create(prefix, Constant.create(i)));
					}
					
					if (!ambiguous) {
						parent.getElement(i).clear();
					}
					
					parent.getElement(i).add(sources);
				}
			}
			
			if (to instanceof Root) {
				LocalVariable parent;

				if (to instanceof ReturnValue) {
					parent = returns.get(to);
				} else {
					parent = locals.get(to);
				}
				
				ret.add(parent.getAccessExpression());
					
				if (!ambiguous) {
					parent.getSlot().clear();
				}
					
				parent.getSlot().add(sources);
			}
		}
		
		return ret;
	}

	/**
	 * Creates (if not already existent) object in the universe to match an anonymous expression
	 */
	private void ensureAnonymousObjectExistance(Expression expr) {
		if (expr instanceof AnonymousExpression) {
			universe.add(((AnonymousExpression) expr).getReference());
		}
	}

	@Override
	public boolean isArray(AccessExpression path) {
		boolean isArray = false;
		
		for (Value value : lookupValues(path)) {
			if (value instanceof PrimitiveValue) return false;
			
			isArray |= value instanceof HeapArray;
		}
		
		return isArray;
	}

	@Override
	public boolean isObject(AccessExpression path) {
		boolean isObject = false;
		
		for (Value value : lookupValues(path)) {
			if (value instanceof PrimitiveValue) return false;
			
			isObject |= value instanceof HeapObject;
		}
		
		return isObject;
	}

	@Override
	public boolean isPrimitive(AccessExpression path) {
		boolean isPrimitive = false;
		
		for (Value value : lookupValues(path)) {
			if (value instanceof StructuredValue) return false;
			
			isPrimitive |= value instanceof PrimitiveValue;
		}
		
		return isPrimitive;
	}
	
	@Override
	public FlatSymbolTable clone() {
		FlatSymbolTable clone = new FlatSymbolTable(abstraction);
		
		clone.universe = universe.clone();
		clone.locals = new HashMap<Root, LocalVariable>();
		clone.returns = new HashMap<ReturnValue, LocalVariable>();
		clone.classes = new HashMap<PackageAndClass, ClassObject>();
		
		for (Root l : locals.keySet()) {
			Root lClone = l.clone();
			LocalVariable lValue = locals.get(l).cloneInto(clone.universe);
			
			clone.locals.put(lClone, lValue);
		}
		
		for (ReturnValue r : returns.keySet()) {
			ReturnValue rClone = r.clone();
			LocalVariable lValue = returns.get(r).cloneInto(clone.universe);

			clone.returns.put(rClone, lValue);
		}

		for (PackageAndClass c : classes.keySet()) {
			PackageAndClass cClone = c.clone();
			ClassObject cValue = classes.get(c).cloneInto(clone.universe);
			
			clone.classes.put(cClone, cValue);
		}

		return clone;
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		Set<AccessExpression> order = new TreeSet<AccessExpression>(new Comparator<AccessExpression>() {

			public int compare(AccessExpression expr1, AccessExpression expr2) {
				if (expr1.getLength() < expr2.getLength()) return -1;
				if (expr2.getLength() < expr1.getLength()) return +1;
				
				return expr1.toString(Notation.DOT_NOTATION).compareTo(expr2.toString(Notation.DOT_NOTATION));
			}
		});
		
		order.addAll(getFilteredRelevantAccessExpressions());
				
		int maxLength = 0;
		int padding = 4;
		
		for (AccessExpression expr : order) {
			maxLength = Math.max(maxLength, expr.toString(Notation.DOT_NOTATION).length());
		}
		
		for (AccessExpression expr : order) {
			ret.append(expr.toString(Notation.DOT_NOTATION));
			
			for (int i = 0; i < maxLength - expr.toString(Notation.DOT_NOTATION).length() + padding; ++i) {
				ret.append(' ');
			}
			
			ret.append(lookupValues(expr));
			
			ret.append('\n');
		}
		
		return ret.toString();
	}
	
	/**
	 * All symbolic expressions pointing to some values in the universe (up to the length of the longest path in a predicate currently being used in the abstraction) that are not filtered out by their name (some system objects are omitted from the output to increase the readability)
	 */
	private Set<AccessExpression> getFilteredRelevantAccessExpressions() {
		Set<AccessExpression> filtered = new HashSet<AccessExpression>();
	
		for (AccessExpression expr : getAllRelevantAccessExpressions()) {
			if (expr.getRoot() instanceof PackageAndClass) {
				boolean excluded = false;
				
				for (String e : doNotPrint) {
					excluded |= expr.getRoot().getName().startsWith(e);
				}
				
				if (excluded) {
					continue;
				}
			}
			
			filtered.add(expr);
		}
		
		return filtered;
	}

	/**
	 * All symbolic expressions pointing to some values in the universe (up to the length of the longest path in a predicate currently being used in the abstraction)
	 */
	private Set<AccessExpression> getAllRelevantAccessExpressions() {
		Set<AccessExpression> ret = new HashSet<AccessExpression>();
		
		for (Root l : locals.keySet()) {			
			for (Value value : locals.get(l).getSlot().getPossibleValues()) {
				ret.addAll(subValueAccessExpressions(value, l, getMaximalAccessExpressionLength()));
			}
		}
		
		for (ReturnValue r : returns.keySet()) {
			for (Value value : returns.get(r).getSlot().getPossibleValues()) {
				ret.addAll(subValueAccessExpressions(value, r, getMaximalAccessExpressionLength()));
			}
		}

		for (PackageAndClass c : classes.keySet()) {			
			for (Value value : classes.get(c).getSlot().getPossibleValues()) {
				ret.addAll(subValueAccessExpressions(value, c, getMaximalAccessExpressionLength()));
			}
		}
		
		return ret;
	}

	/**
	 * Create symbolic expressions accessing subvalues (fields, elements, or deeper) of a given value starting with the prefix (stop after a threshold is reached)
	 */
	private Set<AccessExpression> subValueAccessExpressions(Value value, AccessExpression prefix, int maximalAccessExpressionLength) {
		Set<AccessExpression> ret = new HashSet<AccessExpression>();
		
		if (maximalAccessExpressionLength == 0) return ret;
		
		if (value instanceof StructuredObject) {
			StructuredObject object = (StructuredObject) value;
			
			for (String fieldName : object.getFields().keySet()) {
				for (Value field : object.getField(fieldName).getPossibleValues()) {
					ret.addAll(subValueAccessExpressions(field, DefaultObjectFieldRead.create(prefix, fieldName), maximalAccessExpressionLength - 1));
				}
			}
			
			ret.add(prefix);
		}
		
		if (value instanceof StructuredArray) {
			StructuredArray array = (StructuredArray) value;
			
			for (Integer index : array.getElements().keySet()) {
				for (Value element : array.getElement(index).getPossibleValues()) {
					ret.addAll(subValueAccessExpressions(element, DefaultArrayElementRead.create(prefix, Constant.create(index)), maximalAccessExpressionLength - 1));
				}
			}
			
			ret.add(prefix);
		}
		
		if (value instanceof PrimitiveValue) {
			ret.add(prefix);
		}
		
		return ret;
	}

	private int getMaximalAccessExpressionLength() {
		int ret = GUARANTEED_LENGTH;

        Set<AccessExpression> paths = new HashSet<AccessExpression>();
		
		for (Predicate predicate : abstraction.getPredicateValuation().getPredicates()) {
            predicate.addAccessExpressionsToSet(paths);

			for (AccessExpression expr : paths) {
				ret = Math.max(ret, expr.getLength());
			}

            paths.clear();
		}
		
		return ret;
	}
	
	/**
	 * Compare two symbol tables and find differences
	 */
	public Set<AccessExpression> getModifiedObjectAccessExpressions(FlatSymbolTable table) {
		Set<AccessExpression> ret = new HashSet<AccessExpression>();

		for (Value value : universe.getModifiedObjects(table.universe)) {
			ret.addAll(valueToAccessExpressions(value, Math.max(getMaximalAccessExpressionLength(), table.getMaximalAccessExpressionLength())));
		}
		
		return ret;
	}

	public void removeLocals() {
		for (Root l : locals.keySet()) {
			LocalVariable local = locals.get(l);
			
			local.getSlot().clear();
		}
		
		for (ReturnValue r : returns.keySet()) {
			LocalVariable ret = returns.get(r);

			ret.getSlot().clear();
		}

		locals.clear();
		returns.clear();
	}

	/**
	 * Copy the abstract heap from the given table here, keep locals/returns valid in this scope
	 */
	public void updateUniverse(FlatSymbolTable top) {
		FlatSymbolTable clone = top.clone();
				
		clone.removeLocals();
		
		for (Root l : locals.keySet()) {
			Root lClone = l.clone();
			LocalVariable lValue = locals.get(l).cloneInto(clone.universe);
			
			clone.locals.put(lClone, lValue);
		}
		
		for (ReturnValue r : returns.keySet()) {
			ReturnValue rClone = r.clone();
			LocalVariable rValue = returns.get(r).cloneInto(clone.universe);

			clone.returns.put(rClone, rValue);
		}

		locals = clone.locals;
		classes = clone.classes;
		universe = clone.universe;
	}

}
