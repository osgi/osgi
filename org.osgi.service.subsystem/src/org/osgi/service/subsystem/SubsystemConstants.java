/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
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

import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.framework.resource.ResourceConstants;

/**
 * Defines the constants used by subsystems.
 */
public class SubsystemConstants {
	private SubsystemConstants() {
		throw new AssertionError("This class is not designed to be instantiated");
	}
	
	/**
	 * Manifest header identifying the resources to be deployed.
	 */
	public static final String DEPLOYED_CONTENT = "Deployed-Content";

	/**
	 * Manifest header attribute identifying the deployed version.
	 */
	public static final String DEPLOYED_VERSION_ATTRIBUTE = "deployed-version";
	
	/**
	 * The standard name for a subsystem deployment manifest file.
	 */
	public static final String DEPLOYMENT_MANIFEST = "DEPLOYMENT.MF";
	
	/**
	 * Manifest header identifying packages offered for export.
	 * 
	 * @see Constants#EXPORT_PACKAGE
	 */
	public static final String EXPORT_PACKAGE = Constants.EXPORT_PACKAGE;
	
	/**
	 * Manifest header attribute identifying the resource type. The default 
	 * value is {@link #IDENTITY_TYPE_BUNDLE}.
	 * 
	 * @see ResourceConstants#IDENTITY_TYPE_ATTRIBUTE
	 */
	public static final String IDENTITY_TYPE_ATTRIBUTE = ResourceConstants.IDENTITY_TYPE_ATTRIBUTE;

	/**
	 * Manifest header attribute value identifying a bundle resource type.
	 * 
	 * @see ResourceConstants#IDENTITY_TYPE_BUNDLE
	 */
	public static final String IDENTITY_TYPE_BUNDLE = ResourceConstants.IDENTITY_TYPE_BUNDLE;
	
	/**
	 * Manifest header attribute value identifying a fragment resource type.
	 * 
	 * @see ResourceConstants#IDENTITY_TYPE_FRAGMENT
	 */
	public static final String IDENTITY_TYPE_FRAGMENT = ResourceConstants.IDENTITY_TYPE_FRAGMENT;

	/**
	 * Manifest header attribute value identifying a subsystem resource type.
	 */
	public static final String IDENTITY_TYPE_SUBSYSTEM = "osgi.subsystem";

	/**
	 * Manifest header identifying packages required for import.
	 * 
	 * @see Constants#IMPORT_PACKAGE
	 */
	public static final String IMPORT_PACKAGE = Constants.IMPORT_PACKAGE;
	
	/**
	 * The name of the standard directory within which subsystem related
	 * manifests are found.
	 */
	public static final String MANIFEST_DIRECTORY = "OSGI-INF";
	
	/**
	 * Manifest header used to express a preference for particular resources to
	 * satisfy implicit package dependencies.
	 */
	public static final String PREFERRED_PROVIDER = "Preferred-Provider";
	
	/**
	 * A value for the {@link #PROVISION_POLICY_DIRECTIVE provision-policy}
	 * directive indicating the subsystem accepts transitive resources. The root
	 * subsystem has this provision policy.
	 */
	public static final String PROVISION_POLICY_ACCEPT_TRANSITIVE = "acceptTransitive";
	
	/**
	 * Manifest header directive identifying the provision policy. The default 
	 * value is {@link #PROVISION_POLICY_REJECT_TRANSITIVE rejectTransitive}.
	 */
	public static final String PROVISION_POLICY_DIRECTIVE = "provision-policy";
	
	/**
	 * A value for the {@link #PROVISION_POLICY_DIRECTIVE provision-policy}
	 * directive indicating the subsystem does not accept transitive resources.
	 * This is the default value.
	 */
	public static final String PROVISION_POLICY_REJECT_TRANSITIVE = "rejectTransitive";

	/**
	 * Manifest header identifying the resources to be deployed to satisfy the 
	 * transitive dependencies of a subsystem.
	 */
	public static final String PROVISION_RESOURCE = "Provision-Resource";
	
	/**
	 * The symbolic name prefix for a region context bundle. The prefix is
	 * followed by the {@link Subsystem#getSubsystemId() subsystem ID}.
	 */
	public static final String REGION_CONTEXT_BUNDLE_SYMBOLICNAME_PREFIX = "org.osgi.service.subsystem.region.context.";
	
	/**
	 * The version for the region context bundle.
	 */
	public static final Version REGION_CONTEXT_BUNDLE_VERSION = Version.parseVersion("1.0.0");

	/**
	 * Manifest header identifying symbolic names of required bundles. 
	 */
	public static final String REQUIRE_BUNDLE = Constants.REQUIRE_BUNDLE;

	/**
	 * Manifest header directive identifying the resolution type. The default 
	 * value is {@link #RESOLUTION_MANDATORY}.
	 * 
	 * @see Constants#RESOLUTION_DIRECTIVE
	 */
	public static final String RESOLUTION_DIRECTIVE = Constants.RESOLUTION_DIRECTIVE;
	
