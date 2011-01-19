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

import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;

/**
 * The {@link BundleRevision bundle revisions} of a bundle. When a bundle is
 * installed and each time a bundle is updated, a new bundle revision of the
 * bundle is created. The current bundle revision is the most recent bundle
 * revision. An in use bundle revision is associated with an
 * {@link BundleWiring#isInUse() in use} {@link BundleWiring}. Only the current
 * bundle revision and all in use bundle revisions are returned.
 * 
 * <p>
 * The bundle revisions for a bundle can be obtained by calling
 * {@link Bundle#adapt(Class) bundle.adapt}({@link BundleRevisions}.class).
 * {@link #getRevisions()} on the bundle.
 * 
 * @ThreadSafe
 * @noimplement
 * @version $Id$
 */
public interface BundleRevisions extends BundleReference {
	/**
	 * Return the bundle revisions for the {@link BundleReference#getBundle()
	 * referenced} bundle.
	 * 
	 * <p>
	 * The result is a list of the current bundle revision and the in use bundle
	 * revisions. The list is ordered in reverse chronological order such that
	 * the first item is the current bundle revision and last item is the oldest
	 * in use bundle revision.
	 * 
	 * <p>
	 * Generally the list will have at least one bundle revision for the bundle:
	 * the current bundle revision. However, for an uninstalled bundle with no
	 * in use bundle revisions, the list will be empty.
	 * 
	 * @return A list containing a snapshot of the {@link BundleRevision}s for
	 *         the referenced bundle.
	 */
	List<BundleRevision> getRevisions();
}
