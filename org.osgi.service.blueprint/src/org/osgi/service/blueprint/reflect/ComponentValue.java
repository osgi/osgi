package org.osgi.service.blueprint.reflect;

/**
 * A value represented by an anonymous local component definition.
 */
public interface ComponentValue extends Value {

	LocalComponentMetadata getComponentMetadata();
	
}
