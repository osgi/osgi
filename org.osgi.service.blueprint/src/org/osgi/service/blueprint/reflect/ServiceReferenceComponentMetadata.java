package org.osgi.service.blueprint.reflect;

import java.util.Collection;
import java.util.Set;


/**
 * Metadata describing a reference to a service that is to be imported into the module
 * context from the OSGi service registry.
 */
public interface ServiceReferenceComponentMetadata extends ComponentMetadata {

	/**
	 * A matching service is required at all times.
	 */
	public static final int MANDATORY_AVAILABILITY = 1;
	
	/**
	 * A matching service is not required to be present.
	 */
	public static final int OPTIONAL_AVAILABILITY = 2;
	
	/**
	 * Whether or not a matching service is required at all times.
	 * 
	 * @return one of MANDATORY_AVAILABILITY or OPTIONAL_AVAILABILITY
	 */
	int getServiceAvailabilitySpecification();
	
	/**
	 * The interface types that the matching service must support
	 * 
	 * @return an immutable set of type names
	 */
	Set getInterfaceNames();
	
	/**
	 * The filter expression that a matching service must pass
	 * 
	 * @return filter expression
	 */
	String getFilter();

	/**
	 * The set of listeners registered to receive bind and unbind events for
	 * backing services.
	 * 
	 * @return an immutable collection of registered BindingListenerMetadata
	 */
	Collection /*<BindingListenerMetadata>*/ getBindingListeners();
	
}
