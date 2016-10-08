package zhuboss.framework.mybatis.query;

import java.util.Collections;
import java.util.List;

public class SQLExpression implements Condition {
	private final String sql;

	protected SQLExpression(String sql) {
		this.sql = sql;
	}

	@Override
	public String getSQLString(Query criteria, Dialect dialect) {
		return sql;
	}

	@Override
	public List<Val> getParams() {
		return Collections.emptyList();
	}


}
