package org.osgi.service.blueprint.reflect;

import java.util.List;


/**
 * Metadata describing a method to be invoked as part of component configuration.
 *
 */
public interface MethodInjectionMetadata {
	
	/**
	 * The name of the method to be invoked.
	 * 
	 * @return the method name, overloaded methods are disambiguated by
	 * parameter specifications.
	 */
	String getName();
	
	/**
	 * The parameter specifications that determine which method to invoke
	 * (in the case of overloading) and what arguments to pass to it.
	 * 
	 * @return an immutable List of ParameterSpecification, or an empty list if the
	 * method takes no arguments. The list is ordered by ascending parameter index.
	 * I.e., the first parameter is first in the list, and so on.
	 */
	List /*<ParameterSpecification>*/ getParameterSpecifications();
}
