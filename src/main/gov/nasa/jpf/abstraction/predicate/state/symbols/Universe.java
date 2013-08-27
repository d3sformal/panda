package gov.nasa.jpf.abstraction.predicate.state.symbols;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ObjectAccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultAccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

class Field {
	Integer object;
	String name;
	
	Field(Integer object, String name) {
		this.object = object;
		this.name = name;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Field) {
			Field f = (Field) o;
			
			return object == f.object && name.equals(f.name);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return object + name.hashCode();
	}
}

class Element {
	Integer array;
	Integer index;
	
	Element(Integer array, Integer index) {
		this.array = array;
		this.index = index;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Element) {
			Element e = (Element) o;
			
			return array == e.array && index == e.index;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return array + index;
	}
}

abstract class Value {
	boolean isNew;
	
	protected Value(boolean isNew) {
		this.isNew = isNew;
	}
}

abstract class PrimitiveValue extends Value {

	protected PrimitiveValue() {
		super(true);
	}
	
}

class HeapObject extends Value {
	Integer reference;
	
	protected HeapObject(Integer reference, boolean isNew) {
		super(isNew);
		
		this.reference = reference;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof HeapObject) {
			HeapObject ho = (HeapObject) o;
			
			return reference == ho.reference;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return reference.hashCode();
	}
	
	@Override
	public String toString() {
		return "ref(" + reference + ")";
	}
	
	public static HeapObject create(Integer reference, Universe universe) {
		return new HeapObject(reference, universe.isObject(reference));
	}
}

interface Slot {
	public Slot merge(Slot slot);
	public Set<Value> getPossibilities();
}

class ObjectSlot implements Slot {
	Set<HeapObject> possibilities = new HashSet<HeapObject>();
	
	protected ObjectSlot(HeapObject... objects) {
		add(objects);
	}
	
	public void add(HeapObject... objects) {
		add(Arrays.asList(objects));
	}
	
	public void add(Collection<HeapObject> objects) {
		possibilities.addAll(objects);
	}
	
	public static ObjectSlot create() {
		return new ObjectSlot();
	}
	
	public static ObjectSlot create(HeapObject object) {
		return new ObjectSlot(object);
	}
	
	public static ObjectSlot create(HeapObject... objects) {
		return new ObjectSlot(objects);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ObjectSlot) {
			ObjectSlot slot = (ObjectSlot) o;
			
			return possibilities.equals(slot);
		}
		
		return false;
	}

	@Override
	public Slot merge(Slot slot) {
		if (slot instanceof ObjectSlot) {
			ObjectSlot objectSlot = (ObjectSlot) slot;
			ObjectSlot ret = create();
			
			ret.add(possibilities);
			ret.add(objectSlot.possibilities);
			
			return ret;
		}
		
		throw new RuntimeException("Merging incompatible slots: " + this + ", " + slot);
	}
	
	@Override
	public String toString() {
		return possibilities.toString();
	}

	@Override
	public Set<Value> getPossibilities() {
		Set<Value> ret = new HashSet<Value>();
		
		ret.addAll(possibilities);
		
		return ret;
	}
}

class PrimitiveSlot implements Slot {
	Set<PrimitiveValue> possibilities = new HashSet<PrimitiveValue>();
	
	protected PrimitiveSlot(PrimitiveValue... primitives) {
		add(primitives);
	}
	
	public void add(PrimitiveValue... primitives) {
		add(Arrays.asList(primitives));
	}
	
	public void add(Collection<PrimitiveValue> primitives) {
		possibilities.addAll(primitives);
	}
	
	public static PrimitiveSlot create() {
		return new PrimitiveSlot();
	}
	
	public static PrimitiveSlot create(PrimitiveValue primitives) {
		return new PrimitiveSlot(primitives);
	}
	
	public static PrimitiveSlot create(PrimitiveValue... primitives) {
		return new PrimitiveSlot(primitives);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PrimitiveSlot) {
			PrimitiveSlot slot = (PrimitiveSlot) o;
			
			return possibilities.equals(slot);
		}
		
		return false;
	}

	@Override
	public Slot merge(Slot slot) {
		if (slot instanceof PrimitiveSlot) {
			PrimitiveSlot objectSlot = (PrimitiveSlot) slot;
			PrimitiveSlot ret = create();
			
			ret.add(possibilities);
			ret.add(objectSlot.possibilities);
			
			return ret;
		}
		
		throw new RuntimeException("Merging incompatible slots: " + this + ", " + slot);
	}
	
	@Override
	public String toString() {
		return possibilities.toString();
	}
	
	@Override
	public Set<Value> getPossibilities() {
		Set<Value> ret = new HashSet<Value>();
		
		ret.addAll(possibilities);
		
		return ret;
	}
}

