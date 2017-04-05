/**
 * 
 */
package org.springframework.data.wxs.repository.support;

import java.io.Serializable;
import org.springframework.data.keyvalue.core.KeyValueOperations;
import org.springframework.data.keyvalue.repository.support.SimpleKeyValueRepository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.wxs.repository.WXSBaseRepository;

/**
 * @author Bharat
 *
 */
public class SimpleWXSRepository<T, ID extends Serializable> extends SimpleKeyValueRepository<T, ID> implements WXSBaseRepository<T, ID>{

	public SimpleWXSRepository(EntityInformation<T, ID> metadata, KeyValueOperations operations) {
		super(metadata, operations);
	}
	
}
