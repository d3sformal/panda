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


		SerializerTest st = new SerializerTest();
		st.compute();
		st.display();
	}
 

	private int fx;
	private int fy;
	private int fz;
	private int fv;
	
	public SerializerTest()
	{
		fx = Debug.makeAbstractInteger(1);
		fy = Debug.makeAbstractInteger(-1);
		fz = Debug.makeAbstractInteger(0);

		fv = Debug.makeAbstractInteger(0);
	}

	public void compute()
	{
		boolean b = Verify.getBoolean();

		if (b)
		{
			fv = fx + fz;
		}
		else
		{
			b = true;

			fv = fy + fz;
		}
	}

	public void display()
	{
		Verify.breakTransition();

		// default serializer will handle this incorrectly (premature state matching)
			// field "fv" has the same concrete value (0) in both branches

		// this should be printed twice with the sign abstraction and correct serializer
		System.out.printf("fv = %s\n", Debug.getAbstractInteger(fv));
	}
}

