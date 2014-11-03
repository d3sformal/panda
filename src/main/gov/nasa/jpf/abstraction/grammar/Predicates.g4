grammar Predicates;

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

predicates returns [Predicates val]
    : cs=predicatecontextlist {
        $ctx.val = new Predicates($cs.val);
    }
    ;

expressions returns [Expressions val]
    : cs=expressioncontextlist {
        $ctx.val = new Expressions($cs.val);
    }
    ;

standalonepath returns [DefaultAccessExpression[] val]
    : p=path {
        $ctx.val = $p.val;
    }
    ;

predicatecontextlist returns [List<gov.nasa.jpf.abstraction.common.PredicateContext> val]
    : /* EMPTY */ {
        $ctx.val = new ArrayList<gov.nasa.jpf.abstraction.common.PredicateContext>();
    }
    | cs=predicatecontextlist c=predicatecontext {
        $ctx.val = $cs.val;
        $ctx.val.add($c.val);
    }
    ;

expressioncontextlist returns [List<gov.nasa.jpf.abstraction.common.ExpressionContext> val]
    : /* EMPTY */ {
        $ctx.val = new ArrayList<gov.nasa.jpf.abstraction.common.ExpressionContext>();
    }
    | cs=expressioncontextlist c=expressioncontext {
        $ctx.val = $cs.val;
        $ctx.val.add($c.val);
    }
    ;

expressioncontext returns [gov.nasa.jpf.abstraction.common.ExpressionContext val] locals [List<String> name = new LinkedList<String>()]
    : '[' STATIC_TOKEN ']' ps=expressionlist {
        $ctx.val = new StaticExpressionContext($ps.val);
    }
    | '[' OBJECT_TOKEN ( pkg=ID_TOKEN {$ctx.name.add($pkg.text);} '.' ) * c=ID_TOKEN {$ctx.name.add($c.text);} ']' ps=expressionlist {
        DefaultPackageAndClass packageAndClass = DefaultPackageAndClass.create($ctx.name);

        $ctx.val = new ObjectExpressionContext(packageAndClass, $ps.val);
    }
    | '[' METHOD_TOKEN ( pkg=ID_TOKEN {$ctx.name.add($pkg.text);} '.' ) * c=ID_TOKEN {$ctx.name.add($c.text);} '.' m=ID_TOKEN ']' ps=expressionlist {
        DefaultPackageAndClass packageAndClass = DefaultPackageAndClass.create($ctx.name);
        DefaultMethod method = DefaultMethod.create(packageAndClass, $m.text);

        $ctx.val = new MethodExpressionContext(method, $ps.val);
    }
    | '[' METHOD_TOKEN ( pkg=ID_TOKEN {$ctx.name.add($pkg.text);} '.' ) * c=ID_TOKEN {$ctx.name.add($c.text);} '.' m=INIT_TOKEN ']' ps=expressionlist {
        DefaultPackageAndClass packageAndClass = DefaultPackageAndClass.create($ctx.name);
        DefaultMethod method = DefaultMethod.create(packageAndClass, $m.text);

        $ctx.val = new MethodExpressionContext(method, $ps.val);
    }
    ;

