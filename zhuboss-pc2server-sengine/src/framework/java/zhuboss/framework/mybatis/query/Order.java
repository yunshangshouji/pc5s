package zhuboss.framework.mybatis.query;

import java.io.Serializable;


public class Order implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -208465872852553471L;
	private boolean ascending;
	private String propertyName;

	public String toString() {
		return propertyName + ' ' + (ascending ? ESortOrder.ASC : ESortOrder.DESC);
	}

	/**
	 * Constructor for Order.
	 */
	protected Order(String propertyName, boolean ascending) {
		this.propertyName = propertyName;
		this.ascending = ascending;
	}

	/**
	 * Render the SQL fragment
	 * 
	 */
	public String toSqlString(Query criteria) {
		StringBuffer fragment = new StringBuffer();
		fragment.append(propertyName).append(' ')
				.append(ascending ? ESortOrder.ASC : ESortOrder.DESC);
		return fragment.toString();
	}

	/**
	 * Ascending order
	 * 
	 * @param propertyName
	 * @return Order
	 */
	public static Order asc(String propertyName) {
		return new Order(propertyName, true);
	}

	/**
	 * Descending order
	 * 
	 * @param propertyName
	 * @return Order
	 */
	public static Order desc(String propertyName) {
		return new Order(propertyName, false);
	}

	/**
	 * @return the ascending
	 */
	public boolean isAscending() {
		return ascending;
	}

	/**
	 * @param ascending the ascending to set
	 */
	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @param propertyName the propertyName to set
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	
	
}
