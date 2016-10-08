package zhuboss.framework.mybatis.query;

import java.util.ArrayList;
import java.util.List;

public class LogicalExpression implements Condition {
	private final Condition lhs;
	private final Condition rhs;
	private final ELogicalOper op;
	private List<Val> params = new ArrayList<Val>();

	protected LogicalExpression(Condition lhs, Condition rhs, ELogicalOper op) {
		this.lhs = lhs;
		this.rhs = rhs;
		this.op = op;
		if (lhs != null) {
			params.addAll(lhs.getParams());
		}
		if (rhs != null) {
			params.addAll(rhs.getParams());
		}
	}

	@Override
	public String getSQLString(Query criteria, Dialect dialect) {
		StringBuilder fragment = new StringBuilder();
		if (lhs != null) {
			String lsql = lhs.getSQLString(criteria, dialect);
			if (rhs != null) {
				String rsql = rhs.getSQLString(criteria, dialect);
				if (lsql != null && !"".equals(lsql.trim()) && rsql != null
						&& !"".equals(rsql.trim())) {
					fragment.append('(');
				}
				fragment.append(lsql);
				if (lsql != null && !"".equals(lsql.trim()) && rsql != null
						&& !"".equals(rsql.trim())) {
					fragment.append(' ');
					fragment.append(op);
					fragment.append(' ');
				}
				fragment.append(rsql);
				if (lsql != null && !"".equals(lsql.trim()) && rsql != null
						&& !"".equals(rsql.trim())) {
					fragment.append(')');
				}

			} else {
				fragment.append(lsql);
			}

		} else {
			if (rhs != null) {
				String rsql = rhs.getSQLString(criteria, dialect);
				fragment.append(rsql);
			}
		}
		return fragment.toString();
	}

	@Override
	public List<Val> getParams() {
		return params;
	}

	public String toString() {
		return lhs.toString() + ' ' + op + ' ' + rhs.toString();
	}
}
