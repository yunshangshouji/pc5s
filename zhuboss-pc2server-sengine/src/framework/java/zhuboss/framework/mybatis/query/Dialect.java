package zhuboss.framework.mybatis.query;

public abstract class Dialect {
	public Object getSQLValue(Object value) {
		if (value == null || "".equals(value)) {
			return "";
		}
		if (value instanceof Number) {
			return value;
		} else {
			StringBuilder sql = new StringBuilder();
			sql.append('\'');
			sql.append(value);
			sql.append('\'');
			return sql;
		}
	}

	public abstract String getPagedQuerySQL(String sql, int offset, int limit);
}
