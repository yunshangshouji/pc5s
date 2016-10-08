package zhuboss.framework.mybatis.query;

import java.util.Collections;
import java.util.List;

public class NotNullExpression implements Condition {
	private final String propertyName;

	protected NotNullExpression(String propertyName) {
		this.propertyName = propertyName;
	}

	@Override
	public String getSQLString(Query criteria, Dialect dialect) {
		StringBuilder fragment = new StringBuilder();
		fragment.append(propertyName);
		fragment.append(' ');
		fragment.append(EOperChar.NOT_NULL.getOper());
		return fragment.toString();
	}

	@Override
	public List<Val> getParams() {
		return Collections.emptyList();
	}

	public String toString() {
		return propertyName + " is not null";
	}

}
