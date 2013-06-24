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
	| path=dotpath {
		$ctx.val = $path.val;
	}
/*
	| path=funpath {
		$ctx.val = $path.val;
	}
*/
	| '(' e=expression ')' {
		$ctx.val = $e.val;
	}
	;

dotpath returns [AccessPath val]
	: ID {
		$ctx.val = new AccessPath($ID.text);
	}
	| subpath=dotpath '.' field=ID {
		$ctx.val = $subpath.val;
		$ctx.val.appendField($field.text);
	}
	| subpath=dotpath '[' e=expression ']' {
		$ctx.val = $subpath.val;
		$ctx.val.appendIndex($e.val);
	}
	;

/*	
funpath returns [AccessPath val]
	: ID {
		$ctx.val = new AccessPath($ID.text);
	}
	| 'fread' '(' field=ID ',' subpath=funpath ')' {
		$ctx.val = $subpath.val;
		$ctx.val.appendField($field.text);
	}
	| 'aread' '(' 'arr' ',' subpath=dotpath ',' e=expression ')' {
		$ctx.val = $subpath.val;
		$ctx.val.appendIndex($e.val);
	}
	;
*/
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