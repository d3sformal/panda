package gov.nasa.jpf.abstraction.predicate.state.universe;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import gov.nasa.jpf.vm.MJIEnv;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.StaticElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectAccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;

public class Universe {
    public static int NULL = MJIEnv.NULL;
    
    public static Reference nullReference = UniverseNull.nullReference;

    private Map<StructuredValueIdentifier, StructuredValue> currentStructuredRealization = new HashMap<StructuredValueIdentifier, StructuredValue>();
    private Map<PrimitiveValueIdentifier, PrimitiveValue> currentPrimitiveRealization = new HashMap<PrimitiveValueIdentifier, PrimitiveValue>();

    public Universe() {
        currentStructuredRealization.put(nullReference, new UniverseNull());
    }

    public boolean contains(UniverseIdentifier id) {
        if (id instanceof PrimitiveValueIdentifier) {
            return contains((PrimitiveValueIdentifier) id);
        }
        
        if (id instanceof StructuredValueIdentifier) {
            return contains((StructuredValueIdentifier) id);
        }

        return false;
    }

    public boolean contains(StructuredValueIdentifier id) {
        return currentStructuredRealization.containsKey(id);
    }

    public boolean contains(PrimitiveValueIdentifier id) {
        return currentPrimitiveRealization.containsKey(id);
    }

    public UniverseValue get(UniverseIdentifier id) {
        if (id instanceof PrimitiveValueIdentifier) {
            return get((PrimitiveValueIdentifier) id);
        }

        if (id instanceof StructuredValueIdentifier) {
            return get((StructuredValueIdentifier) id);
        }

        return null;
    }

    public StructuredValue get(StructuredValueIdentifier id) {
        return currentStructuredRealization.get(id);
    }

    public PrimitiveValue get(PrimitiveValueIdentifier id) {
        return currentPrimitiveRealization.get(id);
    }

    public void put(UniverseIdentifier id, UniverseValue value) {
        if (id instanceof PrimitiveValueIdentifier) {
            put((PrimitiveValueIdentifier) id, (PrimitiveValue) value);
            return;
        }

        if (id instanceof StructuredValueIdentifier) {
            put((StructuredValueIdentifier) id, (StructuredValue) value);
            return;
        }

        throw new RuntimeException("Updating an unknown type of universe entity `" + id + "` (" + (id == null ? "null" : id.getClass().getSimpleName()) + ") with `" + value + "`");
    }

    public void put(StructuredValueIdentifier id, StructuredValue value) {
        currentStructuredRealization.put(id, value);
    }

    public void put(PrimitiveValueIdentifier id, PrimitiveValue value) {
        currentPrimitiveRealization.put(id, value);
    }

    public PrimitiveValueIdentifier add() {
        PrimitiveValue p = new PrimitiveValue();

        currentPrimitiveRealization.put(p.getIdentifier(), p);

        return p.getIdentifier();
    }

    public void addSlot(StructuredValueIdentifier parent, UniverseSlotKey slotKey, UniverseIdentifier value) {
        UniverseSlot slot = null;

        if (value instanceof PrimitiveValueIdentifier) {
            PrimitiveValueSlot pslot = new PrimitiveValueSlot(parent, slotKey);

            pslot.addPossiblePrimitiveValue((PrimitiveValueIdentifier) value);

            slot = pslot;
        }

        if (value instanceof StructuredValueIdentifier) {
            StructuredValueSlot pslot = new StructuredValueSlot(parent, slotKey);

            pslot.addPossibleStructuredValue((StructuredValueIdentifier) value);

            slot = pslot;
        }

        get(parent).addSlot(slotKey, slot);
        get(value).addParentSlot(parent, slotKey);
    }

