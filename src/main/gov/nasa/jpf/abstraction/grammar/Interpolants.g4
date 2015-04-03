grammar Interpolants;

@header {
    import java.util.AbstractMap;
    import java.util.HashMap;
    import java.util.HashSet;
    import java.util.List;
    import java.util.LinkedList;
    import java.util.Map;
    import java.util.Set;
    import java.util.SortedSet;
    import java.util.TreeSet;

    import gov.nasa.jpf.vm.ElementInfo;
    import gov.nasa.jpf.vm.ThreadInfo;

    import gov.nasa.jpf.abstraction.PandaConfig;
    import gov.nasa.jpf.abstraction.PredicateAbstraction;
    import gov.nasa.jpf.abstraction.common.*;
    import gov.nasa.jpf.abstraction.common.impl.*;
    import gov.nasa.jpf.abstraction.common.access.*;
    import gov.nasa.jpf.abstraction.common.access.impl.*;
    import gov.nasa.jpf.abstraction.concrete.*;
    import gov.nasa.jpf.abstraction.state.universe.*;
    import gov.nasa.jpf.abstraction.util.*;
}

@members{
    public static class Helper {
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
                                        return DefaultArrayElementWrite.create((AccessExpression) s.getIndex(), valueS.getIndex(), valueS.getValue());
                                    }
                                }
                            }
                        }
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
}

predicates returns [Predicate[] val]
    : '(' p=predicatelist ')' {
        $ctx.val = $p.val;
    }
    ;

predicatelist returns [Predicate[] val]
    : /* EMPTY */ {
        $ctx.val = new Predicate[0];
    }
    | ps=predicatelist p=predicate {
        $ctx.val = new Predicate[$ps.val.length + 1];

        for (int i = 0; i < $ps.val.length; ++i) {
            $ctx.val[i] = $ps.val[i];
        }

        $ctx.val[$ps.val.length] = $p.val;
    }
    ;

letpair returns [Map.Entry<String, Object> val]
    : '(' '.' v=ID_TOKEN e=expression ')' {
        $ctx.val = new AbstractMap.SimpleEntry<String, Object>($v.text, $e.val);
    }
    | '(' v=ID_TOKEN '!' n=CONSTANT_TOKEN e=expression ')' {
        $ctx.val = new AbstractMap.SimpleEntry<String, Object>($v.text + '!' + Integer.parseInt($n.text), $e.val);
    }
    | '(' '.' v=ID_TOKEN p=predicate ')' {
        $ctx.val = new AbstractMap.SimpleEntry<String, Object>($v.text, $p.val);
    }
    | '(' v=ID_TOKEN '!' n=CONSTANT_TOKEN p=predicate ')' {
        $ctx.val = new AbstractMap.SimpleEntry<String, Object>($v.text + '!' + Integer.parseInt($n.text), $p.val);
    }
    ;

standalonepredicate returns [Predicate val]
    : p=predicate {
        $ctx.val = $p.val;
    }
    ;

