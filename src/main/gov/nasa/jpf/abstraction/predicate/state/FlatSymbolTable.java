package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.predicate.state.symbols.ClassObject;
import gov.nasa.jpf.abstraction.predicate.state.symbols.HeapArray;
import gov.nasa.jpf.abstraction.predicate.state.symbols.HeapObject;
import gov.nasa.jpf.abstraction.predicate.state.symbols.HeapValue;
import gov.nasa.jpf.abstraction.predicate.state.symbols.HeapValueSlot;
import gov.nasa.jpf.abstraction.predicate.state.symbols.LocalVariable;
import gov.nasa.jpf.abstraction.predicate.state.symbols.PrimitiveValue;
import gov.nasa.jpf.abstraction.predicate.state.symbols.PrimitiveValueSlot;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Slot;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Universe;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Value;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class FlatSymbolTable implements SymbolTable, Scope {
	
	private Universe universe = new Universe();
	
	private Map<Root, LocalVariable> locals = new HashMap<Root, LocalVariable>();
	private Map<Root, ClassObject> classes = new HashMap<Root, ClassObject>();
	
	public void addPrimitiveLocal(String name) {
		Root l = DefaultRoot.create(name);
		LocalVariable v = new LocalVariable(l);
		
		v.addSlot(new PrimitiveValueSlot(v, name, new PrimitiveValue()));
		
		locals.put(l, v);
	}
	
	public void addHeapValueLocal(String name) {
		Root l = DefaultRoot.create(name);
		LocalVariable v = new LocalVariable(l);
		
		v.addSlot(new HeapValueSlot(v, name, universe.get(universe.NULL)));
		
		locals.put(l, v);
	}
	
	private Set<Slot> resolve(AccessExpression expression) {
		if (expression.getRoot().isLocalVariable()) {			
			return universe.resolve(locals.get(expression.getRoot()).getSlots(), expression);
		}
		
		if (expression.getRoot().isStatic() && expression.getLength() >= 2) {			
			return universe.resolve(classes.get(expression.getRoot()).getSlots(), expression);
		}
		
		return null;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<AccessExpression> processPrimitiveStore(AccessExpression to) {
		// TODO Auto-generated method stub
		return new HashSet<AccessExpression>();
	}

	@Override
	public Set<AccessExpression> processObjectStore(Expression from, AccessExpression to) {
		// TODO Auto-generated method stub
		return new HashSet<AccessExpression>();
	}

	@Override
	public boolean isArray(AccessExpression path) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isObject(AccessExpression path) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPrimitive(AccessExpression path) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public FlatSymbolTable clone() {
		return this;
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
		
		for (Root local : locals.keySet()) {
			//System.out.println("Looking up symbols rooted in local variable: " + local.getName());
			
			for (Slot slot : locals.get(local).getSlots()) {				
				for (Value value : slot.getPossibleValues()) {
					order.addAll(resolve(value, 10));
				}
			}
		}
		
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
			
			ret.append('{');
			
			for (Slot slot : resolve(expr)) {
				Iterator<Value> it = slot.getPossibleValues().iterator();
				
				if (it.hasNext()) {
					ret.append(it.next());
					
					while (it.hasNext()) {
						ret.append(',');
						ret.append(' ');
						ret.append(it.next());
					}	
				}
			}
			
			ret.append('}');
			
			ret.append('\n');
		}
		
		return ret.toString();
	}

}
