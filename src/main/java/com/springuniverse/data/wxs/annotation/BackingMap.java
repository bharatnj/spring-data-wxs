/**
 * 
 */
package com.springuniverse.data.wxs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.data.annotation.Persistent;
import org.springframework.data.keyvalue.annotation.KeySpace;

/**
 * @author Bharat
 *
 */
@Persistent
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface BackingMap {
	
	@KeySpace String mapName() default "";

}
