package zhuboss.framework.mybatis.customize;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.ibatis.executor.SimpleExecutor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.type.TypeHandler;

import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.mybatis.query.Val;

public class QueryClauseSimpleExecutor extends SimpleExecutor {

	public QueryClauseSimpleExecutor(Configuration configuration, Transaction transaction) {
		super(configuration, transaction);
	}

	
	private Statement prepareStatement(StatementHandler handler, Log statementLog) throws SQLException {
	    Statement stmt;
	    Connection connection = getConnection(statementLog);
	    stmt = handler.prepare(connection);
	    handler.parameterize(stmt);
	    return stmt;
	  }

	
	@Override
	public <E> List<E> doQuery(MappedStatement ms, Object parameter,
			RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql)
			throws SQLException {
		 Statement stmt = null;
		    try {
		      Configuration configuration = ms.getConfiguration();
		      StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, rowBounds, resultHandler, boundSql);
		      stmt = prepareStatement(handler, ms.getStatementLog());
		      /**
		       * begin by ZhuZhengquan
		       */
		      if(parameter instanceof QueryClauseBuilder ){
		    	  QueryClauseBuilder queryClauseBuilder = (QueryClauseBuilder)parameter;
		    	  for(int i=0;i<queryClauseBuilder.getParams().size(); i++){
		    		  Val val = queryClauseBuilder.getParams().get(i);
		    		  TypeHandler TypeHandler = configuration.getTypeHandlerRegistry().getTypeHandler(val.getValue().getClass());
		    		  TypeHandler.setParameter((PreparedStatement)stmt, i+1, val.getValue(), null);
		    	  }
		      }
		      /**
		       * end by ZhuZhengquan
		       */
		      return handler.<E>query(stmt, resultHandler);
		    } finally {
		      closeStatement(stmt);
		    }
		    
		    
		    
	}


	@Override
	public int doUpdate(MappedStatement ms, Object parameter)
			throws SQLException {
		Statement stmt = null;
	    try {
	      Configuration configuration = ms.getConfiguration();
	      StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, RowBounds.DEFAULT, null, null);
	      stmt = prepareStatement(handler, ms.getStatementLog());
	      
	      /**
	       * begin by ZhuZhengquan
	       */
	      if(parameter instanceof QueryClauseBuilder ){
	    	  QueryClauseBuilder queryClauseBuilder = (QueryClauseBuilder)parameter;
	    	  for(int i=0;i<queryClauseBuilder.getParams().size(); i++){
	    		  Val val = queryClauseBuilder.getParams().get(i);
	    		  TypeHandler TypeHandler = configuration.getTypeHandlerRegistry().getTypeHandler(val.getValue().getClass());
	    		  TypeHandler.setParameter((PreparedStatement)stmt, i+1, val.getValue(), null);
	    	  }
	      }
	      /**
	       * end by ZhuZhengquan
	       */
	      
	      return handler.update(stmt);
	    } finally {
	      closeStatement(stmt);
	    }
	}

	
}