class PrimitiveField extends PrimitiveValue {
	Integer reference;
	String name;
	
	protected PrimitiveField(Integer reference, String name) {		
		this.reference = reference;
		this.name = name;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PrimitiveField) {
			PrimitiveField f = (PrimitiveField) o;
			
			return reference == f.reference && name.equals(f.name);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return reference + name.hashCode();
	}
	
	@Override
	public String toString() {
		return "ref(" + reference + ")." + name;
	}
	
	public static PrimitiveField create(Integer reference, String name) {
		if (reference == null || name == null) {
			return null;
		}
		
		return new PrimitiveField(reference, name);
	}
}

class PrimitiveElement extends PrimitiveValue {
	Integer reference;
	Integer index;
	
	protected PrimitiveElement(Integer reference, Integer index) {		
		this.reference = reference;
		this.index = index;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PrimitiveElement) {
			PrimitiveElement e = (PrimitiveElement) o;
			
			return reference == e.reference && index == e.index;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return reference + index;
	}
	
	@Override
	public String toString() {
		return "ref(" + reference + ")[" + index + "]";
	}
	
	public static PrimitiveElement create(Integer reference, Integer index) {
		if (reference == null || index == null) {
			return null;
		}
		
		return new PrimitiveElement(reference, index);
	}
}

class PrimitiveLocal extends PrimitiveValue {
	String name;
	
	protected PrimitiveLocal(String name) {		
		this.name = name;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PrimitiveLocal) {
			PrimitiveLocal l = (PrimitiveLocal) o;
			
			return name.equals(l.name);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public String toString() {
		return "var(" + name + ")";
	}
	
	public static PrimitiveLocal create(String name) {
		if (name == null) {
			return null;
		}
		
		return new PrimitiveLocal(name);
	}
}

class UniverseState {
	Map<Root, Slot> locals = new HashMap<Root, Slot>();
	Map<Root, Slot> classes = new HashMap<Root, Slot>();
	
	Set<Integer> objects = new HashSet<Integer>();
	Map<Integer, Integer> arraylengths = new HashMap<Integer, Integer>();
	
	Map<Field, Slot> fields = new HashMap<Field, Slot>();
	Map<Element, Slot> elements = new HashMap<Element, Slot>();
}

public class Universe {
	private UniverseState state = new UniverseState();
	
	public boolean isObject(Integer reference) {
		return state.objects.contains(reference);
	}
	public boolean isArray(Integer reference) {
		return state.arraylengths.containsKey(reference);
	}
	
	public void addObject(Integer reference) {
		state.objects.add(reference);
		}
	public void addArray(Integer reference, Integer length) {
		state.objects.add(reference);
		state.arraylengths.put(reference, length);
	}
	
	public Map<String, Slot> getFields(Integer reference) {
		Map<String, Slot> fields = new HashMap<String, Slot>();
		
		for (Field field : state.fields.keySet()) {
			if (field.object == reference) {
				fields.put(field.name, state.fields.get(field));
			}
		}
		
		return fields;
	}
	public Map<Integer, Slot> getElements(Integer reference) {
		Map<Integer, Slot> elements = new HashMap<Integer, Slot>();
		
		for (Element element : state.elements.keySet()) {
			if (element.array == reference) {
				elements.put(element.index, state.elements.get(element));
			}
		}
		
		return elements;
	}
	
	public Slot getField(Integer reference, String name) {
		return state.fields.get(new Field(reference, name));
	}
	public Slot getElement(Integer reference, Integer index) {
		return state.elements.get(new Element(reference, index));
	}
	public Integer getLength(Integer reference) {
		return state.arraylengths.get(reference);
	}
	
	public void setField(Integer reference, String name, Slot slot) {
		if (slot instanceof PrimitiveSlot) {
			slot = PrimitiveSlot.create(PrimitiveField.create(reference, name));
		}
		
		state.fields.put(new Field(reference, name), slot);
	}
	public void setElement(Integer reference, Integer index, Slot slot) {
		if (slot instanceof PrimitiveSlot) {
			slot = PrimitiveSlot.create(PrimitiveElement.create(reference, index));
		}
		
		state.elements.put(new Element(reference, index), slot);
	}
	
	public Set<Root> getLocals() {
		return state.locals.keySet();
	}
	public Slot getLocal(Root root) {
		return state.locals.get(root);
	}
	public Set<Root> getClasses() {
		return state.classes.keySet();
	}
	public Slot getClass(Root root) {
		return state.classes.get(root);
	}
	
