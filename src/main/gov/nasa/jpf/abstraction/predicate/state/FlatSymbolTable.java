package gov.nasa.jpf.abstraction.predicate.state;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.NotationPolicy;
import gov.nasa.jpf.abstraction.concrete.AnonymousExpression;
import gov.nasa.jpf.abstraction.concrete.ArrayReference;
import gov.nasa.jpf.abstraction.concrete.ObjectReference;
import gov.nasa.jpf.abstraction.concrete.PartialVariableID;
import gov.nasa.jpf.abstraction.concrete.VariableID;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteAccessExpression;

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
	private HashMap<AccessExpression, Set<VariableID>> prefixToVariableIDs = new HashMap<AccessExpression, Set<VariableID>>();
	
	/**
	 * Maps VARIABLEID to all known (from the point of our execution) PATHS
	 * 
	 * heap field a -> {x.y.a, z.a, u[1].a}
	 * ...
	 */
	private HashMap<VariableID, Set<AccessExpression>> variableIDToPrefixes = new HashMap<VariableID, Set<AccessExpression>>();

	@Override
	public Set<AccessExpression> lookupAccessPaths(AccessExpression prefix) {
		Set<AccessExpression> ret = new HashSet<AccessExpression>();
		
		for (AccessExpression path : prefixToVariableIDs.keySet()) {
			if (prefix.isPrefixOf(path)) {
				ret.add(path);
			}
		}
		
		return ret;
	}

	@Override
	public Set<AccessExpression> lookupEquivalentAccessPaths(VariableID var) {
		Set<AccessExpression> ret = variableIDToPrefixes.get(var);
		
		if (var == null || ret == null) {
			ret = new HashSet<AccessExpression>(); 
		}
		
		return ret;
	}
	
	@Override
	public Set<AccessExpression> lookupEquivalentAccessPaths(AccessExpression path) {
		Set<AccessExpression> ret = new HashSet<AccessExpression>();
		
		for (VariableID var : resolvePath(path)) {
			ret.addAll(lookupEquivalentAccessPaths(var));
		}
		
		return ret;
	}

	private Set<VariableID> resolvePath(AccessExpression path) {
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
		
		clone.prefixToVariableIDs = (HashMap<AccessExpression, Set<VariableID>>)prefixToVariableIDs.clone();
		clone.variableIDToPrefixes = (HashMap<VariableID, Set<AccessExpression>>)variableIDToPrefixes.clone();
		
		return clone;
	}
	
	private void setPathToVar(AccessExpression path, VariableID var) {
		initialisePathToNumbers(path);
		initialiseNumberToPaths(var);

		prefixToVariableIDs.get(path).add(var);
		variableIDToPrefixes.get(var).add(path);
	}
	
	private void setPathToVars(AccessExpression path, Set<VariableID> vars) {
		for (VariableID var : vars) {
			setPathToVar(path, var);
		}
	}
	
	private void unsetPath(AccessExpression path) {
		initialisePathToNumbers(path);
		
		Set<VariableID> vars = prefixToVariableIDs.get(path);
		
		for (VariableID number : vars) {
			initialiseNumberToPaths(number);

			Set<AccessExpression> paths = variableIDToPrefixes.get(number);
		
			if (paths.isEmpty()) {
				variableIDToPrefixes.remove(number);
			}
		}
		
		prefixToVariableIDs.remove(path);
	}
	
	private void initialisePathToNumbers(AccessExpression path) {
		if (!prefixToVariableIDs.containsKey(path)) {
			prefixToVariableIDs.put(path, new HashSet<VariableID>());
		}
	}
	
	private void initialiseNumberToPaths(VariableID var) {
		if (!variableIDToPrefixes.containsKey(var)) {
			variableIDToPrefixes.put(var, new HashSet<AccessExpression>());
		}
	}
	
	@Override
	public void processLoad(ConcreteAccessExpression from) {
		Map<AccessExpression, VariableID> vars = from.partialResolve().processed;
		
		for (AccessExpression source : vars.keySet()) {
			unsetPath(source);
			setPathToVar(source, vars.get(source));
		}
	}
	
	@Override
	public Set<AccessExpression> processPrimitiveStore(ConcreteAccessExpression destination) {
		Set<AccessExpression> affected = new HashSet<AccessExpression>();
		
		if (destination == null) {
			return affected;
		}
			
		Map<AccessExpression, VariableID> destinationCandidates = destination.resolve().processed;
		
		for (AccessExpression destinationPath : destinationCandidates.keySet()) {
			// ASSIGN A PRIMITIVE VALUE - STORES NEW VALUE (REWRITES DATA => NO PATH UNSETTING, VARID STAY THE SAME, ONLY THE VALUES CHANGED)
			setPathToVar(destinationPath, destinationCandidates.get(destinationPath));
						
	    	Set<AccessExpression> equivalentPaths = lookupEquivalentAccessPaths(destinationPath);
	    	equivalentPaths.add(destinationPath);
	    			
	    	affected.addAll(equivalentPaths);
		}
		
		return affected;
	}
	
	@Override
	public Set<AccessExpression> processObjectStore(Expression sourceExpression, ConcreteAccessExpression destinationPrefix) {
		Set<AccessExpression> affected = new HashSet<AccessExpression>();
		
		ConcreteAccessExpression sourcePrefix = null;
		
		if (sourceExpression instanceof ConcreteAccessExpression) {
			sourcePrefix = (ConcreteAccessExpression) sourceExpression;
		}

		if (destinationPrefix == null) {
			return affected;
		}
		
		Set<AccessExpression> destinationCandidates = destinationPrefix.partialExhaustiveResolve().processed.keySet();
		boolean unambiguous = destinationCandidates.size() == 1;
		
		if (sourceExpression instanceof AnonymousExpression) {
			AnonymousExpression anonymous = (AnonymousExpression) sourceExpression;
			
			for (AccessExpression destination : destinationCandidates) {
				if (unambiguous) {
					for (AccessExpression subobjects : lookupAccessPaths(destination)) {
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
		
		Map<AccessExpression, VariableID> sourceCandidates = sourcePrefix.partialResolve().processed;

		Map<AccessExpression, Set<VariableID>> rewrites = new HashMap<AccessExpression, Set<VariableID>>();
		
		for (AccessExpression destination : destinationCandidates) {			
			Set<AccessExpression> affectedObjectPaths = new HashSet<AccessExpression>();

			for (AccessExpression affectedPrefixCandidate : prefixToVariableIDs.keySet()) {
				if (affectedPrefixCandidate.isProperPrefixOf(destination) && affectedPrefixCandidate.getLength() >= destination.getLength() - 1) {
					AccessExpression affectedPrefix = affectedPrefixCandidate;
					
					AccessExpression element = destination.get(affectedPrefix.getLength());

					for (AccessExpression equivalentObjectPathPrefix : lookupEquivalentAccessPaths(affectedPrefix)) {
						AccessExpression equivalentObjectPath = equivalentObjectPathPrefix.clone();
						
						if (element instanceof ObjectFieldRead) {
							ObjectFieldRead sub = (ObjectFieldRead) element;
							
							equivalentObjectPath = DefaultObjectFieldRead.create(equivalentObjectPath, sub.getField().getName());
						} else if (element instanceof ArrayElementRead) {
							ArrayElementRead index = (ArrayElementRead) element;
							
							equivalentObjectPath = DefaultArrayElementRead.create(equivalentObjectPath, index.getIndex());
						}
						
						affectedObjectPaths.add(equivalentObjectPath);
					}
				}
			}
			
			affectedObjectPaths.add(destination);
			affected.addAll(affectedObjectPaths);

			for (AccessExpression sourceCandidate : sourceCandidates.keySet()) {
				for (AccessExpression source : lookupAccessPaths(sourceCandidate)) {
					for (AccessExpression prefix : affectedObjectPaths) {
						AccessExpression newPath = source.clone();
						AccessExpression oldPrefix = sourceCandidate;
						AccessExpression newPrefix = prefix.clone();
						
						newPath.reRoot(oldPrefix, newPrefix);
												
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
		
		for (AccessExpression path : rewrites.keySet()) {
			if (unambiguous) {
				for (AccessExpression subobjects : lookupAccessPaths(path)) {
					unsetPath(subobjects);
				}
			}

			setPathToVars(path, rewrites.get(path));
		}
		
		System.out.println(">> >> " + affected);
		
		return affected;
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		
		int padding = 0;

		for (AccessExpression p : prefixToVariableIDs.keySet()) {
			String path = p.toString(NotationPolicy.DOT_NOTATION);
			
			padding = padding < path.length() ? path.length() : padding;
		}
		
		padding += 4;

        TreeSet<AccessExpression> paths = new TreeSet<AccessExpression>(new Comparator<AccessExpression>() {
            @Override
            public int compare(AccessExpression p1, AccessExpression p2) {
                return p1.toString(NotationPolicy.DOT_NOTATION).compareTo(p2.toString(NotationPolicy.DOT_NOTATION));
            }
        });

        paths.addAll(prefixToVariableIDs.keySet());
		
		for (AccessExpression p : paths) {
			String path = p.toString(NotationPolicy.DOT_NOTATION);
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
	public boolean isObject(AccessExpression path) {
		if (prefixToVariableIDs.containsKey(path)) {
			for (VariableID var : prefixToVariableIDs.get(path)) {
				if (var instanceof PartialVariableID) {
					PartialVariableID partial = (PartialVariableID) var;
					
					return partial.getRef() instanceof ObjectReference;
				}
				
				return false;
			}
		}
		
		return false;
	}

	@Override
	public boolean isArray(AccessExpression path) {
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
