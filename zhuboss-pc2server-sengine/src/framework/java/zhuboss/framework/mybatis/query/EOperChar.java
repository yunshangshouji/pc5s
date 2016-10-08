package zhuboss.framework.mybatis.query;

/**
 * 查询操作符
 * 
 * @author dean_lu
 * 
 */
public enum EOperChar {
	NULL("IS NULL", "NU"), NOT_NULL("IS NOT NULL", "NNU"), EQUAL("=", "EQ"), NOT_EQUAL(
			"<>", "NEQ"), GREATER_THEN(">", "GT"), GREATER_THEN_OR_EQUAL(">=",
			"GTEQ"), LESS_THEN("<", "LT"), LESS_THEN_OR_EQUAL("<=", "LTEQ"), LIKE(
			"LIKE", "LIKE"), NOT_LIKE("NOT LIKE", "NLIKE"), BETWEEN("BETWEEN",
			"BETWEEN"), NOT_BETWEEN("NOT BETWEEN", "NBETWEEN"), IN("IN", "IN"), NOT_IN(
			"NOT IN", "NIN");
	private String oper;
	private String alias;

	private EOperChar(String oper, String alias) {
		this.oper = oper;
		this.alias = alias;
	}

	/**
	 * 操作符
	 * 
	 * @return
	 */
	public String getOper() {
		return oper;
	}

	/**
	 * 操作别名
	 * 
	 * @return
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * 查找对应操作的枚举
	 * 
	 * @param oper
	 * @return
	 */
	public static EOperChar findOperChar(String oper) {
		for (EOperChar operChar : values()) {
			if (operChar.getOper().equals(oper)) {
				return operChar;
			}
		}
		return null;
	}

	/**
	 * 根据别名查找对应操作的枚举
	 * 
	 * @param alias
	 * @return
	 */
	public static EOperChar findOperCharByAlias(String alias) {
		for (EOperChar operChar : values()) {
			if (operChar.getAlias().equals(alias)) {
				return operChar;
			}
		}
		return null;
	}
}
