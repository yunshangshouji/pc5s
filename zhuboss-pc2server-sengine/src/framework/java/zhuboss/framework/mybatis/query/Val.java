package zhuboss.framework.mybatis.query;

public class Val {
	private String paramName;
	private Object value;

	public Val(Object value) {
		super();
		this.value = value;
	}

	public Val(String paramName, Object value) {
		super();
		this.paramName = paramName;
		this.value = value;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
