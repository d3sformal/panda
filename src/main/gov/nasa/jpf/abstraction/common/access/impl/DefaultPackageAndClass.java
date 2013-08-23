package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.LinkedList;
import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.access.PackageAndClass;

public class DefaultPackageAndClass extends DefaultRoot implements PackageAndClass {
	
	protected DefaultPackageAndClass(List<String> name) {
		this(createName(name));
	}
	
	protected DefaultPackageAndClass(String name) {
		super(name);
	}
	
	private static String createName(List<String> name) {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < name.size() - 1; ++i) {
			builder.append(name.get(i));
			builder.append('.');
		}
		
		builder.append(name.get(name.size() - 1));
		
		return builder.toString();
	}
	
	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PackageAndClass) {
			PackageAndClass r = (PackageAndClass) o;
			
			return getName().equals(r.getName());
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return ("class_" + getName()).hashCode();
	}
	
	@Override
	public DefaultPackageAndClass clone() {
		return create(getName());
	}
	
	public static DefaultPackageAndClass create(List<String> name) {
		if (name == null || name.size() == 0) {
			return null;
		}
		
		return new DefaultPackageAndClass(name);
	}
	
	public static DefaultPackageAndClass create(String name) {
		List<String> items = new LinkedList<String>();
		
		for (String item : name.split("\\.")) {
			items.add(item);
		}
				
		return create(items);
	}
}
