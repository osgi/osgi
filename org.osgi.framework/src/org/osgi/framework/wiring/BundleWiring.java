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

import java.util.Collection;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;
import org.osgi.framework.Constants;

/**
 * A wiring for a bundle. Each time a bundle is resolved, a new bundle wiring
 * for the bundle is created. A bundle wiring consists of a bundle and it
 * attached fragments and represents the dependencies with other bundle wirings.
 * 
 * <p>
 * The bundle wiring for a bundle is the {@link #isCurrent() current} bundle
 * wiring if the bundle is resolved and the bundle wiring is the most recent
 * bundle wiring. All bundle wirings for a bundle except the current bundle
 * wiring are considered removal pending. A bundle wiring is {@link #isInUse()
 * in use} if it is the current wiring or if some other in use bundle wiring is
 * dependent upon it. For example, wired to a package exported by the bundle
 * wiring or requires the bundle wiring. An in use bundle wiring has a class
 * loader. Once a bundle wiring is no longer in use, it is considered stale and
 * is discarded by the framework.
 * 
 * <p>
 * A list of all in use bundle wirings for a bundle can be obtained by calling
 * {@link Bundle#adapt(Class) bundle.adapt}({@link BundleWirings}.class).
 * {@link BundleWirings#getWirings() getWirings()}. For non-fragment bundles,
 * the first item in the returned list is the current bundle wiring.
 * 
 * <p>
 * The current bundle wiring for a non-fragment bundle can be obtained by
 * calling {@link Bundle#adapt(Class) bundle.adapt}(BundleWiring.class). A
 * fragment bundle does not itself have bundle wirings. So calling
 * {@link Bundle#adapt(Class) bundle.adapt}(BundleWiring.class) on a fragment
 * must return {@code null}.
 * 
 * @ThreadSafe
 * @version $Id$
 */
public interface BundleWiring extends BundleReference {
	/**
	 * Returns the package exports for packages exported by this bundle wiring.
	 * 
	 * <p>
	 * If this bundle wiring is for the system bundle, that is the bundle with
	 * id zero, this method returns all the packages known to be exported by the
	 * system bundle. This will include the package specified by the
	 * {@link Constants#FRAMEWORK_SYSTEMPACKAGES} and
	 * {@link Constants#FRAMEWORK_SYSTEMPACKAGES_EXTRA} framework properties as
	 * well as any other package exported by the framework implementation.
	 * 
	 * @return A collection containing the {@link ExportedPackage}s , or an
	 *         empty collection if this bundle wiring exports no packages. If
	 *         this bundle wiring is not {@link #isInUse() in use}, {@code null}
	 *         will be returned.
	 */
	Collection<ExportedPackage> getExportedPackages();

	/**
	 * Returns the package exports for packages imported by this bundle wiring.
	 * 
	 * <p>
	 * If this bundle wiring requires other bundle wirings then the package
	 * exports from the required bundle wirings are included in the result. (See
	 * {@link BundleWiring#getRequiredBundles()}).
	 * 
	 * <p>
	 * Since packages can be dynamically imported, the result of this method can
	 * change if this bundle wiring dynamically imports another package.
	 * 
	 * @return A collection containing a snapshot of the {@link ExportedPackage}
	 *         s, or an empty collection if the bundle wiring imports no
	 *         packages. If this bundle wiring is not {@link #isInUse() in use},
	 *         {@code null} will be returned.
	 */
	Collection<ExportedPackage> getImportedPackages();

	/**
	 * Returns the bundle wirings that require this bundle wiring.
	 * 
	 * <p>
	 * If this bundle wiring is required and then re-exported by another bundle
	 * wiring, then the result will include the requiring bundle wirings of the
	 * re-exporting bundle wiring.
	 * 
	 * <p>
	 * The result of this method can change if additional bundle wirings require
	 * this bundle wiring.
	 * 
	 * @return A collection containing a snapshot of the {@link BundleWiring}s
	 *         requiring this bundle wiring, or an empty collection if no bundle
	 *         wirings require this bundle wiring. If this bundle wiring is not
	 *         {@link #isInUse() in use}, {@code null} will be returned.
	 */
	Collection<BundleWiring> getRequiringBundles();

