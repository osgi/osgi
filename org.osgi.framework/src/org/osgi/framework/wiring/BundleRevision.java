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

import java.util.Collection;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;
import org.osgi.framework.Version;

/**
 * Bundle Revision. When a bundle is installed and each time a bundle is
 * updated, a new bundle revision of the bundle is created. Since a bundle
 * update can change the entries in a bundle, different bundle wirings for the
 * same bundle can be associated with different bundle revisions.
 * 
 * <p>
 * The current bundle revision is the most recent bundle revision. The current
 * bundle revision for a bundle can be obtained by calling
 * {@link Bundle#adapt(Class) bundle.adapt}(BundleRevision.class).
 * 
 * @ThreadSafe
 * @noimplement
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

	/**
	 * Returns the capabilities declared by this bundle revision.
	 * 
	 * @param namespace The name space of the declared capabilities to return or
	 *        {@code null} to return the declared capabilities from all name
	 *        spaces.
	 * @return A list containing a snapshot of the declared {@link Capability}s,
	 *         or an empty list if this bundle revision declares no capabilities
	 *         in the specified name space. The list contains the declared
	 *         capabilities in the order they are specified in the manifest.
	 */
	List<Capability> getDeclaredCapabilities(String namespace);

	/**
	 * Returns the requirements declared by this bundle revision.
	 * 
	 * @param namespace The name space of the declared requirements to return or
	 *        {@code null} to return the declared requirements from all name
	 *        spaces.
	 * @return A list containing a snapshot of the declared {@link Requirement}
	 *         s, or an empty list if this bundle revision declares no
	 *         requirements in the specified name space. The list contains the
	 *         declared requirements in the order they are specified in the
	 *         manifest.
	 * @since 1.1
	 */
	List<Requirement> getDeclaredRequirements(String namespace);

	/**
	 * Returns the special types of this bundle revision. The bundle revision
	 * type values are:
	 * <ul>
	 * <li>{@link #TYPE_FRAGMENT}
	 * </ul>
	 * 
	 * A bundle revision may be more than one type at a time. A type code is
	 * used to identify the bundle revision type for future extendability.
	 * 
	 * <p>
	 * If this bundle revision is not one or more of the defined types then 0 is
	 * returned.
	 * 
	 * @return The special types of this bundle revision. The type values are
	 *         ORed together.
	 */
	int getTypes();

	/**
	 * Bundle revision type indicating the bundle revision is a fragment.
	 * 
	 * @see #getTypes()
	 */
	int	TYPE_FRAGMENT	= 0x00000001;

	/**
	 * Returns the bundle wiring which is using this bundle revision.
	 * 
	 * @return The bundle wiring which is using this bundle revision or
	 *         {@code null} if this bundle revision is a {@link #TYPE_FRAGMENT
	 *         fragment} or no bundle wiring is using this bundle revision.
	 * @see BundleWiring#getBundleRevision()
	 */
	BundleWiring getBundleWiring();

	/**
	 * Returns the bundle wirings to which this fragment revision is attached.
	 * 
	 * @return The bundle wirings to which this fragment revision is attached.
	 *         The returned collection will be empty if this bundle revision is
	 *         not a {@link #TYPE_FRAGMENT fragment} or this fragment revision
	 *         is not attached to any bundle wirings.
	 * @see BundleWiring#getFragmentRevisions()
	 */
	Collection<BundleWiring> getHostWirings();
}
