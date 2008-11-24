
package org.osgi.service.blueprint.convert;

/**
 * Implemented by type converters that extend the type conversion
 * capabilties of a module context container.
 */
public interface Converter {
	
	/**
	 * The type that this converter converts String values into.
	 * @return Class object for the class that this converter converts to
	 */
	Class getTargetClass();

	/**
	 * Convert an object to an instance of the target class.
	 * @param source the object to be converted
	 * @return an instance of the class returned by getTargetClass
	 * @throws Exception if the conversion cannot succeed. This exception is
	 * checked because callers should expect that not all source objects
	 * can be successfully converted.
	 */
	Object convert(Object source) throws RuntimeException;
}
