package org.osgi.service.blueprint.reflect;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Set;


/**
 * Metadata representing a service to be exported by a module context.
 *
 */
public interface ServiceExportComponentMetadata extends ComponentMetadata {

	/**
	 * Do not auto-detect types for advertised service intefaces
	 */
	public static final int EXPORT_MODE_DISABLED = 1;
	
	/**
	 * Advertise all Java interfaces implemented by the exported component as
	 * service interfaces.
	 */
	public static final int EXPORT_MODE_INTERFACES= 2;
	
	/**
	 * Advertise all Java classes in the hierarchy of the exported component's type
	 * as service interfaces.
	 */
	public static final int EXPORT_MODE_CLASS_HIERARCHY = 3;
	
	/**
	 * Advertise all Java classes and interfaces in the exported component's type as 
	 * service interfaces.
	 */
	public static final int EXPORT_MODE_ALL = 4;
	
	/**
	 * The component that is to be exported as a service. Value must refer to a component and
	 * therefore be either a ComponentValue, ReferenceValue, or ReferenceNameValue.
	 * 
	 * @return the component to be exported as a service.
	 */
	Value getExportedComponent();
	
	/**
	 * The type names of the set of interface types that the service should be advertised
	 * as supporting.
	 * 
	 * @return an immutable set of (String) type names, or an empty set if using auto-export
	 */
	Set getInterfaceNames();
	
	/**
	 * Return the auto-export mode specified.
	 * 
	 * @return One of EXPORT_MODE_DISABLED, EXPORT_MODE_INTERFACES, EXPORT_MODE_CLASS_HIERARCHY, EXPORT_MODE_ALL
	 */
	int getAutoExportMode();
	
	/**
	 * The user declared properties to be advertised with the service.
	 * 
	 * @return Dictionary containing the set of user declared service properties (may be
	 * empty if no properties were specified).
	 */
	Dictionary getServiceProperties();

	/**
	 * The ranking value to use when advertising the service
	 *  
	 * @return service ranking
	 */
	int getRanking();
	
	/**
	 * The listeners that have registered to be notified when the exported service
	 * is registered and unregistered with the framework.
	 * 
	 * @return an immutable collection of RegistrationListenerMetadata
	 */
	Collection /*<RegistrationListenerMetadata>*/ getRegistrationListeners();
	
}
