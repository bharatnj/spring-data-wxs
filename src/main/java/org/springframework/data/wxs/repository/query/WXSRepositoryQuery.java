package org.springframework.data.wxs.repository.query;
import java.lang.reflect.Method;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.keyvalue.core.query.KeyValueQuery;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.ResultProcessor;
import org.springframework.data.wxs.core.WXSOperations;
import org.springframework.util.Assert;

/**
 * 
 */

/**
 * @author Bharat
 *
 */
public class WXSRepositoryQuery implements RepositoryQuery{
	
	private final QueryMethod queryMethod;
	private final Method method;
	private final WXSOperations wxsOperations;
	
	public WXSRepositoryQuery(WXSOperations wxsOperations,Method method, QueryMethod queryMethod) {

		Assert.notNull(queryMethod, "Query method must not be null!");
		Assert.notNull(method, "Method type must not be null!");
		this.method = method;
		this.queryMethod = queryMethod;
		this.wxsOperations = wxsOperations;
	}

	@Override
	public Object execute(Object[] parameters) {
		Object returnObj = new Object();
		ParameterAccessor accessor = new ParametersParameterAccessor(getQueryMethod().getParameters(), parameters);
		ResultProcessor processor = queryMethod.getResultProcessor().withDynamicProjection(accessor);
		if(this.method.getName().equalsIgnoreCase("byQuery"))
		{
			KeyValueQuery<String> keyValueQuery = new KeyValueQuery<>(StringUtils.join(parameters, " and "));
			returnObj = processor.processResult(this.wxsOperations.find(keyValueQuery,this.queryMethod.getEntityInformation().getJavaType()));
		}
		else if(StringUtils.containsIgnoreCase(this.method.getName(),"index"))
		{
			Object[] param = new Object[]{"Index",this.method.getName()};
			KeyValueQuery<Object[]> keyValueQuery = new KeyValueQuery<>(ArrayUtils.addAll(param, parameters));
			returnObj = processor.processResult(this.wxsOperations.find(keyValueQuery,this.queryMethod.getEntityInformation().getJavaType()));
		}
		return returnObj;
	}

	@Override
	public QueryMethod getQueryMethod() {
		return this.queryMethod;
	}

}
