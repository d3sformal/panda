package gov.nasa.jpf.abstraction.predicate.smt;

import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import java.util.Map;

/**
 * A container holding information used to infer a truth value of a particular predicate
 *
 * its positive weakest precondition: WP(statement, p) ... e.g. WP(a++, a = 3) = (a + 1 = 3)
 * its negative weakest precondition: WP(statement, not p)
 *
 * all predicates (often transitive closure) that may affect the truth value of the predicate in question by their truth values:
 *
 * p: a = 3
 *
 * determinants:
 *
 * a = b, b > 2, a < c, c = 4
 */
public class PredicateValueDeterminingInfo {
    public Predicate positiveWeakestPrecondition;
    public Predicate negativeWeakestPrecondition;
    public Map<Predicate, TruthValue> determinants;

    public PredicateValueDeterminingInfo(Predicate positiveWeakestPrecondition, Predicate negativeWeakestPrecondition, Map<Predicate, TruthValue> determinants) {
        this.positiveWeakestPrecondition = positiveWeakestPrecondition;
        this.negativeWeakestPrecondition = negativeWeakestPrecondition;
        this.determinants = determinants;
    }
}
