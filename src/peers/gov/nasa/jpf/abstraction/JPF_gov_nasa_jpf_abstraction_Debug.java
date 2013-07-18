package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.impl.NonEmptyAttribute;
import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;

public class JPF_gov_nasa_jpf_abstraction_Debug extends NativePeer {

	@MJI
	public static int getAbstractInteger__I__Ljava_lang_String_2(MJIEnv env, int objRef, int v) {
		Object [] attrs = env.getArgAttributes();
		AbstractValue abs_arg;
		if(attrs == null || (abs_arg=(AbstractValue)attrs[0])==null)
			return env.newString(Integer.toString(v));

		return env.newString(abs_arg.toString());
	}

    @MJI
    public static int getAbstractDouble__D__Ljava_lang_String_2(MJIEnv env, int objRef, double v) {
    	Object [] attrs = env.getArgAttributes();
		AbstractValue abs_arg;
		if(attrs == null || (abs_arg=(AbstractValue)attrs[0])==null)
			return env.newString(Double.toString(v));

		return env.newString(abs_arg.toString());
    }
    
    @MJI
    public static int getAbstractFloat__F__Ljava_lang_String_2(MJIEnv env, int objRef, float v) {
    	Object [] attrs = env.getArgAttributes();
		AbstractValue abs_arg;
		if(attrs == null || (abs_arg=(AbstractValue)attrs[0])==null)
			return env.newString(Float.toString(v));

		return env.newString(abs_arg.toString());
    }    
    
    @MJI
    public static int getAbstractLong__J__Ljava_lang_String_2(MJIEnv env, int objRef, long v) {
    	Object [] attrs = env.getArgAttributes();
		AbstractValue abs_arg;
		if(attrs == null || (abs_arg=(AbstractValue)attrs[0])==null)
			return env.newString(Long.toString(v));

		return env.newString(abs_arg.toString());
    }       

    @MJI
    public static int getAbstractBoolean__Z__Ljava_lang_String_2(MJIEnv env, int objRef, boolean v) {
    	Object [] attrs = env.getArgAttributes();
		AbstractValue abs_arg;
		if(attrs == null || (abs_arg=(AbstractValue)attrs[0])==null)
			return env.newString(Boolean.toString(v));

		return env.newString(abs_arg.toString());

    }

    @MJI
	public static float makeAbstractFloat__F__F(MJIEnv env, int objRef, float v){
		env.setReturnAttribute(new NonEmptyAttribute(AbstractInstructionFactory.abs.abstractMap(v), Constant.create(v)));
		return v;
	}
	
    @MJI
	public static double makeAbstractDouble__D__D(MJIEnv env, int objRef, double v){
    	env.setReturnAttribute(new NonEmptyAttribute(AbstractInstructionFactory.abs.abstractMap(v), Constant.create(v)));
		return v;
	}	
    
    @MJI
    public static int makeAbstractInteger__I__I(MJIEnv env, int objRef, int v){
    	env.setReturnAttribute(new NonEmptyAttribute(AbstractInstructionFactory.abs.abstractMap(v), Constant.create(v)));
    	return v;
    }
    
    @MJI
	public static long makeAbstractLong__J__J(MJIEnv env, int objRef, long v){
    	env.setReturnAttribute(new NonEmptyAttribute(AbstractInstructionFactory.abs.abstractMap(v), Constant.create(v)));
		return v;
	}    

}
