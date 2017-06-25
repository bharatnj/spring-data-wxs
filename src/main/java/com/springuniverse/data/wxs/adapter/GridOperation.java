/**
 * 
 */
package com.springuniverse.data.wxs.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.RecoverableDataAccessException;

import com.ibm.websphere.objectgrid.ClientClusterContext;
import com.ibm.websphere.objectgrid.ObjectGrid;
import com.ibm.websphere.objectgrid.ObjectGridException;
import com.ibm.websphere.objectgrid.ObjectGridManagerFactory;
import com.ibm.websphere.objectgrid.ObjectMap;
import com.ibm.websphere.objectgrid.Session;
import com.ibm.websphere.objectgrid.plugins.TransactionCallbackException;
import com.springuniverse.boot.autoconfigure.wxs.WXSProperties;
import com.springuniverse.data.wxs.repository.query.QueryExecutor;

/**
 * @author Bharat
 *
 */
public class GridOperation {
	
	private static final Class<?> CLASS_NAME = GridOperation.class;
	private final Logger log = LoggerFactory.getLogger(CLASS_NAME);
	@Autowired
	private WXSProperties wxsProperties;
	
	@Autowired
	private ConcurrentHashMap<String, ObjectGrid> objectGrids;
	
	@Autowired
	private ConcurrentHashMap<String, String> objectGridMaps;
	/*
	 * TODO: replace generic exceptions to checked exception 
	*/
	public Object performOperationOnMap(String mapName, MapOperation mapOperation, Object key, Object value)
	{
		try
		{
			Session session = getSession(mapName);
			ObjectMap map = session.getMap(formatMapName(mapName));
			switch(mapOperation.value())
			{
				case 1: map.insert(key, value);return value;
				case 2: map.update(key, value);return value;
				case 3: map.remove(key);return key;
				case 4: map.upsert(key, value);return value;
				case 5: return map.get(key) ;
				case 6: map.clear(); return null;
				case 7: return map.containsKey(key);
				default : throw new InvalidDataAccessApiUsageException("Invalid Grid Operation " + mapOperation +" performed on map " + mapName);
			}
		} catch(ObjectGridException e)
		{
			log.error("Error while performing operation " + mapOperation + " on map " + mapName ,e);
			throw new RecoverableDataAccessException("Error while performing operation " + mapOperation + " on map " + mapName ,e);
		} 
		catch(Exception e)
		{
			log.error("Error while performing operation " + mapOperation + " on map " + mapName ,e);
			throw new RecoverableDataAccessException("Error while performing operation " + mapOperation + " on map " + mapName ,e);
		}
	}
	
	
	
	
	
	
	
	
	
	public Collection<?> getAll(String mapName)
	{
		try {
			return getByQuery(mapName,"",wxsProperties.getDefaultPage(), wxsProperties.getDefaultSize());
		} catch (Exception e) {
			log.error("Error while getting from map " + mapName,e);
			throw new DataRetrievalFailureException("Error while getting from map " + mapName,e);
		}
	}
	
	public List<Object> getByQuery(String mapName, String query, Integer page, Integer size) 
	{
		String tempQuery = "SELECT o FROM " + formatMapName(mapName) + " o ";
		try
		{
			
			page = page!=null?page:wxsProperties.getDefaultPage();
			size = size!=null?size:wxsProperties.getDefaultSize();
			int noOfPartitions = getSession(mapName).getObjectGrid().getMap(formatMapName(mapName)).getPartitionManager().getNumOfPartitions();
			if(null!=query && !query.trim().equalsIgnoreCase(""))
			{
				tempQuery = tempQuery + " WHERE "+ query;
			}
		return executeObjectQuery(mapName,tempQuery, true, page, size, noOfPartitions);
		} catch(Exception e)
		{
			log.error("Error while running query " + tempQuery + " on map " + mapName ,e);
			throw new RecoverableDataAccessException("Error while running query " + tempQuery + " on map " + mapName ,e);
		}
			
	}
	
	public List<Object> executeObjectQuery(String mapName, String query, Boolean isPageOn, Integer page, Integer size, Integer noOfPartitions) 
			throws InterruptedException, ExecutionException
	{
		List<Object> returnList = new ArrayList<>();
		ExecutorService executorService = null;
		try
		{
			executorService = Executors.newFixedThreadPool(noOfPartitions);
			List<Callable<List<Object>>> callables = new ArrayList<Callable<List<Object>>>();
			for(int i = 0; i<noOfPartitions;i++)
			{
				QueryExecutor queryExecutor = new QueryExecutor(objectGrids,objectGridMaps, mapName, query, isPageOn, page, size, noOfPartitions, i);
				callables.add(queryExecutor);
			}
			
			List<Future<List<Object>>> returnFutureList = executorService.invokeAll(callables);
			for(Future<List<Object>> future: returnFutureList)
			{
				returnList.addAll(future.get());
			}
		}
		finally
		{
			if(null!=executorService)
			executorService.shutdown();
		}
		return returnList;
	}
	
	public Session getSession(String inputGridMapName) throws TransactionCallbackException, ObjectGridException
	{
		if(objectGridMaps.containsKey(inputGridMapName))
			return objectGrids.get(objectGridMaps.get(inputGridMapName)).getSession();
		else
		{
			for(String gridMapName: objectGridMaps.keySet())
			{
				if(StringUtils.equals(formatMapName(gridMapName), inputGridMapName))
				{
					inputGridMapName = gridMapName;
					break;
				}
			}
			return objectGrids.get(objectGridMaps.get(inputGridMapName)).getSession();
		}
	}
	
	public ObjectGrid connectClient(String catalogServerEndPoint, String gridName) throws ObjectGridException {
		try {
			ClientClusterContext ccc = ObjectGridManagerFactory.getObjectGridManager().connect(catalogServerEndPoint, null, null);
			ObjectGrid objectgrid = ObjectGridManagerFactory.getObjectGridManager().getObjectGrid(ccc, gridName);
			return objectgrid;
		} catch (ObjectGridException e) {
			log.error("Unable to connect to catalog server at endpoints:" + catalogServerEndPoint, e);
			throw new ObjectGridException("Unable to connect to catalog server at endpoints:" + catalogServerEndPoint, e);
		}
	}
	
	public String formatMapName(String mapName)
	{
		if(!mapName.contains("."))
			return mapName;
		else
		{
			String[] mapNames = mapName.split("\\.");
			if(mapNames.length>1)
				return mapNames[1];
			else return mapNames[0];
		}
	}

}
