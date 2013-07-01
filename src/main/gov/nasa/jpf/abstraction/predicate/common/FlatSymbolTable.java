package gov.nasa.jpf.abstraction.predicate.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Comparator;

public class FlatSymbolTable implements SymbolTable, Cloneable {
	
	private HashMap<AccessPath, VariableID> path2num = new HashMap<AccessPath, VariableID>();
	private HashMap<VariableID, Set<AccessPath>> num2paths = new HashMap<VariableID, Set<AccessPath>>();

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
	public Set<AccessPath> lookupEquivalentAccessPaths(VariableID number) {
		return num2paths.get(number);
	}

	@Override
	public VariableID resolvePath(AccessPath path) {
		return path2num.get(path);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public FlatSymbolTable clone() {
		FlatSymbolTable clone = new FlatSymbolTable();
		
		clone.path2num = (HashMap<AccessPath, VariableID>)path2num.clone();
		clone.num2paths = (HashMap<VariableID, Set<AccessPath>>)num2paths.clone();
		
		return clone;
	}

	@Override
	public void register(AccessPath path, VariableID number) {
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
		
		path2num.remove(path);
		path2num.put(path, number);		
		num2paths.get(number).add(path);
	}
	
	@Override
	public String toString() {
		String ret = "--------------------- (" + path2num.size() + ")\n";
		
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
		
		ret += "---------------------\n";
		
		return ret;
	}

}
