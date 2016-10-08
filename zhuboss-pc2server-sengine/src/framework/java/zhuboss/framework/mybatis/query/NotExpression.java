package zhuboss.framework.mybatis.query;

import java.util.List;

public class NotExpression implements Condition {
	private Condition criterion;
	
	protected NotExpression(Condition criterion) {
		this.criterion = criterion;
	}

	@Override
	public String getSQLString(Query criteria, Dialect dialect) {
		if (dialect instanceof MySQLDialect) {
			return "NOT (" + criterion.getSQLString(criteria, dialect) + ')';
		} else {
			return "NOT " + criterion.getSQLString(criteria, dialect);
		}
	}

	@Override
	public List<Val> getParams() {
		return criterion.getParams();
	}

}
