/**
 * 
 */
package org.springframework.data.wxs.repository.support;

import java.io.Serializable;

import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.DefaultEvaluationContextProvider;
import org.springframework.data.wxs.core.WXSOperations;
import org.springframework.data.wxs.repository.query.WXSRepositoryQuery;
import org.springframework.util.Assert;

/**
 * @author Bharat
 */
public class WXSRepositoryFactoryBean<R extends Repository<T, ID>, T, ID extends Serializable> extends 
RepositoryFactoryBeanSupport<R, T, ID> {
	
	private WXSOperations wxsOperations;

	protected WXSRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
		super(repositoryInterface);
	}
	
	@Override
	public void setMappingContext(MappingContext<?, ?> mappingContext) {
		super.setMappingContext(mappingContext);
	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory() {
		return new WXSRepositoryFactory(wxsOperations,WXSRepositoryQuery.class);
	}

	public WXSOperations getWxsOperations() {
		return wxsOperations;
	}

	public void setWxsOperations(WXSOperations wxsOperations) {
		Assert.notNull(wxsOperations, "WXSOperations must not be null!");
		this.wxsOperations = wxsOperations;
	}
	
	@Override
	public void afterPropertiesSet() {
		Assert.notNull(wxsOperations, "WXSOperations must not be null!");
		super.afterPropertiesSet();
	}
}
