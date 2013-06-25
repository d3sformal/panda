grammar PredicateGrammar;

@header {
	import gov.nasa.jpf.abstraction.predicate.common.*;
}

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

path returns [AccessPath val]
	: f=ID {
		$ctx.val = new AccessPath($f.text);
	}
	| p=path d=dotpath {
		$ctx.val = $p.val;
		$ctx.val.append($d.val);
	}
	| p=funpath {
		$ctx.val = $p.val;
	}
	;

dotpath returns [PathElement val]
	: '.' f=ID {
		$ctx.val = new PathFieldElement($f.text);
	}
	| '[' e=expression ']' {
		$ctx.val = new PathIndexElement($e.val);
	}
	;

funpath returns [AccessPath val]
	: 'fread' '(' f=ID ',' p=path ')' {
		$ctx.val = $p.val;
		$ctx.val.append(new PathFieldElement($f.text));
	}
	| 'aread' '(' 'arr' ',' p=path ',' e=expression ')' {
		$ctx.val = $p.val;
		$ctx.val.append(new PathIndexField($e.val));
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
	: (' ')+ { skip(); }
	;