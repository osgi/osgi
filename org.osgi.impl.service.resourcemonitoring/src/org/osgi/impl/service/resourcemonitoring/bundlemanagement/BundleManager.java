/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.impl.service.resourcemonitoring.bundlemanagement;

import org.osgi.framework.BundleContext;
import org.osgi.service.resourcemonitoring.ResourceContext;

/**
 * A BundleManager manages bundle locks and bundle association with
 * ResourceContext object.
 * 
 * @author Gregory BONNARDEL (Orange)
 */
public interface BundleManager {

	/**
	 * Initializes bundle manager.
	 * <p>
	 * This method registers a Bundle Listener.<:p>
	 * 
	 * @param bundleContext bundle context
	 * @exception BundleManagerException if the provided bundleContext is null
	 */
	public void start(BundleContext bundleContext) throws BundleManagerException;

	/**
	 * Releases all internal data structures and unregisters the Bundle
	 * Listener.
	 */
	public void stop();

	/**
	 * <p>
	 * Add a bundle to a bundle holder (e.g. a Resource Context).
	 * <p>
	 * This method should ensure the unicity of the association between a bundle
	 * and its holder. A bundle can only be associated to a single holder. This
	 * method is thread safe.
	 * 
	 * @param bundleId bundle identifier
	 * @param bundleHolder bundle holder
	 * @throws BundleManagerException if the bundle is associated to another
	 *         bundle holder.
	 */
	void addBundleToHolder(long bundleId, BundleHolder bundleHolder)
			throws BundleManagerException;

	/**
	 * <p>
	 * Remove a bundle from a bundle holder.
	 * <p>
	 * This method is thread safe
	 * 
	 * @param bundleId bundle identifier
	 * @param bundleHolder bundle holder
	 * @throws BundleManagerException if the bundle is not currently associated
	 *         with the specified bundle holder.
	 */
	void removeBundleFromHolder(long bundleId, BundleHolder bundleHolder)
			throws BundleManagerException;

	/**
	 * Retrieves the ResourceContext associated with the provided bundle.
	 * 
	 * @param bundleId bundle id
	 * @return the ResourceContext holding the bundle or null.
	 */
	ResourceContext getResourceContext(long bundleId);

}
