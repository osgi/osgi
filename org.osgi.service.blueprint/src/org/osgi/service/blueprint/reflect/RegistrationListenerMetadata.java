
package org.osgi.service.blueprint.reflect;

/**
 * Metadata for a listener interested in service registration and unregistration
 * events for an exported service.
 */
public interface RegistrationListenerMetadata {

	/**
	 * The component instance that will receive registration and unregistration 
	 * events. The returned value must reference a component and therefore be
	 * either a ComponentValue, ReferenceValue, or ReferenceNameValue.
	 * 
	 * @return the listener component reference.
	 */
	Value getListenerComponent();
	
	/**
	 * The name of the method to invoke on the listener component when
	 * the exported service is registered with the service registry.
	 * 
	 * @return the registration callback method name.
	 */
	String getRegistrationMethodName();
	
	/**
	 * The name of the method to invoke on the listener component when
	 * the exported service is unregistered from the service registry.
	 * 
	 * @return the unregistration callback method name.
	 */
	String getUnregistrationMethodName();
	
}
