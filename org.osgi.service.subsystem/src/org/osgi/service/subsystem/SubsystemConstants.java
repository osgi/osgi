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
	 * Key for the event property that holds the subsystem id.
	 */
	public static final String EVENT_SUBSYSTEM_ID = "subsystem.id";
	
	/**
	 * Key for the event property that holds the subsystem location.
	 */
	public static final String EVENT_SUBSYSTEM_LOCATION = "subsystem.location";
	
	/**
	 * Key for the event property that holds the subsystem state.
	 */
	public static final String EVENT_SUBYSTEM_STATE = "subsystem.state";
	
	/**
	 * Key for the event property that holds the subsystem symbolic name.
	 */
	public static final String EVENT_SUBSYSTEM_SYMBOLICNAME = "subsystem.symbolicname";
	
	/**
	 * Key for the event property that holds the subsystem version.
	 */
	public static final String EVENT_SUBSYSTEM_VERSION = "subsystem.version";
	
	/**
	 * The topic for subsystem event admin events.
	 */
	public static final String EVENT_TOPIC = "org/osgi/service/Subsystem/";
	
	/**
	 * The topic for subsystem internal event admin events.
	 */
	public static final String EVENT_TOPIC_INTERNALS = "org/osgi/service/SubsystemInternals/";

	/**
	 * The subsystem lifecycle event types that can be produced by a subsystem. 
	 * See ? and Subsystem for details on the circumstances under which these 
	 * events are fired.
	 */
	public static enum EVENT_TYPE {
		/**
		 * Event type used to indicate a subsystem is installing.
		 */
		INSTALLING,
		/**
		 * Event type used to indicate a subsystem has been installed.
		 */
		INSTALLED,
		/**
		 * Event type used to indicate a subsystem is resolving.
		 */
		RESOLVING,
		/**
		 * Event type used to indicate a subsystem has been resolved.
		 */
		RESOLVED,
		/**
		 * Event type used to indicate a subsystem is starting.
		 */
		STARTING,
		/**
		 * Event type used to indicate a subsystem has been started.
		 */
		STARTED,
		/**
		 * Event type used to indicate a subsystem is stopping.
		 */
		STOPPING,
		/**
		 * Event type used to indicate a subsystem has been stopped.
		 */
		STOPPED,
		/**
		 * Event type used to indicate a subsystem is updating.
		 */
		UPDATING,
		/**
		 * Event type used to indicate a subsystem has been updated.
		 */
		UPDATED,
		/**
		 * Event type used to indicate a subsystem is uninstalling.
		 */
		UNINSTALLING,
		/**
		 * Event type used to indicate a subsystem has been uninstalled.
		 */
		UNINSTALLED,
		/**
		 * Event type used to indicate that a subsystem operation is being 
		 * cancelled.
		 */
		CANCELING,
		/**
		 * Event type used to indicate that the operations was cancelled (e.g. 
		 * an install was cancelled).
		 */
		CANCELED,
		/**
		 * Event type used to indicate that the operation failed (e.g. an 
		 * exception was thrown during installation).
		 */
		FAILED
	}
	
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
	 * Manifest header used to express a preference for particular resources to
	 * satisfy implicit package dependencies.
	 */
	public static final String PREFERRED_PROVIDER = "Preferred-Provider";

	/**
	 * Manifest header identifying the resources to be deployed to satisfy the 
	 * transitive dependencies of a subsystem.
	 */
	public static final String PROVISION_RESOURCE = "Provision-Resource";

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
	 * Manifest header directive identifying the start level.
	 */
	public static final String START_LEVEL_DIRECTIVE = "start-level";
	
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
	 * Manifest header identifying services required for import.
	 */
	public static final String SUBSYSTEM_IMPORTSERVICE = "Subsystem-ImportService";
	
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
	 * Symbolic name for the application. Must be present.
	 */
	public static final String SUBSYSTEM_SYMBOLICNAME = "Subsystem-SymbolicName";
	
	/**
	 * Manifest header identifying the subsystem type.
	 */
	public static final String SUBSYSTEM_TYPE = "Subsystem-Type";
	
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
	 * Manifest header attribute indicating a version or version range. The 
	 * default value is {@link Version#emptyVersion}.
	 */
	public static final String VERSION_ATTRIBUTE = Constants.VERSION_ATTRIBUTE;
}
