/**
 * 
 */
package com.springuniverse.data.wxs.adapter;

/**
 * @author Bharat
 *
 */
public enum MapOperation {
	
	INSERT(1),
	UPDATE(2),
	DELETE(3),
	UPSERT(4),
	SELECT(5),
	CLEAR(6),
	CONTAINS(7),
	UNKNOWN(-1);
	
	public int code;
	MapOperation(int code)
	{
		this.code = code;
	}
	
	public int value()
	{
		return code;
	}

}
