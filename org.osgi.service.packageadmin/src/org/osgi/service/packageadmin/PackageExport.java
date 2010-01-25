/*
 * Copyright (c) OSGi Alliance (2001, 2010). All Rights Reserved.
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

import org.osgi.framework.Version;

/**
 * A package that has been exported from a {@link BundleWiring bundle wiring}.
 * This package may or may not be imported by any bundle wiring.
 * 
 * <p>
 * Objects implementing this interface are created by the framework.
 * 
 * @ThreadSafe
 * @version $Revision$
 * @since 1.6
 */
public interface PackageExport {
	/**
	 * Returns the name of the package.
	 * 
	 * @return The name of the package.
	 */
	String getName();

	/**
	 * Returns the version of the package.
	 * 
	 * @return The version of the package, or {@link Version#emptyVersion} if no
	 *         version information is available.
	 */
	Version getVersion();

	/**
	 * Returns the bundle wiring exporting the package.
	 * 
	 * @return The bundle wiring exporting the package. If the bundle wiring is
	 *         not {@link BundleWiring#isInUse() in use}, <code>null</code> will
	 *         be returned.
	 */
	BundleWiring getExporter();

	/**
	 * Returns the bundle wirings that are importing the package.
	 * 
	 * <p>
	 * Bundle wirings which require the bundle wiring exporting the package are
	 * considered to be importing the package and are included in the result.
	 * 
	 * <p>
	 * The result of this method can change if additional bundle wirings import
	 * the package.
	 * 
	 * @return A <code>Collection</code> containing a snapshot of bundle 
	 *         wirings currently wired to this package, or an empty
	 *         collection if no bundles are wired to this package. If the
	 *         exporter's bundle wiring is not {@link BundleWiring#isInUse() in
	 *         use}, <code>null</code> will be returned.
	 */
	Collection<BundleWiring> getImporters();
}
