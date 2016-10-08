package zhuboss.framework.mybatis.query;

/**
 * 数据类型
 * 
 * @author dean_lu
 * 
 */
public enum EDataType {
	STRING("VARCHAR"), DATE("DATE"), INT("INTEGER"), LONG("BIGINT"), DECIMAL(
			"DECIMAL"), BOOL("CHAR"), BINARY("BLOB"), OBJECT(""), DICT(""), COLLECTION(
			"");
	private String sqlType;

	private EDataType(String sqlType) {
		this.sqlType = sqlType;
	}

	public String sqlType() {
		return this.sqlType;
	}

	public static EDataType findJavaType(String sqlType) {
		for (EDataType type : values()) {
			if (type.sqlType.equals(sqlType)) {
				return type;
			}
		}
		return null;
	}
}
