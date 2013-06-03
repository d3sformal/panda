package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.abstraction.numeric.Abstraction;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;


public class JPF_gov_nasa_jpf_abstraction_Debug extends NativePeer {

	public static int getAbstractInteger(MJIEnv env, int objRef, int v) {
		Object [] attrs = env.getArgAttributes();
		Abstraction abs_arg;
		if(attrs == null || (abs_arg=(Abstraction)attrs[0])==null)
			return env.newString(Integer.toString(v));

		return env.newString(abs_arg.toString());
	}

    public static int getAbstractDouble(MJIEnv env, int objRef, double v) {
    	Object [] attrs = env.getArgAttributes();
		Abstraction abs_arg;
		if(attrs == null || (abs_arg=(Abstraction)attrs[0])==null)
			return env.newString(Double.toString(v));

		return env.newString(abs_arg.toString());
    }
    
    public static int getAbstractFloat(MJIEnv env, int objRef, float v) {
    	Object [] attrs = env.getArgAttributes();
		Abstraction abs_arg;
		if(attrs == null || (abs_arg=(Abstraction)attrs[0])==null)
			return env.newString(Float.toString(v));

		return env.newString(abs_arg.toString());
    }    
    
    public static int getAbstractLong(MJIEnv env, int objRef, long v) {
    	Object [] attrs = env.getArgAttributes();
		Abstraction abs_arg;
		if(attrs == null || (abs_arg=(Abstraction)attrs[0])==null)
			return env.newString(Long.toString(v));

		return env.newString(abs_arg.toString());
    }       

    public static int getAbstractBoolean(MJIEnv env, int objRef, boolean v) {
    	Object [] attrs = env.getArgAttributes();
		Abstraction abs_arg;
		if(attrs == null || (abs_arg=(Abstraction)attrs[0])==null)
			return env.newString(Boolean.toString(v));

		return env.newString(abs_arg.toString());

    }

	public static float makeAbstractFloat(MJIEnv env, int objRef, float v){
		env.setReturnAttribute(AbstractInstructionFactory.abs.abstractMap(v));
		return v;
	}
	
	public static double makeAbstractDouble(MJIEnv env, int objRef, double v){
		env.setReturnAttribute(AbstractInstructionFactory.abs.abstractMap(v));
		return v;
	}	
    
    public static int makeAbstractInteger(MJIEnv env, int objRef, int v){
    	env.setReturnAttribute(AbstractInstructionFactory.abs.abstractMap(v));
    	return v;
    }
    
	public static long makeAbstractLong(MJIEnv env, int objRef, long v){
		env.setReturnAttribute(AbstractInstructionFactory.abs.abstractMap(v));
		return v;
	}    

}