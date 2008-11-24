
package org.osgi.service.blueprint.reflect;

/**
 * A simple string value that will be type-converted if necessary before
 * injecting into a target.
 *
 */
public interface TypedStringValue extends Value {
	
	/**
	 * The string value (unconverted) of this value). 
	 */
	String getStringValue();

	/**
	 * The name of the type to which this value should be coerced. May be null.
	 */
	String getTypeName();
}
