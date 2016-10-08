package zhuboss.framework.mybatis.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import zhuboss.framework.mybatis.query.QueryClauseBuilder;

public interface BaseMapper<PO extends AbstractPO,PK extends Serializable> {
	
	List<PO> selectByClause(QueryClauseBuilder queryClause);
	
	Integer selectCountByClause(QueryClauseBuilder queryClause);
	
	PO selectByPK(@Param("id")PK id);
	
	void insert(PO object);
	
	void updateByPK(PO object);
	
	void deleteByPK(PK id);
	
	void deleteByClause(QueryClauseBuilder queryClause);
}
