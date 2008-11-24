
package org.osgi.service.blueprint.namespace;

import java.net.URL;

import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A namespace handler provides support for parsing custom namespace elements and attributes in 
 * module context configuration files. It manipulates component definitions and the 
 * component registry to implement the intended semantices of the namespace.
 * 
 * Instances of NamespaceHandler are discovered through the service registry where
 * they should be published with a service property org.osgi.module.context.namespace set
 * to the schema URI of the schema that they handle.
 * 
 * Implementations of NamespaceHandler are required to be thread-safe.
 *
 */
public interface NamespaceHandler {
	
	/**
	 * The URL where the xsd file for the schema may be found. Typically used to return a URL to a
	 * bundle resource entry so as to avoid needing to lookup schemas remotely. 
	 * If null is returned then the schema location will be determined from the xsi:schemaLocation attribute
	 * value.
	 */
	URL getSchemaLocation();
	
	/**
	 * Called when a top-level (i.e. non-nested) element from the namespace is encountered.
	 * Implementers may register component definitions themselves, and/or return a component definition
	 * to be registered.
	 *
	 * @param element the dom element from the namespace that has just been encountered
	 * @param context parser context giving access component registry and context information about the
	 *   current parsing location.
	 *   
	 * @return a component metadata instance to be registered for the context, or null if there are no
	 * additional component descriptions to register.
	 */
	ComponentMetadata parse(Element element, ParserContext context);
	
	/**
	 * Called when an attribute or nested element is encountered. Implementors should parse the 
	 * supplied Node and decorated the provided component, returning the decorated component.
	 *
	 * @param node the dom Node from the namespace that has just been encountered
	 * @param component the component metadata for the component in which the attribute or nested element
	 * was encountered
	 * @param context parser context giving access component registry and context information about the
	 *   current parsing location.
	 *   
	 * @return the decorated component to replace the original, or simply the original component if no
	 * decoration is required. 
	 */
	ComponentMetadata decorate(Node node, ComponentMetadata component, ParserContext context);

}
