grammar Interpolants;

@header {
    import java.util.HashMap;
    import java.util.List;
    import java.util.LinkedList;
    import java.util.Map;
    import java.util.SortedSet;
    import java.util.TreeSet;

    import gov.nasa.jpf.abstraction.common.*;
    import gov.nasa.jpf.abstraction.common.impl.*;
    import gov.nasa.jpf.abstraction.common.access.*;
    import gov.nasa.jpf.abstraction.common.access.impl.*;
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

predicate returns [Predicate val] locals [static Map<String, Object> let = new HashMap<String, Object>()]
    : '.' id=ID_TOKEN {
        $ctx.val = (Predicate) PredicateContext.let.get($id.text);
    }
    | '(' LET_TOKEN '(''(' '.' v=ID_TOKEN e=expression ')' {PredicateContext.let.put($v.text, $e.val);} ')' p=predicate ')' {
        $ctx.val = $p.val;
    }
    | '(' LET_TOKEN '(''(' '.' v=ID_TOKEN q=predicate ')' {PredicateContext.let.put($v.text, $q.val);} ')' p=predicate ')' {
        $ctx.val = $p.val;
    }
    | TRUE_TOKEN {
        $ctx.val = Tautology.create();
    }
    | FALSE_TOKEN {
        $ctx.val = Contradiction.create();
    }
    | '(' AND_TOKEN p=predicate q=predicate ')' {
        $ctx.val = Conjunction.create($p.val, $q.val);
    }
    | '(' OR_TOKEN p=predicate q=predicate ')' {
        $ctx.val = Disjunction.create($p.val, $q.val);
    }
    | '(' NOT_TOKEN p=predicate ')' {
        $ctx.val = Negation.create($p.val);
    }
    | '(=>' p=predicate q=predicate ')' {
        $ctx.val = Disjunction.create(Negation.create($p.val), $q.val);
    }
    | '(' DISTINCT_TOKEN a=expression b=expression ')' {
        $ctx.val = Negation.create(Equals.create($a.val, $b.val));
    }
    | '(=' a=expression b=expression ')' {
        $ctx.val = Equals.create($a.val, $b.val);
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
    ;

expression returns [Expression val]
    : t=term {
        $ctx.val = $t.val;
    }
    | '(+' a=term b=term ')' {
        $ctx.val = Add.create($a.val, $b.val);
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
    | CONSTANT_TOKEN {
        $ctx.val = Constant.create(Integer.parseInt($CONSTANT_TOKEN.text));
    }
    | '(-' CONSTANT_TOKEN ')' {
        $ctx.val = Constant.create(-Integer.parseInt($CONSTANT_TOKEN.text));
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
    ;

path returns [DefaultAccessExpression val]
    : FRESH_TOKEN {
        $ctx.val = DefaultFresh.create();
    }
    | f=RETURN_TOKEN {
        $ctx.val = DefaultReturnValue.create();
    }
    | f=ID_TOKEN {
        $ctx.val = DefaultRoot.create($f.text.replaceAll("var_ssa_[0-9]+_depth_[0-9]+_", ""));
    }
    | '(' SELECT_TOKEN a=ARR_TOKEN p=path ')' {
        $ctx.val = $p.val;
    }
    | '(' SELECT_TOKEN f=ID_TOKEN p=path ')' {
        $ctx.val = DefaultObjectFieldRead.create($p.val, $f.text.replaceAll("field_ssa_[0-9]+_", ""));
    }
    | '(' SELECT_TOKEN p=path e=expression ')' {
        $ctx.val = DefaultArrayElementRead.create($p.val, $e.val);
    }
    ;

AND_TOKEN      : 'and';
ARR_TOKEN      : 'ssa_'[0-9]+'_arr';
ARRLEN_TOKEN   : 'arrlen';
DISTINCT_TOKEN : 'distinct';
FALSE_TOKEN    : 'false';
FRESH_TOKEN    : 'fresh_'[0-9]+;
INIT_TOKEN     : '<init>';
LET_TOKEN      : 'let';
NOT_TOKEN      : 'not';
NULL_TOKEN     : 'null';
OR_TOKEN       : 'or';
RETURN_TOKEN   : 'var_ssa_'[0-9]+'_depth_'[0-9]+'_return_pc[0-9]+';
SELECT_TOKEN   : 'select';
TRUE_TOKEN     : 'true';

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
