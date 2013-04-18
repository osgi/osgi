
package org.osgi.dmt.ddf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents the Scope of a type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Scope {
	public enum SCOPE {
		P,
		D,
		A
	};

	SCOPE value();
}
