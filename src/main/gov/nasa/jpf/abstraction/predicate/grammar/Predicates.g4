grammar Predicates;

@header {
	import java.util.List;
	import java.util.LinkedList;

	import gov.nasa.jpf.abstraction.common.*;
	import gov.nasa.jpf.abstraction.common.impl.*;
	import gov.nasa.jpf.abstraction.common.access.*;
	import gov.nasa.jpf.abstraction.common.access.impl.*;
}

predicates returns [Predicates val]
	: cs=contextlist {
		$ctx.val = new Predicates($cs.val);
	}
	;

standalonepath returns [DefaultAccessExpression val]
	: p=path {
		$ctx.val = $p.val;
	}
	;

contextlist returns [List<Context> val]
	: /* EMPTY */ {
		$ctx.val = new ArrayList<Context>();
	}
	| cs=contextlist c=context {
		$ctx.val = $cs.val;
		$ctx.val.add($c.val);
	}
	;

context returns [Context val] locals [List<String> name = new LinkedList<String>()]
	: '[' STATIC_TOKEN ']' ps=predicatelist {
		$ctx.val = new StaticContext($ps.val);
	}
	| '[' OBJECT_TOKEN ( pkg=ID_TOKEN {$ctx.name.add($pkg.text);} '.' ) * c=ID_TOKEN {$ctx.name.add($c.text);} ']' ps=predicatelist {
		DefaultPackageAndClass packageAndClass = DefaultPackageAndClass.create($ctx.name);

		$ctx.val = new ObjectContext(packageAndClass, $ps.val);
	}
    | '[' METHOD_TOKEN ASSUME_TOKEN PRE_TOKEN ( pkg=ID_TOKEN {$ctx.name.add($pkg.text);} '.' ) * c=ID_TOKEN {$ctx.name.add($c.text);} '.' m=ID_TOKEN ']' ps=predicatelist {
		DefaultPackageAndClass packageAndClass = DefaultPackageAndClass.create($ctx.name);
		DefaultMethod method = DefaultMethod.create(packageAndClass, $m.text);

		$ctx.val = new MethodAssumePreContext(method, $ps.val);
    }
    | '[' METHOD_TOKEN ASSUME_TOKEN PRE_TOKEN ( pkg=ID_TOKEN {$ctx.name.add($pkg.text);} '.' ) * c=ID_TOKEN {$ctx.name.add($c.text);} '.' m=INIT_TOKEN ']' ps=predicatelist {
		DefaultPackageAndClass packageAndClass = DefaultPackageAndClass.create($ctx.name);
		DefaultMethod method = DefaultMethod.create(packageAndClass, $m.text);

		$ctx.val = new MethodAssumePreContext(method, $ps.val);
    }
    | '[' METHOD_TOKEN ASSUME_TOKEN POST_TOKEN ( pkg=ID_TOKEN {$ctx.name.add($pkg.text);} '.' ) * c=ID_TOKEN {$ctx.name.add($c.text);} '.' m=ID_TOKEN ']' ps=predicatelist {
		DefaultPackageAndClass packageAndClass = DefaultPackageAndClass.create($ctx.name);
		DefaultMethod method = DefaultMethod.create(packageAndClass, $m.text);

		$ctx.val = new MethodAssumePostContext(method, $ps.val);
    }
    | '[' METHOD_TOKEN ASSUME_TOKEN POST_TOKEN ( pkg=ID_TOKEN {$ctx.name.add($pkg.text);} '.' ) * c=ID_TOKEN {$ctx.name.add($c.text);} '.' m=INIT_TOKEN ']' ps=predicatelist {
		DefaultPackageAndClass packageAndClass = DefaultPackageAndClass.create($ctx.name);
		DefaultMethod method = DefaultMethod.create(packageAndClass, $m.text);

		$ctx.val = new MethodAssumePostContext(method, $ps.val);
    }
	| '[' METHOD_TOKEN ( pkg=ID_TOKEN {$ctx.name.add($pkg.text);} '.' ) * c=ID_TOKEN {$ctx.name.add($c.text);} '.' m=ID_TOKEN ']' ps=predicatelist {
		DefaultPackageAndClass packageAndClass = DefaultPackageAndClass.create($ctx.name);
		DefaultMethod method = DefaultMethod.create(packageAndClass, $m.text);

		$ctx.val = new MethodContext(method, $ps.val);
	}
	| '[' METHOD_TOKEN ( pkg=ID_TOKEN {$ctx.name.add($pkg.text);} '.' ) * c=ID_TOKEN {$ctx.name.add($c.text);} '.' m=INIT_TOKEN ']' ps=predicatelist {
		DefaultPackageAndClass packageAndClass = DefaultPackageAndClass.create($ctx.name);
		DefaultMethod method = DefaultMethod.create(packageAndClass, $m.text);

		$ctx.val = new MethodContext(method, $ps.val);
	}
	;

predicatelist returns [List<Predicate> val]
	: /* EMPTY */ {
		$ctx.val = new ArrayList<Predicate>();
	}
	| ps=predicatelist p=predicate {
		$ctx.val = $ps.val;
		$ctx.val.add($p.val);
	}
	;

