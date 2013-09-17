package gov.nasa.jpf.abstraction.predicate.state.symbols;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ObjectAccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.concrete.Reference;
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
	private Map<UniverseIdentifier, StructuredValue> objects = new HashMap<UniverseIdentifier, StructuredValue>();
	
	private static int ID = 0;
	public static int NULL = -1;
	
	private int id;

	public Universe() {
		id = ID++;
		factory.createNull();
	}
	
	public int getID() {
		return id;
	}

	public ValueFactory getFactory() {
		return factory;
	}
	
	public boolean contains(UniverseIdentifier identifier) {
		return objects.containsKey(identifier);
	}
	
	public boolean contains(int reference) {
		return contains(new HeapObjectReference(reference));
	}
	
	public boolean contains(String className) {
		return contains(new ClassStaticsReference(className));
	}
	
	public StructuredValue get(UniverseIdentifier identifier) {
		return objects.get(identifier);
	}
	
	public StructuredValue get(int reference) {
		return objects.get(new HeapObjectReference(reference));
	}
	
	public StructuredValue get(String className) {
		return objects.get(new ClassStaticsReference(className));
	}
	
	public void add(StructuredValue value) {
		objects.put(value.getReference(), value);
	}
	
	public StructuredValue add(Reference reference) {
		return add(reference.getThreadInfo(), reference.getElementInfo());
	}
	
	public StructuredValue add(ThreadInfo threadInfo, ElementInfo elementInfo) {		
		if (elementInfo == null) return get(NULL);
		
		String className = elementInfo.getClassInfo().getName(); // TODO
		Integer ref = elementInfo.getObjectRef();
		
		boolean existed;
		
		if (elementInfo instanceof StaticElementInfo) {
			existed = contains(className);
		} else {
			existed = contains(ref);
		}
		
		if (elementInfo.isArray()) {			
			StructuredArray array = factory.createArray(ref, elementInfo.arrayLength());
			
			if (existed) return (StructuredValue) array;
			
			for (int i = 0; i < elementInfo.arrayLength(); ++i) {
				if (elementInfo.isReferenceArray()) {
					Integer subRef = elementInfo.getReferenceElement(i);
					
					ElementInfo subElementInfo = threadInfo.getElementInfo(subRef);
					
					array.setElement(i, add(threadInfo, subElementInfo));
				} else {
					array.setElement(i, new PrimitiveValue(this));
				}
			}
			
			return (StructuredValue) array;
		} else {
			StructuredObject object;
			
			if (elementInfo instanceof StaticElementInfo) {
				object = factory.createClass(className);
			} else  {
				object = factory.createObject(ref);
			}
			
			if (existed) return (StructuredValue) object;
			
			for (int i = 0; i < elementInfo.getNumberOfFields(); ++i) {
				FieldInfo fieldInfo = elementInfo.getFieldInfo(i);
				
				if (fieldInfo.isReference()) {
					Integer subRef = elementInfo.getReferenceField(fieldInfo);
					
					ElementInfo subElementInfo = threadInfo.getElementInfo(subRef);
					
					object.setField(fieldInfo.getName(), add(threadInfo, subElementInfo));
				} else {
					object.setField(fieldInfo.getName(), new PrimitiveValue(this));
				}
			}
			
			return (StructuredValue) object;
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
			StructuredValue parent = (StructuredValue) value;
			
			if (read instanceof ObjectFieldRead) {
				StructuredObject object = (StructuredObject) parent;
				ObjectFieldRead fieldRead = (ObjectFieldRead) read;
				
				children.addAll(object.getField(fieldRead.getField().getName()).getPossibleValues());
			}
			
			if (read instanceof ArrayElementRead) {
				StructuredArray array = (StructuredArray) parent;
				
				for (int i = 0; i < array.getLength(); ++i) {
					children.addAll(array.getElement(i).getPossibleValues());
				}
			}
		}
		
		return children;
	}
	
	public Set<StructuredValue> getModifiedObjects(Universe universe) {
		Set<StructuredValue> ret = new HashSet<StructuredValue>();
		
		for (UniverseIdentifier identifier : objects.keySet()) {
			StructuredValue originalValue = objects.get(identifier);
			StructuredValue modifiedValue = universe.objects.get(identifier);
			
			if (originalValue instanceof StructuredObject) {
				if (modifiedValue instanceof StructuredObject) {
					StructuredObject originalObject = (StructuredObject) originalValue;
					StructuredObject modifiedObject = (StructuredObject) modifiedValue;
					
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
			
			if (originalValue instanceof StructuredArray) {
				if (modifiedValue instanceof StructuredArray) {
					StructuredArray originalArray = (StructuredArray) originalValue;
					StructuredArray modifiedArray = (StructuredArray) modifiedValue;
					
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
		
		for (UniverseIdentifier identifier : objects.keySet()) {
			StructuredValue value = objects.get(identifier);
			
			value.cloneInto(clone);
		}
		
		return clone;
	}
	
	public void check() {
		// CHECK
		for (UniverseIdentifier identifier : objects.keySet()) {
			StructuredValue value = objects.get(identifier);

			for (Slot s : value.getSlots()) {
				Value parent = s.getParent();

				if (parent.getUniverseID() != value.getUniverseID()) throw new RuntimeException(value + " (univ: " + value.getUniverseID() + ") with parent " + parent + " (univ: " + parent.getUniverseID() + ")");
			}

			if (value instanceof StructuredObject) {
				StructuredObject o = (StructuredObject) value;

				for (String field : o.getFields().keySet()) {
					for (Value f : o.getField(field).getPossibleValues()) {
						if (f.getUniverseID() != value.getUniverseID()) throw new RuntimeException(value + " (univ: " + value.getUniverseID() + ") with field " + f + " (univ: " + f.getUniverseID() + ")");
					}
				}
			}

			if (value instanceof StructuredArray) {
				StructuredArray o = (StructuredArray) value;

				for (Integer index : o.getElements().keySet()) {
					for (Value e : o.getElement(index).getPossibleValues()) {
						if (e.getUniverseID() != value.getUniverseID()) throw new RuntimeException(value + " (univ: " + value.getUniverseID() + ") with element " + e + " (univ: " + e.getUniverseID() + ")");
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		
		for (UniverseIdentifier i : objects.keySet()) {
			traverse(objects.get(i), new HashSet<Value>(), "", ret);
		}
		
		return ret.toString();
	}

	private void traverse(Value value, Set<Value> visited, String indentation, StringBuilder builder) {
		boolean isVisited = visited.contains(value);
		
		visited.add(value);
		
		builder.append(value + "\n");
		
		if (isVisited) return;
		if (value instanceof PrimitiveValue) return;
		
		indentation += "  ";
		
		if (value instanceof StructuredObject) {
			StructuredObject o = (StructuredObject) value;
			
			for (String f : o.getFields().keySet()) {
				for (Value v : o.getField(f).getPossibleValues()) {
					builder.append(indentation + f + ": ");
					
					traverse(v, visited, indentation, builder);
				}
			}
		}
		
		if (value instanceof StructuredArray) {
			StructuredArray o = (StructuredArray) value;
			
			for (Integer i : o.getElements().keySet()) {
				for (Value v : o.getElement(i).getPossibleValues()) {
					builder.append(indentation + i + ": ");
					
					traverse(v, visited, indentation, builder);
				}
			}
		}
	}
	
	public Set<StructuredValue> getStructuredValues() {
		Set<StructuredValue> ret = new HashSet<StructuredValue>();
		
		ret.addAll(objects.values());
		
		return ret;
	}
}
