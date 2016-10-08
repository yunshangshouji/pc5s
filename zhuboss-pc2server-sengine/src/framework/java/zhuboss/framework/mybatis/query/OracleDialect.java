package zhuboss.framework.mybatis.query;

public class OracleDialect extends Dialect {

	@Override
	public String getPagedQuerySQL(String sql, int offset, int limit) {
		StringBuilder answer = new StringBuilder();
		answer.append("select * from (select a.*,rownum row_num from (")
				.append(sql).append(") a where rownum < ")
				.append(offset + 1 + limit).append(" ) b where b.row_num >= ")
				.append(offset + 1);
		return answer.toString();
	}

}
