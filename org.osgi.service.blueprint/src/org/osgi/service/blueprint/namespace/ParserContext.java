/*
 * Copyright (c) OSGi Alliance (2008, 2009). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
