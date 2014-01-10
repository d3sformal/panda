package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultAccessExpression;
import gov.nasa.jpf.abstraction.common.impl.DefaultExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.PredicatesVisitable;
import gov.nasa.jpf.abstraction.predicate.parser.PredicatesLexer;
import gov.nasa.jpf.abstraction.predicate.parser.PredicatesParser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * A common ancestor to all predicates used in the abstraction
 */
public abstract class Predicate implements PredicatesVisitable {
	public abstract List<AccessExpression> getPaths();
	public abstract Predicate replace(Map<AccessExpression, Expression> replacements);

    public final Predicate replace(AccessExpression original, Expression replacement) {
        Map<AccessExpression, Expression> replacements = new HashMap<AccessExpression, Expression>();

        replacements.put(original, replacement);

        return replace(replacements);
    }

    protected int hashCodeValue;

    public String toString() {
    	return toString(Notation.policy);
    }
    public String toString(Notation policy) {
    	return Notation.convertToString(this, policy);
	}
    
    /**
     * @param universe Universe of all predicates that may or may not determine the value of this predicate
     * @return A selection of those predicates from the universe that may directly determine the value of this predicate
     */
    public Set<Predicate> selectDeterminants(Set<Predicate> universe) {
    	Set<Predicate> ret = new HashSet<Predicate>();
    	
	for (AccessExpression path : getPaths()) {
    		for (Predicate candidate : universe) {
			List<AccessExpression> candidatePaths = candidate.getPaths();

			for (AccessExpression candidatePath : candidatePaths) {
				for (AccessExpression candidateSubPath : candidatePath.getAllPrefixes()) {
					if (candidateSubPath.isSimilarToPrefixOf(path)) {
						ret.add(candidate);
					}
				}
			}
		}
	}
    	
    	return ret;
    }
    
    /**
     * Finds a transitive closure of all predicates that may infer the value of this one.
     * 
     * @param universe Universe of all predicates that may or may not determine the value of this predicate
     * @return A selection of those predicates from the universe that may determine the value of this predicate
     */
	public Set<Predicate> determinantClosure(Set<Predicate> universe) {
		Set<Predicate> cur;
		Set<Predicate> ret = selectDeterminants(universe);
		
		int prevSize = 0;
		
		while (prevSize != ret.size()) {
			prevSize = ret.size();

			cur = new HashSet<Predicate>();

			for (Predicate predicate : ret) {
				cur.addAll(predicate.selectDeterminants(universe));
			}
			
			ret = cur;
		}
		
		return ret;
	}
	
	/**
	 * Changes all access expression present in the predicate to a form which reflects an assignment "expression := newExpression"
	 * 
	 * Let p = (a = 3):
	 *   update(p, a, b + 3) returns b + 3 = 3
	 *   
	 * Let q = (aread(arr, a, 1) = 0):
	 *   update(q, a[0], 3) returns aread(awrite(arr, a, 0, 3), 1) = 0
	 *   
	 * Let r = (fread(f, o) = 10)
	 *   update(r, s.f, x) returns fread(fwrite(f, s, x), o) = 10
	 *   
	 * Used to determine weakest preconditions.
	 * 
	 * @param expression an access expression being written to (e.g. local variable "a")
	 * @param newExpression any arbitrary expression being written (e.g. "b + 3")
	 * @return a predicate reflecting the updates
	 */
	public abstract Predicate update(AccessExpression expression, Expression newExpression);
	
	public static Predicate createFromString(String definition) {
		ANTLRInputStream chars = new ANTLRInputStream(definition);
		PredicatesLexer lexer = new PredicatesLexer(chars);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PredicatesParser parser = new PredicatesParser(tokens);

		return parser.predicate().val;
	}

	@Override
	public final int hashCode() {
		return hashCodeValue;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Predicate) {
			Predicate p = (Predicate) o;

			return toString(Notation.DOT_NOTATION).equals(p.toString(Notation.DOT_NOTATION));
		}
		
		return false;
	}

	/**
	 * Test of basic operations
	 */
	public static void main(String[] args) {
		Notation.policy = Notation.DOT_NOTATION;

		Predicate p = createFromString("a + b = 2");

		AccessExpression a = DefaultAccessExpression.createFromString("a");
		Expression aplusb = DefaultExpression.createFromString("a + b");
		AccessExpression b = DefaultAccessExpression.createFromString("b");
		AccessExpression c = DefaultAccessExpression.createFromString("c");

		Map<AccessExpression, Expression> replacements = new HashMap<AccessExpression, Expression>();
		replacements.put(a, aplusb);
		replacements.put(b, c);

		Predicate q = p.replace(replacements);

		System.out.println(q);
	}
}
