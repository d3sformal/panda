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

public abstract class Predicate implements PredicatesVisitable {
	public abstract List<AccessExpression> getPaths();
	public abstract Predicate replace(Map<AccessExpression, Expression> replacements);
	
    public String toString() {
    	return toString(Notation.policy);
    }
    public String toString(Notation policy) {
    	return Notation.convertToString(this, policy);
	}
    
    public Set<Predicate> selectDeterminants(Set<Predicate> universe) {
    	Set<Predicate> ret = new HashSet<Predicate>();
    	
    	for (Predicate candidate : universe) {
			List<AccessExpression> candidatePaths = candidate.getPaths();
			
			for (AccessExpression path : getPaths()) {
				for (AccessExpression candidatePath : candidatePaths) {
					if (candidatePath.isSimilarToPrefixOf(path)) {
						ret.add(candidate);
					}
				}
			}
		}
    	
    	return ret;
    }
    
	public Set<Predicate> determinantClosure(Set<Predicate> universe) {
		Set<Predicate> cur;
		Set<Predicate> ret = selectDeterminants(universe);
		
		int formerSize = 0;
		
		while (formerSize != ret.size()) {
			formerSize = ret.size();

			cur = new HashSet<Predicate>();

			for (Predicate predicate : ret) {
				cur.addAll(predicate.selectDeterminants(universe));
			}
			
			ret = cur;
		}
		
		return ret;
	}
	
	public abstract Predicate update(AccessExpression expression, Expression newExpression);
	
	public static Predicate createFromString(String definition) {
		ANTLRInputStream chars = new ANTLRInputStream(definition);
		PredicatesLexer lexer = new PredicatesLexer(chars);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PredicatesParser parser = new PredicatesParser(tokens);

		return parser.predicate().val;
	}

	@Override
	public int hashCode() {
		return toString(Notation.DOT_NOTATION).hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Predicate) {
			Predicate p = (Predicate) o;

			return toString(Notation.DOT_NOTATION).equals(p.toString(Notation.DOT_NOTATION));
		}
		
		return false;
	}

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
