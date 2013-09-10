package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayLengthRead;
import gov.nasa.jpf.abstraction.impl.EmptyAttribute;
import gov.nasa.jpf.abstraction.impl.NonEmptyAttribute;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Retrieves the lenght of an array
 */
public class ARRAYLENGTH extends gov.nasa.jpf.jvm.bytecode.ARRAYLENGTH {
	@Override
	public Instruction execute(ThreadInfo ti) {
		StackFrame sf = ti.getTopFrame();
		
		Attribute attr = (Attribute) sf.getOperandAttr();
		
		attr = Attribute.ensureNotNull(attr);
		
		AccessExpression path = null;
		
		if (attr.getExpression() instanceof AccessExpression) {
			path = (AccessExpression) attr.getExpression();
			path = DefaultArrayLengthRead.create(path);
		}
		
		Instruction ret = super.execute(ti);
		
        /**
         * Store the symbolic value for predicate abstraction purposes
         */
		sf = ti.getModifiableTopFrame();
		sf.addOperandAttr(new NonEmptyAttribute(null, path));
		
		return ret;
	}
}
