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
package org.osgi.service.blueprint.reflect;

import java.util.List;

/**
 * Base class for all components.
 *
 * @see BeanMetadata
 * @see ServiceReferenceMetadata
 * @see ServiceMetadata
 */
public interface ComponentMetadata extends NonNullMetadata {

	/**
	 * The component will be eagerly instanciated
	 */
	static final int INITIALIZATION_EAGER = 1;

	/**
 	 * The component will be lazily instanciated
 	 */
	static final int INITIALIZATION_LAZY = 2;

	/**
	 * The id of the component.
	 *
	 * @return component id. The component id can be <code>null</code> if this is an anonymously
	 * defined inner component.
	 */
	String getId();

	/**
 	 * Is this component to be lazily instantiated?
 	 *
 	 * This is the <code>initialization</code> attribute or the
 	 * <code>default-initialization</code> in the <code>blueprint</code> element
 	 * if not set.
 	 *
 	 * @return the initialization method
 	 * @see #INITIALIZATION_EAGER
 	 * @see #INITIALIZATION_LAZY
 	 */
	int getInitialization();

    /**
     * The names of any components listed in a "depends-on" attribute for this
     * component.
     *
     * @return an immutable List of component names for components that we have explicitly
     * declared a dependency on, or an empty set if none.
     */
    List/*<String>*/ getDependsOn();
}