	public void addLocal(Root root, Slot slot) {
		if (slot instanceof PrimitiveSlot) {
			slot = PrimitiveSlot.create(PrimitiveLocal.create(root.getName()));
		}
		
		state.locals.put(root, slot);
	}
	
	public void addClass(Root root, Integer classObjectReference) {
		state.classes.put(root, ObjectSlot.create(HeapObject.create(classObjectReference, this)));
	}
	
	private static int NULL = -1;
	
	public Universe() {
		addObject(NULL);
	}
	
	public HeapObject add(ThreadInfo threadInfo, ElementInfo elementInfo) {
		if (elementInfo == null) return null;
		
		Integer ref = elementInfo.getObjectRef();
		HeapObject ret = HeapObject.create(ref, this);
		
		if (!ret.isNew) return ret;
		
		if (elementInfo.isArray()) {
			addArray(ref, elementInfo.arrayLength());
			
			for (int i = 0; i < elementInfo.arrayLength(); ++i) {
				Slot slot;				
				
				if (elementInfo.isReferenceArray()) {
					Integer subRef = elementInfo.getReferenceElement(i);
					
					ElementInfo subElementInfo = threadInfo.getElementInfo(subRef);
					
					slot = ObjectSlot.create(add(threadInfo, subElementInfo));
				} else {
					slot = PrimitiveSlot.create(PrimitiveElement.create(ref, i));
				}
				
				setElement(ref, i, slot);
			}
		} else {
			addObject(ref);
			
			for (int i = 0; i < elementInfo.getNumberOfFields(); ++i) {
				Slot slot;
				FieldInfo fieldInfo = elementInfo.getFieldInfo(i);
				
				if (fieldInfo.isNumericField()) {
					slot = PrimitiveSlot.create(PrimitiveField.create(ref, fieldInfo.getName()));
				} else {
					Integer subRef = elementInfo.getReferenceField(fieldInfo);
					
					ElementInfo subElementInfo = threadInfo.getElementInfo(subRef);
					
					slot = ObjectSlot.create(add(threadInfo, subElementInfo));
				}
				
				setField(ref, fieldInfo.getName(), slot);
			}
		}
		
		return ret;
	}
	
	public Slot resolve(AccessExpression path) {
		
		if (path.getRoot().isLocalVariable()) {
			return resolve(getLocal(path.getRoot()), path);
		}
		
		if (path.isStatic() && path.getLength() >= 2) {
			return resolve(getClass(path.getRoot()), path);
		}
		
		return null;
	}
	
	public Slot resolve(Slot roots, AccessExpression path) {
		if (path instanceof Root) return roots;
		
		ObjectAccessExpression read = (ObjectAccessExpression) path;
		
		ObjectSlot objects = (ObjectSlot) resolve(roots, read.getObject());
		Set<Slot> subObjects = new HashSet<Slot>();
		
		for (HeapObject object : objects.possibilities) {
			Integer ref = object.reference;
			
			if (read instanceof ObjectFieldRead) {
				ObjectFieldRead fieldRead = (ObjectFieldRead) read;
				subObjects.add(getField(ref, fieldRead.getField().getName()));
			}
			
			if (read instanceof ArrayElementRead) {
				for (int i = 0; i < getLength(ref); ++i) {
					subObjects.add(getElement(ref, i));
				}
			}
		}
		
		Iterator<Slot> it = subObjects.iterator();
		
		Slot ret = it.next();
		
		while (it.hasNext()) {
			ret = ret.merge(it.next());
		}
		
		return ret;
	}
	
	public Set<AccessExpression> writeLocal(Expression from, Root to) {
		Set<AccessExpression> affected = new HashSet<AccessExpression>();
		Slot slot;
		
		if (from instanceof AccessExpression) {
			slot = resolve((AccessExpression) from);	
		} else {
			slot = PrimitiveSlot.create(PrimitiveLocal.create(to.getName()));
		}
		
		addLocal(to, slot);
		affected.add(to);
		
		return affected;
	}
	
