/**
 * 
 */
package com.springuniverse.boot.autoconfigure.wxs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Bharat
 *
 */
//@Component
@ConfigurationProperties(prefix="spring.data.wxs")
public class WXSProperties {
	
	private List<String> gridNames;
	private String catalogEndpoint;
	private Integer defaultPage;
	private Integer defaultSize;
	
	public WXSProperties()
	{
		this.defaultPage = 0;
		this.defaultSize = 20;
		this.gridNames = new ArrayList<>();
	}
	
	public List<String> getGridNames() {
		return gridNames;
	}
	public void setGridNames(List<String> gridNames) {
		this.gridNames = gridNames;
	}
	public String getCatalogEndpoint() {
		return catalogEndpoint;
	}
	public void setCatalogEndpoint(String catalogEndpoint) {
		this.catalogEndpoint = catalogEndpoint;
	}
	public Integer getDefaultPage() {
		return defaultPage;
	}
	public void setDefaultPage(Integer defaultPage) {
		this.defaultPage = defaultPage;
	}
	public Integer getDefaultSize() {
		return defaultSize;
	}
	public void setDefaultSize(Integer defaultSize) {
		this.defaultSize = defaultSize;
	}
	

}
