package zhuboss.framework.mybatis.query;

public enum ESortOrder {
	ASC("asc"), DESC("desc");
	private final String sql;

	private ESortOrder(String sql) {
		this.sql = sql;
	}

	public String toSQL() {
		return sql;
	}
}
