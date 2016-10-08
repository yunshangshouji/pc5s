package zhuboss.framework.mybatis.query;


public abstract class AbsNamedQuery implements NamedQuery {
	private final String name;

	public AbsNamedQuery(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
