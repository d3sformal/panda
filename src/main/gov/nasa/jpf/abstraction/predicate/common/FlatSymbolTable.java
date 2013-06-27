package gov.nasa.jpf.abstraction.predicate.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FlatSymbolTable implements SymbolTable, Cloneable {
	
	private HashMap<AccessPath, Number> path2num = new HashMap<AccessPath, Number>();
	private HashMap<Number, Set<AccessPath>> num2paths = new HashMap<Number, Set<AccessPath>>();

	@Override
	public List<AccessPath> lookupEquivalentAccessPaths(Number number) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Number resolvePath(AccessPath path) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public FlatSymbolTable clone() {
		FlatSymbolTable clone = new FlatSymbolTable();
		
		clone.path2num = (HashMap<AccessPath, Number>)path2num.clone();
		clone.num2paths = (HashMap<Number, Set<AccessPath>>)num2paths.clone();
		
		return clone;
	}

	@Override
	public void register(AccessPath path, Number number) {
		if (!num2paths.containsKey(number)) {
			num2paths.put(number, new HashSet<AccessPath>());
		}

		// TODO: verify
		// Current state: path -> number1 ... implies ... number1 -> {..., path, ...}
		// Update: path -> number2
		// Therefore: number1 -> {...}
		if (path2num.containsKey(path) && !path2num.get(path).equals(number)) {
			Number old = path2num.get(path);
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
		
		for (AccessPath p : path2num.keySet()) {
			String path = p.toString(AccessPath.NotationPolicy.DOT_NOTATION);
			String pad = "";
			
			for (int i = 0; i < padding - path.length(); ++i) {
				pad += " ";
			}
			
			ret += path + pad + path2num.get(p).getClass().getSimpleName() + "\n";
		}
		
		ret += "---------------------\n";
		
		return ret;
	}

}
