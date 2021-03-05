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

package org.osgi.dmt.residential;

import static org.osgi.dmt.ddf.Scope.SCOPE.A;
import org.osgi.dmt.ddf.AddableMAP;
import org.osgi.dmt.ddf.MAP;
import org.osgi.dmt.ddf.Mutable;
import org.osgi.dmt.ddf.Scope;

/**
 * The Framework node represents the information about the Framework itself.
 * 
 * The Framework node allows manipulation of the OSGi framework, start level,
 * framework life cycle, and bundle life cycle.
 * <p>
 * All modifications to a Framework object must occur in an atomic session. All
 * changes to the framework must occur during the commit.
 * <p>
 * The Framework node allows the manager to install (create a new child node in
 * {@link #Bundle() Bundle}), to uninstall change the state of the bundle (see
 * {@link Bundle#RequestedState()}), update the bundle (see {@link Bundle#URL()
 * URL} ), start/stop bundles, and update the framework. The implementation must
 * execute these actions in the following order during the commit of the
 * session:
 * <ol>
 * <li>Create a snapshot of the current installed bundles and their state.</li>
 * <li>stop all bundles that will be uinstalled and updated</li>
 * <li>Uninstall all the to be uninstalled bundles (bundles whose RequestedState
 * is {@link Bundle#UNINSTALLED})</li>
 * <li>Update all bundles that have a modified {@link Bundle#URL() URL} with
 * this URL using the Bundle {@code update(InputStream)} method in the order
 * that the order that the URLs were last set.</li>
 * <li>Install any new bundles from their {@link Bundle#URL() URL} in the order
 * that the order that the URLs were last set.</li>
 * <li>Refresh all bundles that were updated and installed</li>
 * <li>Ensure that all the bundles have their correct start level</li>
 * <li>If the {@link Bundle#RequestedState() RequestedState} was set, follow
 * this state. Otherwise ensure that any Bundles that have the
 * {@link Bundle#AutoStart() AutoStart} flag set to {@code true} are started
 * persistently. Transiently started bundles that were stopped in this process
 * are not restarted. The bundle id order must be used.</li>
 * <li>Wait until the desired start level has been reached</li>
 * <li>Return from the commit without error.</li>
 * </ol>
 * If any of the above steps runs in an error (except the restart) than the
 * actions should be undone and the system state must be restored to the
 * snapshot.
 * <p>
 * If the System Bundle was updated (its URL) node was modified, then after the
 * commit has returned successfully, the OSGi Framework must be restarted.
 */

public interface Framework {
	/**
	 * The StartLevel manages the Framework's current Start Level. Maps to the
	 * Framework Start Level {@code set/getStartLevel()} methods.
	 * <p>
	 * This node can set the requested Framework's StartLevel, however it
	 * doesn't store the value. This node returns the Framework's StartLevel at
	 * the moment of the call.
	 * 
	 * @return A Start Level node.
	 */
	@Scope(A)
	Mutable<Integer> StartLevel();

	/**
	 * Configures the initial bundle start level, maps to the the
	 * FrameworkStartLevel {@code set/getInitialBundleStartLevel()} method.
	 * 
	 * @return the Initial bundle start level node.
	 */
	@Scope(A)
	Mutable<Integer> InitialBundleStartLevel();

	/**
	 * The MAP of location -> Bundle. Each Bundle is uniquely identified by its
	 * location. The location is a string that must be unique for each bundle
	 * and can be chosen by the management system.
	 * <p>
	 * The Bundles node will be automatically filled from the installed bundles,
	 * representing the actual state.
	 * <p>
	 * New bundles can be installed by creating a new node with a given
	 * location. At commit, this bundle will be installed from their
	 * {@link Bundle#URL} node.
	 * <p>
	 * The location of the System Bundle must be "System Bundle" (see the Core's
	 * {@code Constants.SYSTEM_BUNDLE_LOCATION}), this node cannot be
	 * uninstalled and most operations on this node have special meaning.
	 * <p>
	 * It is strongly recommended to use a logical name for the location of a
	 * bundle, for example reverse domain names or a UUID.
	 * <p>
	 * To uninstall a bundle, set the {@link Bundle#RequestedState} to
	 * {@code UNINSTALLED}, the nodes in {@link #Bundle} cannot be deleted.
	 * 
	 * @return The Bundles node
	 */
	@Scope(A)
	AddableMAP<String, Bundle> Bundle();

	/**
	 * The Framework Properties.
	 * <p>
	 * The Framework properties come from the Bundle Context
	 * {@code getProperty()} method. However, this method does not provide the
	 * names of the available properties. If the handler of this node is aware
	 * of the framework properties then these should be used to provide the node
	 * names. If these properties are now known, the handler must synthesize the
	 * names from the following sources
	 * <ul>
	 * <li>System Properties (as they are backing the Framework properties)</li>
	 * <li>Launching properties as defined in the OSGi Core specification</li>
	 * <li>Properties in the residential specification</li>
	 * <li>Other known properties</li>
	 * </ul>
	 * <p>
	 * 
	 * @return The Framework's properties.
	 */
	@Scope(A)
	MAP<String, String> Property();

}
