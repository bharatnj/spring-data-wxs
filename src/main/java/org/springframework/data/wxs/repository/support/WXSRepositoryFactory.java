/**
 * 
 */
package org.springframework.data.wxs.repository.support;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.springframework.beans.BeanUtils;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.PersistentEntityInformation;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.wxs.core.WXSOperations;
import org.springframework.util.ClassUtils;

/**
 * @author Bharat
 */
public class WXSRepositoryFactory<T, I extends Serializable> extends RepositoryFactorySupport {
	
	private final WXSOperations wxsOperations;
	private final MappingContext<?, ?> context;
	private final Class<? extends RepositoryQuery> repositoryQueryType;
	
	public WXSRepositoryFactory(WXSOperations wxsOperations, Class<? extends RepositoryQuery> repositoryQueryType)
	{
		this.wxsOperations = wxsOperations;
		this.context = wxsOperations.getMappingContext();
		this.repositoryQueryType = repositoryQueryType;
	}

	@Override
	public <T, ID extends Serializable> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
		PersistentEntity entity = this.context.getPersistentEntity(domainClass);
		PersistentEntityInformation entityInformation = new PersistentEntityInformation(entity);
		return entityInformation;
	}

	@Override
	protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
		return SimpleWXSRepository.class;
	}

	@Override
	protected Object getTargetRepository(RepositoryInformation repositoryInformation) {
		return super.getTargetRepositoryViaReflection(repositoryInformation,
				new Object[] { getEntityInformation(repositoryInformation.getDomainType()), this.wxsOperations });
	}
	
	@Override
	protected QueryLookupStrategy getQueryLookupStrategy(Key key, EvaluationContextProvider evaluationContextProvider) {
		return new WXSQueryLookupStrategy(wxsOperations,repositoryQueryType);
	}
	
	private static class WXSQueryLookupStrategy implements QueryLookupStrategy {
		private WXSOperations wxsOperations;
		private Class<? extends RepositoryQuery> repositoryQueryType;
		public WXSQueryLookupStrategy(WXSOperations wxsOperations, Class<? extends RepositoryQuery> repositoryQueryType)
		{
			this.wxsOperations = wxsOperations;
			this.repositoryQueryType = repositoryQueryType;
		}

		@Override
		public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory,
				NamedQueries arg3) {
			QueryMethod queryMethod = new QueryMethod(method, metadata, factory);
			Constructor constructor = ClassUtils.getConstructorIfAvailable(this.repositoryQueryType, new Class[] {
					WXSOperations.class,Method.class, QueryMethod.class });
			return (RepositoryQuery) BeanUtils.instantiateClass(constructor, new Object[] { wxsOperations, method, queryMethod});
		}
		
	}
	

}
