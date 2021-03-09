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
package org.osgi.test.cases.tr069todmt.junit;

import org.osgi.service.tr069todmt.TR069Connector;
import org.osgi.service.tr069todmt.TR069Exception;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This testcase checks for the correct Constants in the TR069Connector interface.
 * 
 * @author steffen.druesedow@telekom.de
 *
 */
public class TR069ConnectorConstantsTestCase extends DefaultTestBundleControl {

	/**
	 * This test ensures that the specified Interfaces define the specified Constants.
	 *  
	 * @throws Exception
	 */
	public void testInterfaceConstants() throws Exception {

		// TR069Connector
		assertConstant(0, "TR069_DEFAULT", TR069Connector.class);
		assertConstant(1, "TR069_INT", TR069Connector.class);
		assertConstant(2, "TR069_UNSIGNED_INT", TR069Connector.class);
		assertConstant(4, "TR069_LONG", TR069Connector.class);
		assertConstant(8, "TR069_UNSIGNED_LONG", TR069Connector.class);
		assertConstant(16, "TR069_STRING", TR069Connector.class);
		assertConstant(32, "TR069_BOOLEAN", TR069Connector.class);
		assertConstant(64, "TR069_BASE64", TR069Connector.class);
		assertConstant(128, "TR069_HEXBINARY", TR069Connector.class);
		assertConstant(256, "TR069_DATETIME", TR069Connector.class);
		assertConstant("application/x-tr-069-", "PREFIX", TR069Connector.class);
		assertConstant("application/x-tr-069-base64", "TR069_MIME_BASE64", TR069Connector.class);
		assertConstant("application/x-tr-069-boolean", "TR069_MIME_BOOLEAN", TR069Connector.class);
		assertConstant("application/x-tr-069-dateTime", "TR069_MIME_DATETIME", TR069Connector.class);
		assertConstant("application/x-tr-069-default", "TR069_MIME_DEFAULT", TR069Connector.class);
		assertConstant("application/x-tr-069-eager", "TR069_MIME_EAGER", TR069Connector.class);
		assertConstant("application/x-tr-069-hexBinary", "TR069_MIME_HEXBINARY", TR069Connector.class);
		assertConstant("application/x-tr-069-int", "TR069_MIME_INT", TR069Connector.class);
		assertConstant("application/x-tr-069-long", "TR069_MIME_LONG", TR069Connector.class);
		assertConstant("application/x-tr-069-string", "TR069_MIME_STRING", TR069Connector.class);
		assertConstant("application/x-tr-069-string-list", "TR069_MIME_STRING_LIST", TR069Connector.class);
		assertConstant("application/x-tr-069-unsignedInt", "TR069_MIME_UNSIGNED_INT", TR069Connector.class);
		assertConstant("application/x-tr-069-unsignedLong", "TR069_MIME_UNSIGNED_LONG", TR069Connector.class);

		// TR069Exception
		assertConstant(9002, "INTERNAL_ERROR", TR069Exception.class);
		assertConstant(9003, "INVALID_ARGUMENTS", TR069Exception.class);
		assertConstant(9005, "INVALID_PARAMETER_NAME", TR069Exception.class);
		assertConstant(9006, "INVALID_PARAMETER_TYPE", TR069Exception.class);
		assertConstant(9007, "INVALID_PARAMETER_VALUE", TR069Exception.class);
		assertConstant(9000, "METHOD_NOT_SUPPORTED", TR069Exception.class);
		assertConstant(9008, "NON_WRITABLE_PARAMETER", TR069Exception.class);
		assertConstant(9009, "NOTIFICATION_REJECTED", TR069Exception.class);
		assertConstant(9001, "REQUEST_DENIED", TR069Exception.class);
		assertConstant(9004, "RESOURCES_EXCEEDED", TR069Exception.class);
	}
	
}
