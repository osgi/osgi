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

/*
 * TODO
 * There are disparities within the ODT between Field Summary and Field Detail.
 * This interface is currently combines Field Summary and Field Detail.
 */

/**
 * Defines the constants used by subsystems. These fall into four categories: 
 * <ol>
 * 		<li>Headers for the applications subsystem type.</li>
 * 		<li>Headers for the composite bundle subsystem type. Where appropriate, 
 *          these header values are identical to those for a bundle.</li> 
 * 		<li>Headers for the library subsystem type.</li>
 * 		<li>Attributes used in the above headers</li>
 * </ol>
 */
public class SubsystemConstants {
	private SubsystemConstants() {
		throw new AssertionError("This class is not designed to be instantiated");
	}
	
	/**
	 * The list of application contents identified by a symbolic name and 
	 * version.
	 */
	public static final String APPLICATION_CONTENT = "";
	/**
	 * Human readable description.
	 */
	public static final String APPLICATION_DESCRIPTION = "";
	/**
	 * Name of the resource to use for localized headers.
	 */
	public static final String APPLICATION_LOCALIZATION = "";
	/**
	 * The subsystem manifest version header must be present and equals to 1.0 
	 * for this version of applications. 
	 */
	public static final String APPLICATION_MANIFESTVERSION = "";
	/**
	 * Human readable application name.
	 */
	public static final String APPLICATION_NAME = "";
	/**
	 * Descriptions of resources contained within the application archive which 
	 * cannot be determine reflectively. 
	 */
	public static final String APPLICATION_RESOURCES = "";
	/**
	 * Symbolic name for the application. Must be present.
	 */
	public static final String APPLICATION_SYMBOLICNAME = "";
	/**
	 * Location to use when updating the application.
	 */
	public static final String APPLICATION_UPDATELOCATION = "";
	/**
	 * Version of the application. If not present, the default value is 0.0.0.
	 */
	public static final String APPLICATION_VERSION = "";
	/**
	 * The human readable composite bundle description (re-using the bundle 
	 * header). 
	 */
	public static final String BUNDLE_DESCRIPTION = "";
	/**
	 * The packages to be exported by the composite bundle for use outside the 
	 * composite bundle (re-using the bundle header). The packages declarations 
	 * must match a package provide by a bundle listed in the composite bundle 
	 * content. 
	 */
	public static final String BUNDLE_EXPORTPACKAGE = "";
	/**
	 * The packages to be imported into the composite bundle for use by the 
	 * composite bundle contents (re-using the bundle header). 
	 */
	public static final String BUNDLE_IMPORTPACKAGE = "";
	/**
	 * Name of the resource to use for localized headers (re-using the bundle 
	 * header). 
	 */
	public static final String BUNDLE_LOCALIZATION = "";
	/**
	 * The human readable composite bundle name (re-using the bundle header). 
	 */
	public static final String BUNDLE_NAME = "";
	/**
	 * A list of Bundles the composite bundle requires (re-using the bundle 
	 * header). These bundles are made available to satisfy Require-Bundle 
	 * statements for the composite bundle content bundles. 
	 */
	public static final String BUNDLE_REQUIREBUNDLE = "";
	/**
	 * Symbolic name for the composite bundle (re-using the bundle header). 
	 * Must be present. 
	 */
	public static final String BUNDLE_SYMBOLICNAME = "";
	/**
	 * Location to use when updating the composite bundle (re-using the bundle 
	 * header). 
	 */
	public static final String BUNDLE_UPDATELOCATION = "";
	/**
	 * Version of the composite bundle (re-using the bundle header). If not 
	 * present, the default value is 0.0.0. 
	 */
	public static final String BUNDLE_VERSION = "";
	/**
	 * The list of composite bundle contents identified by a symbolic name and 
	 * version.
	 */
	public static final String COMPOSITEBUNDLE_CONTENT = "";
	/**
	 * A list of service filters used to identify the services provided by 
	 * bundles inside the composite bundle that can be exported outside the 
	 * composite bundle. 
	 */
	public static final String COMPOSITEBUNDLE_EXPORTSERVICE = "";
	/**
	 * A list of service filters used to identify services that this composite 
	 * bundle requires. 
	 */
	public static final String COMPOSITEBUNDLE_IMPORTSERVICE = "";
	/**
	 * The composite bundle manifest version header must be present and equal 
	 * to 1.0 for this version of composite bundles. 
	 */
	public static final String COMPOSITEBUNDLE_MANIFESTVERSION = "";
	/**
	 * Descriptions of resources contained within the composite bundle archive 
	 * which cannot be determine reflectively. 
	 */
	public static final String COMPOSITEBUNDLE_RESOURCES = "";
	/**
	 * 
	 */
	public static final String FEATURE_CONTENT = "Feature-Content";
	/**
	 * 
	 */
	public static final String FEATURE_DESCRIPTION = "Feature-Description";
	/**
	 * 
	 */
	public static final String FEATURE_MANIFESTVERSION = "Feature-ManifestVersion";
	/**
	 * 
	 */
	public static final String FEATURE_NAME = "Feature-Name";
	/**
	 * 
	 */
	public static final String FEATURE_SYMBOLICNAME = "Feature-SymbolicName";
	/**
	 * 
	 */
	public static final String FEATURE_VERSION = "Feature-Version";
	/**
	 * The list of library contents identified by a symbolic name and version.
	 */
	public static final String LIBRARY_CONTENT = "";
	/**
	 * Human readable library description.
	 */
	public static final String LIBRARY_DESCRIPTION = "";
	/**
	 * Name of the resource to use for localized headers.
	 */
	public static final String LIBRARY_LOCALIZATION = "";
	/**
	 * The subsystem manifest version header must be present and equals to 1.0 
	 * for this version of applications.
	 */
	public static final String LIBRARY_MANIFESTVERSION = "";
	/**
	 * Human readable library name.
	 */
	public static final String LIBRARY_NAME = "";
	/**
	 * Descriptions of resources contained within the library archive which 
	 * cannot be determine reflectively. 
	 */
	public static final String LIBRARY_RESOURCES = "";
	/**
	 * Symbolic name for the application. Must be present.
	 */
	public static final String LIBRARY_SYMBOLICNAME = "";
	/**
	 * Location to use when updating the library.
	 */
	public static final String LIBRARY_UPDATELOCATION = "";
	/**
	 * Version of the application. If not present, the default value is 0.0.0.
	 */
	public static final String LIBRARY_VERSION = "";
	/**
	 * Service property to be set on ResourceProcessor services to indicate 
	 * which namespaces of resource it can process.
	 */
	public static final String OSGI_RESOURCE_NAMESPACE = "";
	/**
	 * Attribute to indicate the location of a resource.
	 */
	public static final String RESOURCE_LOCATION_ATTRIBUTE = "";
	/**
	 * Attribute to indicate the namespace of a resource.
	 */
	public static final String RESOURCE_NAMESPACE_ATTRIBUTE = "osgi.resource.namespace";
	/**
	 * Identify resources that are bundles (this is the default type).
	 */
	public static final String RESOURCE_NAMESPACE_BUNDLE = "";
	/**
	 * Identify resources that are subsystems.
	 */
	public static final String RESOURCE_NAMESPACE_SUBSYSTEM = "";
	/**
	 * Attribute to indicate a bundle needs to be started, defaults to true.
	 */
	public static final String RESOURCE_START_ATTRIBUTE = "";
	/**
	 * Attribute to indicate the start level that must be associated to a 
	 * constituent bundle or subsystem.
	 */
	public static final String RESOURCE_START_LEVEL_ATTRIBUTE = "";
	/**
	 * Attribute to indicate the type on a resource.
	 */
	public static final String RESOURCE_TYPE_ATTRIBUTE = "";
	/**
	 * Identify resources that are bundles (this is the default type).
	 */
	public static final String RESOURCE_TYPE_BUNDLE = "";
	/**
	 * Identify resources that are subsystems.
	 */
	public static final String RESOURCE_TYPE_SUBSYSTEM = "";
	/**
	 * Attribute to indicate a bundle needs to be forced updated, even if the 
	 * version is the same, defaults to false
	 */
	public static final String RESOURCE_UPDATE_ATTRIBUTE = "";
	/**
	 * Service property to be set on ResourceProcessor services to indicate 
	 * which types of resource it can process. 
	 */
	public static final String SERVICE_RESOURCE_TYPE = "";
	/**
	 * Directive on the manifest header of a composite indicating the composite 
	 * is managed as a subsystem. 
	 */
	public static final String SUBSYSTEM_DIRECTIVE = "";
	
	// Event related constants.

	/**
	 * The subsystem lifecycle event types that can be produced by a subsystem. 
	 * See ? and Subsystem for details on the circumstances under which these 
	 * events are fired.
	 */
	public static enum EventType {
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
	 * Key for the event property that holds the subsystem id.
	 */
	public static final String SUBSYSTEM_ID = "subsystem.id";
	/**
	 * Key for the event property that holds the subsystem location.
	 */
	public static final String SUBSYSTEM_LOCATION = "subsystem.location";
	/**
	 * Key for the event property that holds the subsystem state.
	 */
	public static final String SUBYSTEM_STATE = "subsystem.state";
	/**
	 * Key for the event property that holds the subsystem symbolic name.
	 */
	public static final String SUBSYSTEM_SYMBOLICNAME = "subsystem.symbolicname";
	/**
	 * Key for the event property that holds the subsystem version.
	 */
	public static final String SUBSYSTEM_VERSION = "subsystem.version";
	/**
	 * The topic for subsystem event admin events.
	 */
	public static final String TOPIC = "org/osgi/service/Subsystem/";
	/**
	 * The topic for subsystem internal event admin events.
	 */
	public static final String TOPIC_INTERNALS = "org/osgi/service/SubsystemInternals/";
}
