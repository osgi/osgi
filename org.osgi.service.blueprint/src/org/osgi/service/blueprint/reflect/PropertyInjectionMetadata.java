package org.osgi.service.blueprint.reflect;

/**
 * Metadata describing a property to be injected. Properties are defined
 * following JavaBeans conventions.
 */
public interface PropertyInjectionMetadata {
	
	/**
	 * The name of the property to be injected, following JavaBeans conventions.
	 * 
	 * @return the property name.
	 */
	String getName();
	
	/**
	 * The value to inject the property with.
	 * 
	 * @return the property value.
	 */
	Value getValue();
}
