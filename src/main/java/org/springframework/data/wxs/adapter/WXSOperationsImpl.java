/**
 * 
 */
package org.springframework.data.wxs.adapter;

import org.springframework.data.keyvalue.core.KeyValueAdapter;
import org.springframework.data.keyvalue.core.KeyValueTemplate;
import org.springframework.data.wxs.core.WXSOperations;

/**
 * @author Bharat
 *
 */
public class WXSOperationsImpl extends KeyValueTemplate implements WXSOperations {
	
	
	public WXSOperationsImpl(KeyValueAdapter adapter) {
		super(adapter);
	}

}
