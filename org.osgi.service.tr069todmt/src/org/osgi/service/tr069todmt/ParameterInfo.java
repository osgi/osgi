package org.osgi.service.tr069todmt;

import info.dmtree.*;

/**
 * Maps to the TR-069 {@code ParameterInfoStruct} that is returned from the
 * {@link TR069Adapter#getParameterNames(DmtSession,String)} method.
 */
public interface ParameterInfo {

	/**
	 * The name of the parameter.
	 * 
	 * @return The name of the parameter
	 */
	String getName();

	/**
	 * Return {@code true} if this parameter is writeable, otherwise
	 * {@code false}. A parameter is writeable if the SetParamaterValue with the
	 * given name would be successful if an appropriate value was given.
	 * 
	 * @return If this code is writeable
	 */
	boolean isWriteable();

}
