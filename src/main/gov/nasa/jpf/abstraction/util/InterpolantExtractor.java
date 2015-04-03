package gov.nasa.jpf.abstraction.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.Add;
import gov.nasa.jpf.abstraction.common.Comparison;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Disjunction;
import gov.nasa.jpf.abstraction.common.Divide;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Formula;
import gov.nasa.jpf.abstraction.common.LessThan;
import gov.nasa.jpf.abstraction.common.Modulo;
import gov.nasa.jpf.abstraction.common.Multiply;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.Operation;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Subtract;
import gov.nasa.jpf.abstraction.common.Tautology;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ArrayElementWrite;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthRead;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayElementWrite;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultArrayLengthRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.impl.Select;
import gov.nasa.jpf.abstraction.common.access.impl.Store;

public class InterpolantExtractor {
    public static void collectQuantifiedVarValues(Predicate p, Map<Root, Set<Expression>> values) {
        // Recurse on compound formula
        if (p instanceof Formula) {
            Formula f = (Formula) p;

            collectQuantifiedVarValues(f.a, values);
            collectQuantifiedVarValues(f.b, values);
        }
        // Recurse over negation
        if (p instanceof Negation) {
            Negation n = (Negation) p;

            collectQuantifiedVarValues(n.predicate, values);
        }
        // Place related values into Eq options
        if (p instanceof Comparison) {
            Comparison c = (Comparison) p;

            Expression a = c.a;
            Expression b = c.b;

            // Detect quantified variables
            boolean aQuant = false;
            boolean bQuant = false;

            if (a instanceof Root) {
                Root r = (Root) a;

                if (r.getName().startsWith("%")) {
                    aQuant = true;
                }
            }

            if (b instanceof Root) {
                Root r = (Root) b;

                if (r.getName().startsWith("%")) {
                    bQuant = true;
                }
            }

            Root r = null;
            Expression opt = null;

            if (aQuant && !bQuant) {
                r = (Root) a;
                opt = b;
            }

            if (bQuant && !aQuant) {
                r = (Root) b;
                opt = a;
            }

            // Add possible equality
            if (r != null) {
                if (!values.containsKey(r)) {
                    values.put(r, new HashSet<Expression>());
                }

                values.get(r).add(opt);
            }
        }
    }

    public static Predicate expandQuantifiedVars(Predicate p, Map<Root, Set<Expression>> values) {
        Predicate ret = p;

        for (Root var : values.keySet()) {
            Predicate instantiated = Contradiction.create();

            for (Expression e : values.get(var)) {
                Predicate instance = ret.replace(var, e);

                if (!(instance instanceof Tautology)) { // True would consume the rest of the interpolant (in disjunction via absorption law)
                    instantiated = Disjunction.create(instantiated, instance);
                }
            }

            ret = instantiated;
        }

        return ret;
    }

    public static void println(String s) {
        System.out.println(s.replaceAll("\\s+", " "));
    }

