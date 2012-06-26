package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.jvm.Verify;


public class SerializerTest 
{
	public static void main (String[] args) 
	{
		System.out.println("\n=== AbstractionSerializer testing ===\n");
	
		int x = Debug.makeAbstractInteger(1);
		int y = Debug.makeAbstractInteger(-1);
		int z = Debug.makeAbstractInteger(0);

		int v = Debug.makeAbstractInteger(0);
		
		if (Verify.getBoolean())
		{
			v = x + z;
		}
		else
		{
			v = y + z;
		}

		// default serializer will handle this incorrectly (premature state matching)
			// local variable "v" has the same concrete value (0) in both branches

		// this should be printed twice with the sign abstraction and correct serializer
		System.out.printf("v = %s\n", Debug.getAbstractInteger(v));
	}
  
}

