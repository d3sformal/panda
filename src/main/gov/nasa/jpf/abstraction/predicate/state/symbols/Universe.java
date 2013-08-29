package gov.nasa.jpf.abstraction.predicate.state.symbols;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ObjectAccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.concrete.Reference;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Universe implements Cloneable {
	private ValueFactory factory = new ValueFactory(this);
	private Map<Integer, HeapValue> objects = new HashMap<Integer, HeapValue>();
	
	public static int NULL = -1;
	
	public Universe() {
		factory.createObject(NULL);
	}
	
	public ValueFactory getFactory() {
		return factory;
	}
	
	public boolean contains(Integer reference) {
		return objects.containsKey(reference);
	}
	
	public HeapValue get(Integer reference) {
		return objects.get(reference);
	}
	
	public void add(HeapValue value) {
		objects.put(value.getReference(), value);
	}
	
	public HeapValue add(Reference reference) {
		return add(reference.getThreadInfo(), reference.getElementInfo());
	}
	
	public HeapValue add(ThreadInfo threadInfo, ElementInfo elementInfo) {
		if (elementInfo == null) return get(NULL);
		
		Integer ref = elementInfo.getObjectRef();
		
		if (contains(ref)) return get(ref);
		
		if (elementInfo.isArray()) {
			HeapArray array = factory.createArray(ref, elementInfo.arrayLength());
			
			for (int i = 0; i < elementInfo.arrayLength(); ++i) {
				if (elementInfo.isReferenceArray()) {
					Integer subRef = elementInfo.getReferenceElement(i);
					
					ElementInfo subElementInfo = threadInfo.getElementInfo(subRef);
					
					array.setElement(i, add(threadInfo, subElementInfo));
				} else {
					array.setElement(i, new PrimitiveValue());
				}
			}
			
			return array;
		} else {
			HeapObject object = factory.createObject(ref);
			
			for (int i = 0; i < elementInfo.getNumberOfFields(); ++i) {
				FieldInfo fieldInfo = elementInfo.getFieldInfo(i);
				
				if (fieldInfo.isNumericField()) {
					object.setField(fieldInfo.getName(), new PrimitiveValue());
				}
				
				if (fieldInfo.isReference()) {
					Integer subRef = elementInfo.getReferenceField(fieldInfo);
					
					ElementInfo subElementInfo = threadInfo.getElementInfo(subRef);
					
					object.setField(fieldInfo.getName(), add(threadInfo, subElementInfo));
				}
			}
			
			return object;
		}
	}
	
	public Set<Value> resolve(Set<Value> roots, AccessExpression expression) {
		Set<Value> ret = new HashSet<Value>();
		
		for (Value root : roots) {
			ret.addAll(resolve(root, expression));
		}
		
		return ret;
	}
	
	public Set<Value> resolve(Value root, AccessExpression expression) {		
		if (expression instanceof Root) {
			Set<Value> values = new HashSet<Value>();
			
			values.add(root);
			
			return values;
		}
		
		ObjectAccessExpression read = (ObjectAccessExpression) expression;
		
		Set<Value> parents = resolve(root, read.getObject());
		Set<Value> children = new HashSet<Value>();
		
		for (Value value : parents) {
			HeapValue parent = (HeapValue) value;
			
			if (read instanceof ObjectFieldRead) {
				HeapObject object = (HeapObject) parent;
				ObjectFieldRead fieldRead = (ObjectFieldRead) read;
				
				children.addAll(object.getField(fieldRead.getField().getName()).getPossibleValues());
			}
			
			if (read instanceof ArrayElementRead) {
				HeapArray array = (HeapArray) parent;
				
				for (int i = 0; i < array.getLength(); ++i) {
					children.addAll(array.getElement(i).getPossibleValues());
				}
			}
		}
		
		return children;
	}
	
	@Override
	public Universe clone() {
		Universe clone = new Universe();
		
		for (Integer reference : objects.keySet()) {
			HeapValue value = objects.get(reference);
			
			value.cloneInto(clone);
		}
		
		return clone;
	}
}
