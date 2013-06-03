package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.vm.Verify;


public class SerializerTestFields
{
	public static void main (String[] args) 
	{
		System.out.println("\n=== AbstractionSerializer testing : fields ===\n");
	
		SerializerTestFields st = new SerializerTestFields();
		st.compute();
		st.display();
	}
 

	private int fx;
	private int fy;
	private int fz;
	private int fv;
	
	public SerializerTestFields()
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