predicate returns [Predicate val] locals [static ScopedDefineMap let = new ScopedDefineMap(); Predicate acc;]
    : '.' id=ID_TOKEN {
        $ctx.val = (Predicate) PredicateContext.let.get($id.text);
    }
    | id=ID_TOKEN '!' n=CONSTANT_TOKEN {
        $ctx.val = (Predicate) PredicateContext.let.get($id.text + '!' + Integer.parseInt($n.text));
    }
    | '(' LET_TOKEN {PredicateContext.let.enterNested();} '(' (l=letpair {PredicateContext.let.put($l.val.getKey(), $l.val.getValue());})* ')' p=predicate ')' {
        PredicateContext.let.exitNested();

        $ctx.val = $p.val;
    }
    | TRUE_TOKEN {
        $ctx.val = Tautology.create();
    }
    | FALSE_TOKEN {
        $ctx.val = Contradiction.create();
    }
    | '(' AND_TOKEN p=predicate {$ctx.acc = $p.val;} (q=predicate {$ctx.acc = Conjunction.create($ctx.acc, $q.val);})+ ')' {
        $ctx.val = $ctx.acc;
    }
    | '(' OR_TOKEN p=predicate {$ctx.acc = $p.val;} (q=predicate {$ctx.acc = Disjunction.create($ctx.acc, $q.val);})+ ')' {
        $ctx.val = $ctx.acc;
    }
    | '(' NOT_TOKEN p=predicate ')' {
        $ctx.val = Negation.create($p.val);
    }
    | '(' ITE_TOKEN p=predicate q=predicate r=predicate ')' {
        $ctx.val = Disjunction.create(Conjunction.create($p.val, $q.val), Conjunction.create(Negation.create($p.val), $r.val));
    }
    | '(' FORALL_TOKEN {PredicateContext.let.enterNested(); String vars = "";} '(' ('(' '%' n=CONSTANT_TOKEN TYPE_TOKEN ')' {vars += " %" + $n.text; PredicateContext.let.put("%" + Integer.parseInt($n.text), DefaultRoot.create("%" + Integer.parseInt($n.text)));} )+ ')' '(' '!' p=predicate ':qid' 'itp' ')' ')' {
        PredicateContext.let.exitNested();

        if (PandaConfig.getInstance().enabledVerbose(this.getClass())) {
            Helper.println("[WARNING] Omitting quantifier in FOR ALL" + vars + ": " + $p.val);
        }

        // Collect values that the quantified variables may possess
        Map<Root, Set<Expression>> eqOptions = new HashMap<Root, Set<Expression>>();

        Helper.collectQuantifiedVarValues($p.val, eqOptions);

        Predicate p = Helper.expandQuantifiedVars($p.val, eqOptions);

        if (PandaConfig.getInstance().enabledVerbose(this.getClass())) {
            Helper.println("[WARNING] Expanding quantified formula FOR ALL" + vars + " using " + eqOptions + ": ");
            System.out.print("[WARNING] \t\t");
            Helper.println(p.toString());
        }

        $ctx.val = p;
    }
    | '(=>' p=predicate q=predicate ')' {
        $ctx.val = Disjunction.create(Negation.create($p.val), $q.val);
    }
    | '(=' '.' id1=ID_TOKEN '.' id2=ID_TOKEN ')' {
        Object o1 = PredicateContext.let.get($id1.text);
        Object o2 = PredicateContext.let.get($id2.text);

        if (o1 instanceof Predicate && o2 instanceof Predicate) {
            Predicate p = (Predicate) o1;
            Predicate q = (Predicate) o2;

            $ctx.val = Disjunction.create(Conjunction.create(p, q), Conjunction.create(Negation.create(p), Negation.create(q)));
        }

        if (o1 instanceof Expression && o2 instanceof Expression) {
            Expression a = (Expression) o1;
            Expression b = (Expression) o2;

            $ctx.val = Helper.eq(a, b);
        }
    }
    | '(=' id1=ID_TOKEN '!' n1=CONSTANT_TOKEN id2=ID_TOKEN '!' n2=CONSTANT_TOKEN ')' {
        Object o1 = PredicateContext.let.get($id1.text + '!' + Integer.parseInt($n1.text));
        Object o2 = PredicateContext.let.get($id2.text + '!' + Integer.parseInt($n2.text));

        if (o1 instanceof Predicate && o2 instanceof Predicate) {
            Predicate p = (Predicate) o1;
            Predicate q = (Predicate) o2;

            $ctx.val = Disjunction.create(Conjunction.create(p, q), Conjunction.create(Negation.create(p), Negation.create(q)));
        }

        if (o1 instanceof Expression && o2 instanceof Expression) {
            Expression a = (Expression) o1;
            Expression b = (Expression) o2;

            $ctx.val = Helper.eq(a, b);
        }
    }
    | '(=' p=predicate q=predicate ')' {
        $ctx.val = Disjunction.create(Conjunction.create($p.val, $q.val), Conjunction.create(Negation.create($p.val), Negation.create($q.val)));
    }
    | '(' DISTINCT_TOKEN a=expression b=expression ')' {
        $ctx.val = Negation.create(Helper.eq($a.val, $b.val));
    }
    | '(=' a=expression b=expression ')' {
        $ctx.val = Helper.eq($a.val, $b.val);
    }
    | '(=' a=expression NULL_TOKEN ')' {
        $ctx.val = Helper.eq($a.val, NullExpression.create());
    }
    | '(=' NULL_TOKEN b=expression ')' {
        $ctx.val = Helper.eq(NullExpression.create(), $b.val);
    }
    | NULL_TOKEN '=' NULL_TOKEN {
        $ctx.val = Tautology.create();
    }
    | '(<' a=expression b=expression ')' {
        $ctx.val = Helper.lt($a.val, $b.val);
    }
    | '(>' a=expression b=expression ')' {
        $ctx.val = Helper.lt($b.val, $a.val);
    }
    | '(<=' a=expression b=expression ')' {
        $ctx.val = Helper.lt($b.val, $a.val);
    }
    | '(>=' a=expression b=expression ')' {
        $ctx.val = Helper.lt($a.val, $b.val);
    }
    | '(=' ARRLEN_TOKEN ARRLEN_TOKEN ')' {
        $ctx.val = Tautology.create();
    }
    ;

