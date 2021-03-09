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

package org.osgi.test.cases.framework.secure.fragments.tb7f;

import java.net.MalformedURLException;
import java.net.URL;

import org.osgi.framework.Bundle;

/**
 *
 * Test class used for running test related to URL permissions.
 * 
 * @author $Id$
 */
public class TestClass {
	/**
	 * Exceute URL permission related tests.   
	 * @param bundle
	 */
	public void run(Bundle bundle, URL resource) throws Exception {
		URL url;
		String spec;
		// Try creating a URL for a restricted resource using the
		// constructor URL#URL(String spec)
		
		try {
			spec = resource.toExternalForm();
			url = new URL(spec);
			url.openStream();
			throw new Exception("Expecting MalformedURLException");
		}
		catch (MalformedURLException e) {
			
		}
		// Try creating a URL for a restricted resource using the
		// constructor URL#URL(URL context, String spec)
		try {
			url = new URL(resource, ".");
			url.openStream();
			throw new Exception("Expecting MalformedURLException");
		}
		catch (MalformedURLException e) {
			
		}
		// Try creating a URL for a restricted resource using the
		// constructor URL#URL(String protocol, String host, int port, String
		// file)
		try {
			url = new URL(resource.getProtocol(), resource.getHost(), resource
					.getPort(), resource.getPath());
			url.openStream();
			throw new Exception("Expecting SecurityException");
		}
		catch (SecurityException e) {
			
		}
	}
}
