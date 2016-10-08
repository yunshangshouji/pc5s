package zhuboss.framework.mybatis.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


/**
 * 查询条件构建器
 * 
 * <pre>
 * QueryClauseBuilder cond = new QueryClauseBuilder();
 * cond.add(Restrictions.gt(&quot;age&quot;, 30));
 * cond.orEqualTo(&quot;city&quot;, &quot;上海&quot;).orEqualTo(&quot;city&quot;, &quot;北京&quot;).andEqualTo(&quot;sex&quot;, &quot;男&quot;)
 * 		.orEqualTo(&quot;like&quot;, &quot;看书&quot;).orEqualTo(&quot;like&quot;, &quot;打球&quot;);
 * cond.add(Restrictions.or(
 * 		Restrictions.and(Restrictions.eq(&quot;city&quot;, &quot;北京&quot;),
 * 				Restrictions.eq(&quot;sex&quot;, &quot;男&quot;)),
 * 		Restrictions.and(Restrictions.eq(&quot;city&quot;, &quot;上海&quot;),
 * 				Restrictions.eq(&quot;sex&quot;, &quot;女&quot;))));
 * System.out.println(cond.getSql());
 * </pre>
 * 
 * 
 */
public class QueryClauseBuilder implements Query {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Integer start;
    private Integer limit;
    
	protected List<Condition> andCriteria;

	private OrExpression currentOriteria;

	/**
	 * 选取不重复的记录
	 */
	protected boolean distinct;
	/**
	 * 排序条件
	 */
	protected List<String> orderByClause;

	/**
	 * 分组条件
	 */
	protected List<String> groupByClause;

	protected Map<String,String> extendSQLMap = new HashMap<String,String>();
	
	public QueryClauseBuilder addExtendSQL(String key, String sql){
		extendSQLMap.put(key, sql);
		return this;
	}

	public QueryClauseBuilder() {
		super();
		andCriteria = new ArrayList<Condition>();
		orderByClause = new ArrayList<String>();
		groupByClause = new ArrayList<String>();
	}

	public QueryClauseBuilder page(int start, int limit){
		this.start = start;
		this.limit = limit;
		return this;
	}
	/**
	 * @return the andCriteria
	 */
	public List<Condition> getAndCriteria() {
		return andCriteria;
	}

	public QueryClauseBuilder andEqual(String property, Object value) {
		Assert.notNull(value);
		add(Restrictions.eq(property, value));
		return this;
	}

	public QueryClauseBuilder andSQL(String sql) {
		add(Restrictions.sql(sql));
		return this;
	}
	
	public QueryClauseBuilder andNotEqual(String property, Object value) {
		add(Restrictions.ne(property, value));
		return this;
	}

	public QueryClauseBuilder isNull(String property){
		add(Restrictions.isNull(property));
		return this;
	}
	
	public QueryClauseBuilder isNotNull(String property){
		add(Restrictions.isNotNull(property));
		return this;
	}
	
	public QueryClauseBuilder andLike(String property, String value) {
		add(Restrictions.like(property, value, MatchMode.ANYWHERE));
		return this;
	}

	public QueryClauseBuilder andGreat(String property, Object value) {
		add(Restrictions.gt(property, value));
		return this;
	}
	
	public QueryClauseBuilder andGreatEqual(String property, Object value) {
		add(Restrictions.ge(property, value));
		return this;
	}
	
	public QueryClauseBuilder andLess(String property, Object value) {
		add(Restrictions.lt(property, value));
		return this;
	}
	
	public QueryClauseBuilder andLessEqual(String property, Object value) {
		add(Restrictions.le(property, value));
		return this;
	}
	
	public QueryClauseBuilder andIn(String property, Collection<?> values) {
		add(Restrictions.in(property, values));
		return this;
	}

	public QueryClauseBuilder andNotIn(String property, Collection<?> values) {
		add(Restrictions.not(new InExpression(property, values.toArray())));
		return this;
	}
	
	public QueryClauseBuilder orEqual(String property, Object value) {
		return or(Restrictions.eq(property, value));
	}

	public QueryClauseBuilder orNotEqual(String property, Object value) {
		return or(Restrictions.ne(property, value));
	}

	public QueryClauseBuilder orLike(String property, String value) {
		return or(Restrictions.like(property, value, MatchMode.ANYWHERE));
	}

	public QueryClauseBuilder orIn(String property, Collection<?> value) {
		return or(Restrictions.in(property, value));
	}

