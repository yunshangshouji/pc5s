package zhuboss.framework.mybatis.customize;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 系统初始化的时候构建所有statements,避免系统动态解析的高并发，引发Mapped Statements collection already contains value for
 * 注意：由于mapper的扫描注入器使用了spring的。。。 等容器加载完成，才能完成所有注册
 * @author Administrator
 *
 */
public class MybatisConfigurationInitial implements ApplicationListener<ContextRefreshedEvent> {
	SqlSessionFactory sqlSessionFactory;
	
	
	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		sqlSessionFactory.getConfiguration();//.buildAllStatements();	
		
	}
	
}
