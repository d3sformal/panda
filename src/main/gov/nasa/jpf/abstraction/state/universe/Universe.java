/*
 * Copyright (C) 2015, Charles University in Prague.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.nasa.jpf.abstraction.state.universe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.StaticElementInfo;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthRead;
import gov.nasa.jpf.abstraction.common.access.ObjectAccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.Root;

public class Universe {
    public static int NULL = MJIEnv.NULL;

    public static Reference nullReference = UniverseNull.nullReference;

    private Map<StructuredValueIdentifier, StructuredValue> currentStructuredRealization = new HashMap<StructuredValueIdentifier, StructuredValue>();
    private Map<PrimitiveValueIdentifier, PrimitiveValue> currentPrimitiveRealization = new HashMap<PrimitiveValueIdentifier, PrimitiveValue>();
    private Reference fresh = nullReference;

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

    public Reference getFresh() {
        return fresh;
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
        } else {
            StructuredValueSlot sslot = new StructuredValueSlot(parent, slotKey);

            sslot.addPossibleStructuredValue((StructuredValueIdentifier) value);

            slot = sslot;
        }

        get(parent).addSlot(slotKey, slot);

        if (get(value).isFrozen()) {
            put(value, get(value).createShallowCopy());
        }
        get(value).addParentSlot(parent, slotKey);
    }

    public StructuredValueIdentifier add(ElementInfo elementInfo, ThreadInfo threadInfo) {
        if (elementInfo == null) {
            return nullReference;
        }

        StructuredValueIdentifier identifier;

        if (elementInfo instanceof StaticElementInfo) {
            identifier = new ClassName((StaticElementInfo) elementInfo);
        } else {
            identifier = new Reference(elementInfo);
        }

        if (contains(identifier)) {
            return identifier;
        }

        if (identifier instanceof Reference) {
            fresh = (Reference) identifier;
        }

        if (elementInfo.isArray()) {
            UniverseArray array = new UniverseArray(elementInfo);

            currentStructuredRealization.put(array.getReference(), array);

            for (int i = 0; i < elementInfo.arrayLength(); ++i) {
                if (elementInfo.isReferenceArray()) {
                    ElementInfo subElementInfo = threadInfo.getElementInfo(elementInfo.getReferenceElement(i));

                    addSlot(array.getIdentifier(), new ElementIndex(i), add(subElementInfo, threadInfo));
                } else {
                    addSlot(array.getIdentifier(), new ElementIndex(i), add());
                }
            }

            array.getLengthSlot().addPossiblePrimitiveValue(add());

            return array.getReference();
        } else {
            StructuredValue value;

            if (elementInfo instanceof StaticElementInfo) {
                value = new UniverseClass((StaticElementInfo) elementInfo);
            } else {
                value = new UniverseObject(elementInfo);
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

        // Preallocate SlotKey (FieldName or ElementIndex) to avoid repetitive reallocations
        FieldName fName = null;
        ElementIndex eIndex = null;

        if (read instanceof ObjectFieldRead) {
            ObjectFieldRead fieldRead = (ObjectFieldRead) read;

            fName = new FieldName(fieldRead.getField().getName());
        }

        Set<UniverseIdentifier> parents = new HashSet<UniverseIdentifier>();

        lookupValues(root, read.getObject(), parents);

        for (UniverseIdentifier parent : parents) {
            if (!(parent instanceof StructuredValueIdentifier)) {
                continue;
            }

            StructuredValue parentObject = get((StructuredValueIdentifier) parent);

            if (read instanceof ObjectFieldRead) {
                if (parentObject instanceof UniverseNull) continue;
                Associative object = (Associative) parentObject;
                UniverseSlot field = object.getField(fName);

                if (field == null) continue;

                outValues.addAll(object.getField(fName).getPossibleValues());
            } else if (read instanceof ArrayElementRead) {
                if (parentObject instanceof UniverseNull) continue;

                Indexed array = (Indexed) parentObject;

                ArrayElementRead aeRead = (ArrayElementRead) read;

                // Get the exact element in case of a constant index
                int[] indices = null;

                if (aeRead.getIndex() instanceof Constant) {
                    indices = new int[] {((Constant) aeRead.getIndex()).value.intValue()};

                    if (indices[0] >= array.getLength()) continue;
                } else {
                   indices = PredicateAbstraction.getInstance().computeAllExpressionValuesInRange(aeRead.getIndex(), 0, array.getLength());
                }

                for (int i : indices) {
                    outValues.addAll(array.getElement(new ElementIndex(i)).getPossibleValues());
                }
            } else if (read instanceof ArrayLengthRead) {
                if (parentObject instanceof UniverseNull) continue;

                Indexed array = (Indexed) parentObject;

                outValues.addAll(array.getLengthSlot().getPossibleValues());
            }
        }
    }

    @Override
    public Universe clone() {
        Universe universe = new Universe();

        universe.currentStructuredRealization.putAll(currentStructuredRealization);
        universe.currentPrimitiveRealization.putAll(currentPrimitiveRealization);
        universe.fresh = fresh;

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

    public Set<UniverseIdentifier> computeReachable(Set<? extends UniverseIdentifier> roots) {
        // Compute reachability closure
        Set<UniverseIdentifier> open = new HashSet<UniverseIdentifier>();
        Set<UniverseIdentifier> closed = new HashSet<UniverseIdentifier>();

        open.addAll(roots);

        while (!open.isEmpty()) {
            Set<UniverseIdentifier> nextOpen = new HashSet<UniverseIdentifier>();

            for (UniverseIdentifier id : open) {
                if (id instanceof StructuredValueIdentifier) {
                    StructuredValue value = currentStructuredRealization.get((StructuredValueIdentifier) id);

                    for (UniverseSlot slot : value.getSlots().values()) {
                        nextOpen.addAll(slot.getPossibleValues());
                    }

                    if (value instanceof UniverseArray) {
                        UniverseArray array = (UniverseArray) value;

                        nextOpen.addAll(array.getLengthSlot().getPossibleValues());
                    }
                }
            }

            closed.addAll(open);
            nextOpen.removeAll(closed);
            open = nextOpen;
        }

        return closed;
    }

    public void retainLiveValuesOnly(Set<UniverseIdentifier> liveRoots) {
        Set<UniverseIdentifier> liveValues = computeReachable(liveRoots);

        // Always keep the representation of null, no matter what
        liveValues.add(nullReference);

        // Remove unreachable structured
        Set<StructuredValueIdentifier> structuredToBeRemoved = new HashSet<StructuredValueIdentifier>();

        for (StructuredValueIdentifier id : currentStructuredRealization.keySet()) {
            if (!liveValues.contains(id)) {
                structuredToBeRemoved.add(id);
            }
        }

        for (StructuredValueIdentifier valueId : structuredToBeRemoved) {
            StructuredValue value = get(valueId);

            for (UniverseSlotKey key : value.getSlots().keySet()) {
                UniverseSlot slot = value.getSlot(key);

                for (UniverseIdentifier subValueId : slot.getPossibleValues()) {
                    // Primitive values are owned by a single parent, if that is unreachable, then the primitive value is unreachable as well
                    // Therefore only shared objects are processed here
                    // We do not need to update other unreachable objects
                    if (subValueId instanceof StructuredValueIdentifier && !structuredToBeRemoved.contains(subValueId)) {
                        UniverseValue subValue = get(subValueId);

                        if (subValue.isFrozen()) {
                            subValue = subValue.createShallowCopy();

                            put(subValueId, subValue);
                        }
                        subValue.removeParentSlot(valueId, key);
                    }
                }
            }
        }

        for (StructuredValueIdentifier valueId : structuredToBeRemoved) {
            currentStructuredRealization.remove(valueId);
        }

        // Remove unreachable primitive
        Set<PrimitiveValueIdentifier> primitiveToBeRemoved = new HashSet<PrimitiveValueIdentifier>();

        for (PrimitiveValueIdentifier id : currentPrimitiveRealization.keySet()) {
            if (!liveValues.contains(id)) {
                primitiveToBeRemoved.add(id);
            }
        }

        for (PrimitiveValueIdentifier id : primitiveToBeRemoved) {
            currentPrimitiveRealization.remove(id);
        }
    }
}
