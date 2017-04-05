/**
 * 
 */
package org.springframework.boot.autoconfigure.wxs;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.wxs.adapter.GridOperation;

import com.ibm.websphere.objectgrid.ClientClusterContext;
import com.ibm.websphere.objectgrid.ObjectGrid;
import com.ibm.websphere.objectgrid.ObjectGridException;
import com.ibm.websphere.objectgrid.ObjectGridManagerFactory;

/**
 * @author Bharat
 *
 */
@Configuration
@EnableConfigurationProperties(WXSProperties.class)
public class WXSAutoConfiguration {

	@Autowired
	private WXSProperties wxsProperties;
	
	@Bean
	public ConcurrentHashMap<String, ObjectGrid> objectGrids() throws ObjectGridException
	{
		ConcurrentHashMap<String, ObjectGrid> objectGrids = new ConcurrentHashMap<>();
		for(String gridName:wxsProperties.getGridNames())
		{
			objectGrids.put(gridName, 
					connectClient(wxsProperties.getCatalogEndpoint(),gridName));
		}
		return objectGrids;
	}
	
	@Bean
	public ConcurrentHashMap<String, String> objectGridMaps(ConcurrentHashMap<String, ObjectGrid> objectGrids) throws ObjectGridException
	{
		ConcurrentHashMap<String, String> objectGridMaps = new ConcurrentHashMap<>();
		for(String gridName:objectGrids.keySet())
		{
			for(Object mapName:objectGrids.get(gridName).getListOfMapNames())
			{
				objectGridMaps.put(gridName+"."+mapName.toString(), gridName);
			}
		}
		return objectGridMaps;
		
	}
	
	@Bean
	public GridOperation gridOperation()
	{
		return new GridOperation();
	}
	
	public static ObjectGrid connectClient(String catalogServerEndPoint, String gridName) throws ObjectGridException {
		try {
			ClientClusterContext ccc = ObjectGridManagerFactory.getObjectGridManager().connect(catalogServerEndPoint, null, null);
			ObjectGrid objectgrid = ObjectGridManagerFactory.getObjectGridManager().getObjectGrid(ccc, gridName);
			return objectgrid;
		} catch (ObjectGridException e) {
			throw new ObjectGridException("Unable to connect to catalog server at endpoints:" + catalogServerEndPoint, e);
		}
	}

}
