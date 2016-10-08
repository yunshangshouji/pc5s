package zhuboss.framework.mybatis.query;

import java.util.Collections;
import java.util.List;

public class PropertyExpression implements Condition {
	private final String propertyName;
	private final String otherPropertyName;
	private final EOperChar op;

	protected PropertyExpression(String propertyName, String otherPropertyName,
			EOperChar op) {
		super();
		this.propertyName = propertyName;
		this.otherPropertyName = otherPropertyName;
		this.op = op;
	}

	@Override
	public String getSQLString(Query criteria, Dialect dialect) {
		StringBuilder fragment = new StringBuilder();
		fragment.append(propertyName);
		fragment.append(' ');
		fragment.append(op.getOper());
		fragment.append(' ');
		fragment.append(otherPropertyName);
		return fragment.toString();
	}

	@Override
	public List<Val> getParams() {
		return Collections.emptyList();
	}

	@Override
	public String toString() {
		return propertyName + op.getOper() + otherPropertyName;
	}

}
