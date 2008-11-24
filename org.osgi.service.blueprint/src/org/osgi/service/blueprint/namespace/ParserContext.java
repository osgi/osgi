package org.osgi.service.blueprint.namespace;

import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.w3c.dom.Node;

/**
 * A ParserContext provides contextual information to a NamespaceHandler when
 * parsing an Element or Node from the namespace.
 */
public interface ParserContext {

	/**
	 * The dom Node which we are currently processing
	 */
	Node getSourceNode();
	
	/**
	 * The component definition registry containing all of the registered component
	 * definitions for this context
	 */
	ComponentDefinitionRegistry getComponentDefinitionRegistry();
	
	/**
	 * The enclosing component definition in the context of which the
	 * source node is to be processed.
	 */
	ComponentMetadata getEnclosingComponent();
}