    private static Expression extractHighLevelConstructs(Expression e) {
        if (e instanceof Constant) {
            return e;
        }
        if (e instanceof AccessExpression) {
            // Especially inspect all the Entities that can be produced by the grammar rules for the `path` non-terminal symbol
            // Because those objects may still contain unextracted select and store structures
            if (e instanceof ObjectFieldWrite) {
                ObjectFieldWrite w = (ObjectFieldWrite) e;

                AccessExpression o = (AccessExpression) extractHighLevelConstructs(w.getObject());
                Expression v = extractHighLevelConstructs(w.getNewValue());

                ObjectFieldWrite newW = DefaultObjectFieldWrite.create(o, w.getField(), v);

                if (newW.equals(w)) {
                    return w;
                }

                return newW;
            }
            if (e instanceof ArrayLengthRead) {
                ArrayLengthRead r = (ArrayLengthRead) e;

                AccessExpression a = (AccessExpression) extractHighLevelConstructs(r.getArray());

                ArrayLengthRead newR = DefaultArrayLengthRead.create(a);

                if (newR.equals(r)) {
                    return r;
                }

                return newR;
            }
            if (e instanceof Select) {
                Select s = (Select) e;

                AccessExpression from = s.getFrom();
                Expression index = s.getIndex();

                index = extractHighLevelConstructs(index);

                if (!s.isRoot()) {
                    from = (AccessExpression) extractHighLevelConstructs(from);

                    if (from instanceof Root) {
                        Root r = (Root) from;

                        if (r.getName().startsWith("field_ssa_")) {
                            String f = r.getName().replaceAll("field_ssa_[0-9]*_", "");

                            return DefaultObjectFieldRead.create((AccessExpression) index, f);
                        }
                    }
                }

                // (select (select ... ...) ...)
                if (from instanceof Select) {
                    Select fromS = (Select) from;

                    // (select (select arr ...) ...)
                    if (fromS.isRoot()) {
                        return DefaultArrayElementRead.create((AccessExpression) fromS.getIndex(), s.getIndex());
                    }

                    // (select (select (awrite ... ... ... ...) ...) ...)
                    if (fromS.getFrom() instanceof ArrayElementWrite) {
                        ArrayElementWrite w = (ArrayElementWrite) fromS.getFrom();

                        return DefaultArrayElementRead.create((AccessExpression) fromS.getIndex(), w, s.getIndex());
                    }
                }

                Select newSelect = Select.create(from, index);

                if (newSelect.equals(s)) {
                    return s;
                }

                return newSelect;
            }
            if (e instanceof Store) {
                Store s = (Store) e;

                AccessExpression to = s.getTo();
                Expression index = s.getIndex();
                Expression value = s.getValue();

                if (!s.isRoot()) {
                    to = (AccessExpression) extractHighLevelConstructs(to);
                }

                index = extractHighLevelConstructs(index);
                value = extractHighLevelConstructs(value);

                // (store arr ... ...)
                if (s.isRoot()) {

                    // (store arr ... (store ... ... ...))
                    if (value instanceof Store) {
                        Store valueS = (Store) value;

                        // (store arr ... (store (select ... ...) ... ...))
                        if (valueS.getTo() instanceof Select) {
                            Select valueSToS = (Select) valueS.getTo();

                            // (store arr ... (store (select arr ...) ... ...))
                            if (valueSToS.isRoot()) {

                                // (store arr A (store (select arr A) ... ...)
                                if (s.getIndex().equals(valueSToS.getIndex())) {
                                    return DefaultArrayElementWrite.create((AccessExpression) index, valueS.getIndex(), valueS.getValue());
                                }
                            }
                        }
                    }
                }

                // (store (fwrite ... ... ...) ... ...)
                if (to instanceof ObjectFieldWrite) {
                    ObjectFieldWrite w = (ObjectFieldWrite) to;

                    return DefaultObjectFieldWrite.create((AccessExpression) index, w.getField(), value);
                }

                Store newStore = Store.create(to, index, value);

                if (newStore.equals(s)) {
                    return s;
                }

                return newStore;
            }
            return e;
        }
        if (e instanceof Operation) {
            if (e instanceof Add) {
                Add a = (Add) e;

                Expression newAdd = Add.create(extractHighLevelConstructs(a.a), extractHighLevelConstructs(a.b));

                if (newAdd.equals(a)) {
                    return a;
                }

                return newAdd;
            }
            if (e instanceof Subtract) {
                Subtract s = (Subtract) e;

                Expression newSubtract = Subtract.create(extractHighLevelConstructs(s.a), extractHighLevelConstructs(s.b));

                if (newSubtract.equals(s)) {
                    return s;
                }

                return newSubtract;
            }
            if (e instanceof Multiply) {
                Multiply m = (Multiply) e;

                Expression newMultiply = Multiply.create(extractHighLevelConstructs(m.a), extractHighLevelConstructs(m.b));

                if (newMultiply.equals(m)) {
                    return m;
                }

                return newMultiply;
            }
            if (e instanceof Divide) {
                Divide d = (Divide) e;

                Expression newDivide = Divide.create(extractHighLevelConstructs(d.a), extractHighLevelConstructs(d.b));

                if (newDivide.equals(d)) {
                    return d;
                }

                return newDivide;
            }
            if (e instanceof Modulo) {
                Modulo m = (Modulo) e;

                Expression newModulo = Modulo.create(extractHighLevelConstructs(m.a), extractHighLevelConstructs(m.b));

                if (newModulo.equals(m)) {
                    return m;
                }

                return newModulo;
            }
            return e;
        }

        return null;
    }

