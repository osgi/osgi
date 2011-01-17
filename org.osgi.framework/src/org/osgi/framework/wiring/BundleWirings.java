/*
 * Copyright (c) OSGi Alliance (2010, 2011). All Rights Reserved.
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

import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;

/**
 * The {@link BundleWiring#isInUse() in use} bundle wirings for a bundle. Each
 * time a bundle is resolved, a new bundle wiring of the bundle is created. A
 * bundle wiring consists of a bundle and its attached fragments and represents
 * the dependencies with other bundle wirings.
 * 
 * <p>
 * The in use bundle wirings for a bundle can be obtained by calling
 * {@link Bundle#adapt(Class) bundle.adapt}({@link BundleWirings}.class) on the
 * bundle. {@link BundleWirings#getWirings() getWirings()}. A fragment does not
 * itself have bundle wirings. So calling {@link Bundle#adapt(Class)
 * bundle.adapt}({@link BundleWirings}.class) on a fragment must return
 * {@code null}. See {@link FragmentWirings} for the bundle wirings in which a
 * fragment participates.
 * 
 * @ThreadSafe
 * @noimplement
 * @version $Id$
 */
public interface BundleWirings extends BundleReference {
	/**
	 * Return the {@link BundleWiring#isInUse() in use} wirings for the
	 * {@link BundleReference#getBundle() referenced} bundle.
	 * 
	 * <p>
	 * The result is a list of in use bundle wirings. The list is ordered in
	 * reverse chronological order such that the first bundle wiring is the
	 * {@link BundleWiring#isCurrent() current} bundle wiring and last wiring is
	 * the oldest in use bundle wiring.
	 * 
	 * <p>
	 * The list must only contain in use bundle wirings. Generally the list will
	 * have at least one bundle wiring for the bundle: the current bundle
	 * wiring. However, for an uninstalled bundle with no in use bundle wirings
	 * or a newly installed bundle which has not been resolved, the list will be
	 * empty.
	 * 
	 * @return A list containing a snapshot of the {@link BundleWiring}s for the
	 *         referenced bundle.
	 */
	List<BundleWiring> getWirings();
}
