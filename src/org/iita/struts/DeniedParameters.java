/**
 * 
 */
package org.iita.struts;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * List parameters that should be blocked by {@link StrictParamsInterceptor}
 * 
 * @author mobreza
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DeniedParameters {
	String[] value();
}
