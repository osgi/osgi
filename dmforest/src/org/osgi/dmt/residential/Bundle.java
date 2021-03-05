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
import java.util.Date;
import org.osgi.dmt.ddf.LIST;
import org.osgi.dmt.ddf.MAP;
import org.osgi.dmt.ddf.Mutable;
import org.osgi.dmt.ddf.Opt;
import org.osgi.dmt.ddf.Scope;

/**
 * The management node for a Bundle. It provides access to the life cycle
 * control of the bundle as well to its metadata, resources, and wiring.
 * <p/>
 * To install a new bundle an instance of this node must be created. Since many
 * of the sub-nodes are not yet valid as the information from the bundle is not
 * yet available. These nodes are marked to be optional and will only exists
 * after the bundle has been really installed.
 * 
 */
public interface Bundle {
	/**
	 * The type returned for a fragment bundle.
	 */
	String	FRAGMENT	= "FRAGMENT";

	/**
	 * The URL to download the archive from for this bundle.
	 * 
	 * By default this is the empty string. In an atomic session this URL can be
	 * replaced to a new URL, which will trigger an update of this bundle during
	 * commit. If this value is set it must point to a valid JAR from which a
	 * URL can be downloaded, unless it is the system bundle. If it is the empty
	 * string no action must be taken except when it is the system bundle.
	 * 
	 * <p>
	 * If the URL of Bundle 0 (The system bundle) is replaced to any value,
	 * including the empty string, then the framework will restart.
	 * <p>
	 * If both a the URL node has been set the bundle must be updated before any
	 * of the other aspects are handled like {@link #RequestedState()
	 * RequestedState} and {@link #StartLevel() StartLevel}.
	 * 
	 * @return The last used URL or empty string if not known
	 */
	@Scope(A)
	Mutable<String> URL();

	/**
	 * Indicates if this Bundle must be started when the Framework is started.
	 * <p>
	 * If the AutoStart node is {@code true} then this bundle is started when
	 * the framework is started and its {@link #StartLevel() StartLevel} is met.
	 * <p>
	 * If the {@code AutoStart} node is set to {@code true} and the bundle is
	 * not started then it will automatically be started if the start level
	 * permits it. If the {@code AutoStart} node is set to {@code false} then
	 * the bundle must not be stopped immediately.
	 * <p>
	 * If the {@code AutoStart} value of the System Bundle is changed then the
	 * operation must be ignored.
	 * <p>
	 * The default value for this node is {@code true}
	 * 
	 * @return The AutoStart value
	 */
	@Scope(A)
	Mutable<Boolean> AutoStart();

	/**
	 * The BundleException type associated with a failure on this bundle, -1 if
	 * no fault is associated with this bundle. If there was no Bundle Exception
	 * associated with the failure the code must be 0 (UNSPECIFIED). The
	 * {@link #FaultMessage() FaultMessage} provides a human readable message.
	 * <p/>
	 * Only present after the bundle is installed.
	 * 
	 * @return The FaultType value
	 */
	@Scope(A)
	Opt<Integer> FaultType();

	/**
	 * A human readable message detailing an error situation or an empty string
	 * if no fault is associated with this bundle.
	 * <p/>
	 * Only present after the bundle is installed.
	 * 
	 * 
	 * @return The FaultMessage node
	 */
	@Scope(A)
	Opt<String> FaultMessage();

	/**
	 * The Bundle Id as defined by the {@code getBundleId()} method.
	 * <p>
	 * If there is no installed Bundle yet, then this node is not present.
	 * 
	 * @return The BundleId node
	 */
	@Scope(A)
	Opt<Long> BundleId();

	/**
	 * The Bundle Symbolic Name as defined by the Bundle
	 * {@code getSymbolicName()} method. If this result is {@code null} then the
	 * value of this node must be the empty string.
	 * <p>
	 * If there is no installed Bundle yet, then this node is not present.
	 * 
	 * @return The SymbolicName node
	 */
	@Scope(A)
	Opt<String> SymbolicName();

	/**
	 * The Bundle's version as defined by the Bundle {@code getVersion()}
	 * method.
	 * 
	 * <p>
	 * If there is no installed Bundle yet, then this node is not present.
	 * 
	 * @return The Version node
	 */
	@Scope(A)
	Opt<String> Version();