expression returns [Expression val] locals [Expression acc]
    : t=term {
        $ctx.val = $t.val;
    }
    | '(+' a=term {$ctx.acc = $a.val;} (b=term {$ctx.acc = Add.create($ctx.acc, $b.val);})+ ')' {
        $ctx.val = $ctx.acc;
    }
    | '(-' a=term b=term ')' {
        $ctx.val = Subtract.create($a.val, $b.val);
    }
    ;

term returns [Expression val]
    : f=factor {
        $ctx.val = $f.val;
    }
    | '(*' a=factor b=factor ')' {
        $ctx.val = Multiply.create($a.val, $b.val);
    }
    | '(/' a=factor b=factor ')' {
        $ctx.val = Divide.create($a.val, $b.val);
    }
    ;

factor returns [Expression val]
    : '.' id=ID_TOKEN {
        $ctx.val = (Expression) PredicateContext.let.get($id.text);
    }
    | id=ID_TOKEN '!' n=CONSTANT_TOKEN {
        $ctx.val = (Expression) PredicateContext.let.get($id.text + '!' + Integer.parseInt($n.text));
    }
    | '%' n=CONSTANT_TOKEN {
        $ctx.val = (Expression) PredicateContext.let.get("%" + Integer.parseInt($n.text));
    }
    | CONSTANT_TOKEN {
        $ctx.val = Constant.create(Integer.parseInt($CONSTANT_TOKEN.text));
    }
    | '(' SELECT_TOKEN ARRLEN_TOKEN p=path ')' {
        $ctx.val = DefaultArrayLengthRead.create($p.val);
    }
    | p=path {
        $ctx.val = $p.val;
    }
    | '(' e=expression ')' {
        $ctx.val = $e.val;
    }
    | '(-' e=expression ')' {
        $ctx.val = Subtract.create(Constant.create(0), $e.val);
    }
    ;

