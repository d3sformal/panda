package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.ThreadInfoSet;

/**
 * Represents a null object whose elements/fields are not accessible
 */
public class Null extends StructuredValue implements StructuredObject, StructuredArray {

    private static class NullClassInfo extends ClassInfo {
        @Override
        public String getName() {
            return "null";
        }
    }

    private static class NullElementInfo extends ElementInfo {
        public NullElementInfo() {
            super(Universe.NULL, new NullClassInfo(), null, null, null);
        }

        @Override
        public ElementInfo getModifiableInstance() {
            return this;
        }
        @Override
        public ThreadInfoSet createThreadInfoSet(ThreadInfo ti) {
            return null;
        }
        @Override
        public boolean isObject() {
            return true;
        }
        @Override
        public boolean hasFinalizer() {
            return false;
        }
        @Override
        public FieldInfo getDeclaredFieldInfo(String clsBase, String fname) {
            return null;
        }
        @Override
        public FieldInfo getFieldInfo(String fname) {
            return null;
        }
        @Override
        public FieldInfo getFieldInfo(int index) {
            return null;
        }
        @Override
        public int getNumberOfFieldsOrElements() {
            return 0;
        }
        @Override
        public int getNumberOfFields() {
            return 0;
        }
    }

    private static ElementInfo nullElementInfo;
    
    static {
        try {
            nullElementInfo = new NullElementInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	protected Null(Universe universe) {
		super(universe, new HeapObjectReference(Universe.NULL), nullElementInfo);
	}

    @Override
    protected final int compareSignatureTo(StructuredValue value) {
        if (value instanceof Null) {
            return 0;
        }

        return compareClasses(value);
    }

    @Override
    protected final int compareSlots(StructuredValue value) {
        return 0;
    }

	@Override
	public void setElement(Integer index, StructuredValue... values) {
		throw new RuntimeException("NullPointer accessed");
	}

	@Override
	public void setElement(Integer index, PrimitiveValue... values) {
		throw new RuntimeException("NullPointer accessed");
	}

	@Override
	public Slot getElement(Integer index) {
		throw new RuntimeException("NullPointer accessed");
	}

	@Override
	public Map<Integer, Slot> getElements() {
		return new HashMap<Integer, Slot>();
	}

	@Override
	public Integer getLength() {
		return 0;
	}

	@Override
	public void setField(String name, StructuredValue... values) {
		throw new RuntimeException("NullPointer accessed");
	}

	@Override
	public void setField(String name, PrimitiveValue... values) {
		throw new RuntimeException("NullPointer accessed");
	}

	@Override
	public Slot getField(String name) {
		throw new RuntimeException("NullPointer accessed");
	}

	@Override
	public Map<String, Slot> getFields() {
		return new HashMap<String, Slot>();
	}

	@Override
	public Null cloneInto(Universe universe, Slot slot) {		
		Null clone = cloneInto(universe);
		
		clone.addSlot(slot);
		
		return clone;
	}
	
	@Override
	public Null cloneInto(Universe universe) {	
		return universe.getFactory().createNull();
	}

}
