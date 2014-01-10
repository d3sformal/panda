package gov.nasa.jpf.abstraction.bytecode;

import gov.nasa.jpf.abstraction.AbstractValue;
import gov.nasa.jpf.abstraction.Attribute;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;

public class TypeConversionExecutor {

    private DataWordManipulator source;
    private DataWordManipulator target;

    public TypeConversionExecutor(DataWordManipulator source, DataWordManipulator target) {
        this.source = source;
        this.target = target;
    }

	public Instruction execute(ThreadInfo ti, TypeConvertor ins) {
		StackFrame sf = ti.getModifiableTopFrame();
		Attribute attr = Attribute.ensureNotNull(source.getAttribute(sf));
		AbstractValue abs_val = attr.getAbstractValue();
        Instruction ret;
		
		if (abs_val == null) {
			ret = ins.executeConcrete(ti);
		} else {
		    source.pop(sf);
            target.push(sf, 0);
            ret = ins.getNext(ti);
        }
		target.setAttribute(sf, attr);

		return ret;
	}

}
