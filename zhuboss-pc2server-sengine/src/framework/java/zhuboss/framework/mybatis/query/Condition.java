package zhuboss.framework.mybatis.query;

import java.util.List;

public interface Condition {
	public String getSQLString(Query criteria, Dialect dialect);
	public List<Val> getParams();
}
