package zhuboss.framework.mybatis.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InExpression implements Condition {
	private final String propertyName;
	private final Object[] values;
	private List<Val> params = new ArrayList<Val>();

	protected InExpression(String propertyName, Object[] values) {
		this.propertyName = propertyName;
		this.values = values;
		for (int i = 0; i < values.length; i++) {
			params.add(new Val(values[i]));
		}
	}

	@Override
	public String getSQLString(Query criteria, Dialect dialect) {
		StringBuilder fragment = new StringBuilder();
		if (values != null && values.length > 0) {
			fragment.append(propertyName);
			fragment.append(' ');
			fragment.append(EOperChar.IN.getOper());
			fragment.append(' ');
			fragment.append('(');
			for (int i = 0; i < values.length; i++) {
				fragment.append('?');
				if (i < values.length - 1) {
					fragment.append(",");
				}
			}
			fragment.append(')');
		}
		return fragment.toString();
	}

	@Override
	public List<Val> getParams() {
		return params;
	}

	public String toString() {
		return propertyName + " in (" + Arrays.toString(values) + ')';
	}

}
