package gov.nasa.jpf.abstraction.predicate.state.symbols;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ObjectAccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.concrete.Reference;
import gov.nasa.jpf.abstraction.util.StaticClassObjectTracker;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.StaticElementInfo;
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
		
		StaticClassObjectTracker.dumpElementInfo(threadInfo, elementInfo);
		
		if (elementInfo == null) return get(NULL);
		
		Integer ref = elementInfo.getObjectRef();
		boolean existed = contains(ref);
		
		if (elementInfo.isArray()) {			
			HeapArray array = factory.createArray(ref, elementInfo.arrayLength());
			
			if (existed) return array;
			
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
			
			if (existed) return object;
			
			for (int i = 0; i < elementInfo.getNumberOfFields(); ++i) {
				FieldInfo fieldInfo = elementInfo.getFieldInfo(i);
				
				if (fieldInfo.isReference()) {
					Integer subRef = elementInfo.getReferenceField(fieldInfo);
					
					ElementInfo subElementInfo = threadInfo.getElementInfo(subRef);
					
					object.setField(fieldInfo.getName(), add(threadInfo, subElementInfo));
				} else {
					object.setField(fieldInfo.getName(), new PrimitiveValue());
				}
			}
			
			return object;
		}
	}
	
	public Set<Value> lookupValues(Set<Value> roots, AccessExpression expression) {
		Set<Value> ret = new HashSet<Value>();
		
		for (Value root : roots) {
			ret.addAll(lookupValues(root, expression));
		}
		
		return ret;
	}
	
	public Set<Value> lookupValues(Value root, AccessExpression expression) {		
		if (expression instanceof Root) {
			Set<Value> values = new HashSet<Value>();
			
			values.add(root);
			
			return values;
		}
		
		ObjectAccessExpression read = (ObjectAccessExpression) expression;
		
		Set<Value> parents = lookupValues(root, read.getObject());
		Set<Value> children = new HashSet<Value>();
		
		for (Value value : parents) {
			HeapValue parent = (HeapValue) value;
			
			if (read instanceof ObjectFieldRead) {
				System.out.println(read);
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
	
	public Set<HeapValue> getModifiedObjects(Universe universe) {
		Set<HeapValue> ret = new HashSet<HeapValue>();
		
		for (Integer reference : objects.keySet()) {
			HeapValue originalValue = objects.get(reference);
			HeapValue modifiedValue = universe.objects.get(reference);
			
			if (originalValue instanceof HeapObject) {
				if (modifiedValue instanceof HeapObject) {
					HeapObject originalObject = (HeapObject) originalValue;
					HeapObject modifiedObject = (HeapObject) modifiedValue;
					
					for (String field : originalObject.getFields().keySet()) {
						Slot originalField = originalObject.getField(field);
						Slot modifiedField = modifiedObject.getField(field);
						
						if (originalField.getSize() == modifiedField.getSize()) {
							for (Value originalSubValue : originalField.getPossibleValues()) {
								if (!modifiedField.getPossibleValues().contains(originalSubValue)) {
									ret.add(originalValue);
								}
							}
						} else {
							ret.add(originalValue);
						}
					}
				} else {
					ret.add(originalValue);
				}
			}
			
			if (originalValue instanceof HeapArray) {
				if (modifiedValue instanceof HeapArray) {
					HeapArray originalArray = (HeapArray) originalValue;
					HeapArray modifiedArray = (HeapArray) modifiedValue;
					
					for (Integer i : originalArray.getElements().keySet()) {
						Slot originalElement = originalArray.getElement(i);
						Slot modifiedElement = modifiedArray.getElement(i);
						
						if (originalElement.getSize() == modifiedElement.getSize()) {
							for (Value originalSubValue : originalElement.getPossibleValues()) {
								if (!modifiedElement.getPossibleValues().contains(originalSubValue)) {
									ret.add(originalValue);
								}
							}
						} else {
							ret.add(originalValue);
						}
					}
				} else {
					ret.add(originalValue);
				}
			}
		}
		
		return ret;
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
