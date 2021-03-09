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
package org.osgi.test.cases.framework.secure.junit.hooks.weaving.export;

import org.osgi.test.support.OSGiTestCaseProperties;

public class TestConstants {
	public static final String DYNAMIC_IMPORT_PACKAGE = "org.osgi.resource";
	public static final String DYNAMIC_IMPORT_CLASS = DYNAMIC_IMPORT_PACKAGE + ".Resource";
	
	public static final String PROP_PREFIX = "org.osgi.test.cases.framework.secure.weaving.";
	public static final String PROP_EXPECT_SECURITYEXCEPTION = PROP_PREFIX + "expectSecurityException";
	public static final String PROP_EXPECT_SECURITYEXCEPTION_ALL = PROP_EXPECT_SECURITYEXCEPTION + ".all";
	public static final String PROP_EXPECT_SECURITYEXCEPTION_ADDDYNAMICIMPORT = PROP_EXPECT_SECURITYEXCEPTION + ".addDynamicImport";
	public static final String PROP_INVALID_SETBYTES = PROP_PREFIX + "invalidSetBytes";
	public static final String PROP_RETHROW_SECURITYEXCEPTION = PROP_PREFIX + "rethrowSecurityException";
	
	public static final String WOVEN_CLASS = "org.osgi.test.cases.framework.secure.weaving.tb.woven.WeaveMe";
	
	public static boolean isExpectSecurityExceptionAll() {
		return OSGiTestCaseProperties.getBooleanProperty(
				PROP_EXPECT_SECURITYEXCEPTION_ALL, false);
	}
	
	public static boolean isExpectSecurityExceptionAddDynamicImport() {
		return OSGiTestCaseProperties.getBooleanProperty(
				PROP_EXPECT_SECURITYEXCEPTION_ADDDYNAMICIMPORT, false);
	}
	
	public static boolean isInvalidSetBytes() {
		return OSGiTestCaseProperties.getBooleanProperty(PROP_INVALID_SETBYTES,
				false);
	}
	
	public static boolean isRethrowingSecurityException() {
		return OSGiTestCaseProperties.getBooleanProperty(
				PROP_RETHROW_SECURITYEXCEPTION, false);
	}
}
