package zhuboss.framework.mybatis.query;

public class MySQLDialect extends Dialect {

	@Override
	public String getPagedQuerySQL(String sql, int offset, int limit) {
		StringBuilder answer = new StringBuilder();
		answer.append(sql).append(" limit ").append(offset).append(',')
				.append(limit);
		return answer.toString();
	}

}
