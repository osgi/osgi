/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
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
package org.osgi.framework.hooks.bundle;

import java.util.Collection;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * OSGi Framework Bundle Context Hook Service.
 * 
 * <p>
 * Bundles registering this service will be called during framework bundle find
 * (get bundles) operations.
 * 
 * @ThreadSafe
 * @version $Id$ 
 */
public interface FindHook {
	/**
	 * Find hook method. This method is called during the bundle find operation
	 * (for example, {@link BundleContext#getBundle(long) getBundle} and
	 * {@link BundleContext#getBundles()} methods). This method can filter the 
	 * result of the find operation.
	 * 
	 * @param context The bundle context of the bundle performing the find
	 *        operation.
	 * @param bundles A collection of Bundles to be returned as a
	 *        result of the find operation. The implementation of this method
	 *        may remove bundles from the collection to prevent the
	 *        bundles from being returned to the bundle performing the find
	 *        operation. The collection supports all the optional
	 *        {@code Collection} operations except {@code add} and
	 *        {@code addAll}. Attempting to add to the collection will
	 *        result in an {@code UnsupportedOperationException}. The
	 *        collection is not synchronized.
	 */
	// TODO probably should somehow prevent a hook from hiding a bundle from itself.
	void find(BundleContext context, Collection<Bundle> bundles);
}
