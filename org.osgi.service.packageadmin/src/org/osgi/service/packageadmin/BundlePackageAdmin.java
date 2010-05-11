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

package org.osgi.service.packageadmin;

import java.util.Collection;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;

/**
 * The bundle adapter for the Package Admin service. Package Admin bundle
 * adapters for a bundle can be obtained by calling {@link Bundle#adapt(Class)
 * bundle.adapt}(BundlePackageAdmin.class).
 * 
 * <p>
 * Objects implementing this interface are created by the framework.
 * 
 * @ThreadSafe
 * @version $Revision$
 * @since 1.3
 */
public interface BundlePackageAdmin extends BundleReference {
	/**
	 * Return the {@link BundleWiring#isInUse() in use} wirings for the
	 * referenced bundle.
	 * 
	 * <p>
	 * If the referenced bundle is a non-fragment bundle, then the result is a
	 * <code>List</code> of wirings where the list is ordered in reverse
	 * chronological order such that the first wiring is the
	 * {@link BundleWiring#isCurrent() current} bundle wiring and last wiring is
	 * the oldest bundle wiring.
	 * 
	 * <p>
	 * If the referenced bundle is a fragment bundle, then the result is a
	 * <code>Collection</code> of wirings which include the referenced fragment
	 * bundle.
	 * 
	 * <p>
	 * The collection will only contain in use wirings. Generally the collection
	 * will have at least one wiring for the bundle; the current wiring.
	 * However, for an uninstalled bundle with no in use wirings or a newly
	 * installed bundle which has not been resolved, the collection will be
	 * empty.
	 * 
	 * @return A <code>Collection</code> containing a snapshot of the
	 *         {@link BundleWiring}s for the referenced bundle.
	 */
	Collection<BundleWiring> getWirings();
}