predicate returns [Predicate val]
	: TRUE_TOKEN {
		$ctx.val = Tautology.create();
	}
	| FALSE_TOKEN {
		$ctx.val = Contradiction.create();
	}
	| NOT_TOKEN '(' p=predicate ')' {
		$ctx.val = Negation.create($p.val);
	}
	| a=expression '=' b=expression {
		$ctx.val = Equals.create($a.val, $b.val);
	}
	| a=expression '=' NULL_TOKEN {
		$ctx.val = Equals.create($a.val, NullExpression.create());
	}
	| NULL_TOKEN '=' b=expression {
		$ctx.val = Equals.create(NullExpression.create(), $b.val);
	}
	| NULL_TOKEN '=' NULL_TOKEN {
		$ctx.val = Tautology.create();
	}
	| a=expression '<' b=expression {
		$ctx.val = LessThan.create($a.val, $b.val);
	}
	| a=expression '>' b=expression {
		$ctx.val = LessThan.create($b.val, $a.val);
	}
	| a=expression '<=' b=expression {
		$ctx.val = Negation.create(LessThan.create($b.val, $a.val));
	}
	| a=expression '>=' b=expression {
		$ctx.val = Negation.create(LessThan.create($a.val, $b.val));
	}
	| a=expression '!=' b=expression {
		$ctx.val = Negation.create(Equals.create($a.val, $b.val));
	}
	| a=expression '!=' NULL_TOKEN {
		$ctx.val = Negation.create(Equals.create($a.val, NullExpression.create()));
	}
	| NULL_TOKEN '!=' b=expression {
		$ctx.val = Negation.create(Equals.create(NullExpression.create(), $b.val));
	}
	| NULL_TOKEN '!=' NULL_TOKEN {
		$ctx.val = Contradiction.create();
	}
	;

expression returns [Expression val]
	: t=term {
		$ctx.val = $t.val;
	}
	| a=term '+' b=term {
		$ctx.val = Add.create($a.val, $b.val);
	}
	| a=term '-' b=term {
		$ctx.val = Subtract.create($a.val, $b.val);
	}
	;

term returns [Expression val]
	: f=factor {
		$ctx.val = $f.val;
	}
	| a=factor '*' b=factor {
		$ctx.val = Multiply.create($ctx.a.val, $ctx.b.val);
	}
	| a=factor '/' b=factor {
		$ctx.val = Divide.create($ctx.a.val, $ctx.b.val);
	}
	;

factor returns [Expression val]
	: CONSTANT_TOKEN {
		$ctx.val = Constant.create(Integer.parseInt($CONSTANT_TOKEN.text));
	}
	| ALENGTH_TOKEN '(' ARRLEN_TOKEN ',' p=path ')' {
		$ctx.val = DefaultArrayLengthRead.create($p.val);
	}
	| p=path {
		$ctx.val = $p.val;
	}
	| '(' e=expression ')' {
		$ctx.val = $e.val;
	}
	;

path returns [DefaultAccessExpression val] locals [List<String> name = new LinkedList<String>()]
    : f=RETURN_TOKEN {
        $ctx.val = DefaultReturnValue.create();
    }
	| f=ID_TOKEN {
		$ctx.val = DefaultRoot.create($f.text);
	}
	| CLASS_TOKEN '(' ( pkg=ID_TOKEN {$ctx.name.add($pkg.text);} '.' ) * c=ID_TOKEN {$ctx.name.add($c.text);} ')' '.' f=ID_TOKEN {
		$ctx.val = DefaultObjectFieldRead.create(DefaultPackageAndClass.create($ctx.name), $f.text);
	}
	| SFREAD_TOKEN '(' f=ID_TOKEN ',' ( pkg=ID_TOKEN {$ctx.name.add($pkg.text);} '.' ) * c=ID_TOKEN {$ctx.name.add($c.text);} ')' {
		$ctx.val = DefaultObjectFieldRead.create(DefaultPackageAndClass.create($ctx.name), $f.text);
	}
	| p=path '.' f=ID_TOKEN {
		$ctx.val = DefaultObjectFieldRead.create($p.val, $f.text);
	}
	| p=path '[' e=expression ']' {
		$ctx.val = DefaultArrayElementRead.create($p.val, $e.val);
	}
	| FREAD_TOKEN '(' f=ID_TOKEN ',' p=path ')' {
		$ctx.val = DefaultObjectFieldRead.create($p.val, $f.text);
	}
	| AREAD_TOKEN '(' ARR_TOKEN ',' p=path ',' e=expression ')' {
		$ctx.val = DefaultArrayElementRead.create($p.val, $e.val);
	}
	;

ALENGTH_TOKEN: 'alength';
AREAD_TOKEN  : 'aread';
ARR_TOKEN    : 'arr';
ARRLEN_TOKEN : 'arrlen';
ASSUME_TOKEN : 'assume';
CLASS_TOKEN  : 'class';
FALSE_TOKEN  : 'false';
FREAD_TOKEN  : 'fread';
INIT_TOKEN   : '<init>';
METHOD_TOKEN : 'method';
NOT_TOKEN    : 'not';
NULL_TOKEN   : 'null';
OBJECT_TOKEN : 'object';
PRE_TOKEN    : 'pre';
POST_TOKEN   : 'post';
RETURN_TOKEN : 'return';
SFREAD_TOKEN : 'sfread';
STATIC_TOKEN : 'static';
TRUE_TOKEN   : 'true';

CONSTANT_TOKEN
	: [-+]?'0'('.' [0-9]*)?
	| [-+]?[1-9][0-9]*('.' [0-9]*)?
	;

ID_TOKEN
	: [a-zA-Z_][a-zA-Z0-9_]*
	;

COMMENT_TOKEN
    : '//' ~('\n')* { skip(); }
    ;

WS_TOKEN
	: ([ \t\n])+ { skip(); }
	;
