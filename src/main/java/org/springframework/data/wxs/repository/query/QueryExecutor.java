/**
 * 
 */
package org.springframework.data.wxs.repository.query;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import com.ibm.commons.collections.IteratorUtils;
import com.ibm.websphere.objectgrid.ObjectGrid;
import com.ibm.websphere.objectgrid.ObjectGridException;
import com.ibm.websphere.objectgrid.Session;
import com.ibm.websphere.objectgrid.plugins.TransactionCallbackException;
import com.ibm.websphere.objectgrid.query.ObjectQuery;

/**
 * @author Bharat
 *
 */
public class QueryExecutor implements Callable<List<Object>>{
	private static final Class<?> CLASS_NAME = QueryExecutor.class;
	private org.slf4j.Logger log = LoggerFactory.getLogger(CLASS_NAME);
	private ConcurrentHashMap<String, ObjectGrid> objectGrids;
	
	private String query;
	private String mapName;
	private Session gridSession;
	private int partitionId;
	private Integer noOfPartitions;
	private String gridType;
	private Boolean isPageOn;
	private Integer maxResults = 100;
	private Integer firstResult = 0;
	private Integer page = 0;
	private Integer size = 100;
	private ConcurrentHashMap<String, String> objectGridMaps;
	
	public QueryExecutor(ConcurrentHashMap<String, ObjectGrid> objectGrids, ConcurrentHashMap<String, String> objectGridMaps,
			String mapName, String query, Boolean isPageOn, 
			Integer page, Integer size, Integer noOfPartitions, Integer partitionId) {
		this.objectGridMaps = objectGridMaps;
		this.objectGrids = objectGrids;
		this.query = query;
		this.mapName = mapName;
		this.partitionId = partitionId;
		this.noOfPartitions = noOfPartitions;
		this.isPageOn = isPageOn;
		this.page = page;
		this.size = size;
	}
	
	@Override
	public List<Object> call() throws Exception {
		ObjectQuery objectQuery = null;
		List<Object> returnList = new ArrayList<>();
		try
		{
			size = size < noOfPartitions ? noOfPartitions : size;
			gridSession = getSession(mapName);
			log.debug("Applying query - " + query );
			gridSession.begin();
			objectQuery = gridSession.createObjectQuery(query );
			objectQuery.setPartition(partitionId);
			Integer sizePerPartition = 1;
			Integer additionalSizeIn0Partition = 0;
			if(isPageOn)
			{
					sizePerPartition = size/noOfPartitions > 0 ? size/noOfPartitions:1;
					additionalSizeIn0Partition = size%noOfPartitions;
					if(page>1)
					firstResult = page * sizePerPartition;
					maxResults = sizePerPartition;
					if(partitionId==0 && additionalSizeIn0Partition>0)
					{
						maxResults = maxResults + additionalSizeIn0Partition;
					}
					log.debug("min & max results from partition " + partitionId + " is " + firstResult + ":" + maxResults);
					objectQuery.setFirstResult(firstResult);
					objectQuery.setMaxResults(maxResults);
					returnList = IteratorUtils.toList(objectQuery.getResultIterator());
			}
			else
			{
				returnList = IteratorUtils.toList(objectQuery.getResultIterator());
			}
		}
		finally
		{
			if(null!=gridSession || gridSession.isTransactionActive())
			{
				gridSession.close();
			}
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
				if(StringUtils.containsIgnoreCase(gridMapName, inputGridMapName))
				{
					inputGridMapName = gridMapName;
					break;
				}
			}
			return objectGrids.get(objectGridMaps.get(inputGridMapName)).getSession();
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

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public Session getGridSession() {
		return gridSession;
	}

	public void setGridSession(Session gridSession) {
		this.gridSession = gridSession;
	}

	public int getPartitionId() {
		return partitionId;
	}

	public void setPartitionId(int partitionId) {
		this.partitionId = partitionId;
	}

	public String getGridType() {
		return gridType;
	}

	public void setGridType(String gridType) {
		this.gridType = gridType;
	}

	public Boolean getIsPageOn() {
		return isPageOn;
	}

	public void setIsPageOn(Boolean isPageOn) {
		this.isPageOn = isPageOn;
	}

	public Integer getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}

	public Integer getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getNoOfPartitions() {
		return noOfPartitions;
	}

	public void setNoOfPartitions(Integer noOfPartitions) {
		this.noOfPartitions = noOfPartitions;
	}


}
