package gov.nasa.jpf.abstraction.predicate.concrete;


public class CompleteVariableID extends VariableID {
	public int getInteger() throws Exception {
		throw new Exception("Cannot obtain integer value from a variable");
	}
	
	public int getFloat() throws Exception {
		throw new Exception("Cannot obtain float value from a variable");
	}
	
	public int getLong() throws Exception {
		throw new Exception("Cannot obtain long value from a variable");
	}
	
	public int getDouble() throws Exception {
		throw new Exception("Cannot obtain double value from a variable");
	}
}
