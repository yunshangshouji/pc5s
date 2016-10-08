package zhuboss.framework.mybatis.query;

import java.util.List;


public interface Query {
	public Query add(Condition condition);

	public List<Condition> getConditions();

	public Query addOrder(Order order);

	public List<Order> getOrders();


	public Query addProperty(Property property);

	public List<Property> getProperties();

	public Query group(Property... group);

	public Property[] getGroup();

	public Query having(Condition having);

	public List<Condition> getHaving();

	public String toSQL(Dialect dialect);

	public List<Val> getParams();
}