predicatecontext returns [gov.nasa.jpf.abstraction.common.PredicateContext val] locals [List<String> name = new LinkedList<String>()]
    : '[' STATIC_TOKEN ']' ps=predicatelist {
        $ctx.val = new StaticPredicateContext($ps.val);
    }
    | '[' OBJECT_TOKEN ( pkg=ID_TOKEN {$ctx.name.add($pkg.text);} '.' ) * c=ID_TOKEN {$ctx.name.add($c.text);} ']' ps=predicatelist {
        DefaultPackageAndClass packageAndClass = DefaultPackageAndClass.create($ctx.name);

        $ctx.val = new ObjectPredicateContext(packageAndClass, $ps.val);
    }
    | '[' ASSUME_TOKEN PRE_TOKEN METHOD_TOKEN ( pkg=ID_TOKEN {$ctx.name.add($pkg.text);} '.' ) * c=ID_TOKEN {$ctx.name.add($c.text);} '.' m=ID_TOKEN ']' ps=predicatelist {
        DefaultPackageAndClass packageAndClass = DefaultPackageAndClass.create($ctx.name);
        DefaultMethod method = DefaultMethod.create(packageAndClass, $m.text);

        $ctx.val = new MethodAssumePrePredicateContext(method, $ps.val);
    }
    | '[' ASSUME_TOKEN PRE_TOKEN METHOD_TOKEN ( pkg=ID_TOKEN {$ctx.name.add($pkg.text);} '.' ) * c=ID_TOKEN {$ctx.name.add($c.text);} '.' m=INIT_TOKEN ']' ps=predicatelist {
        DefaultPackageAndClass packageAndClass = DefaultPackageAndClass.create($ctx.name);
        DefaultMethod method = DefaultMethod.create(packageAndClass, $m.text);

        $ctx.val = new MethodAssumePrePredicateContext(method, $ps.val);
    }
    | '[' ASSUME_TOKEN POST_TOKEN METHOD_TOKEN ( pkg=ID_TOKEN {$ctx.name.add($pkg.text);} '.' ) * c=ID_TOKEN {$ctx.name.add($c.text);} '.' m=ID_TOKEN ']' ps=predicatelist {
        DefaultPackageAndClass packageAndClass = DefaultPackageAndClass.create($ctx.name);
        DefaultMethod method = DefaultMethod.create(packageAndClass, $m.text);

        $ctx.val = new MethodAssumePostPredicateContext(method, $ps.val);
    }
    | '[' ASSUME_TOKEN POST_TOKEN METHOD_TOKEN ( pkg=ID_TOKEN {$ctx.name.add($pkg.text);} '.' ) * c=ID_TOKEN {$ctx.name.add($c.text);} '.' m=INIT_TOKEN ']' ps=predicatelist {
        DefaultPackageAndClass packageAndClass = DefaultPackageAndClass.create($ctx.name);
        DefaultMethod method = DefaultMethod.create(packageAndClass, $m.text);

        $ctx.val = new MethodAssumePostPredicateContext(method, $ps.val);
    }
    | '[' METHOD_TOKEN ( pkg=ID_TOKEN {$ctx.name.add($pkg.text);} '.' ) * c=ID_TOKEN {$ctx.name.add($c.text);} '.' m=ID_TOKEN ']' ps=predicatelist {
        DefaultPackageAndClass packageAndClass = DefaultPackageAndClass.create($ctx.name);
        DefaultMethod method = DefaultMethod.create(packageAndClass, $m.text);

        $ctx.val = new MethodPredicateContext(method, $ps.val);
    }
    | '[' METHOD_TOKEN ( pkg=ID_TOKEN {$ctx.name.add($pkg.text);} '.' ) * c=ID_TOKEN {$ctx.name.add($c.text);} '.' m=INIT_TOKEN ']' ps=predicatelist {
        DefaultPackageAndClass packageAndClass = DefaultPackageAndClass.create($ctx.name);
        DefaultMethod method = DefaultMethod.create(packageAndClass, $m.text);

        $ctx.val = new MethodPredicateContext(method, $ps.val);
    }
    ;

predicatelist returns [List<Predicate> val] locals [BytecodeRange scope = BytecodeUnlimitedRange.getInstance()]
    : /* EMPTY */ {
        $ctx.val = new ArrayList<Predicate>();
    }
    | ps=predicatelist (s=predicatescope ':' {$ctx.scope = $s.val;})? p=predicate {
        $ctx.val = $ps.val;

        for (Predicate p : $p.val) {
            p.setScope($ctx.scope);
            $ctx.val.add(p);
        }
    }
    ;

predicatescope returns [BytecodeRange val]
    : r=predicaterange {
        $ctx.val = $r.val;
    }
    | ps=predicatescope ',' pr=predicaterange {
        $ctx.val = $ps.val.merge($pr.val);
    }
    ;

