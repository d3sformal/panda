package gov.nasa.jpf.abstraction.predicate.state.symbols;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.concrete.Reference;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Fields;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Object extends StructuredValue {
	protected Map<String, Field> fields = new HashMap<String, Field>();
	
	public Object(Reference reference) {
		super(reference);
	}
	
	public void setField(String field, Value value) {
		if (!fields.containsKey(field)) {
			fields.put(field, new Field(field, this));
		}
		
		Set<Value> values = fields.get(field).getValues();
		
		values.clear();
		values.add(value);
		value.addSlot(fields.get(field));
	}
	
	public Field getField(String field) {
		if (!fields.containsKey(field)) {
			fields.put(field, new Field(field, this));
		}
		
		return fields.get(field);
	}
	
	public void write(String field, Set<Value> fromValues) {
		getField(field).write(fromValues);
	}
	
	public void clear(String field) {
		if (fields.containsKey(field)) {
			fields.get(field).clear();
		}
	}
	
	@Override
	public void build(int max) {		
		ElementInfo ei = getReference().getElementInfo();
		
		if (ei == null || max == 0) return;
		
		for (int i = 0; i < ei.getNumberOfFields(); ++i) {
			FieldInfo fi = ei.getFieldInfo(i);
			java.lang.Object o = ei.getFieldValueObject(fi.getName());
			
			if (o instanceof ElementInfo) {
				ElementInfo sei = (ElementInfo) o;
				
				Value value;
				
				if (sei.isArray()) {
					value = new Array(new Reference(getReference().getThreadInfo(), sei));
				} else {
					value = new Object(new Reference(getReference().getThreadInfo(), sei));
				}
				
				setField(fi.getName(), value);
				value.build(max - 1);
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
		
		for (String name : fields.keySet()) {
			AccessExpression fieldPath = DefaultObjectFieldRead.create(prefix, name);
			
			for (Value value : fields.get(name).getValues()) {
				Map<AccessExpression, Set<Value>> resolution = value.resolve(fieldPath, max - 1);
				
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
