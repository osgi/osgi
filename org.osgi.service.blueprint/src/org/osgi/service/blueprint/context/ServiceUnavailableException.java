package org.osgi.service.blueprint.context;

/**
 * Thrown when an invocation is made on an OSGi service reference component, and 
 * a backing service is not available.
 */
public class ServiceUnavailableException extends RuntimeException {

	private final Class serviceType;
	private final String filter;
	
	public ServiceUnavailableException(
           String message,
           Class serviceType,
           String filterExpression) {
		super(message);
		this.serviceType = serviceType;
		this.filter = filterExpression;
	}
  
	/**
	 * The type of the service that would have needed to be available in 
	 * order for the invocation to proceed.
	 */
	public Class getServiceType() {
		return this.serviceType;
	}
 
	/**
	 * The filter expression that a service would have needed to satisfy in order
	 * for the invocation to proceed.
	 */
	public String getFilter() {
		return this.filter;
	}
}

