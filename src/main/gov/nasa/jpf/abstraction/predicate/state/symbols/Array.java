package gov.nasa.jpf.abstraction.predicate.state.symbols;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.concrete.Reference;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Array extends StructuredValue {
	private Map<Integer, Element> elements = new HashMap<Integer, Element>();
	
	public Array(Reference reference) {
		super(reference);
	}
	
	public void setElement(int index, Value value) {
		if (!elements.containsKey(index)) {
			elements.put(index, new Element(index, this));
		}
		
		Set<Value> values = elements.get(index).getValues();
		
		values.clear();
		values.add(value);
		value.addSlot(elements.get(index));
	}
	
	public Element getElement(int index) {
		if (!elements.containsKey(index)) {
			elements.put(index, new Element(index, this));
		}
		
		return elements.get(index);
	}
	
	public void write(int index, Set<Value> fromValues) {
		getElement(index).write(fromValues);
	}
	
	public void clear(int index) {
		if (elements.containsKey(index)) {
			elements.get(index).clear();
		}
	}
	
	@Override
	public void build(int max) {		
		ElementInfo ei = getReference().getElementInfo();
				
		if (ei == null || max == 0) return;
		
		for (int i = 0; i < ei.arrayLength(); ++i) {
			if (ei.isReferenceArray()) {
				ElementInfo sei = getReference().getThreadInfo().getElementInfo(ei.getReferenceElement(i));
				Value value = new Object(new Reference(getReference().getThreadInfo(), null));
				
				if (sei != null) {
					if (sei.isArray()) {
						value = new Array(new Reference(getReference().getThreadInfo(), sei));
					} else {
						value = new Object(new Reference(getReference().getThreadInfo(), sei));
					}
				}
				
				setElement(i, value);
				value.build(max - 1);
			} else {
				setElement(i, new PrimitiveValue());
			}
		}
	}

	@Override
	public Map<AccessExpression, Set<Value>> resolve(AccessExpression prefix, int max) {
		Map<AccessExpression, Set<Value>> ret = new HashMap<AccessExpression, Set<Value>>();
		Set<Value> vals = new HashSet<Value>();
		
		if (max == 0) return ret;
		
		vals.add(this);
		ret.put(prefix, vals);
		
		for (Integer index : elements.keySet()) {
			AccessExpression elementPath = DefaultArrayElementRead.create(prefix, Constant.create(index));
			
			for (Value value : elements.get(index).getValues()) {
				Map<AccessExpression, Set<Value>> resolution = value.resolve(elementPath, max - 1);
				
				for (AccessExpression path : resolution.keySet()) {
					if (ret.containsKey(path)) {
						ret.get(path).addAll(resolution.get(path));
					} else {
						ret.put(path, resolution.get(path));
					}
				}
			}
		}
		
		return ret;
	}

}
