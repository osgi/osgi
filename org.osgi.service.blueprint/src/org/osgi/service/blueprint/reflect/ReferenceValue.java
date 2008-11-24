
package org.osgi.service.blueprint.reflect;

/**
 * A value which refers to another component in the module context by name.
 */
public interface ReferenceValue extends Value {

	/**
	 * The name of the referenced component.
	 */
	String getComponentName();
	
}