	/**
	 * Manifest header directive value identifying a mandatory resolution type.
	 * 
	 * @see Constants#RESOLUTION_MANDATORY
	 */
	public static final String RESOLUTION_MANDATORY = Constants.RESOLUTION_MANDATORY;
	
	/**
	 * Manifest header directive value identifying an optional resolution type.
	 * 
	 * @see Constants#RESOLUTION_OPTIONAL
	 */
	public static final String RESOLUTION_OPTIONAL = Constants.RESOLUTION_OPTIONAL;
	
	/**
	 * The ID of the root subsystem.
	 */
	public static final long ROOT_SUBSYSTEM_ID = 0;

	/**
	 * The symbolic name of the root subsystem.
	 */
	public static final String ROOT_SUBSYSTEM_SYMBOLICNAME = "org.osgi.service.subsystem.root";

	/**
	 * The version of the root subsystem.
	 */
	public static final Version ROOT_SUBSYSTEM_VERSION = Version.parseVersion("1.0.0");

	/**
	 * Manifest header directive identifying the start level.
	 */
	public static final String START_LEVEL_DIRECTIVE = "start-level";
	
	/**
	 * The standard file extension of a subsystem archive.
	 */
	public static final String SUBSYSTEM_ARCHIVE_EXTENSION = "ssa";
	
	/**
	 * The list of subsystem contents identified by a symbolic name and version.
	 */
	public static final String SUBSYSTEM_CONTENT = "Subsystem-Content";
	
	/**
	 * Human readable description.
	 */
	public static final String SUBSYSTEM_DESCRIPTION = "Subsystem-Description";
	
	/**
	 * Manifest header identifying services offered for export.
	 */
	public static final String SUBSYSTEM_EXPORTSERVICE = "Subsystem-ExportService";
	
	/**
	 * The name of the service property for the {@link 
	 * Subsystem#getSubsystemId() subsystem ID}.
	 */
	public static final String SUBSYSTEM_ID_PROPERTY = "subsystem.id";
	
	/**
	 * Manifest header identifying services required for import.
	 */
	public static final String SUBSYSTEM_IMPORTSERVICE = "Subsystem-ImportService";
	
	/**
	 * The standard name for a subsystem manifest file.
	 */
	public static final String SUBSYSTEM_MANIFEST = "SUBSYSTEM.MF";
	
	/**
	 * The subsystem manifest version header must be present and equals to 1.0 
	 * for this version of applications. 
	 */
	public static final String SUBSYSTEM_MANIFESTVERSION = "Subsystem-ManifestVersion";
	
	/**
	 * Human readable application name.
	 */
	public static final String SUBSYSTEM_NAME = "Subsystem-Name";
	
	/**
	 * The name of the service property for the subsystem {@link 
	 * Subsystem#getState() state}.
	 */
	public static final String SUBSYSTEM_STATE_PROPERTY = "subsystem.state";
	
	/**
	 * Symbolic name for the application. Must be present.
	 */
	public static final String SUBSYSTEM_SYMBOLICNAME = "Subsystem-SymbolicName";
	
	/**
	 * The name of the service property for the subsystem {@link 
	 * Subsystem#getSymbolicName() symbolic name}.
	 */
	public static final String SUBSYSTEM_SYMBOLICNAME_PROPERTY = "subsystem.symbolicname";
	
	/**
	 * Manifest header identifying the subsystem type.
	 */
	public static final String SUBSYSTEM_TYPE = "Subsystem-Type";
	
	/**
	 * The name of the service property for the subsystem {@link #SUBSYSTEM_TYPE
	 * type}.
	 */
	public static final String SUBSYSTEM_TYPE_PROPERTY = "subsystem.type";
	
	/**
	 * Manifest header value identifying an application subsystem.
	 */
	public static final String SUBSYSTEM_TYPE_APPLICATION = "osgi.application";
	
	/**
	 * Manifest header value identifying a composite subsystem.
	 */
	public static final String SUBSYSTEM_TYPE_COMPOSITE = "osgi.composite";
	
	/**
	 * Manifest header value identifying a feature subsystem.
	 */
	public static final String SUBSYSTEM_TYPE_FEATURE = "osgi.feature";
	
	/**
	 * Version of the application. If not present, the default value is 0.0.0.
	 */
	public static final String SUBSYSTEM_VERSION = "Subsystem-Version";
	
	/**
	 * The name of the service property for the subsystem {@link 
	 * Subsystem#getVersion() version}.
	 */
	public static final String SUBSYSTEM_VERSION_PROPERTY = "subsystem.version";
	
	/**
	 * Manifest header attribute indicating a version or version range. The 
	 * default value is {@link Version#emptyVersion}.
	 */
	public static final String VERSION_ATTRIBUTE = Constants.VERSION_ATTRIBUTE;
}
