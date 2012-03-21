
package org.osgi.dmt.ddf;

import java.lang.annotation.*;

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
