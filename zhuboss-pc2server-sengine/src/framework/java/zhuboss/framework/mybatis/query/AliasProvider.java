package zhuboss.framework.mybatis.query;

public interface AliasProvider<Z extends AliasProvider<Z>> {
	Z as(String alias);

	String getAlias();
}