    public StructuredValueIdentifier add(ElementInfo elementInfo, ThreadInfo threadInfo) {
        if (elementInfo == null) {
            return nullReference;
        }

        StructuredValueIdentifier identifier;

        if (elementInfo instanceof StaticElementInfo) {
            identifier = new ClassName((StaticElementInfo) elementInfo, threadInfo);
        } else {
            identifier = new Reference(elementInfo, threadInfo);
        }

        if (contains(identifier)) {
            return identifier;
        }

        if (elementInfo.isArray()) {
            UniverseArray array = new UniverseArray(elementInfo, threadInfo);

            currentStructuredRealization.put(array.getReference(), array);

            for (int i = 0; i < elementInfo.arrayLength(); ++i) {
                if (elementInfo.isReferenceArray()) {
                    ElementInfo subElementInfo = threadInfo.getElementInfo(elementInfo.getReferenceElement(i));

                    addSlot(array.getIdentifier(), new ElementIndex(i), add(subElementInfo, threadInfo));
                } else {
                    addSlot(array.getIdentifier(), new ElementIndex(i), add());
                }
            }

            return array.getReference();
        } else {
            StructuredValue value;

            if (elementInfo instanceof StaticElementInfo) {
                value = new UniverseClass((StaticElementInfo) elementInfo, threadInfo);
            } else {
                value = new UniverseObject(elementInfo, threadInfo);
            }

            currentStructuredRealization.put(value.getIdentifier(), value);

            for (int i = 0; i < elementInfo.getNumberOfFields(); ++i) {
                FieldInfo fieldInfo = elementInfo.getFieldInfo(i);

                if (fieldInfo.isReference()) {
                    ElementInfo subElementInfo = threadInfo.getElementInfo(elementInfo.getReferenceField(fieldInfo));

                    addSlot(value.getIdentifier(), new FieldName(fieldInfo.getName()), add(subElementInfo, threadInfo));
                } else {
                    addSlot(value.getIdentifier(), new FieldName(fieldInfo.getName()), add());
                }
            }

            return value.getIdentifier();
        }
    }

    public void lookupValues(Set<? extends UniverseIdentifier> roots, AccessExpression expression, Set<UniverseIdentifier> outValues) {
        for (UniverseIdentifier root : roots) {
            lookupValues(root, expression, outValues);
        }
    }

    public void lookupValues(UniverseIdentifier root, AccessExpression expression, Set<UniverseIdentifier> outValues) {
        if (expression instanceof Root) {
            outValues.add(root);

            return;
        }

        ObjectAccessExpression read = (ObjectAccessExpression) expression;

        Set<UniverseIdentifier> parents = new HashSet<UniverseIdentifier>();

        lookupValues(root, read.getObject(), parents);

        for (UniverseIdentifier parent : parents) {
            StructuredValue parentObject = get((StructuredValueIdentifier) parent);

            if (read instanceof ObjectFieldRead) {
                if (parentObject instanceof UniverseNull) continue;

                Associative object = (Associative) parentObject;
                ObjectFieldRead fieldRead = (ObjectFieldRead) read;

                outValues.addAll(object.getField(new FieldName(fieldRead.getField().getName())).getPossibleValues());
            }

            if (read instanceof ArrayElementRead) {
                if (parentObject instanceof UniverseNull) continue;

                Indexed array = (Indexed) parentObject;

                ArrayElementRead aeRead = (ArrayElementRead) read;

                // Get the exact element in case of a constant index
                if (aeRead.getIndex() instanceof Constant) {
                    int i = ((Constant) aeRead.getIndex()).value.intValue();

                    outValues.addAll(array.getElement(new ElementIndex(i)).getPossibleValues());
                } else {
                    for (int i = 0; i < array.getLength(); ++i) {
                        outValues.addAll(array.getElement(new ElementIndex(i)).getPossibleValues());
                    }
                }
            }
        }
    }

    @Override
    public Universe clone() {
        Universe universe = new Universe();

        universe.currentStructuredRealization.putAll(currentStructuredRealization);
        universe.currentPrimitiveRealization.putAll(currentPrimitiveRealization);

        for (StructuredValue value : currentStructuredRealization.values()) {
            value.freeze();

            for (UniverseSlot slot : value.getSlots().values()) {
                slot.freeze();
            }
        }

        // May not be needed
        // Primitive values do not change
        /*
        for (PrimitiveValue value : currentPrimitiveRealization.values()) {
            value.freeze();
        }
        */

        return universe;
    }

    public Set<StructuredValueIdentifier> getStructuredValues() {
        return currentStructuredRealization.keySet();
    }
}