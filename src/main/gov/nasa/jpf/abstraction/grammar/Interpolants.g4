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
            System.out.println("[WARNING] Omitting quantifier in FOR ALL" + vars + ": " + $p.val);
        }

        // Collect values that the quantified variables may possess
        Map<Root, Set<Expression>> eqOptions = new HashMap<Root, Set<Expression>>();

        Helper.collectQuantifiedVarValues($p.val, eqOptions);

        Predicate p = Helper.expandQuantifiedVars($p.val, eqOptions);

        if (PandaConfig.getInstance().enabledVerbose(this.getClass())) {
            System.out.println("[WARNING] Expanding quantified formula FOR ALL" + vars + " using " + eqOptions + ": ");
            System.out.println("[WARNING] \t\t" + p);
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

            $ctx.val = Equals.create(a, b);
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

            $ctx.val = Equals.create(a, b);
        }
    }
    | '(=' p=predicate q=predicate ')' {
        $ctx.val = Disjunction.create(Conjunction.create($p.val, $q.val), Conjunction.create(Negation.create($p.val), Negation.create($q.val)));
    }
    | '(' DISTINCT_TOKEN a=expression b=expression ')' {
        $ctx.val = Negation.create(Equals.create($a.val, $b.val));
    }
    | '(=' a=expression b=expression ')' {
        Predicate fallback = Tautology.create();
        String pattern1 = "field_ssa_[0-9]+_[a-zA-Z_$][a-zA-Z0-9_$]*";
        String pattern2 = "field_ssa_[0-9]+_";
        boolean fail = false;

        if ($a.val instanceof Root) {
            Root r = (Root) $a.val;

            if (r.getName().matches(pattern1)) {
                if ($b.val instanceof ObjectFieldWrite) {
                    ObjectFieldWrite fw = (ObjectFieldWrite) $b.val;

                    if (r.getName().replaceAll(pattern2, "").equals(fw.getField().getName())) {
                        fallback = Equals.create(DefaultObjectFieldRead.create(fw.getObject(), fw.getField()), fw.getNewValue());
                    } else if (PandaConfig.getInstance().enabledVerbose(this.getClass())) {
                        System.out.println("[WARNING] Omitting equality interpolant " + Equals.create($a.val, $b.val) + " (refers to " + r + ")");
                    }
                }

                fail = true;
            } else if ($b.val instanceof ArrayElementWrite) {
                ArrayElementWrite aw = (ArrayElementWrite) $b.val;

                if (aw.getArray().equals($a.val)) {
                    fallback = Equals.create(DefaultArrayElementRead.create(aw.getArray(), aw.getIndex()), aw.getNewValue());
                    fail = true;
                }
            }
        }
        if ($b.val instanceof Root) {
            Root r = (Root) $b.val;

            if (r.getName().matches(pattern1)) {
                if ($a.val instanceof ObjectFieldWrite) {
                    ObjectFieldWrite fw = (ObjectFieldWrite) $a.val;

                    if (r.getName().replaceAll(pattern2, "").equals(fw.getField().getName())) {
                        fallback = Equals.create(DefaultObjectFieldRead.create(fw.getObject(), fw.getField()), fw.getNewValue());
                    } else if (PandaConfig.getInstance().enabledVerbose(this.getClass())) {
                        System.out.println("[WARNING] Omitting equality interpolant " + Equals.create($a.val, $b.val) + " (refers to " + r + ")");
                    }
                }

                fail = true;
            } else if ($a.val instanceof ArrayElementWrite) {
                ArrayElementWrite aw = (ArrayElementWrite) $a.val;

                if (aw.getArray().equals($b.val)) {
                    fallback = Equals.create(DefaultArrayElementRead.create(aw.getArray(), aw.getIndex()), aw.getNewValue());
                    fail = true;
                }
            }
        }

        if (fail) {
            $ctx.val = fallback;

            if (PandaConfig.getInstance().enabledVerbose(this.getClass())) {
                System.out.println("[WARNING] " + $a.val + " = " + $b.val + " re-interpreted as " + fallback);
            }
        } else {
            $ctx.val = Equals.create($a.val, $b.val);
        }
    }
    | '(=' a=expression NULL_TOKEN ')' {
        $ctx.val = Equals.create($a.val, NullExpression.create());
    }
    | '(=' NULL_TOKEN b=expression ')' {
        $ctx.val = Equals.create(NullExpression.create(), $b.val);
    }
    | NULL_TOKEN '=' NULL_TOKEN {
        $ctx.val = Equals.create(NullExpression.create(), NullExpression.create());
    }
    | '(<' a=expression b=expression ')' {
        $ctx.val = LessThan.create($a.val, $b.val);
    }
    | '(>' a=expression b=expression ')' {
        $ctx.val = LessThan.create($b.val, $a.val);
    }
    | '(<=' a=expression b=expression ')' {
        $ctx.val = Negation.create(LessThan.create($b.val, $a.val));
    }
    | '(>=' a=expression b=expression ')' {
        $ctx.val = Negation.create(LessThan.create($a.val, $b.val));
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

        if (ae instanceof ArrayElementWrite) {
            // Losing precision (there is no way to express (select (store arr b (...)) b) - query for a possibly modified array)
            // .. we can only query elements with aread: (select (select (store arr b (...)) b) index)
            ArrayElementWrite aew = (ArrayElementWrite) ae;

            $ctx.val = DefaultArrayElementRead.create(aew.getArray(), $e.val);
        } else {
            $ctx.val = DefaultArrayElementRead.create(ae, $e.val);
        }

        if (PandaConfig.getInstance().enabledVerbose(this.getClass())) {
            System.out.println("[WARNING] LET ." + $id.text + " = " + ae);
            System.out.println("[WARNING] \t\t(select ." + $id.text + " " + $e.text + ") re-interpreted as " + $ctx.val);
        }
    }
    | '(' SELECT_TOKEN id=ID_TOKEN '!' n=CONSTANT_TOKEN e=expression ')' {
        AccessExpression ae = (AccessExpression) PredicateContext.let.get($id.text + '!' + Integer.parseInt($n.text));

        if (ae instanceof ArrayElementWrite) {
            // Losing precision (there is no way to express (select (store arr b (...)) b) - query for a possibly modified array)
            // .. we can only query elements with aread: (select (select (store arr b (...)) b) index)
            ArrayElementWrite aew = (ArrayElementWrite) ae;

            $ctx.val = DefaultArrayElementRead.create(aew.getArray(), $e.val);
        } else {
            $ctx.val = DefaultArrayElementRead.create(ae, $e.val);
        }

        if (PandaConfig.getInstance().enabledVerbose(this.getClass())) {
            System.out.println("[WARNING] LET " + $id.text + "!" + $n.text + " = " + ae);
            System.out.println("[WARNING] \t\t(select " + $id.text + "!" + $n.text + " " + $e.text + ") re-interpreted as " + $ctx.val);
        }
    }
    | '(' SELECT_TOKEN a=ARR_TOKEN p=path ')' {
        $ctx.val = $p.val;

        //System.out.println("[WARNING] (select arr " + $p.text + ") re-interpreted as " + $ctx.val);
    }
    | '(' SELECT_TOKEN f=ID_TOKEN p=path ')' {
        $ctx.val = DefaultObjectFieldRead.create($p.val, $f.text.replaceAll("field_ssa_[0-9]+_", ""));
    }
    | '(' SELECT_TOKEN p=path e=expression ')' {
        if ($p.val instanceof DefaultArrayElementWrite) {
            // Losing precision (there is no way to express (select (store arr b (...)) b) - query for a possibly modified array)
            // .. we can only query elements with aread: (select (select (store arr b (...)) b) index)
            ArrayElementWrite aew = (ArrayElementWrite) $p.val;

            $ctx.val = DefaultArrayElementRead.create(aew.getArray(), $e.val);
        } else {
            $ctx.val = DefaultArrayElementRead.create($p.val, $e.val);
        }

        if (PandaConfig.getInstance().enabledVerbose(this.getClass())) {
            System.out.println("[WARNING] (select " + $p.text + " " + $e.text + ") re-interpreted as " + $ctx.val);
        }
    }
    | '(' STORE_TOKEN '(' SELECT_TOKEN a=ARR_TOKEN p=path ')' e1=expression e2=expression ')' {
        // In interpretation of this term we add additional (store arr ... ...) around the expression
        $ctx.val = DefaultArrayElementWrite.create($p.val, $e1.val, $e2.val);

        if (PandaConfig.getInstance().enabledVerbose(this.getClass())) {
            System.out.println("[WARNING] (store (select arr " + $p.text + ") " + $e1.text + " " + $e2.text + ") re-interpreted as " + $ctx.val);
        }
    }
    | '(' STORE_TOKEN a=ARR_TOKEN p1=path p2=path ')' {
        $ctx.val = $p2.val;

        if (PandaConfig.getInstance().enabledVerbose(this.getClass())) {
            System.out.println("[WARNING] (store arr " + $p1.text + " " + $p2.text + ") re-interpreted as the value being stored: " + $ctx.val);
        }
    }
    | '(' STORE_TOKEN a=ARR_TOKEN p1=path '.' id=ID_TOKEN ')' {
        $ctx.val = (DefaultAccessExpression) PredicateContext.let.get($id.text);

        if (PandaConfig.getInstance().enabledVerbose(this.getClass())) {
            System.out.println("[WARNING] LET ." + $id.text + " = " + $ctx.val);
            System.out.println("[WARNING] \t\t(store arr " + $p1.val + " ." + $id.text + ") re-interpreted as " + $ctx.val);
        }
    }
    | '(' STORE_TOKEN a=ARR_TOKEN p1=path id=ID_TOKEN '!' n=CONSTANT_TOKEN ')' {
        $ctx.val = (DefaultAccessExpression) PredicateContext.let.get($id.text + '!' + Integer.parseInt($n.text));

        if (PandaConfig.getInstance().enabledVerbose(this.getClass())) {
            System.out.println("[WARNING] LET " + $id.text + "!" + $n.text + " = " + $ctx.val);
            System.out.println("[WARNING] \t\t(store arr " + $p1.val + " " + $id.text + "!" + $n.text + ") re-interpreted as " + $ctx.val);
        }
    }
    | '(' STORE_TOKEN f=ID_TOKEN p=path e=expression ')' {
        $ctx.val = DefaultObjectFieldWrite.create($p.val, $f.text.replaceAll("field_ssa_[0-9]+_", ""), $e.val);

        if (PandaConfig.getInstance().enabledVerbose(this.getClass())) {
            System.out.println("[WARNING] (store " + $f.text + " " + $p.text + " " + $e.text + ") re-interpreted as " + $ctx.val);
        }
    }
    ;

AND_TOKEN      : 'and';
ARR_TOKEN      : 'ssa_'[0-9]+'_arr';
ARRLEN_TOKEN   : 'arrlen';
CLASS_TOKEN    : 'class_'[a-zA-Z0-9_$]+;
DISTINCT_TOKEN : 'distinct';
FALSE_TOKEN    : 'false';
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
    : ([ \t\n\r])+ { skip(); }
    ;