	/**
	 * Returns the bundle wirings that are required by this bundle wiring.
	 * 
	 * <p>
	 * If a required bundle wiring requires and then re-exports another bundle
	 * wiring, then the result will include the re-exported bundle wiring.
	 * 
	 * <p>
	 * The bundle wirings in the list are ordered as specified in the
	 * {@code Require-Bundle} header.
	 * 
	 * @return A list containing the {@link BundleWiring}s required by this
	 *         bundle wiring, or an empty list if this bundle wiring does not
	 *         require any bundle. If this bundle wiring is not
	 *         {@link #isInUse() in use}, {@code null} will be returned.
	 */
	List<BundleWiring> getRequiredBundles();

	/**
	 * Returns {@code true} if this bundle wiring is the current bundle wiring.
	 * The bundle wiring for a bundle is the current bundle wiring if the bundle
	 * is resolved and the bundle wiring is the most recent bundle wiring. All
	 * bundle wirings for a bundle except the current bundle wiring are
	 * considered removal pending.
	 * 
	 * @return {@code true} if this bundle wiring is the current bundle wiring;
	 *         {@code false} otherwise.
	 */
	boolean isCurrent();

	/**
	 * Returns {@code true} if this bundle wiring is in use. A bundle wiring is
	 * in use if it is the {@link #isCurrent() current} wiring or if some other
	 * in use bundle wiring is dependent upon it. Once a bundle wiring is no
	 * longer in use, it is considered stale and is discarded by the framework.
	 * 
	 * @return {@code true} if this bundle wiring is in use; {@code false}
	 *         otherwise.
	 */
	boolean isInUse();

	/**
	 * Returns the bundle information for the bundle in this bundle wiring.
	 * Since a bundle update can change some information of a bundle, different
	 * bundle wirings for the same bundle can have different bundle information.
	 * 
	 * <p>
	 * The bundle object {@link BundleReference#getBundle() referenced} by the
	 * returned {@code BundleInfo} may return different information than the
	 * returned {@code BundleInfo} since the returned {@code BundleInfo} may
	 * refer to an older revision of the bundle.
	 * 
	 * @return The bundle information for this bundle wiring.
	 */
	BundleInfo getBundleInfo();

	/**
	 * Returns the bundle information for all attached fragments of this bundle
	 * wiring. Since a bundle update can change some information of a bundle,
	 * different bundle wirings for the same bundle can have different bundle
	 * information.
	 * 
	 * <p>
	 * The bundle infos in the list are ordered in fragment attachment order
	 * such that the first info in the list is the first attached fragment and
	 * the last info in the list is the last attached fragment.
	 * 
	 * @return A list containing the {@link BundleInfo}s for all attached
	 *         fragments attached of this bundle wiring, or an empty list if
	 *         this bundle wiring does not have any attached fragments. If this
	 *         bundle wiring is not {@link #isInUse() in use}, {@code null} will
	 *         be returned.
	 */
	List<BundleInfo> getFragmentInfos();

	/**
	 * Returns the class loader for this bundle wiring. Since a bundle refresh
	 * creates a new bundle wiring for a bundle, different bundle wirings for
	 * the same bundle will have different class loaders.
	 * 
	 * @return The class loader for this bundle wiring. If this bundle wiring is
	 *         not {@link #isInUse() in use}, {@code null} will be returned.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         {@code RuntimePermission("getClassLoader")}, and the Java Runtime
	 *         Environment supports permissions.
	 */
	ClassLoader getClassLoader();

	// TODO RFC 154 ...
	// Define ProvidedCapability type
	// Collection<ProvidedCapability> getProvidedCapabilities();
	// Collection<ProvidedCapability> getRequiredCapabilities();
}
