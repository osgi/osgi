/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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
package org.osgi.test.cases.scaconfigtype.common;

/**
 * @author <a href="mailto:david.savage@paremus.com">David Savage</a>
 *
 */
public interface TestConstants {
	static final String ORG_OSGI_TEST_CASES_SCACONFIG_TYPE_BUNDLES = "org.osgi.test.cases.scaconfigtype.bundles";
	static final String STORAGEROOT = "org.osgi.test.cases.scaconfigtype.storageroot";

	static final String DEFAULT_STORAGEROOT = "generated/testframeworkstorage";
	
	static final String ORG_OSGI_TEST_CASES_SCACONFIGTYPE_COMMON = "org.osgi.test.cases.scaconfigtype.common";
	
	static final String SERVER_FRAMEWORK = "server";
	static final String CLIENT_FRAMEWORK = "client";
	static final long SERVICE_TIMEOUT = Long.getLong("org.osgi.test.cases.scaconfigtype.timeout", 5000L);
	
	static final String BINDING_A_QNAME = "{http://acme.com/defintions}serviceA";
	static final String BINDING_B_QNAME = "{http://acme.com/defintions}serviceB";
	
	static final String BINDING_A_NCNAME = "serviceA";
	static final String BINDING_B_NCNAME = "serviceB";
}
