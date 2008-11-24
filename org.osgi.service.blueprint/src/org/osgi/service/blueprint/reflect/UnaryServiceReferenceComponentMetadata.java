package org.osgi.service.blueprint.reflect;

/**
 * 
 * Service reference that will bind to a single matching service
 * in the service registry.
 *
 */
public interface UnaryServiceReferenceComponentMetadata extends
	ServiceReferenceComponentMetadata {

	/**
	 * Timeout for service invocations when a matching backing service
	 * is unavailable.
     *
	 * @return service invocation timeout in milliseconds
	 */
	long getTimeout();
	
}
