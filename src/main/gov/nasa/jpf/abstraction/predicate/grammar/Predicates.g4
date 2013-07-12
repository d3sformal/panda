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
		$ctx.val = new Equals(new Constant(1), new Constant(1));
	}
	| 'false' {
		$ctx.val = new Equals(new Constant(0), new Constant(1));
	}
	| 'not' '(' p=predicate ')' {
		$ctx.val = new Negation($p.val);
	}
	| a=expression '=' b=expression {
		$ctx.val = new Equals($a.val, $b.val);
	}
	| a=expression '<' b=expression {
		$ctx.val = new LessThan($a.val, $b.val);
	}
	| a=expression '>' b=expression {
		$ctx.val = new LessThan($b.val, $a.val);
	}
	| a=expression '<=' b=expression {
		$ctx.val = new Negation(new LessThan($b.val, $a.val));
	}
	| a=expression '>=' b=expression {
		$ctx.val = new Negation(new LessThan($a.val, $b.val));
	}
	| a=expression '!=' b=expression {
		$ctx.val = new Negation(new Equals($a.val, $b.val));
	}
	;

expression returns [Expression val]
	: t=term {
		$ctx.val = $t.val;
	}
	| a=term '+' b=term {
		$ctx.val = new Add($a.val, $b.val);
	}
	| a=term '-' b=term {
		$ctx.val = new Subtract($a.val, $b.val);
	}
	;

term returns [Expression val]
	: f=factor {
		$ctx.val = $f.val;
	}
	| a=factor '*' b=factor {
		$ctx.val = new Multiply($ctx.a.val, $ctx.b.val);
	}
	| a=factor '/' b=factor {
		$ctx.val = new Divide($ctx.a.val, $ctx.b.val);
	}
	;

factor returns [Expression val]
	: CONSTANT {
		$ctx.val = new Constant(Integer.parseInt($CONSTANT.text));
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
