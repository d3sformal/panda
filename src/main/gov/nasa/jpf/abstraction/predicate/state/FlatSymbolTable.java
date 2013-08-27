package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.predicate.state.symbols.ClassObject;
import gov.nasa.jpf.abstraction.predicate.state.symbols.HeapArray;
import gov.nasa.jpf.abstraction.predicate.state.symbols.HeapObject;
import gov.nasa.jpf.abstraction.predicate.state.symbols.HeapValue;
import gov.nasa.jpf.abstraction.predicate.state.symbols.LocalVariable;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Slot;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Universe;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Value;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FlatSymbolTable implements SymbolTable, Scope {
	
	private Universe universe = new Universe();
	
	private Map<Root, LocalVariable> locals = new HashMap<Root, LocalVariable>();
	private Map<Root, ClassObject> classes = new HashMap<Root, ClassObject>();
	
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
	public Set<AccessExpression> processPrimitiveStore(
			AccessExpression to) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<AccessExpression> processObjectStore(Expression from,
			AccessExpression to) {
		// TODO Auto-generated method stub
		return null;
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

}
