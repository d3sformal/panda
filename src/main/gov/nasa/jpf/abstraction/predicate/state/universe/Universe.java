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

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectAccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;

public class Universe {
    public static int NULL = MJIEnv.NULL;
    
    private static Reference nullReference = UniverseNull.nullReference;
    private static UniverseNull nullObject = new UniverseNull();

    private Map<StructuredValueIdentifier, UniverseStructuredValue> currentRealization = new HashMap<StructuredValueIdentifier, UniverseStructuredValue>();

    public Universe() {
        currentRealization.put(nullReference, nullObject);
    }

    public boolean contains(StructuredValueIdentifier id) {
        return currentRealization.containsKey(id);
    }

    public UniverseStructuredValue get(StructuredValueIdentifier id) {
        return currentRealization.get(id);
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

            for (int i = 0; i < elementInfo.arrayLength(); ++i) {
                if (elementInfo.isReferenceArray()) {
                    ElementInfo subElementInfo = threadInfo.getElementInfo(elementInfo.getReferenceElement(i));

                    array.setElement(new ElementIndex(i), add(subElementInfo, threadInfo));
                } else {
                    array.setElement(new ElementIndex(i), new PrimitiveValueIdentifier());
                }
            }

            currentRealization.put(array.getReference(), array);

            return array.getReference();
        } else {
            UniverseStructuredValue value;

            if (elementInfo instanceof StaticElementInfo) {
                value = new UniverseClass((StaticElementInfo) elementInfo, threadInfo);
            } else {
                value = new UniverseObject(elementInfo, threadInfo);
            }

            for (int i = 0; i < elementInfo.getNumberOfFields(); ++i) {
                FieldInfo fieldInfo = elementInfo.getFieldInfo(i);

                if (fieldInfo.isReference()) {
                    ElementInfo subElementInfo = threadInfo.getElementInfo(elementInfo.getReferenceField(fieldInfo));

                    ((Associative) value).setField(new FieldName(fieldInfo.getName()), add(subElementInfo, threadInfo));
                } else {
                    ((Associative) value).setField(new FieldName(fieldInfo.getName()), new PrimitiveValueIdentifier());
                }
            }

            currentRealization.put(value.getIdentifier(), value);

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
        }

        ObjectAccessExpression read = (ObjectAccessExpression) expression;

        Set<UniverseIdentifier> parents = new HashSet<UniverseIdentifier>();

        lookupValues(root, read.getObject(), parents);

        for (UniverseIdentifier parent : parents) {
            StructuredValueIdentifier parentObject = (StructuredValueIdentifier) parent;

            if (read instanceof ObjectFieldRead) {
                if (parent instanceof UniverseNull) continue;

                UniverseObject object = (UniverseObject) parentObject;
                ObjectFieldRead fieldRead = (ObjectFieldRead) read;

                outValues.addAll(object.getField(new FieldName(fieldRead.getField().getName())).getPossibleValues());
            }

            if (read instanceof ArrayElementRead) {
                if (parent instanceof UniverseNull) continue;

                UniverseArray array = (UniverseArray) parentObject;

                for (int i = 0; i < array.getLength(); ++i) {
                    outValues.addAll(array.getElement(new ElementIndex(i)).getPossibleValues());
                }
            }
        }
    }

    @Override
    public Universe clone() {
        Universe universe = new Universe();

        universe.currentRealization.putAll(currentRealization);

        return universe;
    }

    public Set<StructuredValueIdentifier> getStructuredValues() {
        return currentRealization.keySet();
    }
}
