/**
 * 
 */
package org.osgi.service.blueprint.context;

/**
 * Thrown when an attempt is made to lookup a component by name and no such named
 * component exists in the module context.
 */
public class NoSuchComponentException extends RuntimeException {

	private final String componentName;
	
	public NoSuchComponentException(String componentName) {
		this.componentName = componentName;
	}
	
	public String getComponentName() {
		return this.componentName;
	}

	public String getMessage() {
		return "No component named '" + 
		       (this.componentName == null ? "<null>" : this.componentName) + 
		       "' could be found";
	}
}
