package org.osgi.dmt.residential;

import static org.osgi.dmt.ddf.Scope.SCOPE.*;

import java.util.*;

import org.osgi.dmt.ddf.*;

/**
 * The Bundle node type. This is the management node for a Bundle. It provides
 * access to the life cycle control of the bundle as well to its metadata,
 * resources, and wiring.
 * 
 */
public interface Bundle {
	/**
	 * The type returned for a fragment bundle.
	 */
	String FRAGMENT = "FRAGMENT";

	/**
	 * The URL to download the archive from for this bundle.
	 * 
	 * By default this is the last URL used to download the JAR if it is known,
	 * otherwise it is the empty string. In an atomic session this URL can be
	 * replaced to a new URL, which will trigger an update of this bundle during
	 * commit. If this value is set it must point to a valid JAR from which a
	 * URL can be downloaded, unless it is the system bundle. If the value is
	 * not set it must be an empty string.
	 * 
	 * <p>
	 * If the URL of Bundle 0 (The system bundle) is replaced to any value then
	 * the framework will restart.
	 * 
	 * @return The last used URL or empty string if not known
	 */
	@Scope(A)
	Mutable<String> URL();

	/**
	 * Indicates if this Bundle must be started when the Framework is started.
	 * <p>
	 * If the AutoStart node is {@code true} then this bundle is started when
	 * the framework is started and its {@link #StartLevel()} is met.
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
	 * {@link #FaultMessage()} provides a human readable message.
	 * 
	 * @return The FaultType value
	 */
	@Scope(A)
	int FaultType();

	/**
	 * A human readable message detailing an error situation or an empty string
	 * if no fault is associated with this bundle.
	 * 
	 * @return The FaultMessage node
	 */
	@Scope(A)
	String FaultMessage();

	/**
	 * The Bundle Id as defined by the {@code getBundleId()} method.
	 * 
	 * If there is no installed Bundle yet, then this node is not present.
	 * @return The BundleId node
	 */
	@Scope(A)
	Opt<Long> BundleId();

	/**
	 * The Bundle Symbolic Name as defined by the Bundle
	 * {@code getSymbolicName()} method. If this result is {@code null} then the
	 * value of this node must be the empty string.
	 * 
	 * @return The SymbolicName node
	 */
	@Scope(A)
	Opt<String> SymbolicName();

	/**
	 * The Bundle's version as defined by the Bundle {@code getVersion()}
	 * method.
	 * 
	 * If there is no installed Bundle yet, then this node is not present.
	 * 
	 * @return The Version node
	 */
	@Scope(A)
	Opt<String> Version();

	/**
	 * A list of the types of the bundle. Currently on a single type is
	 * provided:
	 * <ul>
	 * <li>{@link #FRAGMENT}</li>
	 * </ul>
	 * 
	 * If there is no installed Bundle yet, then this node is not present.
	 * 
	 * @return The BundleType node
	 */
	@Scope(A)
	Opt<LIST<String>> BundleType();

	/**
	 * The Bundle {@code getHeaders()} method.
	 * 
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
	String INSTALLED = "INSTALLED";
	/**
	 * The Bundle {@code RESOLVED} state.
	 */
	String RESOLVED = "RESOLVED";
	/**
	 * The Bundle {@code STARTING} state.
	 */
	String STARTING = "STARTING";
	/**
	 * The Bundle {@code ACTIVE} state.
	 */
	String ACTIVE = "ACTIVE";
	/**
	 * The Bundle {@code STOPPING} state.
	 */
	String STOPPING = "STOPPING";

	/**
	 * The Bundle {@code UNINSTALLED} state.
	 */
	String UNINSTALLED = "UNINSTALLED";

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
	 * If there is no installed Bundle yet, then this node is not present.
	 * 
	 * @return The current State
	 */
	@Scope(A)
	Opt<String> State();

	/**
	 * Is the requested state the manager wants the bundle to be in. Can be:
	 * <ul>
	 * <li>{@link #INSTALLED} - Ensure the bundle is stopped. The actual state
	 * can be {@link #INSTALLED} or {@link #RESOLVED}</li>
	 * <li>{@link #RESOLVED} - Ensure the bundle is stopped but attempt to
	 * resolve. The actual state can be {@link #INSTALLED} and {@link #RESOLVED}
	 * </li>
	 * <li>{@link #ACTIVE} - Ensure the bundle is started. The actual state can
	 * be {@link #INSTALLED}, {@link #RESOLVED}, {@link #STARTING} and
	 * {@link #ACTIVE} depending on start level and faults. The Bundle can
	 * transition to another state if the environment changes, for example, a
	 * new higher start level can activate a bundle.</li>
	 * <li>{@link #UNINSTALLED} - Uninstall the bundle</li>
	 * </ul> {@link #STARTING} and {@link #STOPPING} are invalid values for this
	 * node. Any other values are an error.
	 * <p>
	 * 
	 * If the {@link #AutoStart()} node is @{code true} then the bundle must be
	 * persistently started, otherwise it must be transiently started. If the
	 * {@link #StartLevel()} is not met then the commit must fail if
	 * {@link #AutoStart()} is {@code false} as a Bundle cannot be transiently
	 * started when the start level is not met.
	 * 
	 * <p>
	 * The default value of this node is the current state.
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
	 * 
	 * @return The Bundle Start Level node value
	 */
	@Scope(A)
	Mutable<Integer> StartLevel();

	/**
	 * The Last Modified time of this bundle as defined by the Bundle
	 * getLastModified() method. See the Bundle {@code getlastModified()}
	 * method.
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
	 * <ul>
	 * As the Core specification allows custom name spaces this list can be more
	 * extensive.
	 * <p>
	 * This specification adds one additional name space to reflect the
	 * services, this is the {@code osgi.wiring.service} name space. This name
	 * space will have a wire for each time a registered service by this Bundle
	 * was gotten for the first time by a bundle. A capability in the service
	 * name space holds all the registered service properties. The requirement
	 * has no attributes and a single {@code filter} directive that matches the
	 * service id property.
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
