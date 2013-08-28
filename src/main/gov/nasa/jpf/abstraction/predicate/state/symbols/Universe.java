package gov.nasa.jpf.abstraction.predicate.state.symbols;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ObjectAccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.util.ArrayList;
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
	
	public boolean contains(Integer reference) {
		return objects.containsKey(reference);
	}
	
	public HeapValue get(Integer reference) {
		return objects.get(reference);
	}
	
	public void add(HeapValue value) {
		objects.put(value.getReference(), value);
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
				} else {
					Integer subRef = elementInfo.getReferenceField(fieldInfo);
					
					ElementInfo subElementInfo = threadInfo.getElementInfo(subRef);
					
					object.setField(fieldInfo.getName(), add(threadInfo, subElementInfo));
				}
			}
			
			return object;
		}
	}
	
	public Set<Slot> resolve(Set<Slot> roots, AccessExpression expression) {
		if (expression instanceof Root) return roots;
		
		ObjectAccessExpression read = (ObjectAccessExpression) expression;
		
		Set<Slot> parents = resolve(roots, read.getObject());
		Set<Slot> children = new HashSet<Slot>();
		
		for (Slot slot : parents) {
			HeapValueSlot heapValueSlot = (HeapValueSlot) slot;
			
			for (HeapValue parent : heapValueSlot.getPossibleHeapValues()) {
				Integer ref = parent.getReference();
				
				if (read instanceof ObjectFieldRead) {
					HeapObject object = (HeapObject) parent;
					ObjectFieldRead fieldRead = (ObjectFieldRead) read;
					
					children.add(object.getField(fieldRead.getField().getName()));
				}
				
				if (read instanceof ArrayElementRead) {
					HeapArray array = (HeapArray) parent;
					
					for (int i = 0; i < array.getLength(); ++i) {
						children.add(array.getElement(i));
					}
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
			
			if (value instanceof HeapObject) {
				clone.factory.createObject(reference);
			}
			
			if (value instanceof HeapArray) {
				HeapArray array = (HeapArray) value;
				
				clone.factory.createArray(reference, array.getLength());
			}
		}
		
		for (Integer reference : objects.keySet()) {
			HeapValue value = get(reference);
			HeapValue cloneValue = clone.get(reference);
			
			if (value instanceof HeapObject) {
				HeapObject object = (HeapObject) value;
				HeapObject cloneObject = (HeapObject) cloneValue;
				
				for (String name : object.getFields().keySet()) {
					Slot slot = object.getField(name);
					
					if (slot instanceof HeapValueSlot) {
						HeapValueSlot heapValueSlot = (HeapValueSlot) slot;
						ArrayList<HeapValue> values = new ArrayList<HeapValue>();
						
						for (HeapValue field : heapValueSlot.getPossibleHeapValues()) {
							values.add(clone.get(field.getReference()));
						}
						
						cloneObject.setField(name, values.toArray(new HeapValue[values.size()]));
					} else {
						cloneObject.setField(name, new PrimitiveValue());
					}
				}
			}
			
			if (value instanceof HeapArray) {
				HeapArray array = (HeapArray) value;
				HeapArray cloneArray = (HeapArray) cloneValue;
				
				for (Integer index : array.getElements().keySet()) {
					Slot slot = array.getElement(index);
					
					if (slot instanceof HeapValueSlot) {
						HeapValueSlot heapValueSlot = (HeapValueSlot) slot;
						ArrayList<HeapValue> values = new ArrayList<HeapValue>();
						
						for (HeapValue field : heapValueSlot.getPossibleHeapValues()) {
							values.add(clone.get(field.getReference()));
						}
						
						cloneArray.setElement(index, values.toArray(new HeapValue[values.size()]));
					} else {
						cloneArray.setElement(index, new PrimitiveValue());
					}
				}
			}
		}
		
		return clone;
	}
}
