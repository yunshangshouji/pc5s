package zhuboss.framework.mybatis.query;

import java.util.ArrayList;
import java.util.List;

public class BetweenExpression implements Condition {
	private final String propertyName;
	private final Object lo;
	private final Object hi;
	private List<Val> params = new ArrayList<Val>();

	protected BetweenExpression(String propertyName, Object lo, Object hi) {
		this.propertyName = propertyName;
		this.lo = lo;
		this.hi = hi;
		params.add(new Val(lo));
		params.add(new Val(hi));
	}

	@Override
	public String getSQLString(Query criteria, Dialect dialect) {
		StringBuilder fragment = new StringBuilder();
		fragment.append(propertyName);
		fragment.append(' ');
		fragment.append(EOperChar.BETWEEN.getOper());
		fragment.append(' ');
		fragment.append('?');
		fragment.append(' ');
		fragment.append(ELogicalOper.AND);
		fragment.append(' ');
		fragment.append('?');
		return fragment.toString();
	}

	@Override
	public List<Val> getParams() {
		return params;
	}

	public String toString() {
		return propertyName + " between " + lo + " and " + hi;
	}

}