    public static Predicate eq(Expression e1, Expression e2) {
        e1 = extractHighLevelConstructs(e1);
        e2 = extractHighLevelConstructs(e2);

        Predicate ret = Tautology.create();

        // Heuristic
        if (e1 instanceof Select && e2 instanceof Select) {
            Select s1 = (Select) e1;
            Select s2 = (Select) e2;

            if (s1.isRoot() && s2.isRoot()) {
                e1 = s1.getIndex();
                e2 = s2.getIndex();
            }
            if (s2.isRoot() && !s1.isRoot()) {
                Select s = s1;
                s1 = s2;
                s2 = s;
            }

            // (select arr ...) = (select ... ...)
            if (s1.isRoot() && !s2.isRoot()) {

                // (select arr A) = (select ... A)
                if (s1.getIndex().equals(s2.getIndex())) {

                    // (select arr A) = (select (awrite ... ... ... ...) A)
                    if (s2.getFrom() instanceof ArrayElementWrite) {
                        ArrayElementWrite w = (ArrayElementWrite) s2.getFrom();

                        e1 = DefaultArrayElementRead.create(w.getArray(), w.getIndex());
                        e2 = w.getNewValue();
                    }

                    // (select arr A) = (select (store ... ... ...) A)
                    if (s2.getFrom() instanceof Store) {
                        Store s2fromS = (Store) s2.getFrom();

                        return eq(Select.create(s2fromS.getTo(), s2fromS.getIndex()), s2fromS.getValue());
                    }
                }
            }
        }
        if (e1 instanceof Store && e2 instanceof Select) {
            Expression e = e1;
            e1 = e2;
            e2 = e;
        }
        if (e1 instanceof Select && e2 instanceof Store) {
            Select s1 = (Select) e1;
            Store s2 = (Store) e2;

            // (select arr ...) = (store ... ... ...)
            if (s1.isRoot()) {

                // (select arr ...) = (store (select ... ...) ... ...)
                if (s2.getTo() instanceof Select) {
                    Select s2toS = (Select) s2.getTo();

                    // (select arr ...) = (store (select arr ...) ... ...)
                    if (s2toS.isRoot()) {

                        // (select arr A) = (store (select arr A) ... ...)
                        if (s1.getIndex().equals(s2toS.getIndex())) {
                            e1 = DefaultArrayElementRead.create((AccessExpression) s1.getIndex(), s2.getIndex());
                            e2 = s2.getValue();
                        }
                    }
                }
            }

            // (select (awrite ... ... ... ...) ...) = (store ... ... ...)
            if (s1.getFrom() instanceof ArrayElementWrite) {
                ArrayElementWrite w = (ArrayElementWrite) s1.getFrom();

                // (select (awrite A ... ... ...) B) = (store ... ... ...)
                // Track aliasing between A and B
                // It may affect the select-over-store semantics in this example
                ret = Conjunction.create(ret, Equals.create(w.getArray(), s1.getIndex()));

                // (select (awrite ... ... ... ...) ...) = (store (select ... ...) ... ...)
                if (s2.getTo() instanceof Select) {
                    Select s2toS = (Select) s2.getTo();

                    // (select (awrite ... ... ... ...) ...) = (store (select arr ...) ... ...)
                    if (s2toS.isRoot()) {

                        // (select (awrite ... ... ... ...) A) = (store (select arr A) ... ...)
                        if (s1.getIndex().equals(s2toS.getIndex())) {
                            e1 = DefaultArrayElementRead.create((AccessExpression) s1.getIndex(), s2.getIndex());
                            e2 = s2.getValue();
                        }
                    }
                }
            }
        }
        if (e1 instanceof Store && e2 instanceof Root) {
            Expression e = e1;
            e1 = e2;
            e2 = e;
        }
        if (e1 instanceof Root && e2 instanceof Store) {
            Root r1 = (Root) e1;
            Store s = (Store) e2;

            // field = (store ... ...)
            if (r1.getName().startsWith("field_ssa_")) {
                String name1 = r1.getName().replaceAll("field_ssa_[0-9]*_", "");

                if (s.getTo() instanceof Root) {
                    Root r2 = (Root) s.getTo();
                    String name2 = r2.getName().replaceAll("field_ssa_[0-9]*_", "");

                    // field = (store field ...)
                    if (name2.equals(name1)) {
                        e1 = DefaultObjectFieldRead.create((AccessExpression) s.getIndex(), name1);
                        e2 = s.getValue();
                    }
                }
            }
        }

        ret = Conjunction.create(ret, Equals.create(e1, e2));

        return ret;
    }

    public static Predicate lt(Expression e1, Expression e2) {
        e1 = extractHighLevelConstructs(e1);
        e2 = extractHighLevelConstructs(e2);

        Predicate ret = Tautology.create();

        // Heuristic
        ret = Conjunction.create(ret, LessThan.create(e1, e2));

        return ret;
    }
}

