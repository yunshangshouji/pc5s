package zhuboss.framework.mybatis.query;

public class SelectField {
	private String field;

	private String function;

	private String alias;

	
	public SelectField() {
		super();
	}

	public SelectField(String field, String function, String alias) {
		super();
		this.field = field;
		this.function = function;
		this.alias = alias;
	}

	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}

	/**
	 * @param field
	 *            the field to set
	 */
	public void setField(String field) {
		this.field = field;
	}

	/**
	 * @return the function
	 */
	public String getFunction() {
		return function;
	}

	/**
	 * @param function
	 *            the function to set
	 */
	public void setFunction(String function) {
		this.function = function;
	}

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias
	 *            the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		if (function == null || function.length() == 0) {
			str.append(field);
		} else {
			str.append(function).append('(').append(field).append(')');
		}
		if (alias != null && alias.length() > 0) {
			str.append(" ").append(alias);
		}
		return str.toString();
	}

}
