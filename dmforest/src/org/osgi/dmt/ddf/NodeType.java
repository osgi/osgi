
package org.osgi.dmt.ddf;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NodeType {
	String value();
}
