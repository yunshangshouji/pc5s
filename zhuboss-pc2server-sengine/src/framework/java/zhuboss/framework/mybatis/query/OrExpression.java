package zhuboss.framework.mybatis.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OrExpression implements Condition {
	private List<Condition> criterions = new ArrayList<Condition>();
	private List<Val> params = new ArrayList<Val>();

	public OrExpression(Condition criterion) {
		criterions.add(criterion);
		params.addAll(criterion.getParams());
	}

	public OrExpression or(Condition criterion) {
		criterions.add(criterion);
		params.addAll(criterion.getParams());
		return this;
	}

	@Override
	public String getSQLString(Query criteria, Dialect dialect) {
		StringBuilder fragment = new StringBuilder();
		if (criterions.size() > 0) {
			fragment.append('(');
			Iterator<Condition> iterator = criterions.iterator();
			while (iterator.hasNext()) {
				Condition criterion = (Condition) iterator.next();
				fragment.append(criterion.getSQLString(criteria, dialect));
				if (iterator.hasNext()) {
					fragment.append(' ');
					fragment.append(ELogicalOper.OR);
					fragment.append(' ');
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

}
