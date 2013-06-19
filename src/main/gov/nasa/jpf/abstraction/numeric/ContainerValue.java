package gov.nasa.jpf.abstraction.numeric;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ContainerValue extends AbstractValue {
	
	private List<AbstractValue> list;

	public ContainerValue(List<AbstractValue> lst) {
		super(0);
		list = lst;
		// set key
		int key = 0;
		for (int i = 0; i < list.size(); ++i) {
			int cpow = list.get(i).abs.getDomainSize();
			int ckey = list.get(i).getKey();
			key = key * cpow + ckey;
		}

		setKey(key);
	}

	@Override
	public AbstractValue getToken(int idx) {
		int num = getTokensNumber();
		if (idx < 0 || idx >= num)
			throw new RuntimeException("### Error: out of range");
		List<AbstractValue> res = new ArrayList<AbstractValue>();
		for (int i = 0; i < list.size(); ++i)
			if (list.get(i) != null) {
				int cnum = list.get(i).getTokensNumber();
				int cidx = idx % cnum;
				res.add(list.get(i).getToken(cidx));
				idx /= cnum;
			} else
				res.add(null);
		return ((ContainerAbstraction)abs).create(res);
	}

	@Override
	public Set<AbstractValue> getTokens() {
		throw new RuntimeException("get_tokens not implemented");
	}

	@Override
	public int getTokensNumber() {
		int num = 1;
		for (AbstractValue abs : list)
			if (abs != null)
				num *= abs.getTokensNumber();
		return num;
	}
	
	/**
	 * @return true, if this abstraction is a single value from the domain;
	 *         false, if this abstraction represents a set of values from the
	 *         domain.
	 */
	@Override
	public boolean isComposite() {
		return getTokensNumber() > 1;
	}
	
	public List<AbstractValue> getAbstractValues() {
		return list;
	}

	@Override
	public String toString() {
		if (getTokensNumber() > 1)
			return "TOP";
		else {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (int i = 0; i < list.size(); ++i) {
				sb.append(" " + list.get(i));
				if (i + 1 < list.size())
					sb.append(",");
			}
			sb.append(" ]");
			return sb.toString();
		}
	}
}
