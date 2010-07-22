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

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;

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
	 * Returns the capabilities provided by this bundle wiring.
	 * 
	 * @param capabilityNamespace The name space of the provided capabilities to
	 *        return or {@code null} to return the provided capabilities from
	 *        all name spaces.
	 * @return A list containing a snapshot of the {@link Capability}s,
	 *         or an empty list if this bundle wiring provides no capabilities
	 *         in the specified name space. If this bundle wiring is not
	 *         {@link #isInUse() in use}, {@code null} will be returned. The
	 *         list contains the provided capabilities in the order they are
	 *         specified in the manifest.
	 */
	List<Capability> getProvidedCapabilities(
			String capabilityNamespace);

	/**
	 * Returns the required capabilities used by this bundle wiring.
	 * 
	 * <p>
	 * The result of this method can change if this bundle wiring requires
	 * additional capabilities.
	 * 
	 * @param capabilityNamespace The name space of the required capabilities to
	 *        return or {@code null} to return the required capabilities from
	 *        all name spaces.
	 * @return A list containing a snapshot of the {@link Capability}s
	 *         used by this bundle wiring, or an empty list if this bundle
	 *         wiring requires no capabilities in the specified name space. If
	 *         this bundle wiring is not {@link #isInUse() in use}, {@code null}
	 *         will be returned. The list contains the required capabilities in
	 *         the order they are specified in the manifest.
	 */
	List<Capability> getRequiredCapabilities(
			String capabilityNamespace);

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
	 * @return A list containing a snapshot of the {@link BundleInfo}s for all
	 *         attached fragments attached of this bundle wiring, or an empty
	 *         list if this bundle wiring does not have any attached fragments.
	 *         If this bundle wiring is not {@link #isInUse() in use},
	 *         {@code null} will be returned.
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

	/**
	 * Returns a URL to the entry at the specified path in this bundle wiring.
	 * This bundle wiring's class loader is not used to search for the entry.
	 * Only the contents of this bundle wiring are searched for the entry.
	 * <p>
	 * The specified path is always relative to the root of this bundle wiring
	 * and may begin with &quot;/&quot;. A path value of &quot;/&quot; indicates
	 * the root of this bundle wiring.
	 * <p>
	 * Note: Jar and zip files are not required to include directory entries.
	 * URLs to directory entries will not be returned if the bundle wiring
	 * contents do not contain directory entries.
	 * 
	 * @param path The path name of the entry.
	 * @return A URL to the entry, or {@code null} if no entry could be found,
	 *         if this bundle wiring is not {@link #isInUse() in use} or if the
	 *         caller does not have the appropriate
	 *         {@code AdminPermission[bundle,RESOURCE]} and the Java Runtime
	 *         Environment supports permissions.
	 * @see Bundle#getEntry(String)
	 */
	URL getEntry(String path);

	/**
	 * Returns a collection of all the paths ({@code String} objects) to entries
	 * within this bundle wiring whose longest sub-path matches the specified
	 * path. This bundle wiring's class loader is not used to search for
	 * entries. Only the contents of this bundle wiring are searched.
	 * <p>
	 * The specified path is always relative to the root of this bundle wiring
	 * and may begin with a &quot;/&quot;. A path value of &quot;/&quot;
	 * indicates the root of this bundle wiring.
	 * <p>
	 * Returned paths indicating subdirectory paths end with a &quot;/&quot;.
	 * The returned paths are all relative to the root of this bundle wiring and
	 * must not begin with &quot;/&quot;.
	 * <p>
	 * Note: Jar and zip files are not required to include directory entries.
	 * Paths to directory entries will not be returned if the bundle wiring
	 * contents do not contain directory entries.
	 * 
	 * @param path The path name for which to return entry paths.
	 * @return A collection of the entry paths ({@code String} objects) or
	 *         {@code null} if no entry could be found, if this bundle wiring is
	 *         not {@link #isInUse() in use} or if the caller does not have the
	 *         appropriate {@code AdminPermission[bundle,RESOURCE]} and the Java
	 *         Runtime Environment supports permissions.
	 * @see Bundle#getEntryPaths(String)
	 */
	Collection<String> getEntryPaths(String path);

	/**
	 * Returns entries in this bundle wiring and its attached fragments. This
	 * bundle wiring's class loader is not used to search for entries. Only the
	 * contents of this bundle wiring and its attached fragments are searched
	 * for the specified entries.
	 * 
	 * <p>
	 * This method takes into account that the &quot;contents&quot; of this
	 * bundle wiring can include attached fragments. This &quot;bundle
	 * space&quot; is not a namespace with unique members; the same entry name
	 * can be present multiple times. This method therefore returns a list of
	 * URL objects. These URLs can come from different JARs but have the same
	 * path name. This method can either return only entries in the specified
	 * path or recurse into subdirectories returning entries in the directory
	 * tree beginning at the specified path.
	 * 
	 * <p>
	 * Note: Jar and zip files are not required to include directory entries.
	 * URLs to directory entries will not be returned if the bundle contents do
	 * not contain directory entries.
	 * 
	 * @param path The path name in which to look. The path is always relative
	 *        to the root of this bundle wiring and may begin with
	 *        &quot;/&quot;. A path value of &quot;/&quot; indicates the root of
	 *        this bundle wiring.
	 * @param filePattern The file name pattern for selecting entries in the
	 *        specified path. The pattern is only matched against the last
	 *        element of the entry path. If the entry is a directory then the
	 *        trailing &quot;/&quot; is not used for pattern matching. Substring
	 *        matching is supported, as specified in the Filter specification,
	 *        using the wildcard character (&quot;*&quot;). If {@code null} is
	 *        specified, this is equivalent to &quot;*&quot; and matches all
	 *        files.
	 * @param recurse If {@code true}, recurse into subdirectories. Otherwise
	 *        only return entries from the specified path.
	 * @return A list of URL objects for each matching entry, or {@code null} if
	 *         an entry could not be found, if this bundle wiring is not
	 *         {@link #isInUse() in use} or if the caller does not have the
	 *         appropriate {@code AdminPermission[bundle,RESOURCE]}, and the
	 *         Java Runtime Environment supports permissions. The list is
	 *         ordered such that entries from this bundle are returned first
	 *         followed by the entries from attached fragments in ascending
	 *         bundle id order.
	 * @see Bundle#findEntries(String, String, boolean)
	 */
	List<URL> findEntries(String path, String filePattern,
			boolean recurse);

	/**
	 * Returns this bundle wiring's manifest headers and values. This method
	 * returns all the manifest headers and values from the main section of this
	 * bundle wiring's manifest file; that is, all lines prior to the first
	 * blank line.
	 * 
	 * <p>
	 * Manifest header names are case-insensitive. The methods of the returned
	 * map must operate on header names in a case-insensitive manner. This
	 * method will return the raw (unlocalized) manifest headers including any
	 * leading {@literal "%"}.
	 * 
	 * @return A map containing this bundle's Manifest headers and values. If
	 *         this bundle wiring is not {@link #isInUse() in use}, {@code null}
	 *         will be returned.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         {@code AdminPermission[bundle,METADATA]}, and the Java Runtime
	 *         Environment supports permissions.
	 * @see Bundle#getHeaders(String)
	 */
	Map<String, String> getHeaders();
}
