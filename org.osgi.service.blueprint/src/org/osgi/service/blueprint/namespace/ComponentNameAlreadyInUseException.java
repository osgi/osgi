package org.osgi.service.blueprint.namespace;

/**
 * Exception thrown when an attempt is made to register a component with a 
 * name that is already in use by an existing component.
 */
public class ComponentNameAlreadyInUseException extends RuntimeException {

	private String duplicateName;
	
	public ComponentNameAlreadyInUseException(String name) {
		this.duplicateName = name;
	}
	
	public String getMessage() {
		return "Name '" + this.duplicateName + "' is already in use by a registered component";
	}
	
	public String getConflictingName() {
		return this.duplicateName;
	}
}