predicaterange returns [BytecodeRange val]
    : c=CONSTANT_TOKEN {
        int c = Integer.parseInt($c.text);
        $ctx.val = new BytecodeInterval(c, c);
    }
    | c1=CONSTANT_TOKEN '..' c2=CONSTANT_TOKEN {
        int c1 = Integer.parseInt($c1.text);
        int c2 = Integer.parseInt($c2.text);

        $ctx.val = new BytecodeInterval(c1, c2);
    }
    ;

expressionlist returns [List<Expression> val]
    : /* EMPTY */ {
        $ctx.val = new ArrayList<Expression>();
    }
    | ps=expressionlist p=expression {
        $ctx.val = $ps.val;

        for (Expression p : $p.val) {
            $ctx.val.add(p);
        }
    }
    ;

predicate returns [Predicate[] val]
    : TRUE_TOKEN {
        $ctx.val = new Predicate[] {Tautology.create()};
    }
    | FALSE_TOKEN {
        $ctx.val = new Predicate[] {Contradiction.create()};
    }
    | NOT_TOKEN '(' p=predicate ')' {
        $ctx.val = new Predicate[$p.val.length];

        for (int i = 0; i < $p.val.length; ++i) {
            $ctx.val[i] = Negation.create($p.val[i]);
        }
    }
    | a=expression '=' b=expression {
        $ctx.val = new Predicate[$a.val.length * $b.val.length];

        for (int i = 0; i < $a.val.length; ++i) {
            for (int j = 0; j < $b.val.length; ++j) {
                $ctx.val[i * $b.val.length + j] = Equals.create($a.val[i], $b.val[j]);
            }
        }
    }
    | a=expression '=' NULL_TOKEN {
        $ctx.val = new Predicate[$a.val.length];

        for (int i = 0; i < $a.val.length; ++i) {
            $ctx.val[i] = Equals.create($a.val[i], NullExpression.create());
        }
    }
    | NULL_TOKEN '=' b=expression {
        $ctx.val = new Predicate[$b.val.length];

        for (int i = 0; i < $b.val.length; ++i) {
            $ctx.val[i] = Equals.create(NullExpression.create(), $b.val[i]);
        }
    }
    | NULL_TOKEN '=' NULL_TOKEN {
        $ctx.val = new Predicate[] {Tautology.create()};
    }
    | a=expression '<' b=expression {
        $ctx.val = new Predicate[$a.val.length * $b.val.length];

        for (int i = 0; i < $a.val.length; ++i) {
            for (int j = 0; j < $b.val.length; ++j) {
                $ctx.val[i * $b.val.length + j] = LessThan.create($a.val[i], $b.val[j]);
            }
        }
    }
    | a=expression '>' b=expression {
        $ctx.val = new Predicate[$a.val.length * $b.val.length];

        for (int i = 0; i < $a.val.length; ++i) {
            for (int j = 0; j < $b.val.length; ++j) {
                $ctx.val[i * $b.val.length + j] = LessThan.create($b.val[j], $a.val[i]);
            }
        }
    }
    | a=expression '<=' b=expression {
        $ctx.val = new Predicate[$a.val.length * $b.val.length];

        for (int i = 0; i < $a.val.length; ++i) {
            for (int j = 0; j < $b.val.length; ++j) {
                $ctx.val[i * $b.val.length + j] = Negation.create(LessThan.create($b.val[j], $a.val[i]));
            }
        }
    }
    | a=expression '>=' b=expression {
        $ctx.val = new Predicate[$a.val.length * $b.val.length];

        for (int i = 0; i < $a.val.length; ++i) {
            for (int j = 0; j < $b.val.length; ++j) {
                $ctx.val[i * $b.val.length + j] = Negation.create(LessThan.create($a.val[i], $b.val[j]));
            }
        }
    }
    | a=expression '!=' b=expression {
        $ctx.val = new Predicate[$a.val.length * $b.val.length];

        for (int i = 0; i < $a.val.length; ++i) {
            for (int j = 0; j < $b.val.length; ++j) {
                $ctx.val[i * $b.val.length + j] = Negation.create(Equals.create($a.val[i], $b.val[j]));
            }
        }
    }
    | a=expression '!=' NULL_TOKEN {
        $ctx.val = new Predicate[$a.val.length];

        for (int i = 0; i < $a.val.length; ++i) {
            $ctx.val[i] = Negation.create(Equals.create($a.val[i], NullExpression.create()));
        }
    }
    | NULL_TOKEN '!=' b=expression {
        $ctx.val = new Predicate[$b.val.length];

        for (int i = 0; i < $b.val.length; ++i) {
            $ctx.val[i] = Negation.create(Equals.create(NullExpression.create(), $b.val[i]));
        }
    }
    | NULL_TOKEN '!=' NULL_TOKEN {
        $ctx.val = new Predicate[] {Contradiction.create()};
    }
    ;

