package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.concrete.ConcretePath;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Comparator;

public class FlatSymbolTable implements SymbolTable, Scope {
	
	/**
	 *  Maps PATH to all possible VARIABLEIDS
	 *  
	 *  a -> local var a
	 *  b.a -> static field a
	 *  c.a -> heap field a
	 *  d[1] -> heap array element number 1
	 */
	private HashMap<AccessPath, Set<CompleteVariableID>> path2vars = new HashMap<AccessPath, Set<CompleteVariableID>>();
	
	/**
	 * Maps VARIABLEID to all known (from the point of our execution) PATHS
	 * 
	 * heap field a -> {x.y.a, z.a, u[1].a}
	 * ...
	 */
	private HashMap<CompleteVariableID, Set<AccessPath>> var2paths = new HashMap<CompleteVariableID, Set<AccessPath>>();

	@Override
	public Set<AccessPath> lookupAccessPaths(AccessPath prefix) {
		Set<AccessPath> ret = new HashSet<AccessPath>();
		
		for (AccessPath path : path2vars.keySet()) {
			if (prefix.isPrefix(path)) {
				ret.add(path);
			}
		}
		
		return ret;
	}

	@Override
	public Set<AccessPath> lookupEquivalentAccessPaths(CompleteVariableID var) {
		Set<AccessPath> ret = var2paths.get(var);
		
		if (var == null || ret == null) {
			ret = new HashSet<AccessPath>(); 
		}
		
		return ret;
	}
	
	@Override
	public Set<AccessPath> lookupEquivalentAccessPaths(AccessPath path) {
		Set<AccessPath> ret = new HashSet<AccessPath>();
		
		for (CompleteVariableID var : resolvePath(path)) {
			ret.addAll(lookupEquivalentAccessPaths(var));
		}
		
		return ret;
	}

	@Override
	public Set<CompleteVariableID> resolvePath(AccessPath path) {
		Set<CompleteVariableID> ret = path2vars.get(path);
		
		if (path == null || ret == null) {
			ret = new HashSet<CompleteVariableID>();
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public FlatSymbolTable clone() {
		FlatSymbolTable clone = new FlatSymbolTable();
		
		clone.path2vars = (HashMap<AccessPath, Set<CompleteVariableID>>)path2vars.clone();
		clone.var2paths = (HashMap<CompleteVariableID, Set<AccessPath>>)var2paths.clone();
		
		return clone;
	}
	
	private void setPathToVar(AccessPath path, CompleteVariableID var) {
		initialisePathToNumbers(path);
		initialiseNumberToPaths(var);

		path2vars.get(path).add(var);
		var2paths.get(var).add(path);
	}
	
	private void unsetPath(AccessPath path) {
		initialisePathToNumbers(path);
		
		Set<CompleteVariableID> vars = path2vars.get(path);
		
		for (CompleteVariableID number : vars) {
			initialiseNumberToPaths(number);

			Set<AccessPath> paths = var2paths.get(number);
		
			if (paths.isEmpty()) {
				var2paths.remove(number);
			}
		}
		
		path2vars.remove(path);
	}
	
	private void initialisePathToNumbers(AccessPath path) {
		if (!path2vars.containsKey(path)) {
			path2vars.put(path, new HashSet<CompleteVariableID>());
		}
	}
	
	private void initialiseNumberToPaths(CompleteVariableID var) {
		if (!var2paths.containsKey(var)) {
			var2paths.put(var, new HashSet<AccessPath>());
		}
	}
	
	@Override
	public void processLoad(ConcretePath from) {
		Map<AccessPath, CompleteVariableID> vars = from.resolve();
		
		for (AccessPath source : vars.keySet()) {
			unsetPath(source);
			setPathToVar(source, vars.get(source));
		}
	}
	
	private Set<AccessPath> processPrimitiveStore(AccessPath destination, CompleteVariableID var) {
		// ASSIGN A PRIMITIVE VALUE - STORES NEW VALUE (REWRITES DATA => NO PATH UNSETTING, VARID STAY THE SAME, ONLY THE VALUES CHANGED)
		setPathToVar(destination, var);
		
		Set<AccessPath> affected = new HashSet<AccessPath>();
		
    	Set<AccessPath> equivalentPaths = lookupEquivalentAccessPaths(destination);
    	equivalentPaths.add(destination);
    			
    	affected.addAll(equivalentPaths);
		
		return affected;
	}
	
	@Override
	public Set<AccessPath> processPrimitiveStore(ConcretePath destination) {
		Set<AccessPath> affected = new HashSet<AccessPath>();
		
		if (destination == null) {
			return affected;
		}
			
		Map<AccessPath, CompleteVariableID> destinationCandidates = destination.resolve();
		
		for (AccessPath destinationPath : destinationCandidates.keySet()) {
			affected.addAll(processPrimitiveStore(destinationPath, destinationCandidates.get(destinationPath)));
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
				
				unsetPath(equivalentPath);
				for (CompleteVariableID var : resolvePath(source)) {
					setPathToVar(equivalentPath, var);
				}
			
				affected.add(equivalentPath);
			}
		}
		
		return affected;
	}
	
	@Override
	public Set<AccessPath> processObjectStore(ConcretePath sourcePrefix, ConcretePath destinationPrefix) {
		Set<AccessPath> affected = new HashSet<AccessPath>();

		if (sourcePrefix == null || destinationPrefix == null) {
			return affected;
		}
		
		Set<AccessPath> destinationCandidates = destinationPrefix.partialResolve().keySet();
		Set<AccessPath> sourceCandidates = sourcePrefix.partialResolve().keySet();

		for (AccessPath destinationPath : destinationCandidates) {
			for (AccessPath sourcePath : sourceCandidates) {
				affected.addAll(processObjectStore(destinationPath, sourcePath));
			}
		}
		
		return affected;
	}
	
	@Override
	public String toString() {
		String ret = "";
		
		int padding = 0;

		for (AccessPath p : path2vars.keySet()) {
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

        paths.addAll(path2vars.keySet());
		
		for (AccessPath p : paths) {
			String path = p.toString(AccessPath.NotationPolicy.DOT_NOTATION);
			String pad = "";
			
			for (int i = 0; i < padding - path.length(); ++i) {
				pad += " ";
			}
						
			for (CompleteVariableID var : path2vars.get(p)) {
				ret += path + pad + var + "\n";
			}
		}

		return ret;
	}

}
