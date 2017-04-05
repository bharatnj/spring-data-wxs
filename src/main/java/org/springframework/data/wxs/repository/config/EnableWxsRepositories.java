
package org.springframework.data.wxs.repository.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.config.DefaultRepositoryBaseClass;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.data.wxs.repository.support.WXSRepositoryFactoryBean;

/**
 * @author Bharat
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(WXSRepositoriesRegistrar.class)
public @interface EnableWxsRepositories {

	String[] value() default {};

	String[] basePackages() default {};

	Class<?>[] basePackageClasses() default {};

	Filter[] excludeFilters() default {};

	Filter[] includeFilters() default {};

	String repositoryImplementationPostfix() default "Impl";

	String namedQueriesLocation() default "";

	Key queryLookupStrategy() default Key.CREATE_IF_NOT_FOUND;

	Class<?> repositoryFactoryBeanClass() default WXSRepositoryFactoryBean.class;

	Class<?> repositoryBaseClass() default DefaultRepositoryBaseClass.class;

	String wxsOperationsRef() default "wxsOperationsImpl";

	boolean considerNestedRepositories() default false;

	@SuppressWarnings("rawtypes")
	Class<? extends Map> mapType() default ConcurrentHashMap.class;
}
