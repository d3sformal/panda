package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.jvm.MJIEnv;


public class JPF_gov_nasa_jpf_abstraction_Debug {

	public static int getAbstractInteger(MJIEnv env, int objRef, int v) {
		Object [] attrs = env.getArgAttributes();
		Abstraction abs_arg;
		if(attrs == null || (abs_arg=(Abstraction)attrs[0])==null)
			return env.newString(Integer.toString(v));

		return env.newString(abs_arg.toString());
	}

    public static int getAbstractReal(MJIEnv env, int objRef, double v) {
    	Object [] attrs = env.getArgAttributes();
		Abstraction abs_arg;
		if(attrs == null || (abs_arg=(Abstraction)attrs[0])==null)
			return env.newString(Double.toString(v));

		return env.newString(abs_arg.toString());
    }

    public static int getAbstractBoolean(MJIEnv env, int objRef, boolean v) {
    	Object [] attrs = env.getArgAttributes();
		Abstraction abs_arg;
		if(attrs == null || (abs_arg=(Abstraction)attrs[0])==null)
			return env.newString(Boolean.toString(v));

		return env.newString(abs_arg.toString());

    }


    public static int makeAbstractInteger(MJIEnv env, int objRef, int v){
    	env.setReturnAttribute(AbstractInstructionFactory.abs.abstract_map(v));
    	return v;
    }

	public static double makeAbstractReal(MJIEnv env, int objRef, double v){
		// not implemented yet
		env.setReturnAttribute(AbstractInstructionFactory.abs.abstract_map(v));
		return v;
	}

}