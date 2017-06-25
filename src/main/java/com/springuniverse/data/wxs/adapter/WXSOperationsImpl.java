/**
 * 
 */
package com.springuniverse.data.wxs.adapter;

import org.springframework.data.keyvalue.core.KeyValueAdapter;
import org.springframework.data.keyvalue.core.KeyValueTemplate;

import com.springuniverse.data.wxs.core.WXSOperations;

/**
 * @author Bharat
 *
 */
public class WXSOperationsImpl extends KeyValueTemplate implements WXSOperations {
	
	
	public WXSOperationsImpl(KeyValueAdapter adapter) {
		super(adapter);
	}

}
