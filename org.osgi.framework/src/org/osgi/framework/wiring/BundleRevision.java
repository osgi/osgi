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


package org.osgi.framework.wiring;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;
import org.osgi.framework.Version;

/**
 * Bundle Revision. Since a bundle update can change the entries in a bundle,
 * different bundle wirings for the same bundle can be associated with different
 * bundle revisions.
 * 
 * @ThreadSafe
 * @version $Id$
 */
public interface BundleRevision extends BundleReference {
	/**
	 * Returns the symbolic name for this bundle revision.
	 * 
	 * @return The symbolic name for this bundle revision.
	 * @see Bundle#getSymbolicName()
	 */
	String getSymbolicName();

	/**
	 * Returns the version for this bundle revision.
	 * 
	 * @return The version for this bundle revision, or
	 *         {@link Version#emptyVersion} if this bundle revision has no
	 *         version information.
	 * @see Bundle#getVersion()
	 */
	Version getVersion();
}
