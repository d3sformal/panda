package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.concrete.ConcretePath;
import gov.nasa.jpf.abstraction.concrete.VariableID;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Comparator;

public class FlatSymbolTable implements SymbolTable, Scope {
	
	/**
	 *  Maps PATH to a VARIABLEID
	 *  
	 *  a -> local var a
	 *  b.a -> static field a
	 *  c.a -> heap field a
	 *  d[1] -> heap array element number 1
	 */
	private HashMap<AccessPath, CompleteVariableID> path2num = new HashMap<AccessPath, CompleteVariableID>();
	
	/**
	 * Maps VARIABLEID to all known (from the point of our execution) PATHS
	 * 
	 * heap field a -> {x.y.a, z.a, u[1].a}
	 * ...
	 */
	private HashMap<CompleteVariableID, Set<AccessPath>> num2paths = new HashMap<CompleteVariableID, Set<AccessPath>>();

	@Override
	public Set<AccessPath> lookupAccessPaths(AccessPath prefix) {
		Set<AccessPath> ret = new HashSet<AccessPath>();
		
		for (AccessPath path : path2num.keySet()) {
			if (prefix.isPrefix(path)) {
				ret.add(path);
			}
		}
		
		return ret;
	}

	@Override
	public Set<AccessPath> lookupEquivalentAccessPaths(CompleteVariableID number) {
		Set<AccessPath> ret = num2paths.get(number);
		
		if (number == null || ret == null) {
			ret = new HashSet<AccessPath>(); 
		}
		
		return ret;
	}
	
	@Override
	public Set<AccessPath> lookupEquivalentAccessPaths(AccessPath path) {
		return lookupEquivalentAccessPaths(resolvePath(path));
	}