	/**
	 * A list of the types of the bundle. Currently only a single type is
	 * provided:
	 * <ul>
	 * <li>{@link #FRAGMENT}</li>
	 * </ul>
	 * <p>
	 * If there is no installed Bundle yet, then this node is not present.
	 * 
	 * @return The BundleType node
	 */
	@Scope(A)
	Opt<LIST<String>> BundleType();

	/**
	 * The Bundle {@code getHeaders()} method.
	 * <p>
	 * If there is no installed Bundle yet, then this node is not present.
	 * 
	 * @return The Bundle's manifest headers
	 */
	@Scope(A)
	Opt<MAP<String, String>> Headers();

	/**
	 * The Bundle's Location as defined by the Bundle {@code getLocation()}
	 * method.
	 * <p>
	 * The location is specified by the management agent when the bundle is
	 * installed. This location should be a unique name for a bundle chosen by
	 * the management system. The Bundle Location is immutable for the Bundle's
	 * life (it is not changed when the Bundle is updated). The Bundle Location
	 * is also part of the URI to this node.
	 * 
	 * @return The Bundle's location
	 */
	@Scope(A)
	String Location();

	/**
	 * The Bundle {@code INSTALLED} state.
	 */
	String	INSTALLED	= "INSTALLED";
	/**
	 * The Bundle {@code RESOLVED} state.
	 */
	String	RESOLVED	= "RESOLVED";
	/**
	 * The Bundle {@code STARTING} state.
	 */
	String	STARTING	= "STARTING";
	/**
	 * The Bundle {@code ACTIVE} state.
	 */
	String	ACTIVE		= "ACTIVE";
	/**
	 * The Bundle {@code STOPPING} state.
	 */
	String	STOPPING	= "STOPPING";

	/**
	 * The Bundle {@code UNINSTALLED} state.
	 */
	String	UNINSTALLED	= "UNINSTALLED";

	/**
	 * Return the state of the current Bundle. The values can be:
	 * 
	 * <ul>
	 * <li>{@link #INSTALLED}</li>
	 * <li>{@link #RESOLVED}</li>
	 * <li>{@link #STARTING}</li>
	 * <li>{@link #ACTIVE}</li>
	 * <li>{@link #STOPPING}</li>
	 * </ul>
	 * <p/>
	 * If there is no installed Bundle yet, then this node is not present.
	 * <p/>
	 * The default value is {@link #UNINSTALLED} after creation.
	 * 
	 * 
	 * @return The current State
	 */
	@Scope(A)
	Opt<String> State();

	/**
	 * Is the requested state the manager wants the bundle to be in. Can be:
	 * <p>
	 * <ul>
	 * <li>{@link #INSTALLED} - Ensure the bundle is stopped and refreshed.</li>
	 * <li>{@link #RESOLVED} - Ensure the bundle is resolved.</li>
	 * <li>{@link #ACTIVE} - Ensure the bundle is started.</li>
	 * <li>{@link #UNINSTALLED} - Uninstall the bundle.</li>
	 * </ul>
	 * <p>
	 * The Requested State is a request. The management agent must attempt to
	 * achieve the desired state but there is a no guarantee that this state is
	 * achievable. For example,a Framework can resolve a bundle at any time or
	 * the active start level can prevent a bundle from running. Any errors must
	 * be reported on {@link #FaultType() FaultType} and {@link #FaultMessage()
	 * FaultMessage}.
	 * <p>
	 * 
	 * If the {@link #AutoStart() AutoStart} node is {@code true} then the
	 * bundle must be persistently started, otherwise it must be transiently
	 * started. If the {@link #StartLevel() StartLevel} is not met then the
	 * commit must fail if {@link #AutoStart() AutoStart} is {@code false} as a
	 * Bundle cannot be transiently started when the start level is not met.
	 * <p>
	 * If both a the {@link #URL() URL} node has been set as well as the
	 * RequestedState node then this must result in an update after which the
	 * bundle should go to the RequestedState.
	 * <p>
	 * The RequestedState must be stored persistently so that it contains the
	 * last requested state. The initial value of the RequestedState must be
	 * {@link #INSTALLED}.
	 * 
	 * @return The RequestedState node.
	 */
	@Scope(A)
	Mutable<String> RequestedState();

