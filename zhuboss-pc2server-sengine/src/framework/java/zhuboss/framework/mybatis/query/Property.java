package zhuboss.framework.mybatis.query;

import java.util.Collection;


public interface Property extends LikedProperty, NamedQuery,
		AliasProvider<Property> {

	EDataType getType();

	SimpleExpression eq(Object value);

	SimpleExpression ne(Object value);

	SimpleExpression like(Object value);

	SimpleExpression like(String value, MatchMode matchMode);

	SimpleExpression gt(Object value);

	SimpleExpression lt(Object value);

	SimpleExpression le(Object value);

	SimpleExpression ge(Object value);

	Condition between(Object lo, Object hi);

	Condition in(Object[] values);

	Condition in(Collection<?> values);

	Condition isNull();

	Condition isNotNull();

	PropertyExpression eqProperty(String property);

	PropertyExpression neProperty(String property);

	PropertyExpression ltProperty(String property);

	PropertyExpression leProperty(String property);

	PropertyExpression gtProperty(String property);

	PropertyExpression geProperty(String property);

	Property count();

	Property countDistinct();

	Property max();

	Property min();

	Property sum();

	Property avg();
}
