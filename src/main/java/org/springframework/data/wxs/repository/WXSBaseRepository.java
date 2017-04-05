/**
 * 
 */
package org.springframework.data.wxs.repository;

import java.io.Serializable;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author Bharat
 *
 */
@NoRepositoryBean
public interface WXSBaseRepository<T, ID extends Serializable> extends KeyValueRepository<T, ID> {
	
}