	@Override
	public CompleteVariableID resolvePath(AccessPath path) {
		return path2num.get(path);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public FlatSymbolTable clone() {
		FlatSymbolTable clone = new FlatSymbolTable();
		
		clone.path2num = (HashMap<AccessPath, CompleteVariableID>)path2num.clone();
		clone.num2paths = (HashMap<CompleteVariableID, Set<AccessPath>>)num2paths.clone();
		
		return clone;
	}
	
	private void setPathToVar(AccessPath path, CompleteVariableID number) {
		initialiseNumberToPaths(number);

		path2num.put(path, number);
		num2paths.get(number).add(path);
	}
	
	private void unsetPath(AccessPath path) {
		CompleteVariableID var = path2num.get(path);
		
		initialiseNumberToPaths(var);

		Set<AccessPath> paths = num2paths.get(var);
		
		if (paths.isEmpty()) {
			num2paths.remove(var);
		}
		
		path2num.remove(path);
	}
	
	private void initialiseNumberToPaths(CompleteVariableID number) {
		if (!num2paths.containsKey(number)) {
			num2paths.put(number, new HashSet<AccessPath>());
		}
	}
	
	@Override
	public void processLoad(ConcretePath from) {
		
		Map<AccessPath, CompleteVariableID> vars = from.resolve();
		
		if (vars.size() > 1) System.err.println("Ambiguous load. Most probably due to array[exp].");
		
		if (!vars.isEmpty()) {
			AccessPath source = vars.keySet().iterator().next();

			unsetPath(source);
			setPathToVar(source, vars.get(source));
		}
	}
	
	private Set<AccessPath> processPrimitiveStore(AccessPath destination, CompleteVariableID var) {
		// ASSIGN A PRIMITIVE VALUE - STORES NEW VALUE
		Set<AccessPath> affected = new HashSet<AccessPath>();
		
    	Set<AccessPath> equivalentPaths = lookupEquivalentAccessPaths(destination);
    	equivalentPaths.add(destination);
    			
    	affected.addAll(equivalentPaths);
    			
    	for (AccessPath equivalentPath : equivalentPaths) {
    		
    		System.err.println();
    		System.err.println(">>>> PRIMITIVE " + equivalentPath.toString(AccessPath.NotationPolicy.DOT_NOTATION) + " -> " + var);
    		System.err.println();
    		
    		unsetPath(equivalentPath);
    		setPathToVar(equivalentPath, var);
    	}
		
		return affected;
	}
	
	private Set<AccessPath> processObjectStore(AccessPath destinationPrefix, AccessPath sourcePrefix) {
		// ASSIGN AN OBJECT OR ARRAY
		Set<AccessPath> affected = new HashSet<AccessPath>();
		
		Set<AccessPath> sources = lookupAccessPaths(sourcePrefix);
		
		for (AccessPath source : sources) {
			AccessPath destinationPath = source.clone();
			AccessPath newPrefix = destinationPrefix.clone();

			AccessPath.reRoot(destinationPath, sourcePrefix, newPrefix);
			
			// TODO:
			//
			// a.b = c
			// a.b.x := X
			//
			// affected:
			//
			// a.b.x.*
			// c.x.* !!! !!! !!!
			Set<AccessPath> equivalentPaths = lookupEquivalentAccessPaths(destinationPath);
			equivalentPaths.add(destinationPath);
			// >>> TODO <<<<
			// Test whether any of the equivalentPaths is affected by the store

			for (AccessPath equivalentPath : equivalentPaths) {
				// REWRITE ALL PRIMITIVE SUB FIELDS (NO MATTER ITS DEPTH)
				
				System.err.println();
				System.err.println(">>>> OBJECT " + equivalentPath.toString(AccessPath.NotationPolicy.DOT_NOTATION) + " -> " + resolvePath(source));
				System.err.println();
				
				unsetPath(equivalentPath);
				setPathToVar(equivalentPath, resolvePath(source));
			
				affected.add(equivalentPath);
			}
		}
		
		return affected;
	}
	
	@Override
	public Set<AccessPath> processStore(ConcretePath from, ConcretePath to) {	
		Set<AccessPath> affected = new HashSet<AccessPath>();
		
		if (to == null) {
			System.err.println("Undefined destination in store.");
			
			return affected;
		}
		
		Map<AccessPath, CompleteVariableID> vars = to.resolve();
		
		if (vars.size() > 1) System.err.println("Ambiguous store. Most probably due to array[exp] := primitive.");
			
		if (!vars.isEmpty()) {
			AccessPath destination = vars.keySet().iterator().next();

			return processPrimitiveStore(destination, vars.get(destination));
		}
		
		if (from == null) {
			System.err.println("Undefined source in store of an object.");

			return affected;
		}
		
		Map<AccessPath, VariableID> destinationPartialVars = to.partialResolve();
		Map<AccessPath, VariableID> sourcePartialVars = from.partialResolve();
		
		if (destinationPartialVars.size() > 1) System.err.println("Ambiguous store. Most probably due to array[exp] := object.");
		if (sourcePartialVars.size() > 1) System.err.println("Ambiguous store. Most probably due to array[exp] := object.");

		if (!destinationPartialVars.isEmpty() && !sourcePartialVars.isEmpty()) {
			AccessPath destination = destinationPartialVars.keySet().iterator().next();
			AccessPath source = sourcePartialVars.keySet().iterator().next();

			return processObjectStore(destination, source);
		}
		
		return affected;
	}
	
	@Override
	public String toString() {
		String ret = "";
		
		int padding = 0;

		for (AccessPath p : path2num.keySet()) {
			String path = p.toString(AccessPath.NotationPolicy.DOT_NOTATION);
			
			padding = padding < path.length() ? path.length() : padding;
		}
		
		padding += 4;

        TreeSet<AccessPath> paths = new TreeSet<AccessPath>(new Comparator<AccessPath>() {
            @Override
            public int compare(AccessPath p1, AccessPath p2) {
                return p1.toString(AccessPath.NotationPolicy.DOT_NOTATION).compareTo(p2.toString(AccessPath.NotationPolicy.DOT_NOTATION));
            }
        });

        paths.addAll(path2num.keySet());
		
		for (AccessPath p : paths) {
			String path = p.toString(AccessPath.NotationPolicy.DOT_NOTATION);
			String pad = "";
			
			for (int i = 0; i < padding - path.length(); ++i) {
				pad += " ";
			}
			
			ret += path + pad + path2num.get(p) + "\n";
		}

		return ret;
	}

}
