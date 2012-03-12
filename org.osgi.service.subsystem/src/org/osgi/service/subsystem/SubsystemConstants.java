/*
 * Copyright (c) OSGi Alliance (2011, 2012). All Rights Reserved.
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

package org.osgi.service.subsystem;


/**
 * Defines the constants used by subsystems.
 * 
 * @Immutable
 * @version $Id$
 */
public class SubsystemConstants {
	private SubsystemConstants() {
		// non-instantiable
	}

	/**
	 * Manifest header identifying the resources to be deployed.
	 */
	public static final String	DEPLOYED_CONTENT						= "Deployed-Content";

	/**
	 * Manifest header attribute identifying the deployed version.
	 */
	public static final String	DEPLOYED_VERSION_ATTRIBUTE				= "deployed-version";

	/**
	 * Manifest header identifying the deployment manifest version. If not
	 * present, the default value is {@code 1}.
	 */
	public static final String	DEPLOYMENT_MANIFESTVERSION				= "Deployment-ManifestVersion";

	/**
	 * Manifest header used to express a preference for particular resources to
	 * satisfy implicit package dependencies.
	 */
	public static final String	PREFERRED_PROVIDER						= "Preferred-Provider";

	/**
	 * Manifest header directive identifying the provision policy. The default
	 * value is {@link #PROVISION_POLICY_REJECT_DEPENDENCIES rejectDependencies}
	 * 
	 * @see #PROVISION_POLICY_ACCEPT_DEPENDENCIES
	 * @see #PROVISION_POLICY_REJECT_DEPENDENCIES
	 */
	public static final String	PROVISION_POLICY_DIRECTIVE				= "provision-policy";

	/**
	 * A value for the {@link #PROVISION_POLICY_DIRECTIVE provision-policy}
	 * directive indicating the subsystem accepts dependency resources. The root
	 * subsystem has this provision policy.
	 */
	public static final String	PROVISION_POLICY_ACCEPT_DEPENDENCIES	= "acceptDependencies";

	/**
	 * A value for the {@link #PROVISION_POLICY_DIRECTIVE provision-policy}
	 * directive indicating the subsystem does not accept dependency resources.
	 * This is the default value.
	 */
	public static final String	PROVISION_POLICY_REJECT_DEPENDENCIES	= "rejectDependencies";

	/**
	 * Manifest header identifying the resources to be deployed to satisfy the
	 * dependencies of a subsystem.
	 */
	public static final String	PROVISION_RESOURCE						= "Provision-Resource";

	/**
	 * Manifest header directive identifying the start level.
	 */
	public static final String	START_LEVEL_DIRECTIVE					= "start-level";

	/**
	 * Manifest header identifying the list of subsystem contents identified by
	 * a symbolic name and version.
	 */
	public static final String	SUBSYSTEM_CONTENT						= "Subsystem-Content";

	/**
	 * Manifest header identifying the human readable description.
	 */
	public static final String	SUBSYSTEM_DESCRIPTION					= "Subsystem-Description";

	/**
	 * Manifest header identifying services offered for export.
	 */
	public static final String	SUBSYSTEM_EXPORTSERVICE					= "Subsystem-ExportService";

	/**
	 * The name of the service property for the
	 * {@link Subsystem#getSubsystemId() subsystem ID}.
	 */
	public static final String	SUBSYSTEM_ID_PROPERTY					= "subsystem.id";

	/**
	 * Manifest header identifying services required for import.
	 */
	public static final String	SUBSYSTEM_IMPORTSERVICE					= "Subsystem-ImportService";

	/**
	 * Manifest header identifying the subsystem manifest version. If not
	 * present, the default value is {@code 1}.
	 */
	public static final String	SUBSYSTEM_MANIFESTVERSION				= "Subsystem-ManifestVersion";

	/**
	 * Manifest header identifying the human readable subsystem name.
	 */
	public static final String	SUBSYSTEM_NAME							= "Subsystem-Name";

	/**
	 * The name of the service property for the subsystem
	 * {@link Subsystem#getState() state}.
	 */
	public static final String	SUBSYSTEM_STATE_PROPERTY				= "subsystem.state";

	/**
	 * Manifest header value identifying the symbolic name for the subsystem.
	 * Must be present.
	 */
	public static final String	SUBSYSTEM_SYMBOLICNAME					= "Subsystem-SymbolicName";

	/**
	 * The name of the service property for the subsystem
	 * {@link Subsystem#getSymbolicName() symbolic name}.
	 */
	public static final String	SUBSYSTEM_SYMBOLICNAME_PROPERTY			= "subsystem.symbolicName";

	/**
	 * The symbolic name of the root subsystem.
	 */
	public static final String	ROOT_SUBSYSTEM_SYMBOLICNAME				= "org.osgi.service.subsystem.root";

	/**
	 * Manifest header identifying the subsystem type.
	 * 
	 * @see #SUBSYSTEM_TYPE_APPLICATION
	 * @see #SUBSYSTEM_TYPE_COMPOSITE
	 * @see #SUBSYSTEM_TYPE_FEATURE
	 */
	public static final String	SUBSYSTEM_TYPE							= "Subsystem-Type";

	/**
	 * The name of the service property for the {@link #SUBSYSTEM_TYPE subsystem
	 * type}.
	 * 
	 * @see #SUBSYSTEM_TYPE_APPLICATION
	 * @see #SUBSYSTEM_TYPE_COMPOSITE
	 * @see #SUBSYSTEM_TYPE_FEATURE
	 */
	public static final String	SUBSYSTEM_TYPE_PROPERTY					= "subsystem.type";

	/**
	 * The resource type value identifying an application subsystem.
	 * 
	 * <p>
	 * This value is used for the {@code osgi.identity} capability attribute
	 * {@code type}, the {@link #SUBSYSTEM_TYPE} manifest header and the
	 * {@link #SUBSYSTEM_TYPE_PROPERTY} service property.
	 */
	public static final String	SUBSYSTEM_TYPE_APPLICATION				= "osgi.subsystem.application";

	/**
	 * The resource type value identifying an composite subsystem.
	 * 
	 * <p>
	 * This value is used for the {@code osgi.identity} capability attribute
	 * {@code type}, the {@link #SUBSYSTEM_TYPE} manifest header and the
	 * {@link #SUBSYSTEM_TYPE_PROPERTY} service property.
	 */
	public static final String	SUBSYSTEM_TYPE_COMPOSITE				= "osgi.subsystem.composite";

	/**
	 * The resource type value identifying an feature subsystem.
	 * 
	 * <p>
	 * This value is used for the {@code osgi.identity} capability attribute
	 * {@code type}, the {@link #SUBSYSTEM_TYPE} manifest header and the
	 * {@link #SUBSYSTEM_TYPE_PROPERTY} service property.
	 */
	public static final String	SUBSYSTEM_TYPE_FEATURE					= "osgi.subsystem.feature";

	/**
	 * Manifest header value identifying the version of the subsystem. If not
	 * present, the default value is {@code 0.0.0}.
	 */
	public static final String	SUBSYSTEM_VERSION						= "Subsystem-Version";

	/**
	 * The name of the service property for the subsystem
	 * {@link Subsystem#getVersion() version}.
	 */
	public static final String	SUBSYSTEM_VERSION_PROPERTY				= "subsystem.version";
}