standaloneexpression returns [Expression[] val]
    : e=expression {
        $ctx.val = $e.val;
    }
    ;

expression returns [Expression[] val]
    : t=term {
        $ctx.val = $t.val;
    }
    | a=term '+' b=term {
        $ctx.val = new Expression[$a.val.length * $b.val.length];

        for (int i = 0; i < $a.val.length; ++i) {
            for (int j = 0; j < $b.val.length; ++j) {
                $ctx.val[i * $b.val.length + j] = Add.create($a.val[i], $b.val[j]);
            }
        }
    }
    | a=term '-' b=term {
        $ctx.val = new Expression[$a.val.length * $b.val.length];

        for (int i = 0; i < $a.val.length; ++i) {
            for (int j = 0; j < $b.val.length; ++j) {
                $ctx.val[i * $b.val.length + j] = Subtract.create($a.val[i], $b.val[j]);
            }
        }
    }
    | e1=expression ',' e2=expression {
        $ctx.val = new Expression[$e1.val.length + $e2.val.length];

        for (int i = 0; i < $e1.val.length; ++i) {
            $ctx.val[i] = $e1.val[i];
        }

        for (int i = 0; i < $e2.val.length; ++i) {
            $ctx.val[$e1.val.length + i] = $e2.val[i];
        }
    }
    ;

term returns [Expression[] val]
    : f=factor {
        $ctx.val = $f.val;
    }
    | a=factor '*' b=factor {
        $ctx.val = new Expression[$a.val.length * $b.val.length];

        for (int i = 0; i < $a.val.length; ++i) {
            for (int j = 0; j < $b.val.length; ++j) {
                $ctx.val[i * $b.val.length + j] = Multiply.create($a.val[i], $b.val[j]);
            }
        }
    }
    | a=factor '/' b=factor {
        $ctx.val = new Expression[$a.val.length * $b.val.length];

        for (int i = 0; i < $a.val.length; ++i) {
            for (int j = 0; j < $b.val.length; ++j) {
                $ctx.val[i * $b.val.length + j] = Divide.create($a.val[i], $b.val[j]);
            }
        }
    }
    ;

factor returns [Expression[] val]
    : CONSTANT_TOKEN {
        $ctx.val = new Expression[] {Constant.create(Integer.parseInt($CONSTANT_TOKEN.text))};
    }
    | c=CONSTANT_TOKEN '..' d=CONSTANT_TOKEN {
        int a = Integer.parseInt($c.text);
        int b = Integer.parseInt($d.text);

        int i = 0;
        int step = a < b ? +1 : -1;

        $ctx.val = new Expression[Math.abs(a - b - step)];

        while (a != b + step) {
            $ctx.val[i] = Constant.create(a);
            a += step;
            ++i;
        }
    }
    | ALENGTH_TOKEN '(' ARRLEN_TOKEN ',' p=path ')' {
        $ctx.val = new Expression[$p.val.length];

        for (int i = 0; i < $p.val.length; ++i) {
            $ctx.val[i] = DefaultArrayLengthRead.create($p.val[i]);
        }
    }
    | p=path {
        $ctx.val = new Expression[$p.val.length];

        for (int i = 0; i < $p.val.length; ++i) {
            $ctx.val[i] = $p.val[i];
        }
    }
    | '(' e=expression ')' {
        $ctx.val = $e.val;
    }
    ;

