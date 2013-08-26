package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteAccessExpression;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Array;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Object;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Value;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class FlatSymbolTable implements SymbolTable, Scope {
	
	private HashMap<AccessExpression, Set<Value>> previous;
	
	private HashMap<Root, Set<Value>> locals;
	private HashMap<AccessExpression, Set<Value>> statics;
	
	private static int MAX = 100; //TODO read the longest path from predicate from current predicate valuation table

	public FlatSymbolTable() {
		this(
			new HashMap<AccessExpression, Set<Value>>(),
			new HashMap<Root, Set<Value>>(),
			new HashMap<AccessExpression, Set<Value>>()
		);
	}
	
	protected FlatSymbolTable(
			HashMap<AccessExpression, Set<Value>> previous,
			HashMap<Root, Set<Value>> locals,
			HashMap<AccessExpression, Set<Value>> statics) {
		this.previous = previous;
		this.locals = locals;
		this.statics = statics;
	}
	
	private Map<AccessExpression, Set<Value>> localsAndStatics() {
		Map<AccessExpression, Set<Value>> ret = new HashMap<AccessExpression, Set<Value>>();
		
		ret.putAll(statics);
		ret.putAll(locals);
		
		return ret;
	}
	
	private HashMap<AccessExpression, Set<Value>> current() {
		HashMap<AccessExpression, Set<Value>> ret = new HashMap<AccessExpression, Set<Value>>();
		Map<AccessExpression, Set<Value>> localsAndStatics = localsAndStatics();
		
		for (AccessExpression path : localsAndStatics.keySet()) {
			for (Value value : localsAndStatics.get(path)) {
				value.build(MAX);
				ret.putAll(value.resolve(path, MAX));
			}
		}
		
		return ret;
	}
	
	private Set<AccessExpression> affectedLocalsAndStatics(ConcreteAccessExpression to) {
		Set<AccessExpression> ret = new HashSet<AccessExpression>();
		
		if (to.isLocalVariable()) {
			Set<Value> values = new HashSet<Value>();
			
			values.add(to.resolve());
			
			locals.put((Root) to, values);
			
			ret.add(to);
		} else if (to.isStatic() && to.getLength() == 2) {
			Set<Value> values = new HashSet<Value>();
			
			values.add(to.resolve());
			
			statics.put(to, values);
			
			ret.add(to);
		}
		
		return ret;
	}
	
	@Override
	public Set<AccessExpression> processPrimitiveStore(ConcreteAccessExpression to) {
		return affectedLocalsAndStatics(to);
	}
	
	@Override
	public Set<AccessExpression> processObjectStore(Expression from, ConcreteAccessExpression to) {
		Set<AccessExpression> affected = new HashSet<AccessExpression>();
		
		if (to != null) affected.addAll(affectedLocalsAndStatics(to));
		
		HashMap<AccessExpression, Set<Value>> current = current();
		
		for (AccessExpression expr : current.keySet()) {			
			if (!previous.containsKey(expr)) {
				affected.add(expr);
			} else {
				Set<Value> values1 = current.get(expr);
				Set<Value> values2 = previous.get(expr);
				
				if (values1.size() != values2.size()) {
					affected.add(expr);
				}
				
				for (Value value : values1) {
					if (!values2.contains(value)) {
						affected.add(expr);
					}
				}
			}
		}
		
		previous = current;
				
		return affected;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public FlatSymbolTable clone() {
		return new FlatSymbolTable(
			(HashMap<AccessExpression, Set<Value>>) previous.clone(),
			(HashMap<Root, Set<Value>>) locals.clone(),
			(HashMap<AccessExpression, Set<Value>>) statics.clone()
		);
	}
	
	@Override
	public int count() {
		return current().keySet().size();
	}
	
	private String createDump(Set<AccessExpression> symbols) {
		StringBuilder ret = new StringBuilder();
		
		Set<AccessExpression> order = new TreeSet<AccessExpression>(new Comparator<AccessExpression>() {
			public int compare(AccessExpression o1, AccessExpression o2) {
				if (o1 instanceof Root && !(o2 instanceof Root)) return -1;
				if (o2 instanceof Root && !(o1 instanceof Root)) return +1;
				
				return o1.toString(Notation.DOT_NOTATION).compareTo(o2.toString(Notation.DOT_NOTATION));
			}
		});
		
		order.addAll(symbols);
		
		for (AccessExpression expr : order) {
			ret.append(expr.toString(Notation.DOT_NOTATION));
			ret.append("\n");
		}
		
		return ret.toString();
	}
	
	@Override
	public String toString() {
		return createDump(current().keySet());
	}

	@Override
	public boolean isArray(ConcreteAccessExpression path) {
		boolean isArray = false;
		
		Map<AccessExpression, Set<Value>> current = current();
		
		if (current.containsKey(path)) {
			for (Value value : current.get(path)) {
				isArray |= value instanceof Array;
			}
		}
		
		return isArray;
	}

	@Override
	public boolean isObject(ConcreteAccessExpression path) {
		boolean isObject = false;
		
		Map<AccessExpression, Set<Value>> current = current();
		
		if (current.containsKey(path)) {
			for (Value value : current.get(path)) {
				isObject |= value instanceof Object;
			}
		}
		
		return isObject;
	}

	@Override
	public boolean isPrimitive(ConcreteAccessExpression path) {
		return !isObject(path);
	}
	
	public void removeLocals() {
		locals = new HashMap<Root, Set<Value>>();
	}
	
	public HashMap<AccessExpression, Set<Value>> getStatics() {
		return statics;
	}

	public void setStatics(HashMap<AccessExpression, Set<Value>> statics) {
		this.statics = statics;
	}

}
