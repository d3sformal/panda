package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultPackageAndClass;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.concrete.AnonymousExpression;
import gov.nasa.jpf.abstraction.predicate.state.symbols.ClassObject;
import gov.nasa.jpf.abstraction.predicate.state.symbols.HeapArray;
import gov.nasa.jpf.abstraction.predicate.state.symbols.HeapObject;
import gov.nasa.jpf.abstraction.predicate.state.symbols.HeapValue;
import gov.nasa.jpf.abstraction.predicate.state.symbols.LocalVariable;
import gov.nasa.jpf.abstraction.predicate.state.symbols.PrimitiveValue;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Slot;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Universe;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Value;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class FlatSymbolTable implements SymbolTable, Scope {
	
	private enum Verbosity {
		NORMAL,
		VERBOSE
	}
	
	private static Verbosity verbosity = Verbosity.VERBOSE;
	private static String[] NormalVerbosityExludedPackageNames = new String[] {"java", "javax"};
	
	private Universe universe = new Universe();	
	private Map<Root, LocalVariable> locals = new HashMap<Root, LocalVariable>();
	private Map<PackageAndClass, ClassObject> classes = new HashMap<PackageAndClass, ClassObject>();
	
	public Set<Root> getLocalVariables() {
		return locals.keySet();
	}
	
	public Set<PackageAndClass> getClasses() {
		return classes.keySet();
	}
	
	public LocalVariable getLocal(Root l) {
		return locals.get(l);
	}
	
	public ClassObject getClass(Root c) {
		return classes.get(c);
	}
	
	public void addPrimitiveLocal(String name) {
		Root l = DefaultRoot.create(name);
		LocalVariable v = new LocalVariable(l, new PrimitiveValue());
				
		locals.put(l, v);
	}
	
	public void addHeapValueLocal(String name) {
		Root l = DefaultRoot.create(name);
		LocalVariable v = new LocalVariable(l, universe.get(Universe.NULL));
				
		locals.put(l, v);
	}
	
	public void addClass(String name, ThreadInfo threadInfo, ElementInfo elementInfo) {
		PackageAndClass c = DefaultPackageAndClass.create(name);
		ClassObject v = new ClassObject(c, universe.add(threadInfo, elementInfo));
		
		classes.put(c, v);
	}
	
	private Set<Value> resolve(AccessExpression expression) {
		if (expression.getRoot() instanceof AnonymousExpression) {
			AnonymousExpression anonymous = (AnonymousExpression) expression.getRoot();
			Integer reference = anonymous.getReference().getObjectRef();
			
			if (universe.contains(reference)) {
				return universe.resolve(universe.get(reference), expression);
			}
		}
		
		if (expression.getRoot().isLocalVariable()) {
			if (locals.containsKey(expression.getRoot())) {
				return universe.resolve(locals.get(expression.getRoot()).getSlot().getPossibleValues(), expression);
			} else {
				return new HashSet<Value>(); // Not found
			}
		}
		
		if (expression.getRoot().isStatic()) {
			if (classes.containsKey(expression.getRoot())) {
				return universe.resolve(classes.get(expression.getRoot()).getSlot().getPossibleValues(), expression);
			} else {
				return new HashSet<Value>(); // Not found
			}
		}
		
		throw new RuntimeException("Attempting to resolve access expression not rooted in a local variable nor a static field: " + expression);
	}
	
	private Set<AccessExpression> resolve(Value value, int maxLength) {
		Set<AccessExpression> ret = new HashSet<AccessExpression>();
		
		if (maxLength == 0) return ret;		
		if (value instanceof LocalVariable || value instanceof ClassObject) return ret;
		
		
		for (Slot slot : value.getSlots()) {
			Value parent = slot.getParent();
			
			Set<AccessExpression> resolution = resolve(parent,  maxLength - 1);
			
			if (parent instanceof HeapValue) {
				for (AccessExpression prefix : resolution) {
					AccessExpression path = null;
					
					if (parent instanceof HeapObject) {						
						path = DefaultObjectFieldRead.create(prefix, (String) slot.getSlotKey());
					}
					
					if (parent instanceof HeapArray) {
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
		return getAllRelevantAccessExpressions().size();
	}

	@Override
	public Set<AccessExpression> processPrimitiveStore(Expression from, AccessExpression to) {
		ensureAnonymousObjectExistance(from);
		ensureAnonymousObjectExistance(to);
		
		Set<AccessExpression> ret = new HashSet<AccessExpression>();
		Set<Value> destinations = resolve(to);
		
		for (Value destination : destinations) {
			//update destination
			
			ret.addAll(resolve(destination, getMaximalAccessExpressionLength()));
		}
		
		System.out.println(ret + " := PRIMITIVE");
		
		return ret;
	}
	
	@Override
	public Set<AccessExpression> processObjectStore(Expression from, AccessExpression to) {
		ensureAnonymousObjectExistance(from);
		ensureAnonymousObjectExistance(to);
		
		Set<AccessExpression> ret = new HashSet<AccessExpression>();
		Set<Value> destinations = resolve(to.cutTail());
		Set<Value> sources = new HashSet<Value>(); // FALLBACK
		
		if (from instanceof AccessExpression) {
			sources = resolve((AccessExpression) from);
		}
		
		if (from instanceof Constant) {
			Constant referenceConstant = (Constant) from;
			Integer reference = referenceConstant.value.intValue();
			
			if (universe.contains(reference)) {
				sources = new HashSet<Value>();
				sources.add(universe.get(reference));
			} else {
				sources = new HashSet<Value>();
			}
		}
		
		boolean ambiguous = destinations.size() > 1;
		
		for (Value destination : destinations) {
			if (to instanceof ObjectFieldRead) {
				ObjectFieldRead read = (ObjectFieldRead) to;
				HeapObject parent = (HeapObject) destination;
				
				String field = read.getField().getName();
				
				for (AccessExpression prefix : resolve(parent, getMaximalAccessExpressionLength())) {
					ret.add(DefaultObjectFieldRead.create(prefix, field));
				}
				
				if (!ambiguous) {
					parent.getField(field).clear();
				}
				
				parent.getField(field).add(sources);
			}
			
			if (to instanceof ArrayElementRead) {
				HeapArray parent = (HeapArray) destination;
				
				for (int i = 0; i < parent.getLength(); ++i) {
					for (AccessExpression prefix : resolve(parent, getMaximalAccessExpressionLength())) {
						ret.add(DefaultArrayElementRead.create(prefix, Constant.create(i)));
					}
					
					if (!ambiguous) {
						parent.getElement(i).clear();
					}
					
					parent.getElement(i).add(sources);
				}
			}
			
			if (to instanceof Root) {
				LocalVariable parent = locals.get(to);
				
				ret.add(parent.getAccessExpression());
					
				if (!ambiguous) {
					parent.getSlot().clear();
				}
					
				parent.getSlot().add(sources);
			}
		}
		
		System.out.println(ret + " := " + sources);
		
		return ret;
	}

	private void ensureAnonymousObjectExistance(Expression expr) {
		if (expr instanceof AnonymousExpression) {
			universe.add(((AnonymousExpression) expr).getReference());
		}
	}

	@Override
	public boolean isArray(AccessExpression path) {
		boolean isArray = false;
		
		for (Value value : resolve(path)) {
			if (value instanceof PrimitiveValue) return false;
			
			isArray |= value instanceof HeapArray;
		}
		
		return isArray;
	}

	@Override
	public boolean isObject(AccessExpression path) {
		boolean isObject = false;
		
		for (Value value : resolve(path)) {
			if (value instanceof PrimitiveValue) return false;
			
			isObject |= value instanceof HeapObject;
		}
		
		return isObject;
	}

	@Override
	public boolean isPrimitive(AccessExpression path) {
		boolean isPrimitive = false;
		
		for (Value value : resolve(path)) {
			if (value instanceof HeapValue) return false;
			
			isPrimitive |= value instanceof PrimitiveValue;
		}
		
		return isPrimitive;
	}
	
	@Override
	public FlatSymbolTable clone() {
		FlatSymbolTable clone = new FlatSymbolTable();
		
		clone.universe = universe.clone();
		clone.locals = new HashMap<Root, LocalVariable>();
		clone.classes = new HashMap<PackageAndClass, ClassObject>();
		
		for (Root l : locals.keySet()) {
			Root lClone = l.clone();
			LocalVariable lValue = locals.get(l).cloneInto(clone.universe);
			
			clone.locals.put(lClone, lValue);
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
				if (expr1.toString(Notation.DOT_NOTATION).length() < expr2.toString(Notation.DOT_NOTATION).length()) return -1;
				if (expr2.toString(Notation.DOT_NOTATION).length() < expr1.toString(Notation.DOT_NOTATION).length()) return +1;
				
				return expr1.toString(Notation.DOT_NOTATION).compareTo(expr2.toString(Notation.DOT_NOTATION));
			}
		});
		
		Set<AccessExpression> filtered;
		
		switch (verbosity) {
		case NORMAL:
			filtered = new HashSet<AccessExpression>();
		
			for (AccessExpression expr : getAllRelevantAccessExpressions()) {
				if (expr.getRoot() instanceof PackageAndClass) {
					boolean excluded = false;
					
					for (String e : NormalVerbosityExludedPackageNames) {
						excluded |= expr.getRoot().getName().startsWith(e);
					}
					
					if (!excluded) {
						filtered.add(expr);
					}
				}
			}
		
			break;
		
		case VERBOSE:
			filtered = getAllRelevantAccessExpressions();
			break;

		default:
			throw new RuntimeException("Unsupported system table verbosity level.");
		}
		
		order.addAll(filtered);
				
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
			
			ret.append(resolve(expr));
			
			ret.append('\n');
		}
		
		return ret.toString();
	}

	private Set<AccessExpression> getAllRelevantAccessExpressions() {
		Set<AccessExpression> ret = new HashSet<AccessExpression>();
		
		for (Root l : locals.keySet()) {			
			for (Value value : locals.get(l).getSlot().getPossibleValues()) {
				ret.addAll(resolve(value, getMaximalAccessExpressionLength()));
			}
		}
		
		for (Root c : classes.keySet()) {			
			for (Value value : classes.get(c).getSlot().getPossibleValues()) {
				ret.addAll(resolve(value, getMaximalAccessExpressionLength()));
			}
		}
		
		return ret;
	}

	private int getMaximalAccessExpressionLength() {
		return 10;
	}

	public void stealClasses(FlatSymbolTable table) {
		classes.clear();
		classes.putAll(table.classes);
	}

}
