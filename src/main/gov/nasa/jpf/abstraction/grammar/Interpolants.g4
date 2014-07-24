grammar Interpolants;

@header {
    import java.util.List;
    import java.util.LinkedList;
    import java.util.SortedSet;
    import java.util.TreeSet;

    import gov.nasa.jpf.abstraction.common.*;
    import gov.nasa.jpf.abstraction.common.impl.*;
    import gov.nasa.jpf.abstraction.common.access.*;
    import gov.nasa.jpf.abstraction.common.access.impl.*;
}

predicate returns [Predicate val]
    : TRUE_TOKEN {
        $ctx.val = Tautology.create();
    }
    | FALSE_TOKEN {
        $ctx.val = Contradiction.create();
    }
    | '(not' p=predicate ')' {
        $ctx.val = Negation.create($p.val);
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
    : CONSTANT_TOKEN {
        $ctx.val = Constant.create(Integer.parseInt($CONSTANT_TOKEN.text));
    }
    | '(-' CONSTANT_TOKEN ')' {
        $ctx.val = Constant.create(-Integer.parseInt($CONSTANT_TOKEN.text));
    }
    | '(alength' 'arrlen' ',' p=path ')' {
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
    : f=RETURN_TOKEN {
        $ctx.val = DefaultReturnValue.create();
    }
    | f=ID_TOKEN {
        $ctx.val = DefaultRoot.create($f.text.replaceAll("var_ssa_[0-9]+_", ""));
    }
    | '(' SFREAD_TOKEN f=ID_TOKEN ',' c=ID_TOKEN ')' {
        $ctx.val = DefaultObjectFieldRead.create(DefaultPackageAndClass.create($c.text), $f.text.replaceAll("field_ssa_[0-9]+_", ""));
    }
    | '(' FREAD_TOKEN f=ID_TOKEN ',' p=path ')' {
        $ctx.val = DefaultObjectFieldRead.create($p.val, $f.text.replaceAll("field_ssa_[0-9]+_", ""));
    }
    | '(' AREAD_TOKEN ARR_TOKEN ',' p=path ',' e=expression ')' {
        $ctx.val = DefaultArrayElementRead.create($p.val, $e.val);
    }
    ;

ALENGTH_TOKEN : 'alength';
AREAD_TOKEN   : 'aread';
ARR_TOKEN     : 'ssa_'[0-9]+'_arr';
ARRLEN_TOKEN  : 'arrlen';
FALSE_TOKEN   : 'false';
FREAD_TOKEN   : 'fread';
INIT_TOKEN    : '<init>';
NOT_TOKEN     : 'not';
NULL_TOKEN    : 'null';
RETURN_TOKEN  : 'var_ssa_'[0-9]+'_return_pc[0-9]+';
SFREAD_TOKEN  : 'sfread';
TRUE_TOKEN    : 'true';

VAR_SSA_TOKEN : 'var_ssa_'[0-9]+'_';

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
