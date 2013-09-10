package gov.nasa.jpf.abstraction;

/**
 * Abstraction Factory is responsible for creation of some abstraction from its definition in form of string arguments
 *
 * Definition is usually obtained from a .jpf file
 */
public abstract class AbstractionFactory {
	public abstract Abstraction create(String[] args);
}