	public QueryClauseBuilder or(Condition criterion) {
		if (currentOriteria == null) {
			currentOriteria = new OrExpression(criterion);
			andCriteria.add(currentOriteria);
		} else {
			currentOriteria.or(criterion);
		}
		return this;
	}

	/**
	 * 选取不重复的记录
	 * 
	 * @param distinct
	 */
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	/**
	 * 选取不重复的记录
	 * 
	 * @return
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * @return the orderByClause
	 */
	public List<String> getOrderByClause() {
		return orderByClause;
	}

	public QueryClauseBuilder sort(String property) {
		if (property != null && !"".equals(property)) {
			orderByClause.add(property);
		}
		return this;
	}

	public QueryClauseBuilder sort(String property, ESortOrder sort) {
		if (sort == null) {
			return sort(property);
		}
		if (property != null && !"".equals(property)) {
			orderByClause.add(property + " " + sort);
		}
		return this;
	}

	/**
	 * @return the groupByClause
	 */
	public List<String> getGroupByClause() {
		return groupByClause;
	}

	public QueryClauseBuilder group(String... keys) {
		if (keys != null) {
			groupByClause.addAll(Arrays.asList(keys));
		}
		return this;
	}

	public String getSql() {
		return toSQL(null);
	}

	public String toSQL(Dialect dialect) {
		if (dialect == null) {
			dialect = new MySQLDialect();
		}
		StringBuilder fragment = new StringBuilder();
		Iterator<Condition> iterator = andCriteria.iterator();
		while (iterator.hasNext()) {
			Condition criterion = (Condition) iterator.next();
			if (criterion != null) {
				String sqlString = criterion.getSQLString(this, dialect);
				List<Val> params = criterion.getParams();

//				for (Val val : params) {
//					Object value = val.getValue();
//					if (value instanceof Number) {
//						sqlString = sqlString.replaceFirst("\\?",
//								String.valueOf(value));
//					} else {
//						sqlString = sqlString.replaceFirst("\\?",
//								"'" + String.valueOf(value) + "'");
//					}
//				}
				fragment.append(sqlString);
				if (iterator.hasNext()) {
					fragment.append(' ');
					fragment.append(ELogicalOper.AND);
					fragment.append(' ');
				}
			}
		}
		return fragment.toString().trim();
	}

	public int hashCode(){
		StringBuffer sb = new StringBuffer(this.getSql());
		for(Val val : this.getParams()){
			sb.append(","+val.getParamName()+val.getValue());
		}
		sb.append(" order by ");
		for(String order : this.orderByClause){
			sb.append(order+",");
		}
		if(this.start!=null || this.limit!=null){
			sb.append("limit "+start+","+limit);
		}
		return sb.toString().hashCode();
	}
	
	public static void main(String[] args) {
		QueryClauseBuilder cond = new QueryClauseBuilder();
		cond.add(Restrictions.gt("age", 30));
		cond.orEqual("city", "上海").orEqual("city", "北京").andEqual("sex", "男")
				.orEqual("love", "看书").orEqual("love", "打球")
				.orLike("address", "铜锣湾");
		cond.add(Restrictions.or(
				Restrictions.and(Restrictions.eq("city", "北京"),
						Restrictions.eq("sex", "男")),
				Restrictions.and(Restrictions.eq("city", "上海"),
						Restrictions.eq("sex", "女"))));
//		System.out.println(cond.getSql());
	}

	@Override
	public Query add(Condition criterion) {
		andCriteria.add(criterion);
		currentOriteria = null;
		return this;
	}

	@Override
	public Query addOrder(Order order) {
		orderByClause.add(order.toSqlString(this));
		return this;
	}

	@Override
	public List<Condition> getConditions() {
		return andCriteria;
	}

	@Override
	public List<Order> getOrders() {
		return null;
	}

	@Override
	public Query addProperty(Property property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Property> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query group(Property... group) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property[] getGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query having(Condition having) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Condition> getHaving() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Val> getParams() {
		List<Val> params = new ArrayList<Val>();
		Dialect dialect  = new MySQLDialect();
			StringBuilder fragment = new StringBuilder();
			Iterator<Condition> iterator = andCriteria.iterator();
			while (iterator.hasNext()) {
				Condition criterion = (Condition) iterator.next();
				if (criterion != null) {
					params.addAll(criterion.getParams()); //added 2014-4-23
//					}
				}
			}
			
		return params;
	}

}
