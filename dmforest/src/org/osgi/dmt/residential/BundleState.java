package org.osgi.dmt.residential;

import static org.osgi.dmt.ddf.Scope.SCOPE.*;
import info.dmtree.*;

import java.util.*;

import org.osgi.dmt.ddf.*;

/**
 * The Bundle State details the state of an installed bundle.
 */

public interface BundleState {
	/**
	 * The type returned for a bundle that is not a fragment.
	 * 
	 * @remark why not bundle or so, a bit weird
	 */
	String UNDEFINED = "UNDEFINED";

	/**
	 * The type returned for a fragment bundle.
	 */
	String FRAGMENT = "FRAGMENT";

	/**
	 * The id Bundle as define by the {@code getBundleId()} method.
	 * 
	 * @remark As the path to this node already contains the id, why do we need
	 *         to provide it?
	 * 
	 * @return The bundle ID node
	 */
	@Scope(A)
	long ID();

	/**
	 * The Bundle Symbolic Name as defined by the Bundle
	 * {@code getSymbolicName()} method. If this result is {@code null} then the
	 * value of this node must be the empty string.
	 * 
	 * @return The SymbolicName node
	 */
	@Scope(A)
	String SymbolicName();

	/**
	 * The Bundle's version as defined by the Bundle {@code getVersion()}
	 * method. If this result is {@code null} then the value of this node must
	 * be the empty string.
	 * 
	 * @return The Version node
	 */
	@Scope(A)
	String Version();

	/**
	 * The type of the bundle, either:
	 * <ul>
	 * <li>{@link #UNDEFINED}</li>
	 * <li>{@link #FRAGMENT}</li> </li>
	 * 
	 * @return The BundleType node
	 */
	@Scope(A)
	String BundleType();

	/**
	 * The Manifest as string value. The node implementation must get the
	 * headers with the Bundle {@code getHeaders()} method and convert them to
	 * the JAR manifest format.
	 * 
	 * @remark Why not use the resource itself? If there is an error in there
	 *         you would not see it?
	 * @remark Or turn this into MAP&gt;String,String&lt;
	 * 
	 * @return The Bundle's manifest as a string
	 */
	@Scope(A)
	String Manifest();

	/**
	 * The Bundle's Location as defined by the Bundle {@code getLocation()}
	 * method.
	 * 
	 * @remark this should be the way to update ...
	 * @return The Bundle's location
	 */
	@Scope(A)
	String Location();

	/**
	 * A Status node that details the Bundle's status as defined by the Bundle
	 * {@code getState()} method.
	 * 
	 * 
	 * @remark Why do we go a level deeper here? Seems superfluous?
	 * 
	 * @return The value of the Status node
	 */
	@Scope(A)
	Status Status();

