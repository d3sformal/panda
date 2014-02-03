package gov.nasa.jpf.abstraction.predicate.state.universe;

import gov.nasa.jpf.abstraction.common.access.Root;

public class PrimitiveLocalVariable extends PrimitiveValueSlot implements LocalVariable {
    private Root accessExpression;

     public PrimitiveLocalVariable(Root accessExpression) {
         this.accessExpression = accessExpression;
     }

     @Override
     public Root getAccessExpression() {
         return accessExpression;
     }
}
