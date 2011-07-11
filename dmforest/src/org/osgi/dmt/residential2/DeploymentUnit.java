package org.osgi.dmt.residential2;

import java.net.*;

import org.osgi.dmt.ddf.*;

/**
 * This Node Type serves as the Deployment Unit inventory and contains status
 * information about each Deployment Unit. A new instance of this table gets
 * created during the installation of a Software Module.
 * <p/>
 * A Bundle maps to both a Deployment Unit as well as an Execution Unit.
 */
public interface DeploymentUnit {

	/**
	 * A Universally Unique Identifier either provided by the ACS, or generated
	 * by the CPE, at the time of Deployment Unit Installation. The format of
	 * this value is defined by <a
	 * href="http://www.ietf.org/rfc/rfc4122.txt">RFC 4122</a> Version 3
	 * (Name-Based) and TR-069 Amendment 3 Annex H. This value MUST NOT be
	 * altered when the DeploymentUnit is updated.
	 * <p/>
	 * The UUID is mapped to the OSGi Bundle Location
	 * <p>
	 * Maximum length of the String is 36
	 * 
	 * @return The UUID
	 */
	String UUID();

	/**
	 * Deployment Unit Identifier chosen by the targeted Execution Environment.
	 * The format of this value is Execution Environment specific.
	 * <p/>
	 * The DUID is the Bundle Id.
	 * 
	 * <p>
	 * Maximum length of the String is 64
	 * 
	 * @return The Device DUID
	 */
	String DUID();

	/**
	 * A non-volatile handle used to reference this instance. The Alias provides
	 * a mechanism for an ACS to label this instance for future reference. An
	 * initial unique value MUST be assigned when the CPE creates an instance of
	 * this Deployment Unit.
	 * <p>
	 * This name is given as a parameter in the installation of a bundle and
	 * changed by the management system.
	 * <p/>
	 * Maximum length of the String is 64
	 * 
	 * @return The Alias
	 */
	Mutable<String> Alias();

	/**
	 * Indicates the Name of this DeploymentUnit, which is chosen by the author
	 * of the Deployment Unit. The value of this parameter is used in the
	 * generation of the {@link #UUID()} based on the rules defined in TR-069
	 * Amendment 3.
	 * <p/>
	 * Maps to Bundle Symbolic Name
	 * <p/>
	 * Maximum length of the String is 64
	 * 
	 * @return The Name of the Deployment Unit
	 * 
	 */
	String Name();

	/**
	 * This instance is in the process of being Installed and SHOULD transition
	 * to the Installed state.
	 * <p/>
	 * In the OSGi a management agent must use this state to reflect an
	 * {@code INSTALLED} bundle that has not yet been downloaded.
	 */
	String INSTALLING = "Installing";

	/**
	 * This instance has been successfully Installed. The {@link #Resolved()}
	 * flag SHOULD also be referenced for dependency resolution.
	 * <p/>
	 * This maps to the Bundle INSTALLED state and RESOLVED state (if the
	 * {@link #Resolved()} flag is true).
	 */
	String INSTALLED = "Installed";

	/**
	 * This instance is in the process of being Updated and SHOULD transition to
	 * the {@link #INSTALLED} state.
	 * <p/>
	 * A management agent must report this state when a bundle is being updated.
	 * This state must bracket the Bundle {@code update()} operation.
	 */
	String UPDATING = "Updating";

	/**
	 * This instance is in the process of being Uninstalled and SHOULD
	 * transition to the {@link #UNINSTALLED} state.
	 * <p/>
	 * This state is likely not used in OSGi.
	 */
	String UNINSTALLING = "Uninstalling";

	/**
	 * This instance has been successfully {@link #UNINSTALLED}. This status
	 * will typically not be seen within a DeploymentUnit.
	 * <p/>
	 * This maps to the Bundle {@code UNINSTALLED} state.
	 */
	String UNINSTALLED = "Uninstalled";