	/**
	 * The Status node. This node gives status information of the corresponding
	 * bundle.
	 */
	interface Status {

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
		 * Return the state of the current Bundle. The values can be:
		 * 
		 * <ul>
		 * <li>{@link #INSTALLED}</li>
		 * <li>{@link #RESOLVED}</li>
		 * <li>{@link #STARTING}</li>
		 * <li>{@link #ACTIVE}</li>
		 * <li>{@link #STOPPING}</li>
		 * </ul>
		 * These previous values map to the corresponding states defined in
		 * Bundle that are used in the Bundle {@code getState()} method. There
		 * is no corresponding {@code UNINSTALLED} state as this state cannot be
		 * seen remotely.
		 * 
		 * @return The current State
		 */
		@Scope(A)
		String State();

		/**
		 * The Bundle's current Start Level as defined by the BundleStartLevel
		 * adapt interface {@code getStartLevel()} method.
		 * 
		 * @return The Bundle current Start Level node value
		 */
		@Scope(A)
		int StartLevel();

		/**
		 * The Persistently Started flag of this Bundle. If {@code true} then
		 * this Bundle was persistently started, otherwise {@code false}. See
		 * the BundleStartLevel adapt interface {@code getPersistentlyStarted()}
		 * method.
		 * 
		 * @return The PersistentlyStarted node.
		 */
		@Scope(A)
		boolean PersistentlyStarted();

		/**
		 * The Activation Policy Used flag. This flag is {@code true} if this
		 * Bundle was started with activation policy used. See the
		 * BundleStartLevel adapt interface {@code getActivationPolicyUsed()}
		 * method.
		 * 
		 * @return The value of the ActivationPolicyUsed node.
		 */
		@Scope(A)
		boolean ActivationPolicyUsed();

		/**
		 * The Last Modified time of this bundle as defined by the Bundle
		 * getLastModified() method. See the Bundle {@code getlastModified()}
		 * method.
		 * 
		 * @return The value of the LastModified node.
		 */
		@Scope(A)
		Date LastModified();
	}

	/**
	 * A LIST of Bundle Ids that this fragment is hosted by.
	 * 
	 * @return The Hosts node value
	 */
	@Scope(A)
	@NodeType(DmtConstants.DDF_LIST_SUBTREE)
	LIST<Long> Hosts();

	/**
	 * A LIST of fragment Ids Bundles that this bundle is hosting.
	 * 
	 * @return The Fragments node value
	 */
	@Scope(A)
	@NodeType(DmtConstants.DDF_LIST_SUBTREE)
	LIST<Long> Fragments();

	/**
	 * A LIST of Bundle Ids that have a Require-Bundle header for this bundle.
	 * 
	 * @remark to have the common s for plural
	 * @return The Required node
	 */
	@Scope(A)
	@NodeType(DmtConstants.DDF_LIST_SUBTREE)
	LIST<Long> Requireds();

	/**
	 * A LIST of IDs of Bundles that are in this Bundle's Require-Bundle header.
	 * 
	 * @remark to have the common s for plural
	 * @return The Requiring node value
	 */
	@Scope(A)
	@NodeType(DmtConstants.DDF_LIST_SUBTREE)
	LIST<Long> Requirings();

	/**
	 * A LIST of Trusted Signer Certificates. This is the trusted X.509
	 * Certificate Chain for this Bundle as can be obtained with the Bundle
	 * {@code getSignerCertificates(SIGNERS_TRUSTED)}.
	 * 
	 * 
	 * @remark Is this node optional or not? Not clear from the descr.
	 * @remark made name shorter
	 * 
	 * @return The Multiple for TrustedSignerCertificate node
	 */
	@Scope(A)
	@NodeType(DmtConstants.DDF_LIST_SUBTREE)
	LIST<Certificate> TrustedSigners();

	/**
	 * A LIST of Non Trusted Signer Certificates. This is the ALL X.509
	 * Certificate Chain minus the Trusted chain for this Bundle. It can be
	 * obtained with the Bundle {@code getSignerCertificates(SIGNERS_ALL)}
	 * method and then remove the certificates from the Bundle
	 * {@code getSignerCertificates(SIGNERS_ALL)} method.
	 * 
	 * @remark Why not map 1:1 to the API? I.e. All/trusted instead of
	 *         trusted/untrusted
	 * @remark made name shorter
	 * @return The Multiple for NonTrustedSignerCertificate nodes
	 */
	@Scope(A)
	@NodeType(DmtConstants.DDF_LIST_SUBTREE)
	LIST<Certificate> NonTrustedSigners();

	/**
	 * Place holder for the Signers DN names.
	 */
	interface Certificate {
		/**
		 * A list of signer DNs of the certificates in the chain.
		 * 
		 * @remark Should this not be a LIST?
		 * 
		 * @return List of DNs
		 */
		@Scope(A)
		MAP<Long, String> CertificateChain();
	}

	/**
	 * Extension node.
	 * 
	 * @return The extension node
	 */
	@Scope(A)
	Opt<NODE> Ext();

}
