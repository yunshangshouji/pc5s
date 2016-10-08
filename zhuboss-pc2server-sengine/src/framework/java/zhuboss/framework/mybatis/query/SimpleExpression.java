package zhuboss.framework.mybatis.query;

import java.util.ArrayList;
import java.util.List;

public class SimpleExpression implements Condition {
	private final String propertyName;
	private final Object value;
	private final EOperChar op;
	private List<Val> params = new ArrayList<Val>();

	protected SimpleExpression(String propertyName, Object value, EOperChar op) {
		this.propertyName = propertyName;
		this.value = value;
		this.op = op;
		params.add(new Val(value));
	}

	@Override
	public String getSQLString(Query criteria, Dialect dialect) {
		StringBuilder fragment = new StringBuilder();
		fragment.append(propertyName);
		fragment.append(' ');
		fragment.append(op.getOper());
		fragment.append(' ');
		fragment.append('?');
		return fragment.toString();
	}

	@Override
	public List<Val> getParams() {
		return params;
	}

	public String toString() {
		return propertyName + op.getOper() + value;
	}
}
