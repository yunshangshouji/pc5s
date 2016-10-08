package zhuboss.framework.mybatis.query;

import java.util.Collection;

public class Restrictions {
	/**
	 * Apply an "equal" constraint to the named property
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression eq(String propertyName, Object value) {
		return new SimpleExpression(propertyName, value, EOperChar.EQUAL);
	}

	public static SQLExpression sql(String sql) {
		return new SQLExpression(sql);
	}
	
	/**
	 * Apply a "not equal" constraint to the named property
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression ne(String propertyName, Object value) {
		return new SimpleExpression(propertyName, value, EOperChar.NOT_EQUAL);
	}

	/**
	 * Apply a "like" constraint to the named property
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression like(String propertyName, Object value) {
		return new SimpleExpression(propertyName, value, EOperChar.LIKE);
	}

	/**
	 * Apply a "like" constraint to the named property
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression like(String propertyName, String value,
			MatchMode matchMode) {
		return new SimpleExpression(propertyName,
				matchMode.toMatchString(value), EOperChar.LIKE);
	}

	/**
	 * Apply a "greater than" constraint to the named property
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression gt(String propertyName, Object value) {
		return new SimpleExpression(propertyName, value, EOperChar.GREATER_THEN);
	}

	/**
	 * Apply a "less than" constraint to the named property
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression lt(String propertyName, Object value) {
		return new SimpleExpression(propertyName, value, EOperChar.LESS_THEN);
	}

	/**
	 * Apply a "less than or equal" constraint to the named property
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression le(String propertyName, Object value) {
		return new SimpleExpression(propertyName, value,
				EOperChar.LESS_THEN_OR_EQUAL);
	}

	/**
	 * Apply a "greater than or equal" constraint to the named property
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression ge(String propertyName, Object value) {
		return new SimpleExpression(propertyName, value,
				EOperChar.GREATER_THEN_OR_EQUAL);
	}

	/**
	 * Apply a "between" constraint to the named property
	 * 
	 * @param propertyName
	 * @param lo
	 *            value
	 * @param hi
	 *            value
	 * @return Criterion
	 */
	public static Condition between(String propertyName, Object lo, Object hi) {
		return new BetweenExpression(propertyName, lo, hi);
	}

	/**
	 * Apply an "in" constraint to the named property
	 * 
	 * @param propertyName
	 * @param values
	 * @return Criterion
	 */
	public static Condition in(String propertyName, Object[] values) {
		return new InExpression(propertyName, values);
	}

	/**
	 * Apply an "in" constraint to the named property
	 * 
	 * @param propertyName
	 * @param values
	 * @return Criterion
	 */
	public static Condition in(String propertyName, Collection<?> values) {
		return new InExpression(propertyName, values.toArray());
	}

	/**
	 * Apply an "is null" constraint to the named property
	 * 
	 * @return Criterion
	 */
	public static Condition isNull(String propertyName) {
		return new NullExpression(propertyName);
	}

	/**
	 * Apply an "is not null" constraint to the named property
	 * 
	 * @return Criterion
	 */
	public static Condition isNotNull(String propertyName) {
		return new NotNullExpression(propertyName);
	}

	/**
	 * Apply an "equal" constraint to two properties
	 */
	public static PropertyExpression eqProperty(String propertyName,
			String otherPropertyName) {
		return new PropertyExpression(propertyName, otherPropertyName,
				EOperChar.EQUAL);
	}

	/**
	 * Apply a "not equal" constraint to two properties
	 */
	public static PropertyExpression neProperty(String propertyName,
			String otherPropertyName) {
		return new PropertyExpression(propertyName, otherPropertyName,
				EOperChar.NOT_EQUAL);
	}

	/**
	 * Apply a "less than" constraint to two properties
	 */
	public static PropertyExpression ltProperty(String propertyName,
			String otherPropertyName) {
		return new PropertyExpression(propertyName, otherPropertyName,
				EOperChar.LESS_THEN);
	}

	/**
	 * Apply a "less than or equal" constraint to two properties
	 */
	public static PropertyExpression leProperty(String propertyName,
			String otherPropertyName) {
		return new PropertyExpression(propertyName, otherPropertyName,
				EOperChar.LESS_THEN_OR_EQUAL);
	}

	/**
	 * Apply a "greater than" constraint to two properties
	 */
	public static PropertyExpression gtProperty(String propertyName,
			String otherPropertyName) {
		return new PropertyExpression(propertyName, otherPropertyName,
				EOperChar.GREATER_THEN);
	}

	/**
	 * Apply a "greater than or equal" constraint to two properties
	 */
	public static PropertyExpression geProperty(String propertyName,
			String otherPropertyName) {
		return new PropertyExpression(propertyName, otherPropertyName,
				EOperChar.GREATER_THEN_OR_EQUAL);
	}

	/**
	 * Apply any property any oper
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression oper(String propertyName, Object value,
			EOperChar oper) {
		return new SimpleExpression(propertyName, value, oper);
	}

	/**
	 * Return the conjuction of two expressions
	 * 
	 * @param lhs
	 * @param rhs
	 * @return Criterion
	 */
	public static LogicalExpression and(Condition lhs, Condition rhs) {
		return new LogicalExpression(lhs, rhs, ELogicalOper.AND);
	}

	/**
	 * Return the disjuction of two expressions
	 * 
	 * @param lhs
	 * @param rhs
	 * @return Criterion
	 */
	public static LogicalExpression or(Condition lhs, Condition rhs) {
		return new LogicalExpression(lhs, rhs, ELogicalOper.OR);
	}

	/**
	 * Return the negation of an expression
	 * 
	 * @param expression
	 * @return Criterion
	 */
	public static Condition not(Condition expression) {
		return new NotExpression(expression);
	}
}
