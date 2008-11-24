package org.osgi.service.blueprint.context;

/**
 * Exception thrown when a configuration-related error occurs during
 * creation of a module context. 
 */
public class ComponentDefinitionException extends RuntimeException {

	public ComponentDefinitionException(String explanation) {
		super(explanation);
	}
	
}