path returns [DefaultAccessExpression[] val] locals [List<String> name = new LinkedList<String>()]
    : f=RETURN_TOKEN {
        $ctx.val = new DefaultAccessExpression[] {DefaultReturnValue.create()};
    }
    | f=ID_TOKEN {
        $ctx.val = new DefaultAccessExpression[] {DefaultRoot.create($f.text)};
    }
    | f=SPECIAL_TOKEN {
        $ctx.val = new DefaultAccessExpression[] {SpecialVariable.create($f.text)};
    }
    | CLASS_TOKEN '(' ( pkg=ID_TOKEN {$ctx.name.add($pkg.text);} '.' ) * c=ID_TOKEN {$ctx.name.add($c.text);} ')' '.' f=ID_TOKEN {
        $ctx.val = new DefaultAccessExpression[] {DefaultObjectFieldRead.create(DefaultPackageAndClass.create($ctx.name), $f.text)};
    }
    | SFREAD_TOKEN '(' f=ID_TOKEN ',' ( pkg=ID_TOKEN {$ctx.name.add($pkg.text);} '.' ) * c=ID_TOKEN {$ctx.name.add($c.text);} ')' {
        $ctx.val = new DefaultAccessExpression[] {DefaultObjectFieldRead.create(DefaultPackageAndClass.create($ctx.name), $f.text)};
    }
    | p=path '.' f=ID_TOKEN {
        $ctx.val = new DefaultAccessExpression[$p.val.length];

        for (int i = 0; i < $p.val.length; ++i) {
            $ctx.val[i] = DefaultObjectFieldRead.create($p.val[i], $f.text);
        }
    }
    | p=path '[' e=expression ']' {
        $ctx.val = new DefaultAccessExpression[$p.val.length * $e.val.length];

        for (int i = 0; i < $p.val.length; ++i) {
            for (int j = 0; j < $e.val.length; ++j) {
                $ctx.val[i * $e.val.length + j] = DefaultArrayElementRead.create($p.val[i], $e.val[j]);
            }
        }
    }
    | FREAD_TOKEN '(' f=ID_TOKEN ',' p=path ')' {
        $ctx.val = new DefaultAccessExpression[$p.val.length];

        for (int i = 0; i < $p.val.length; ++i) {
            $ctx.val[i] = DefaultObjectFieldRead.create($p.val[i], $f.text);
        }
    }
    | AREAD_TOKEN '(' ARR_TOKEN ',' p=path ',' e=expression ')' {
        $ctx.val = new DefaultAccessExpression[$p.val.length * $e.val.length];

        for (int i = 0; i < $p.val.length; ++i) {
            for (int j = 0; j < $e.val.length; ++j) {
                $ctx.val[i * $e.val.length + j] = DefaultArrayElementRead.create($p.val[i], $e.val[j]);
            }
        }
    }
    ;

ALENGTH_TOKEN : 'alength';
AREAD_TOKEN   : 'aread';
ARR_TOKEN     : 'arr';
ARRLEN_TOKEN  : 'arrlen';
ASSUME_TOKEN  : 'assume';
CLASS_TOKEN   : 'class';
FALSE_TOKEN   : 'false';
FREAD_TOKEN   : 'fread';
INIT_TOKEN    : '<init>';
METHOD_TOKEN  : 'method';
NOT_TOKEN     : 'not';
NULL_TOKEN    : 'null';
OBJECT_TOKEN  : 'object';
PRE_TOKEN     : 'pre';
POST_TOKEN    : 'post';
RETURN_TOKEN  : 'return';
SFREAD_TOKEN  : 'sfread';
SPECIAL_TOKEN : '#'[A-Z0-9]+;
STATIC_TOKEN  : 'static';
TRUE_TOKEN    : 'true';

CONSTANT_TOKEN
    : [-+]?'0'('.'[0-9]+)?
    | [-+]?[1-9][0-9]*('.'[0-9]+)?
    ;

ID_TOKEN
    : [a-zA-Z$_][a-zA-Z0-9$_]*
    ;

COMMENT_TOKEN
    : '//' ~('\n')* { skip(); }
    ;

WS_TOKEN
    : ([ \t\n\r])+ { skip(); }
    ;
