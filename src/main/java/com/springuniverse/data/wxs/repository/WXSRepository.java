package com.springuniverse.data.wxs.repository;
/**
 * 
 */

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

/**
 * @author Bharat
 */
@NoRepositoryBean
public interface WXSRepository<T, ID extends Serializable> extends WXSBaseRepository<T, ID>{
	
	public List<T> masterPatientIdentifierIndex(@Param("mpis") List<Integer> params);
	
	public List<T> byQuery(@Param("query") String query);
	
	public List<T> byIndex(@Param("index") String index, @Param("input") Object[] params);
}
