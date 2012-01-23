package org.osgi.service.tr069todmt;

/**
 * Maps to the TR-069 {@code ParameterInfoStruct} that is returned from the
 * {@link TR069Connector#getParameterNames(String, boolean)} method.
 */
public interface ParameterInfo {

	/**
	 * The path of the parameter, either a parameter path, an instance path, a
	 * table path, or an object path.
	 * 
	 * @return The name of the parameter
	 */
	String getPath();

	/**
	 * Return {@code true} if this parameter is writeable, otherwise
	 * {@code false}. A parameter is writeable if the SetParamaterValue with the
	 * given name would be successful if an appropriate value was given.
	 * 
	 * @return If this parameter is writeable
	 */
	boolean isWriteable();

	/**
	 * Returns {@code true} of this is a parameter, if it returns {@code false}
	 * it is an object or table.
	 * 
	 * @return {@code true} for a parameter, {@code false} otherwise
	 */
	boolean isParameter();

	/**
	 * Provide the value of the node. This method throws an exception if it is
	 * called for anything but a parameter
	 * 
	 * @return The Parameter Value of the corresponding object
	 * @throws TR069Exception
	 *             If there is a problem
	 */

	ParameterValue getParameterValue() throws TR069Exception;
}
