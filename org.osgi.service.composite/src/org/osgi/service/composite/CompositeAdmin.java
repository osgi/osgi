/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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

package org.osgi.service.composite;

import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;

/**
 * The composite admin service is registered by a framework which supports
 * composite bundles.  The composite admin service is used to install
 * composite bundles.
 * 
 * @ThreadSafe
 * @version $Revision$
 * @deprecated This is proposed API. As a result, this API may never be
 *             published or the final API may change substantially by the time
 *             of final publication. You are cautioned against relying upon this
 *             API.
 */
public interface CompositeAdmin {
	/**
	 * Installs a composite bundle into the parent framework with which this 
	 * composite admin service is registered.
	 * <p>
	 * The following steps are required to install a composite:
	 * <ol>
	 * <li>If a bundle containing the same location identifier is already installed, a 
	 * {@link BundleException} is thrown.</li>
	 * <li>The composite manifest is verified.  If this fails, a {@linkplain BundleException}
	 * is thrown.</li>
	 * <li>The composite's associated resources are allocated.  The associated resources 
	 * minimally consist of a unique identifier and a composite framework.  If this 
	 * step fails, a {@linkplain BundleException} is thrown.</li>
	 * <li>The composite framework is initialized and its start-level is set to 0.
	 * At this point no constituent bundles are installed in the composite framework.</li>
	 * <li>A bundle event of type {@linkplain BundleEvent#INSTALLED} is fired.</li>
	 * <li>The <code>CompositeBundle</code> object for the newly installed composite is 
	 * returned</li>
	 * </ol>
	 * </p>
	 * <b>Postconditions, no exceptions thrown </b>
	 * <ul>
	 * <li><code>getState()</code> in &#x007B; <code>INSTALLED</code>,
	 * <code>RESOLVED</code> &#x007D;.
	 * <li>Composite has a unique ID.
	 * <li>The composite framework is initialized, in the {@linkplain Bundle#STARTING} 
	 * state and its start-level is set to 0.
	 * </ul>
	 * <b>Postconditions, when an exception is thrown </b>
	 * <ul>
	 * <li>Composite is not installed and no trace of the composite exists.
	 * </ul>
	 * @param location The location identifier of the composite to install. 
	 * @param compositeManifest The meta-data describing the composite.  This includes
	 *        the symbolic name and version and the sharing policy.
	 * @param configuration The configuration parameters to the composite framework.  See
	 *        {@link CompositeConstants} for the supported configuration parameters.
	 * @return the installed composite bundle.
	 * @throws BundleException if the installation failed.
	 * @throws SecurityException If the caller does not have
	 *         <code>AllPermission</code>.
	 * @throws IllegalStateException If this composite admin service is no longer valid.  
	 *         For example, if the framework has shutdown.
	 */
	CompositeBundle installCompositeBundle(String location,
			Map<String, String> compositeManifest,
			Map<String, String> configuration)
			throws BundleException;

	/**
	 * Returns the parent composite bundle associated with the framework with which 
	 * this composite admin service is registered.  The composite admin service
	 * registered in the root framework must return <code>null</code>.
	 * @return The parent composite bundle or <code>null</code>.
	 */
	CompositeBundle getParentCompositeBundle();

	/**
	 * Returns the <code>String</code> representation of the UUID for the framework 
	 * with which this composite admin service is registered.
	 * @return The UUID for the framework.
	 */
	String getFrameworkUUID();
	
}