	/**
	 * Indicates the status of this DeploymentUnit. Possible values
	 * <ul>
	 * <li>{@link #INSTALLING} - Before it is completely installed</li>
	 * <li>{@link #INSTALLED} - Maps to Bundle {@code INSTALLED} state and
	 * Bundle {@code RESOLVED} state iff the {@link #Resolved()} is true</li>
	 * <li>{@link #UPDATING}</li>
	 * <li>{@link #UNINSTALLING}</li>
	 * <li>{@link #UNINSTALLED}</li>
	 * </ul>
	 * 
	 * @return The Status of the current Deployment Unit
	 */

	String Status();

	/**
	 * Indicates whether or not this DeploymentUnit has resolved all of its
	 * dependencies.
	 * <p/>
	 * If the Bundle is in the {@code RESOLVED}, {@code STARTING},
	 * {@code ACTIVE}, or {@code STOPPING} state then this flag is {@code true}.
	 * 
	 * @return the Resolved state
	 */
	boolean Resolved();

	/**
	 * Contains the URL used by the most recent Install or Update of this
	 * DeploymentUnit.
	 * <p/>
	 * The OSGi management agent must track this URL separately as it is not
	 * possible to use the Bundle {@code getLocation()}; the location of the
	 * bundle can only be set once.
	 * 
	 * @return The last used URL
	 */
	Mutable<String> URL();

	/**
	 * Textual description of this DeploymentUnit. The format of this value is
	 * Execution Environment specific.
	 * <p/>
	 * The contents of the Bundle-Description manifest header.
	 * 
	 * @return The contents of the Bundle Description
	 */
	String Description();

	/**
	 * The author of this DeploymentUnit formatted as a domain name. The value
	 * of this parameter is used in the generation of the {@link #UUID()} based
	 * on the rules defined in TR-069 Amendment 3.
	 * <p/>
	 * The value of the Bundle-Vendor header
	 * 
	 * @remark Not sure this is directly mappable because we can have much wider
	 *         content
	 * @return The Vendor id
	 */
	String Vendor();

	/**
	 * Version of this DeploymentUnit. The format of this value is Execution
	 * Environment specific.
	 * <p/>
	 * The Bundle-Version value or 0.0.0 if not set.
	 * 
	 * @return The Versions
	 */
	String Version();

	/**
	 * Represents the vendor log files that have come into existence because of
	 * this DeploymentUnit. This does not include any vendor log files that have
	 * come into existence because of ExecutionUnit instances that are contained
	 * within this DeploymentUnit. When this DeploymentUnit is uninstalled the
	 * vendor log files referenced here SHOULD be removed from the CPE. Not all
	 * DeploymentUnit instances will actually have a corresponding vendor log
	 * file, in which case the value of this parameter will be empty.
	 * <p>
	 * Not supported in OSGi
	 * 
	 * @remark Support Log service data about this bundle?
	 * @return Empty list
	 */
	LIST<String> VendorLogList();

	/**
	 * Represents the vendor config files that have come into existence because
	 * of this DeploymentUnit. This does not include any vendor config files
	 * that have come into existence because of {{object|#.ExecutionUnit}}
	 * instances that are contained within this DeploymentUnit. When this
	 * DeploymentUnit is uninstalled the vendor config files referenced here
	 * SHOULD be removed from the CPE. Not all DeploymentUnit instances will
	 * actually have a corresponding vendor config file, in which case the value
	 * of this parameter will be {{empty}}.
	 * <p>
	 * Not supported in OSGi
	 * 
	 * @return Empty list
	 */
	LIST<String> VendorConfigList();

	/**
	 * Represents the ExecutionUnit instances that are associated
	 * with this DeploymentUnit instance.
	 * <p/>
	 * As Bundles are mapped to Deployment Unit and Execution Unit this
	 * will always return a list with one element.
	 *  
	 * @return A list of 1 element mapping to the corresponding Execution Unit 
	 */
	LIST<URI> ExecutionUnitList();

	/**
	 * Represents the {{object|#.ExecEnv}} instance where this DeploymentUnit
	 * instance is installed.
	 * <p/>
	 * 
	 * @return The URI to the Execution Environment
	 */
	URI ExecutionEnvRef();

}