path returns [DefaultAccessExpression val]
    : f=FRESH_TOKEN {
        int refId = Integer.parseInt($f.text.replaceAll("^fresh_", ""));

        ElementInfo ei = ThreadInfo.getCurrentThread().getElementInfo(refId);
        Reference ref = new Reference(ei);

        Universe u = PredicateAbstraction.getInstance().getSymbolTable().getUniverse();
        StructuredValue v = u.get(ref);

        if (v instanceof UniverseArray) {
            UniverseArray a = (UniverseArray) v;

            $ctx.val = AnonymousArray.create(ref, Constant.create(a.getLength()));
        } else {
            $ctx.val = AnonymousObject.create(new Reference(ei));
        }
    }
    | f=RETURN_TOKEN {
        String r = $f.text.replaceAll("var_ssa_[0-9]+_frame_[0-9]+_", "");

        if (r.equals("return")) {
            $ctx.val = DefaultReturnValue.create();
        } else {
            $ctx.val = DefaultReturnValue.create(r);
        }
    }
    | f=CLASS_TOKEN {
        $ctx.val = DefaultPackageAndClass.create($f.text.substring("class_".length()).replaceAll("_", "."));
    }
    | f=ID_TOKEN {
        $ctx.val = DefaultRoot.create($f.text.replaceAll("var_ssa_[0-9]+_frame_[0-9]+_", ""));
    }
    | '(' SELECT_TOKEN '.' id=ID_TOKEN e=expression ')' {
        AccessExpression ae = (AccessExpression) PredicateContext.let.get($id.text);

        $ctx.val = Select.create(ae, $e.val);
    }
    | '(' SELECT_TOKEN id=ID_TOKEN '!' n=CONSTANT_TOKEN e=expression ')' {
        AccessExpression ae = (AccessExpression) PredicateContext.let.get($id.text + '!' + Integer.parseInt($n.text));

        $ctx.val = Select.create(ae, $e.val);
    }
    | '(' SELECT_TOKEN a=ARR_TOKEN p=path ')' {
        $ctx.val = Select.create($p.val);
    }
    | '(' SELECT_TOKEN p=path e=expression ')' {
        $ctx.val = Select.create($p.val, $e.val);
    }
    | '(' STORE_TOKEN p=path e1=expression e2=expression ')' {
        $ctx.val = Store.create($p.val, $e1.val, $e2.val);
    }
    | '(' STORE_TOKEN a=ARR_TOKEN p1=path p2=path ')' {
        $ctx.val = Store.create($p1.val, $p2.val);
    }
    | '(' STORE_TOKEN a=ARR_TOKEN p1=path '.' id=ID_TOKEN ')' {
        AccessExpression e = (DefaultAccessExpression) PredicateContext.let.get($id.text);

        $ctx.val = Store.create($p1.val, e);
    }
    | '(' STORE_TOKEN a=ARR_TOKEN p1=path id=ID_TOKEN '!' n=CONSTANT_TOKEN ')' {
        AccessExpression e = (DefaultAccessExpression) PredicateContext.let.get($id.text + '!' + Integer.parseInt($n.text));

        $ctx.val = Store.create($p1.val, e);
    }
    | '(' STORE_TOKEN f=ID_TOKEN p=path e=expression ')' {
        $ctx.val = DefaultObjectFieldWrite.create($p.val, $f.text.replaceAll("field_ssa_[0-9]+_", ""), $e.val);
    }
    ;

AND_TOKEN      : 'and';
ARR_TOKEN      : 'ssa_'[0-9]+'_arr';
ARRLEN_TOKEN   : 'arrlen';
CLASS_TOKEN    : 'class_'[a-zA-Z0-9_$]+;
DISTINCT_TOKEN : 'distinct';
FALSE_TOKEN    : 'false';
//FIELD_TOKEN    : 'field_ssa_'[0-9]+'_'[a-zA-Z$_][a-zA-Z0-9$_]*; // FIELD_TOKEN could appear in equalities and we don't have the machinery for that
FORALL_TOKEN   : 'forall';
FRESH_TOKEN    : 'fresh_'[0-9]+;
INIT_TOKEN     : '<init>';
ITE_TOKEN      : 'ite';
LET_TOKEN      : 'let';
NOT_TOKEN      : 'not';
NULL_TOKEN     : 'null';
OR_TOKEN       : 'or';
RETURN_TOKEN   : 'var_ssa_'[0-9]+'_frame_'[0-9]+'_return'('_pc'[0-9]+)?;
SELECT_TOKEN   : 'select';
STORE_TOKEN    : 'store';
TRUE_TOKEN     : 'true';
TYPE_TOKEN     : 'Int';

CONSTANT_TOKEN
    : [-+]?'0'('.'[0-9]+)?
    | [-+]?[1-9][0-9]*('.'[0-9]+)?
    ;

ID_TOKEN
    : [a-zA-Z$_][a-zA-Z0-9$_]*
    ;

WS_TOKEN
    : ([ \t\n\r])+ -> channel(HIDDEN)
    ;
