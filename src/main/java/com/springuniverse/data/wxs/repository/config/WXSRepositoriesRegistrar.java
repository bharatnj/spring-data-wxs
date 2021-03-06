package com.springuniverse.data.wxs.repository.config;

import java.lang.annotation.Annotation;

import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/**
 * @author Bharat
 */
public class WXSRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport {

	/*
	 * (non-Javadoc)
	 * @see com.springuniverse.data.repository.config.RepositoryBeanDefinitionRegistrarSupport#getAnnotation()
	 */
	@Override
	protected Class<? extends Annotation> getAnnotation() {
		return EnableWxsRepositories.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.springuniverse.data.repository.config.RepositoryBeanDefinitionRegistrarSupport#getExtension()
	 */
	@Override
	protected RepositoryConfigurationExtension getExtension() {
		return new WXSRepositoryConfigurationExtension();
	}
}
