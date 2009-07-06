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
 * Base class for all managers.
 * 
 * @see BeanMetadata
 * @see ServiceReferenceMetadata
 * @see ServiceMetadata
 * @see Target
 * @ThreadSafe
 * @version $Revision$
 */
public interface ComponentMetadata extends NonNullMetadata {

	/**
	 * The manager will be eagerly activate
	 */
	static final int	ACTIVATION_EAGER	= 1;

	/**
	 * The manager will be lazily activated
	 */
	static final int	ACTIVATION_LAZY		= 2;

	/**
	 * The id of the manager.
	 * 
	 * @return manager id. The manager id can be <code>null</code> if this is an
	 *         anonymously defined and/or inlined manager.
	 */
	String getId();

	/**
	 * Activation strategy for this manager.
	 * 
	 * This is the <code>activation</code> attribute or the
	 * <code>default-activation</code> in the <code>blueprint</code> element if
	 * not set. If this is also not set, it is {@link #ACTIVATION_EAGER}.
	 * 
	 * @return the activation method
	 * @see #ACTIVATION_EAGER
	 * @see #ACTIVATION_LAZY
	 */
	int getActivation();

	/**
	 * The id of any managers listed in a <code>depends-on</code> attribute for
	 * this manager.
	 * 
	 * @return an immutable List of manager ids that are explicitly declared as
	 *         a dependency, or an empty List if none.
	 */
	List/* <String> */getDependsOn();
}
