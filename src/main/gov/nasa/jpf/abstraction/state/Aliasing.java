package gov.nasa.jpf.abstraction.state;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.state.universe.UniverseIdentifier;

/**
 * An unused class that holds information about aliasing among selected access expressions (added with the method `add`)
 * Provides aliasing predicates (by method `addAliasingPredicatesToMap`)
 *   if a, b are aliases: a = b
 *   if a, b cannot be aliases: a != b
 */
public class Aliasing {
    private Map<UniverseIdentifier, Set<AccessExpression>> mustAlias = new HashMap<UniverseIdentifier, Set<AccessExpression>>();
    private Map<UniverseIdentifier, Set<AccessExpression>> mayAlias = new HashMap<UniverseIdentifier, Set<AccessExpression>>();

    private Map<AccessExpression, Set<AccessExpression>> mustAliasShortcut = new HashMap<AccessExpression, Set<AccessExpression>>();

    public void add(AccessExpression expr, Set<UniverseIdentifier> vals) {
        if (vals.size() == 1) {
            addMustAlias(vals.iterator().next(), expr);
        }

        for (UniverseIdentifier id : vals) {
            addMayAlias(id, expr);
        }
    }

    private void addMustAlias(UniverseIdentifier val, AccessExpression expr) {
        if (!mustAlias.containsKey(val)) {
            mustAlias.put(val, new HashSet<AccessExpression>());
        }

        mustAlias.get(val).add(expr);
        mustAliasShortcut.put(expr, mustAlias.get(val));
    }

    private void addMayAlias(UniverseIdentifier val, AccessExpression expr) {
        if (!mayAlias.containsKey(val)) {
            mayAlias.put(val, new HashSet<AccessExpression>());
        }

        mayAlias.get(val).add(expr);
    }

    private Set<AccessExpression> getMustAlias(AccessExpression expr) {
        if (!mustAliasShortcut.containsKey(expr)) {
            return Collections.emptySet();
        }

        return mustAliasShortcut.get(expr);
    }

    private Set<AccessExpression> getMayAlias(AccessExpression expr) {
        Set<AccessExpression> ret = new HashSet<AccessExpression>();

        for (Set<AccessExpression> mayAliasClass : mayAlias.values()) {
            if (mayAliasClass.contains(expr)) {
                ret.addAll(mayAliasClass);
            }
        }

        return ret;
    }

    public void addAliasingPredicatesToMap(Set<AccessExpression> exprs, Map<Predicate, TruthValue> map) {
        for (AccessExpression a : exprs) {
            Set<AccessExpression> mustAliasA = getMustAlias(a);
            Set<AccessExpression> mayAliasA = getMayAlias(a);

            for (AccessExpression b : exprs) {
                if (a.equals(b)) continue;

                if (mustAliasA.contains(b)) {
                    map.put(Equals.create(a, b), TruthValue.TRUE);
                } else if (!mayAliasA.contains(b)) {
                    map.put(Equals.create(a, b), TruthValue.FALSE);
                }
            }
        }
    }
}
