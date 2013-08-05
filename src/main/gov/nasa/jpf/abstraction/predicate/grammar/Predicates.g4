grammar Predicates;

@header {
	import gov.nasa.jpf.abstraction.common.*;
	import gov.nasa.jpf.abstraction.predicate.common.*;
}

predicates returns [Predicates val]
	: cs=contextlist {
		$ctx.val = new Predicates($cs.val);
	}
	;
	
standalonepath returns [AccessPath val]
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
	
context returns [Context val]
	: '[' 'static' ']' ps=predicatelist {
		$ctx.val = new StaticContext($ps.val);
	}
	| '[' 'object' c=contextpath ']' ps=predicatelist {
		$ctx.val = new ObjectContext($c.val, $ps.val);
	}
	| '[' 'method' c=contextpath ']' ps=predicatelist {
		$ctx.val = new MethodContext($c.val, $ps.val);
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
	: 'true' {
		$ctx.val = Tautology.create();
	}
	| 'false' {
		$ctx.val = Contradiction.create();
	}
	| 'not' '(' p=predicate ')' {
		$ctx.val = Negation.create($p.val);
	}
	| a=expression '=' b=expression {
		$ctx.val = Equals.create($a.val, $b.val);
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
	: CONSTANT {
		$ctx.val = Constant.create(Integer.parseInt($CONSTANT.text));
	}
	| 'alength' '(' p=path ')' {
		$ctx.val = ArrayLength.create($p.val);
	}
	| p=path {
		$ctx.val = $p.val;
	}
	| '(' e=expression ')' {
		$ctx.val = $e.val;
	}
	;
	
contextpath returns [AccessPath val]
	: f=ID {
		$ctx.val = new AccessPath($f.text);
	}
	| p=contextpath '.' f=ID {
		$ctx.val = $p.val;
		$ctx.val.appendSubElement($f.text);
	}
	;

path returns [AccessPath val]
	: f=ID {
		$ctx.val = new AccessPath($f.text);
	}
	| p=path '.' f=ID {
		$ctx.val = $p.val;
		$ctx.val.appendSubElement($f.text);
	}
	| p=path '[' e=expression ']' {
		$ctx.val = $p.val;
		$ctx.val.appendIndexElement($e.val);
	}
	| 'fread' '(' f=ID ',' p=path ')' {
		$ctx.val = $p.val;
		$ctx.val.appendSubElement($f.text);
	}
	| 'aread' '(' 'arr' ',' p=path ',' e=expression ')' {
		$ctx.val = $p.val;
		$ctx.val.appendIndexElement($e.val);
	}
	;

CONSTANT
	: [-+]?'0'('.' [0-9]*)?
	| [-+]?[1-9][0-9]*('.' [0-9]*)?
	;

ID
	: [a-zA-Z_][a-zA-Z0-9_]*
	;

WS
	: ([ \t\n])+ { skip(); }
	;
