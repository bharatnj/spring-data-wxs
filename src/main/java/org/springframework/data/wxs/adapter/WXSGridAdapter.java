/**
 * 
 */
package org.springframework.data.wxs.adapter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.keyvalue.core.AbstractKeyValueAdapter;
import org.springframework.data.keyvalue.core.query.KeyValueQuery;
import org.springframework.data.util.CloseableIterator;
import org.springframework.util.Assert;

/**
 * @author Bharat
 *
 */
public class WXSGridAdapter extends AbstractKeyValueAdapter {
	
	private static final Class<?> CLASS_NAME = WXSGridAdapter.class;
	private Logger log = LoggerFactory.getLogger(CLASS_NAME);
	
	public WXSGridAdapter()
	{
		
	}
	
	public WXSGridAdapter(Class<?> className)
	{
		
	}
	
	@Autowired
	private GridOperation gridOperation;

	@Override
	public Object put(Serializable id, Object item, Serializable keyspace) {
		return gridOperation.performOperationOnMap(keyspace.toString(),MapOperation.UPSERT,id,item);
	}

	@Override
	public boolean contains(Serializable id, Serializable keyspace) {
		return  (boolean) gridOperation.performOperationOnMap(keyspace.toString(),MapOperation.CONTAINS,id,null);
	}

	@Override
	public Object get(Serializable id, Serializable keyspace) {
		
		Assert.notNull(id, "Cannot get item with null id. WXSGridAdapter");
		return gridOperation.performOperationOnMap(keyspace.toString(),MapOperation.SELECT, id,null);
	}

	@Override
	public Object delete(Serializable id, Serializable keyspace) {
		gridOperation.performOperationOnMap(keyspace.toString(),MapOperation.DELETE,id,null);
		return null;
	}

	@Override
	public Collection<?> getAllOf(Serializable keyspace) {
		//TODO: Can use agents or object query to get all from map by pagination enabled
		return null;
		//return gridOperation.getAll(keyspace.toString());
	}

	@Override
	public CloseableIterator<Entry<Serializable, Object>> entries(Serializable keyspace) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAllOf(Serializable keyspace) {
		gridOperation.performOperationOnMap(keyspace.toString(), MapOperation.CLEAR, null, null);
	}

	@Override
	public void clear() {
		
	}

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long count(Serializable arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public <T> Iterable<T> find(KeyValueQuery<?> query, Serializable keyspace, Class<T> type) {
		/*String queryStr = "";
		if(null!=query && null!=query.getCritieria())
			queryStr = query.getCritieria().toString();
		return (Iterable<T>) gridOperation.getByQuery(keyspace.toString(),queryStr);*/
		return (Iterable<T>) decideFindBY(query,keyspace);
	}

	@Override
	public Collection<?> find(KeyValueQuery<?> query, Serializable keyspace) {
		/*String queryStr = "";
		if(null!=query && null!=query.getCritieria())
			queryStr = query.getCritieria().toString();
		return gridOperation.getByQuery(keyspace.toString(),queryStr);*/
		return decideFindBY(query,keyspace);
	}
	
	private Collection<?> decideFindBY(KeyValueQuery<?> query, Serializable keyspace)
	{
		String queryStr = "";
		if(null!=query && null!=query.getCritieria())
		{
			if(query.getCritieria() instanceof Object[])
			{
				//System.out.println("Query is an instance of object array params are - " + ToStringBuilder.reflectionToString(query.getCritieria()));
				Object[] input = (Object[]) query.getCritieria();
				String indexKeyword = (String)input[0];
				String indexName = (String)input[1];
				if(StringUtils.containsIgnoreCase("Index", indexKeyword))
				{
					//return (Collection<?>) gridOperation.getByIndex(keyspace.toString(),indexName, (List<?>) input[2]);
					return null;
				}
			}
			else
			{
				queryStr = query.getCritieria().toString();
				return gridOperation.getByQuery(keyspace.toString(),queryStr,query.getOffset()/query.getRows(), query.getRows());
			}
		}
		return gridOperation.getByQuery(keyspace.toString(),queryStr,query.getOffset()/query.getRows(), query.getRows());
	}
	

}
