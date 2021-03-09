/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.dmt.spi;

/**
 * This interface can be optionally implemented by a {@code DataPlugin} or
 * {@code ExecPlugin} in order to get information about its absolute mount
 * points in the overall DMT.
 * <p>
 * This is especially interesting, if the plugin is mapped to the tree as part
 * of a list. In such a case the id for this particular data plugin is
 * determined by the DmtAdmin after the registration of the plugin and therefore
 * unknown to the plugin in advance.
 * <p>
 * This is not a service interface, the Data or Exec Plugin does not also have
 * to register this interface as a service, the Dmt Admin should use an
 * {@code instanceof} to detect that a Plugin is also a Mount Plugin.
 * 
 * @author $Id$
 * @since 2.0
 */
public interface MountPlugin {
	/**
	 * Provides the {@code MountPoint} describing the path where the plugin is
	 * mapped in the overall DMT. The given mountPoint is withdrawn with the
	 * {@link #mountPointRemoved(MountPoint)} method. Corresponding mount points
	 * must compare equal and have an appropriate hash code.
	 * 
	 * @param mountPoint the newly mapped mount point
	 */
	void mountPointAdded(MountPoint mountPoint);

	/**
	 * Informs the plugin that the provided {@code MountPoint} objects have been
	 * removed from the mapping. The given mountPoint is withdrawn method. Mount
	 * points must compare equal and have an appropriate hash code with the
	 * given Mount Point in {@link #mountPointAdded(MountPoint)}.
	 * <p>
	 * NOTE: attempts to invoke the {@code postEvent} method on the provided
	 * {@code MountPoint} must be ignored.
	 * 
	 * @param mountPoint The unmapped mount point array of {@code MountPoint}
	 *        objects that have been removed from the mapping
	 */
	void mountPointRemoved(MountPoint mountPoint);
}
