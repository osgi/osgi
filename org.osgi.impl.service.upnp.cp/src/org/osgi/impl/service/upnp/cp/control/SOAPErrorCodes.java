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
package org.osgi.impl.service.upnp.cp.control;

import java.util.Hashtable;

public class SOAPErrorCodes {
	private static Hashtable<String,String[]> errorMap;

	// This is the constructor which initializes the error table and populates
	// it.
	public SOAPErrorCodes() {
		errorMap = new Hashtable<>();
		errorMap.put("401", new String[] {"Invalid Action",
				"No action by that name at this service."});
		errorMap
				.put(
						"402",
						new String[] {
								"Invalid Args",
								"Could be any of the following: not "
										+ "enough in args, too many in args, no in args, no in args by that name, "
										+ "one or more in args are of the wrong data type."});
		errorMap.put("403", new String[] {"Out of Sync",
				"Out of synchronization."});
		errorMap.put("501", new String[] {
				"Action Failed",
				"May be returned in current state of "
						+ "service prevents invoking that action."});
		errorMap.put("404", new String[] {"Invalid Var",
				"No state variable by that name at this service."});
	}

	// This method takes the error code and returns the description.
	static String getDesc(String code) {
		String[] str = errorMap.get(code);
		if (str == null) {
			return null;
		}
		return str[1];
	}

	// This method takes the error code and returns the error description.
	static String getErrorDesc(String code) {
		String[] str = errorMap.get(code);
		if (str == null) {
			return null;
		}
		return str[0];
	}
}