	public Map<AccessExpression, Set<Value>> getAccessExpressions() {
		Map<AccessExpression, Set<Value>> processed = new HashMap<AccessExpression, Set<Value>>();
		Map<AccessExpression, Set<Value>> current = new HashMap<AccessExpression, Set<Value>>();
		
		for (Root l : getLocals()) {
			current.put(l, getLocal(l).getPossibilities());
		}
		
		for (Root c : getClasses()) {
			current.put(c, getClass(c).getPossibilities());
		}
		
		int max = 10; //TODO limit properly 
		
		while (!current.isEmpty() && max >= 0) {
			processed.putAll(current);
			
			Map<AccessExpression, Set<Value>> next = new HashMap<AccessExpression, Set<Value>>();
			
			for (AccessExpression prefix : current.keySet()) {
				for (Value value : current.get(prefix)) {
					if (value instanceof HeapObject) {
						HeapObject object = (HeapObject) value;
						
						Map<String, Slot> fields = getFields(object.reference);
						
						for (String fieldName : fields.keySet()) {
							AccessExpression expr = DefaultObjectFieldRead.create(prefix, fieldName);
							
							if (!next.containsKey(expr)) {
								next.put(expr, new HashSet<Value>());
							}
							
							next.get(expr).addAll(fields.get(fieldName).getPossibilities());
						}
						
						Map<Integer, Slot> elements = getElements(object.reference);
						
						for (Integer index : elements.keySet()) {
							AccessExpression expr = DefaultArrayElementRead.create(prefix, Constant.create(index));
							
							if (!next.containsKey(expr)) {
								next.put(expr, new HashSet<Value>());
							}
							
							next.get(expr).addAll(elements.get(index).getPossibilities());
						}
					}
				}
			}
			
			current = next;
			--max;
		}
		
		return processed;
	}
	
	public Set<AccessExpression> write(Expression from, AccessExpression to) {
		Slot fromSlot;
		
		if (from instanceof AccessExpression) {
			fromSlot = resolve((AccessExpression) from);
		} else {
			fromSlot = PrimitiveSlot.create();
		}
		
		ObjectSlot toSlotParent = (ObjectSlot) resolve(to.cutTail());
		
		for (HeapObject object : toSlotParent.possibilities) {			
			if (to instanceof ObjectFieldRead) {
				ObjectFieldRead read = (ObjectFieldRead) to;
				
				setField(object.reference, read.getField().getName(), fromSlot);
			}
			
			if (to instanceof ArrayElementRead) {
				for (int i = 0; i < getLength(object.reference); ++i) {
					setElement(object.reference, i, fromSlot);
				}
			}
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
        
        Set<AccessExpression> order = new TreeSet<AccessExpression>(new Comparator<AccessExpression>() {
                public int compare(AccessExpression o1, AccessExpression o2) {
                        
                		if (o1.getLength() < o2.getLength()) return -1;
                		if (o1.getLength() > o2.getLength()) return +1;
                        
                        return o1.toString(Notation.DOT_NOTATION).compareTo(o2.toString(Notation.DOT_NOTATION));
                }
        });
        
        Map<AccessExpression, Set<Value>> map = getAccessExpressions();
        
        order.addAll(map.keySet());
        
        int length = 0;
        int padding = 4;
        
        for (AccessExpression expr : order) {
            length = Math.max(length, expr.toString(Notation.DOT_NOTATION).length());
        }
        
        for (AccessExpression expr : order) {
                ret.append(expr.toString(Notation.DOT_NOTATION));
                
                for (int i = 0; i < length - expr.toString(Notation.DOT_NOTATION).length() + padding; ++i) {
                	ret.append(" ");
                }
                
                ret.append(map.get(expr));
                ret.append("\n");
        }
        
        return ret.toString();
	}
	
	public static void main(String[] args) {
		Universe universe = new Universe();
		
		universe.addArray(1, 2);
		universe.addObject(2);
		universe.addObject(3);
		
		Slot v1 = ObjectSlot.create(HeapObject.create(2, universe), HeapObject.create(3, universe));
		Slot v2 = ObjectSlot.create(HeapObject.create(1, universe));
		
		universe.setElement(1, 0, v1);
		universe.setElement(1, 1, v2);
		
		Slot v3 = ObjectSlot.create(HeapObject.create(1, universe));
		
		universe.addLocal(DefaultAccessExpression.createFromString("a").getRoot(), v3);
		universe.addLocal(DefaultAccessExpression.createFromString("c").getRoot(), v1);
		
		Slot v4 = PrimitiveSlot.create(PrimitiveLocal.create("b"));
		
		universe.addLocal(DefaultAccessExpression.createFromString("a").getRoot(), v3);
		universe.addLocal(DefaultAccessExpression.createFromString("b").getRoot(), v4);
		
		universe.write(DefaultAccessExpression.createFromString("b"), DefaultAccessExpression.createFromString("a[0].x"));
		
		System.out.println("Result: " + universe.resolve(DefaultAccessExpression.createFromString("a[0].x")));
		System.out.println(universe.toString());
	}
}
