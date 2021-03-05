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
package org.osgi.test.cases.residentialmanagement;

import org.osgi.test.support.OSGiTestCaseProperties;

/**
 * Defines a number of string constants for use in the testclasses implementing this interface.
 * @author steffen
 *
 */
public interface RMTConstants {

	static final String	RMT_ROOT						= OSGiTestCaseProperties
																.getProperty("org.osgi.dmt.residential");
	static final String FRAMEWORK = "Framework";
	static final String FRAMEWORK_ROOT = RMT_ROOT + "/" + FRAMEWORK;
	static final String FILTER_ROOT = RMT_ROOT + "/Filter";
	static final String LOG = "Log";
	static final String LOG_ROOT = RMT_ROOT + "/" + LOG;
	
	// first level children
	static final String STARTLEVEL = "StartLevel";
	static final String INITIAL_BUNDLE_STARTLEVEL = "InitialBundleStartLevel";
	static final String BUNDLE = "Bundle";
	static final String PROPERTY = "Property";

	// children of the Bundle node
	// Startlevel
	static final String URL_STRING = "URL";
	static final String AUTOSTART = "AutoStart";
	static final String FAULT_TYPE = "FaultType";
	static final String FAULT_MESSAGE = "FaultMessage";
	static final String BUNDLEID = "BundleId";
	static final String SYMBOLIC_NAME = "SymbolicName";
	static final String VERSION = "Version";
	static final String BUNDLETYPE = "BundleType";
	static final String HEADERS = "Headers";
	static final String LOCATION = "Location";
	static final String STATE = "State";
	static final String REQUESTED_STATE = "RequestedState";
	static final String LAST_MODIFIED = "LastModified";
	static final String WIRES = "Wires";
	static final String SIGNERS = "Signers";
	static final String ENTRIES = "Entries";
	static final String INSTANCEID = "InstanceId";
	static final String FRAGMENT = "FRAGMENT";

	// Namespaces: allowed children of a Bundle.Wires node
	static final String NAMESPACE_BUNLDE = "osgi.wiring.bundle"; 
	static final String NAMESPACE_HOST = "osgi.wiring.host"; 
	static final String NAMESPACE_PACKAGE = "osgi.wiring.package"; 
	static final String NAMESPACE_RMT_SERVICE = "osgi.wiring.rmt.service"; 

	// children of a Wire node
	// InstanceId
	static final String NAMESPACE = "Namespace";
	static final String REQUIREMENT = "Requirement";
	static final String CAPABILITY = "Capability";
	static final String REQUIRER = "Requirer";
	static final String PROVIDER = "Provider";
	// children of the Bundle.Wire.Capability node
	static final String DIRECTIVE = "Directive";
	static final String ATTRIBUTE = "Attribute";
	// children of the Bundle.Wire.Capability node
	static final String FILTER = "Filter";
	// Directive
	// Attribute

	
	// children of Signers node
	// InstanceId
	static final String ISTRUSTED = "IsTrusted";
	static final String CERTIFICATECHAIN = "CertificateChain";

	// children of Entries node
	// InstanceId
	static final String PATH = "Path";
	static final String CONTENT = "Content";

	// Log children
	static final String LOG_ENTRIES = "LogEntries";
	// LogEntry children
	static final String TIME = "Time";
	static final String LEVEL = "Level";
	static final String MESSAGE = "Message";
		// Bundle
	static final String EXCEPTION = "Exception";
	
	// FILTER children
	static final String TARGET = "Target";
	static final String LIMIT = "Limit";
	static final String RESULT = "Result";
	static final String RESULT_URI_LIST = "ResultUriList";
	

	
	static final String TESTBUNDLE_EXPORTPACKAGE = "org.osgi.test.cases.residentialmanagement.tb1.jar";
	static final String TESTBUNDLE_IMPORTPACKAGE = "org.osgi.test.cases.residentialmanagement.tb2.jar";
	static final String TESTBUNDLE_REGISTERING_SERVICES = "org.osgi.test.cases.residentialmanagement.tb3.jar";
	static final String TESTBUNDLE_USING_SERVICE2 = "org.osgi.test.cases.residentialmanagement.tb5.jar";
	static final String TESTBUNDLE_FRAGMENT      = "org.osgi.test.cases.residentialmanagement.tb7.jar";
	static final String TESTBUNDLE_REQUIRE 	   = "org.osgi.test.cases.residentialmanagement.tb8.jar";
	static final String TESTBUNDLE_TRUSTED 	   = "org.osgi.test.cases.residentialmanagement.tb9.jar";
	static final String TESTBUNDLE_NON_TRUSTED   = "org.osgi.test.cases.residentialmanagement.tbnontrusted.jar";

}
