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
		return num2paths.get(number);
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

	private Set<AccessPath> registerPathToVariable(AccessPath path, CompleteVariableID number) {
		Set<AccessPath> affected = new HashSet<AccessPath>();
		
		if (!num2paths.containsKey(number)) {
			num2paths.put(number, new HashSet<AccessPath>());
		}

		// TODO: verify
		// Current state: path -> number1 ... implies ... number1 -> {..., path, ...}
		// Update: path -> number2
		// Therefore: number1 -> {...}
		if (path2num.containsKey(path) && !path2num.get(path).equals(number)) {
			VariableID old = path2num.get(path);
			num2paths.get(old).remove(path);
		}
		
		affected.add(path);
		
		path2num.remove(path);
		path2num.put(path, number);		
		num2paths.get(number).add(path);
		
		return affected;
	}
	
	@Override
	public Set<AccessPath> processLoad(AccessPath path, CompleteVariableID number) {
		return registerPathToVariable(path, number);
	}
	
	@Override
	public Set<AccessPath> processStore(ConcretePath from, ConcretePath to) {	
		Set<AccessPath> affected = new HashSet<AccessPath>();
		
		if (to == null) return affected; //TODO verify
			
		if (from == null) {
			Map<AccessPath, CompleteVariableID> vars = to.resolve();

			// ASSIGN A PRIMITIVE VALUE - STORES NEW VALUE
    		for (AccessPath p : vars.keySet()) {
    			affected.addAll(registerPathToVariable(p, vars.get(p)));
    		}
		} else {	
			for (AccessPath path : lookupAccessPaths(from)) {
				CompleteVariableID variableID = resolvePath(path);

				for (AccessPath newFrom : from.partialResolve().keySet()) {
					AccessPath newPath = path.clone();
					AccessPath.reRoot(newPath, from, newFrom);

					// REWRITE ALL PRIMITIVE SUB FIELDS (NO MATTER ITS DEPTH)
					affected.addAll(registerPathToVariable(newPath, variableID));
				}
			}
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
