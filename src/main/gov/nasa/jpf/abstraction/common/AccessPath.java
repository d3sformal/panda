package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.impl.DefaultAccessPathIndexElement;
import gov.nasa.jpf.abstraction.common.impl.DefaultAccessPathRootElement;
import gov.nasa.jpf.abstraction.common.impl.DefaultAccessPathSubElement;
import gov.nasa.jpf.abstraction.common.impl.PredicatesDotStringifier;
import gov.nasa.jpf.abstraction.common.impl.PredicatesFunctionStringifier;
import gov.nasa.jpf.abstraction.predicate.common.Predicates;
import gov.nasa.jpf.abstraction.predicate.parser.PredicatesLexer;
import gov.nasa.jpf.abstraction.predicate.parser.PredicatesParser;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class AccessPath extends Expression {
	public static enum NotationPolicy {
		DOT_NOTATION,
		FUNCTION_NOTATION
	}
	
	public static NotationPolicy policy = NotationPolicy.FUNCTION_NOTATION;
	
	public static PredicatesStringifier getDefaultStringifier() {
		return getStringifier(policy);
	}
	
	public static PredicatesStringifier getStringifier(NotationPolicy policy) {
		switch (policy) {
		case DOT_NOTATION:
			return new PredicatesDotStringifier();
		case FUNCTION_NOTATION:
			return new PredicatesFunctionStringifier();
		}
		return null;
	}
	
	protected AccessPathRootElement root;
	protected AccessPathElement tail;
	protected int length;
	
	protected AccessPathRootElement createRootElement(String name) {
		return new DefaultAccessPathRootElement(name);
	}
	
	protected void initialise(String name) {
		String[] packageLocation = name.split("\\.");
				
		root = createRootElement(packageLocation[0]);
		tail = root;
		
		for (int i = 1; i < packageLocation.length; ++i) {
			appendSubElement(packageLocation[i]);
		}
	}
	
	protected AccessPath() {
	}
	
	public AccessPath(String name) {
		root = createRootElement(name);
		tail = root;
		length = 1;
		
		initialise(name);
	}
	
	public AccessPathRootElement getRoot() {
		return root;
	}
	
	public AccessPathElement getTail() {
		return tail;
	}
	
	public int getLength() {
		return length;
	}
	
	protected void appendElement(AccessPathMiddleElement element) {
		tail.setNext(element);
		element.setPrevious(tail);
		tail = element;
		++length;
	}
	
	public void appendSubElement(String name) {
		appendElement(new DefaultAccessPathSubElement(name));
	}
	
	public void appendIndexElement(Expression index) {
		appendElement(new DefaultAccessPathIndexElement(index));

		paths.addAll(index.getPaths());
	}
	
	public static void reRoot(AccessPath path, AccessPath oldPrefix, AccessPath newPrefix) {
		if (oldPrefix.equals(newPrefix)) return;
		
		AccessPathElement oldPrefixElement = oldPrefix.root;
		AccessPathElement pathElement = path.root;
		
		while (oldPrefixElement != null && pathElement != null && oldPrefixElement.equals(pathElement)) {
			oldPrefixElement = oldPrefixElement.getNext();
			pathElement = pathElement.getNext();
		}

		AccessPathMiddleElement next = (AccessPathMiddleElement) pathElement;

		path.root = newPrefix.root;
		path.paths = newPrefix.paths;

		newPrefix.tail.setNext(next);

		if (next == null) {
			path.tail = newPrefix.tail;
		} else {
			next.setPrevious(newPrefix.tail);
		}
		
		while (next != null) {
			if (next instanceof AccessPathIndexElement) {
				AccessPathIndexElement index = (AccessPathIndexElement) next;
				
				path.paths.addAll(index.getIndex().getPaths());
			}

			next = next.getNext();
		}
	}
	
	public boolean isPrefix(AccessPath path) {
		AccessPathElement prefixElement = root;
		AccessPathElement pathElement = path.root;
		
		if (getLength() > path.getLength()) return false;
		
		while (prefixElement != null && pathElement != null) {
            if (!prefixElement.equals(pathElement)) {
                return false;
            }

			prefixElement = prefixElement.getNext();
			pathElement = pathElement.getNext();
		}
		
		return prefixElement == null || pathElement != null;
	}
	
	public boolean isProperPrefix(AccessPath path) {
		return getLength() < path.getLength() && isPrefix(path);
	}
	
	@Override
	public List<AccessPath> getPaths() {
		List<AccessPath> ret = new ArrayList<AccessPath>();
		
		ret.addAll(paths);
		ret.add(this);
		
		return ret;
	}
	
	@Override
	public int hashCode() {
		return toString(NotationPolicy.DOT_NOTATION).hashCode();
	}
	
	@Override
	public boolean equals(Object path) {
		return toString().equals(path.toString());
	}
	
	@Override
	public AccessPath clone() {
		AccessPath path = new AccessPath();
		
		path.root = root.clone();
		path.tail = path.root;
		path.length = length;
		path.paths = new ArrayList<AccessPath>();
		
		AccessPathElement next = path.root;
		
		while (next != null) {
			if (next instanceof AccessPathIndexElement) {
				AccessPathIndexElement index = (AccessPathIndexElement) next;
				
				path.paths.addAll(index.getIndex().getPaths());
			}
			
			path.tail = next;
			next = next.getNext();
		}
		
		return path;
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Expression replace(AccessPath formerPath, Expression expression) {
		if (equals(formerPath)) {
			return expression;
		}
		
		AccessPath path = this;
		
		if (formerPath.isPrefix(this) && expression instanceof AccessPath) {
			AccessPath newPath = clone();
			AccessPath newPrefix = ((AccessPath)expression).clone();
			AccessPath.reRoot(newPath, formerPath, newPrefix);
			
			path = newPath;
		}

		AccessPath ret = new AccessPath();
		
		ret.root = path.getRoot().replace(formerPath, expression);
		ret.tail = ret.root;
		ret.length = path.getLength();
		
		AccessPathElement next = ret.root;
		
		while (next != null) {
			if (next instanceof AccessPathIndexElement) {
				AccessPathIndexElement index = (AccessPathIndexElement) next;
				
				ret.paths.addAll(index.getIndex().getPaths());
			}

			ret.tail = next;
			next = next.getNext();
		}
		
		return ret;
	}

	public AccessPath cutTail() {
		AccessPath prefix = clone();
		
		if (prefix.tail instanceof AccessPathMiddleElement) {
			AccessPathMiddleElement tail = (AccessPathMiddleElement) prefix.tail;
			
			if (tail instanceof AccessPathIndexElement) {
				AccessPathIndexElement index = (AccessPathIndexElement) tail;

				prefix.paths.removeAll(index.getIndex().getPaths());
			}
			
			prefix.tail = tail.getPrevious();
			prefix.tail.setNext(null);
			--prefix.length;
		}
		
		return prefix;
	}
	
	public boolean similar(AccessPath path) {
		if (length != path.length) {
			return false;
		}

		if (!this.root.getName().equals(path.root.getName())) {
			return false;
		}
		
		AccessPathElement e1 = this.root;
		AccessPathElement e2 = path.root;
		
		while (e1 != null && e2 != null) {
			if (e1.getClass() != e2.getClass()) {
				return false;
			}

			if (e1 instanceof AccessPathSubElement && e2 instanceof AccessPathSubElement) {
				AccessPathSubElement s1 = (AccessPathSubElement) e1;
				AccessPathSubElement s2 = (AccessPathSubElement) e2;
					
				if (!s1.getName().equals(s2.getName())) {
					return false;
				}
			}
			
			e1 = e1.getNext();
			e2 = e2.getNext();
		}
		
		return e1 == null && e2 == null;
	}
	
	public AccessPathElement getElement(int index) {
		AccessPathElement ret = root;
		
		while (index > 0) {
			ret = ret.getNext();
			--index;
		}
		
		return ret;
	}

	public boolean similarPrefix(AccessPath path) {
		if (length > path.length) {
			return false;
		}

		if (!this.root.getName().equals(path.root.getName())) {
			return false;
		}
		
		AccessPathElement e1 = this.root;
		AccessPathElement e2 = path.root;
		
		while (e1 != null && e2 != null) {
			if (e1.getClass() != e2.getClass()) {
				return false;
			}

			if (e1 instanceof AccessPathSubElement && e2 instanceof AccessPathSubElement) {
				AccessPathSubElement s1 = (AccessPathSubElement) e1;
				AccessPathSubElement s2 = (AccessPathSubElement) e2;
					
				if (!s1.getName().equals(s2.getName())) {
					return false;
				}
			}
			
			e1 = e1.getNext();
			e2 = e2.getNext();
		}
		
		return e1 == null || e2 != null;
	}
	
	public static AccessPath createFromString(String definition) throws IOException {
		ANTLRInputStream chars = new ANTLRInputStream(definition);
		PredicatesLexer lexer = new PredicatesLexer(chars);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PredicatesParser parser = new PredicatesParser(tokens);
	
		return parser.standalonepath().val;
	}
	
	//TODO TURN THIS INTO UNIT TESTS MAYBE ;)
	public static void main(String[] args) throws IOException {
		AccessPath.policy = AccessPath.NotationPolicy.DOT_NOTATION;
		
		AccessPath p = createFromString("a[a.length - 1]");
		
		AccessPath a = createFromString("a");
		AccessPath c = createFromString("c");
		
		Expression e = p.replace(a, c);
		
		System.out.println(e + " " + e.getPaths());
		
		AccessPath x = createFromString("x.x");
		AccessPath y = createFromString("y");
		AccessPath.reRoot(x, x, y);
		
		System.out.println(x + " " + x.getTail().getClass().getSimpleName());
	}
	

}
