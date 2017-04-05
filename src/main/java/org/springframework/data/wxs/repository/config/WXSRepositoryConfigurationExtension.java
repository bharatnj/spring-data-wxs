package org.springframework.data.wxs.repository.config;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.config.ParsingUtils;
import org.springframework.data.keyvalue.core.mapping.context.KeyValueMappingContext;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.data.wxs.adapter.WXSGridAdapter;
import org.springframework.data.wxs.adapter.WXSOperationsImpl;
import org.springframework.data.wxs.repository.WXSBaseRepository;
import org.springframework.data.wxs.repository.support.WXSRepositoryFactoryBean;

/**
 * @author Bharat
 */
public class WXSRepositoryConfigurationExtension extends RepositoryConfigurationExtensionSupport {
	
	private static final String WXS_MODULE_PREFIX = "wxs";
	protected static final String MAPPING_CONTEXT_BEAN_NAME = "wxsMappingContext";
	protected static final String WXS_TEMPLATE_BEAN_REF_ATTRIBUTE = "wxsOperationsRef";

	@Override
	public String getRepositoryFactoryClassName() {
		return WXSRepositoryFactoryBean.class.getName();
	}
	
	@Override
	public String getModuleName() {
		return WXS_MODULE_PREFIX;
	}
	
	@Override
	protected String getModulePrefix() {
		return WXS_MODULE_PREFIX;
	}
	
	@Override
	protected Collection<Class<?>> getIdentifyingTypes() {
		return Collections.<Class<?>> singleton(WXSBaseRepository.class);
	}
	
	@Override
	public void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {

		AnnotationAttributes attributes = config.getAttributes();

		builder.addPropertyReference("wxsOperations", attributes.getString(WXS_TEMPLATE_BEAN_REF_ATTRIBUTE));
		//builder.addPropertyValue("queryCreator", getQueryCreatorType(config));
		//builder.addPropertyValue("queryType", getQueryType(config));
		builder.addPropertyReference("mappingContext", MAPPING_CONTEXT_BEAN_NAME);
	}
	
	@Override
	public void registerBeansForRoot(BeanDefinitionRegistry registry, RepositoryConfigurationSource configurationSource) {

		super.registerBeansForRoot(registry, configurationSource);

		RootBeanDefinition mappingContextDefinition = new RootBeanDefinition(KeyValueMappingContext.class);
		mappingContextDefinition.setSource(configurationSource.getSource());

		registerIfNotAlreadyRegistered(mappingContextDefinition, registry, MAPPING_CONTEXT_BEAN_NAME, configurationSource);

		String wxsOperationsImplName = configurationSource.getAttribute(WXS_TEMPLATE_BEAN_REF_ATTRIBUTE);

		// No custom template reference configured and no matching bean definition found
		if (getDefaultWXSOperationsImplRef().equals(wxsOperationsImplName)
				&& !registry.containsBeanDefinition(wxsOperationsImplName)) {

			AbstractBeanDefinition beanDefinition = getDefaultWxsOperationsBeanDefinition(configurationSource);

			if (beanDefinition != null) {
				registerIfNotAlreadyRegistered(beanDefinition, registry, wxsOperationsImplName, configurationSource.getSource());
			}
		}
	}
	
	protected AbstractBeanDefinition getDefaultWxsOperationsBeanDefinition(
			RepositoryConfigurationSource configurationSource) {

		BeanDefinitionBuilder adapterBuilder = BeanDefinitionBuilder.rootBeanDefinition(WXSGridAdapter.class);
		adapterBuilder.addConstructorArgValue(getMapTypeToUse(configurationSource));

		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(WXSOperationsImpl.class);
		builder.addConstructorArgValue(ParsingUtils.getSourceBeanDefinition(adapterBuilder, configurationSource.getSource()));
		builder.setRole(BeanDefinition.ROLE_SUPPORT);

		return ParsingUtils.getSourceBeanDefinition(builder, configurationSource.getSource());
	}
	
	protected String getDefaultWXSOperationsImplRef() {
		return "wxsOperationsImpl";
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Class<? extends Map> getMapTypeToUse(RepositoryConfigurationSource source) {

		return (Class<? extends Map>) ((AnnotationMetadata) source.getSource()).getAnnotationAttributes(
				EnableWxsRepositories.class.getName()).get("mapType");
	}
	
	
}
