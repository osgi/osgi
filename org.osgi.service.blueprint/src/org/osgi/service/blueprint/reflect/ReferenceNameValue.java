package org.osgi.service.blueprint.reflect;

/**
 * A value which represents the name of another component in the module context.
 * The name itself will be injected, not the component that the name refers to.
 *
 */
public interface ReferenceNameValue extends Value {

	String getReferenceName();
	
}
