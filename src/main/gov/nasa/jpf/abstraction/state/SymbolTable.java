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
package gov.nasa.jpf.abstraction.state;

import java.util.Set;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.state.universe.Universe;

/**
 * Interface for a structure capable of handling assignments of symbolic expressions
 */
public interface SymbolTable {
    /**
     * Handles writes to a memory slot of a primitive type (numerical)
     * @param from the symbolic expression which is being written
     * @param to the symbolic access expression to the memory slot which is being written to
     * @return set of all affected currently valid and relevant access expressions
     */
    public Set<AccessExpression> processPrimitiveStore(Expression from, AccessExpression to);

    /**
     * Handles writes to a memory slot of a reference type (object / array)
     * @param from the symbolic expression which is being written
     * @param to the symbolic access expression to the memory slot which is being written to
     * @return set of all affected currently valid and relevant access expressions
     */
    public Set<AccessExpression> processObjectStore(Expression from, AccessExpression to);

    /**
     * @param path access expression of the interest
     * @return true in case that the path points to an array, false otherwise.
     */
    public boolean isArray(AccessExpression path);

    /**
     * @param path access expression of the interest
     * @return true in case that the path points to an object, false otherwise.
     */
    public boolean isObject(AccessExpression path);

    /**
     * @param path access expression of the interest
     * @return true in case that the path points to a primitive value, false otherwise.
     */
    public boolean isPrimitive(AccessExpression path);

    /**
     * @return the underlying structure holding all the objects (abstract heap)
     */
    public Universe getUniverse();
}
