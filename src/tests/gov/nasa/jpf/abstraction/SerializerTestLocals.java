package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.vm.Verify;


public class SerializerTestLocals
{
	public static void main (String[] args) 
	{
		System.out.println("\n=== AbstractionSerializer testing : locals ===\n");
	
		int x = Debug.makeAbstractInteger(1);
		int y = Debug.makeAbstractInteger(-1);
		int z = Debug.makeAbstractInteger(0);

		int v = Debug.makeAbstractInteger(0);
	
		boolean b = Verify.getBoolean();

		if (b)
		{
			v = x + z;
		}
		else
		{
			b = true;

			v = y + z;
		}

		Verify.breakTransition();

		// default serializer will handle this incorrectly (premature state matching)
			// local variable "v" has the same concrete value (0) in both branches

		// this should be printed twice with the sign abstraction and correct serializer
		System.out.printf("v = %s\n", Debug.getAbstractInteger(v));
	}
}