	/**
	 * The Bundle's current Start Level as defined by the BundleStartLevel adapt
	 * interface {@code getStartLevel()} method. Changing the StartLevel can
	 * change the Bundle State as a bundle can become eligible for starting or
	 * stopping.
	 * <p>
	 * If the {@link #URL() URL} node is set then a bundle must be updated
	 * before the start level is set,
	 * 
	 * @return The Bundle Start Level node value
	 */
	@Scope(A)
	Mutable<Integer> StartLevel();

	/**
	 * The Last Modified time of this bundle as defined by the Bundle
	 * {@code getlastModified()} method.
	 * <p>
	 * If there is no installed Bundle yet then this node is not present.
	 * 
	 * @return The value of the LastModified node.
	 */
	@Scope(A)
	Opt<Date> LastModified();

	/**
	 * A MAP of name space -> to Wire. A Wire is a relation between to bundles
	 * where the type of the relation is defined by the name space. For example,
	 * {@code osgi.wiring.package} name space defines the exporting and
	 * importing of packages. Standard osgi name spaces are:
	 * <ul>
	 * <li>{@code osgi.wiring.bundle}</li>
	 * <li>{@code osgi.wiring.package}</li>
	 * <li>{@code osgi.wiring.host}</li>
	 * </ul>
	 * <p>
	 * As the Core specification allows custom name spaces this list can be more
	 * extensive.
	 * <p>
	 * This specification adds one additional name space to reflect the
	 * services, this is the {@code osgi.wiring.rmt.service} name space. This
	 * name space will have a wire for each time a registered service by this
	 * Bundle was gotten for the first time by a bundle. A capability in the
	 * service name space holds all the registered service properties. The
	 * requirement has no attributes and a single {@code filter} directive that
	 * matches the service id property.
	 * <p>
	 * If there is no installed Bundle yet then this node is not present.
	 * 
	 * @return The Wires node.
	 */
	@Scope(A)
	Opt<MAP<String, LIST<Wire>>> Wires();

	/**
	 * Return all signers of the bundle. See the Bundle
	 * {@code getSignerCertificates()} method with the {@code SIGNERS_ALL}
	 * parameter.
	 * <p>
	 * If there is no installed Bundle yet then this node is not present.
	 * 
	 * @return All signers of the bundle
	 */
	@Scope(A)
	Opt<LIST<Certificate>> Signers();

	/**
	 * An optional node providing access to the entries in the Bundle's JAR.
	 * This list must be created from the Bundle {@code getEntryPaths()} method
	 * called with an empty String. For each found entry, an Entry object must
	 * be made available.
	 * <p>
	 * If there is no installed Bundle yet then this node is not present.
	 * 
	 * @return The Entries node
	 */
	@Scope(A)
	Opt<LIST<Entry>> Entries();

	/**
	 * Instance Id used by foreign protocol adapters as a unique integer key not
	 * equal to 0.
	 * 
	 * The instance id for a bundle must be (Bundle Id % 2^32) + 1.
	 * 
	 * @return The instance id.
	 */
	@Scope(A)
	int InstanceId();

	/**
	 * An Entry describes an entry in the Bundle, it combines the path of an
	 * entry with the content. Only entries that have content will be returned,
	 * that is, empty directories in the Bundle's archive are not returned.
	 */
	public interface Entry {
		/**
		 * The path in the Bundle archive to the entry.
		 * 
		 * @return The path to the entry in the archive.
		 */
		@Scope(A)
		String Path();

		/**
		 * The binary content of the entry.
		 * 
		 * @return The binary content.
		 */
		@Scope(A)
		byte[] Content();

		/**
		 * Instance Id to allow addressing by Instance Id.
		 * 
		 * @return The InstanceId
		 */

		@Scope(A)
		int InstanceId();
	}

	/**
	 * Place holder for the Signers DN names.
	 */
	interface Certificate {
		/**
		 * Return if this Certificate is trusted.
		 * 
		 * @return If this is a trusted certificate.
		 */
		@Scope(A)
		boolean IsTrusted();

		/**
		 * A list of signer DNs of the certificates in the chain.
		 * 
		 * @return List of DNs
		 */
		@Scope(A)
		LIST<String> CertificateChain();

		/**
		 * Instance Id to allow addressing by Instance Id.
		 * 
		 * @return The InstanceId
		 */

		@Scope(A)
		int InstanceId();
	}

}
