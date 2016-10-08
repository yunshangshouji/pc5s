package zhuboss.framework.mybatis.customize;

import java.lang.reflect.Field;
import java.util.Properties;

import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Intercepts({ 
	@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
	@Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class}) })
public class QueryClauseInterceptor implements Interceptor {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static Field delegateField ;
	static{
		 try {
			delegateField = CachingExecutor.class.getDeclaredField("delegate");
			delegateField.setAccessible(true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object[] args = invocation.getArgs();
		MappedStatement ms= (MappedStatement)args[0];
		if(invocation.getTarget() instanceof CachingExecutor){
			CachingExecutor executor = (CachingExecutor)invocation.getTarget();
			QueryClauseSimpleExecutor mySimpleExecutor = new QueryClauseSimpleExecutor(ms.getConfiguration(),executor.getTransaction());
			delegateField.set(executor, mySimpleExecutor);
		}
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub

	}

}
