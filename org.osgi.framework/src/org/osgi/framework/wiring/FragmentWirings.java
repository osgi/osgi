/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
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

package org.osgi.framework.wiring;

import java.util.Collection;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;

/**
 * The {@link BundleWiring#isInUse() in use} bundle wirings for a fragment. Each
 * time a bundle is resolved, a new bundle wiring of the bundle is created. A
 * bundle wiring consists of a bundle and its attached fragments and represents
 * the dependencies with other bundle wirings.
 * 
 * <p>
 * The in use bundle wirings for a fragment can be obtained by calling
 * {@link Bundle#adapt(Class) bundle.adapt}({@link FragmentWirings}.class).
 * {@link FragmentWirings#getWirings() getWirings()} on the fragment's
 * {@code Bundle} object. Calling {@link Bundle#adapt(Class) bundle.adapt}(
 * {@link FragmentWirings} .class) on a non-fragment bundle must return
 * {@code null}.
 * 
 * @ThreadSafe
 * @noimplement
 * @version $Id$
 */
public interface FragmentWirings extends BundleReference {
	/**
	 * Return the {@link BundleWiring#isInUse() in use} wirings for the
	 * {@link BundleReference#getBundle() referenced} fragment.
	 * 
	 * <p>
	 * The result is a list of in use bundle wirings to which the referenced
	 * fragment is attached. If the fragment is not attached to any bundle
	 * wiring, then the returned collection will be empty.
	 * 
	 * <p>
	 * The collection must only contain in use bundle wirings.
	 * 
	 * @return A collection containing a snapshot of the {@link BundleWiring}s
	 *         for the referenced fragment.
	 */
	Collection<BundleWiring> getWirings();
}
