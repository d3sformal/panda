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

import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.state.MethodFrameSymbolTable;

public class StructuredLocalVariable extends StructuredValueSlot implements LocalVariable {
    private Root accessExpression;
    private int scope;

    public static LocalVariable.SlotKey slotKey = new LocalVariable.SlotKey();

    public StructuredLocalVariable(Root accessExpression, int scope) {
        this.accessExpression = accessExpression;
        this.scope = scope;

        setParent(this);
        setSlotKey(slotKey);
    }

    @Override
    public StructuredLocalVariable createShallowCopy() {
        StructuredLocalVariable copy = new StructuredLocalVariable(getAccessExpression(), getScope());

        for (StructuredValueIdentifier value : getPossibleStructuredValues()) {
            copy.addPossibleStructuredValue(value);
        }

        return copy;
    }

    @Override
    public Root getAccessExpression() {
        return accessExpression;
    }

    @Override
    public int getScope() {
        return scope;
    }

    @Override
    public int compareTo(Identifier id) {
        if (id instanceof StructuredLocalVariable) {
            StructuredLocalVariable var = (StructuredLocalVariable) id;

            return getAccessExpression().getName().compareTo(var.getAccessExpression().getName());
        }

        return Identifier.Ordering.compare(this, id);
    }

    @Override
    public String toString() {
        return accessExpression.toString();
    }
}
