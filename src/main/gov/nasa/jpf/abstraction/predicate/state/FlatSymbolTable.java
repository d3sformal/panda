package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.AccessPathElement;
import gov.nasa.jpf.abstraction.common.AccessPathIndexElement;
import gov.nasa.jpf.abstraction.common.AccessPathSubElement;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.concrete.AnonymousExpression;
import gov.nasa.jpf.abstraction.concrete.ArrayReference;
import gov.nasa.jpf.abstraction.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.concrete.ConcretePath;
import gov.nasa.jpf.abstraction.concrete.PartialVariableID;
import gov.nasa.jpf.abstraction.concrete.VariableID;

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
	private HashMap<AccessPath, Set<VariableID>> prefixToVariableIDs = new HashMap<AccessPath, Set<VariableID>>();
	
	/**
	 * Maps VARIABLEID to all known (from the point of our execution) PATHS
	 * 
	 * heap field a -> {x.y.a, z.a, u[1].a}
	 * ...
	 */
	private HashMap<VariableID, Set<AccessPath>> variableIDToPrefixes = new HashMap<VariableID, Set<AccessPath>>();

	@Override
	public Set<AccessPath> lookupAccessPaths(AccessPath prefix) {
		Set<AccessPath> ret = new HashSet<AccessPath>();
		
		for (AccessPath path : prefixToVariableIDs.keySet()) {
			if (prefix.isPrefix(path)) {
				ret.add(path);
			}
		}
		
		return ret;
	}

	@Override
	public Set<AccessPath> lookupEquivalentAccessPaths(VariableID var) {
		Set<AccessPath> ret = variableIDToPrefixes.get(var);
		
		if (var == null || ret == null) {
			ret = new HashSet<AccessPath>(); 
		}
		
		return ret;
	}
	
	@Override
	public Set<AccessPath> lookupEquivalentAccessPaths(AccessPath path) {
		Set<AccessPath> ret = new HashSet<AccessPath>();
		
		for (VariableID var : resolvePath(path)) {
			ret.addAll(lookupEquivalentAccessPaths(var));
		}
		
		return ret;
	}

	private Set<VariableID> resolvePath(AccessPath path) {
		Set<VariableID> ret = prefixToVariableIDs.get(path);
		
		if (path == null || ret == null) {
			return new HashSet<VariableID>();
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public FlatSymbolTable clone() {
		FlatSymbolTable clone = new FlatSymbolTable();
		
		clone.prefixToVariableIDs = (HashMap<AccessPath, Set<VariableID>>)prefixToVariableIDs.clone();
		clone.variableIDToPrefixes = (HashMap<VariableID, Set<AccessPath>>)variableIDToPrefixes.clone();
		
		return clone;
	}
	
	private void setPathToVar(AccessPath path, VariableID var) {
		initialisePathToNumbers(path);
		initialiseNumberToPaths(var);

		prefixToVariableIDs.get(path).add(var);
		variableIDToPrefixes.get(var).add(path);
	}
	
	private void setPathToVars(AccessPath path, Set<VariableID> vars) {
		for (VariableID var : vars) {
			setPathToVar(path, var);
		}
	}
	
	private void unsetPath(AccessPath path) {
		initialisePathToNumbers(path);
		
		Set<VariableID> vars = prefixToVariableIDs.get(path);
		
		for (VariableID number : vars) {
			initialiseNumberToPaths(number);

			Set<AccessPath> paths = variableIDToPrefixes.get(number);
		
			if (paths.isEmpty()) {
				variableIDToPrefixes.remove(number);
			}
		}
		
		prefixToVariableIDs.remove(path);
	}
	
	private void initialisePathToNumbers(AccessPath path) {
		if (!prefixToVariableIDs.containsKey(path)) {
			prefixToVariableIDs.put(path, new HashSet<VariableID>());
		}
	}
	
	private void initialiseNumberToPaths(VariableID var) {
		if (!variableIDToPrefixes.containsKey(var)) {
			variableIDToPrefixes.put(var, new HashSet<AccessPath>());
		}
	}
	
	@Override
	public void processLoad(ConcretePath from) {
		Map<AccessPath, VariableID> vars = from.partialResolve();
		
		for (AccessPath source : vars.keySet()) {
			unsetPath(source);
			setPathToVar(source, vars.get(source));
		}
	}
	
	@Override
	public Set<AccessPath> processPrimitiveStore(ConcretePath destination) {
		Set<AccessPath> affected = new HashSet<AccessPath>();
		
		if (destination == null) {
			return affected;
		}
			
		Map<AccessPath, CompleteVariableID> destinationCandidates = destination.resolve();
		
		for (AccessPath destinationPath : destinationCandidates.keySet()) {
			// ASSIGN A PRIMITIVE VALUE - STORES NEW VALUE (REWRITES DATA => NO PATH UNSETTING, VARID STAY THE SAME, ONLY THE VALUES CHANGED)
			setPathToVar(destinationPath, destinationCandidates.get(destinationPath));
						
	    	Set<AccessPath> equivalentPaths = lookupEquivalentAccessPaths(destinationPath);
	    	equivalentPaths.add(destinationPath);
	    			
	    	affected.addAll(equivalentPaths);
		}
		
		return affected;
	}
	
	@Override
	public Set<AccessPath> processObjectStore(Expression sourceExpression, ConcretePath destinationPrefix) {
		Set<AccessPath> affected = new HashSet<AccessPath>();
		
		ConcretePath sourcePrefix = null;
		
		if (sourceExpression instanceof ConcretePath) {
			sourcePrefix = (ConcretePath) sourceExpression;
		}

		if (destinationPrefix == null) {
			return affected;
		}
		
		Set<AccessPath> destinationCandidates = destinationPrefix.partialExhaustiveResolve().keySet();
		boolean unambiguous = destinationCandidates.size() == 1;
		
		if (sourceExpression instanceof AnonymousExpression) {
			AnonymousExpression anonymous = (AnonymousExpression) sourceExpression;
			
			for (AccessPath destination : destinationCandidates) {
				if (unambiguous) {
					for (AccessPath subobjects : lookupAccessPaths(destination)) {
						unsetPath(subobjects);
					}
				}
				
				setPathToVar(destination, anonymous.generateVariableID());
				
				affected.add(destination);
			}
		}
		
		if (sourcePrefix == null) {
			return affected;
		}
		
		Map<AccessPath, VariableID> sourceCandidates = sourcePrefix.partialResolve();

		Map<AccessPath, Set<VariableID>> rewrites = new HashMap<AccessPath, Set<VariableID>>();
		
		for (AccessPath destination : destinationCandidates) {			
			Set<AccessPath> affectedObjectPaths = new HashSet<AccessPath>();

			for (AccessPath affectedPrefixCandidate : prefixToVariableIDs.keySet()) {
				if (affectedPrefixCandidate.isProperPrefix(destination) && affectedPrefixCandidate.getLength() >= destination.getLength() - 1) {
					AccessPath affectedPrefix = affectedPrefixCandidate;
					
					AccessPathElement element = destination.getElement(affectedPrefix.getLength());

					for (AccessPath equivalentObjectPathPrefix : lookupEquivalentAccessPaths(affectedPrefix)) {
						AccessPath equivalentObjectPath = equivalentObjectPathPrefix.clone();
						
						if (element instanceof AccessPathSubElement) {
							AccessPathSubElement sub = (AccessPathSubElement) element;
							
							equivalentObjectPath.appendSubElement(sub.getName());
						} else if (element instanceof AccessPathIndexElement) {
							AccessPathIndexElement index = (AccessPathIndexElement) element;
							
							equivalentObjectPath.appendIndexElement(index.getIndex());
						}
						
						affectedObjectPaths.add(equivalentObjectPath);
					}
				}
			}
			
			affectedObjectPaths.add(destination);
			affected.addAll(affectedObjectPaths);

			for (AccessPath sourceCandidate : sourceCandidates.keySet()) {
				for (AccessPath source : lookupAccessPaths(sourceCandidate)) {
					for (AccessPath prefix : affectedObjectPaths) {
						AccessPath newPath = source.clone();
						AccessPath oldPrefix = sourceCandidate;
						AccessPath newPrefix = prefix.clone();
						AccessPath.reRoot(newPath, oldPrefix, newPrefix);
												
						if (!rewrites.containsKey(newPath)) {
							rewrites.put(newPath, new HashSet<VariableID>());
						}
						
						rewrites.get(newPath).addAll(resolvePath(source));
					}
				}			
				
				if (!rewrites.containsKey(destination)) {
					rewrites.put(destination, new HashSet<VariableID>());
				}
				
				rewrites.get(destination).addAll(resolvePath(sourceCandidate));
			}
		}
		
		for (AccessPath path : rewrites.keySet()) {
			if (unambiguous) {
				for (AccessPath subobjects : lookupAccessPaths(path)) {
					unsetPath(subobjects);
				}
			}

			setPathToVars(path, rewrites.get(path));
		}
		
		return affected;
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		
		int padding = 0;

		for (AccessPath p : prefixToVariableIDs.keySet()) {
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

        paths.addAll(prefixToVariableIDs.keySet());
		
		for (AccessPath p : paths) {
			String path = p.toString(AccessPath.NotationPolicy.DOT_NOTATION);
			StringBuilder pad = new StringBuilder();
			
			for (int i = 0; i < padding - path.length(); ++i) {
				pad.append(" ");
			}
			
			ret.append(path);
			ret.append(pad);
			ret.append("{ ");
			for (VariableID var : prefixToVariableIDs.get(p)) {
				ret.append(var);
				ret.append(" ");
			}
			ret.append("}\n");
		}

		return ret.toString();
	}
	
	@Override
	public int count() {
		return prefixToVariableIDs.keySet().size();
	}

	@Override
	public boolean isArray(AccessPath path) {
		if (prefixToVariableIDs.containsKey(path)) {
			for (VariableID var : prefixToVariableIDs.get(path)) {
				if (var instanceof PartialVariableID) {
					PartialVariableID partial = (PartialVariableID) var;
					
					return partial.getRef() instanceof ArrayReference;
				}
				
				return false;
			}
		}
		
		return false;
	}

}
