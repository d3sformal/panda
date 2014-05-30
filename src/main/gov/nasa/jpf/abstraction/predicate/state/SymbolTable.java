package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.predicate.state.universe.Universe;

import java.util.Set;

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
