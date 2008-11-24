
package org.osgi.service.blueprint.reflect;

import java.util.Set;

/**
 * Metadata for a component defined within a given module context.
 * 
 * @see LocalComponentMetadata
 * @see ServiceReferenceComponentMetadata
 * @see ServiceExportComponentMetadata
 */
public interface ComponentMetadata {
	
	/**
	 * The name of the component.
	 * 
	 * @return component name. The component name may be null if this is an anonymously
	 * defined inner component.
	 */
	String getName();
		
	/**
	 * The names of any components listed in a "depends-on" attribute for this
	 * component.
	 * 
	 * @return an immutable set of component names for components that we have explicitly
	 * declared a dependency on, or an empty set if none.
	 */
	Set getExplicitDependencies();
}
