package org.osgi.service.blueprint.reflect;

/**
 * Metadata for a listener interested in service bind and unbind events for a service
 * reference.
 */
public interface BindingListenerMetadata {

	/**
	 * The component instance that will receive bind and unbind 
	 * events. The returned value must reference a component and therefore be
	 * either a ComponentValue, ReferenceValue, or ReferenceNameValue.
	 * 
	 * @return the listener component reference.
	 */
	Value getListenerComponent();
	
	/**
	 * The name of the method to invoke on the listener component when
	 * a matching service is bound to the reference
	 * 
	 * @return the bind callback method name.
	 */
	String getBindMethodName();
	
	/**
	 * The name of the method to invoke on the listener component when
	 * a service is unbound from the reference.
	 * 
	 * @return the unbind callback method name.
	 */
	String getUnbindMethodName();
	
}
