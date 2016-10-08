package zhuboss.framework.mybatis.query;


public interface LikedProperty extends Condition {
	/**
	 * <code>SELECT y.*, (SELECT a FROM x) FROM y</code>
	 * 
	 * @return
	 */
	public Property asProperty();

	/**
	 * <code>SELECT y.*, (SELECT a FROM x) [alias] FROM y</code>
	 * 
	 * @param alias
	 * @return
	 */
	public Property asProperty(String alias);
}
