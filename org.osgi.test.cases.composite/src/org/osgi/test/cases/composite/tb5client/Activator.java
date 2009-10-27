/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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
package org.osgi.test.cases.composite.tb5client;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.test.cases.composite.TestException;

public class Activator implements BundleActivator {
	private static String TEST_PROTOCOL = "Test-Protocol";
	private static String TEST_HANDLER = "Test-Handler";

	public void start(BundleContext context) throws Exception {
		Dictionary headers = context.getBundle().getHeaders("");

		String testHandler = (String) headers.get(TEST_HANDLER);
		for (int i = 1; true; i++) {
			String protocol = (String) headers.get(TEST_PROTOCOL + i);
			if (protocol == null)
				break;
			testProtocol(protocol, testHandler);
		}

	}

	private void testProtocol(String protocol, String testHandler) throws IOException{
		URL url = null;
		try {
			url = new URL(protocol + "://test");
		} catch (MalformedURLException e) {
			throw new TestException("Protocol not found: " + protocol, TestException.NO_PROTOCOL);
		}
		InputStream in = url.openStream();
		byte[] buf = new byte[in.available()];
		in.read(buf);
		String input = new String(buf);
		if (!input.equals("CustomUrlConnection" + testHandler))
			throw new TestException("Wrong stream hander: " + input, TestException.WRONG_STREAM_HANDER);

		Object ob = url.getContent();
		if (!("CustomContentHandler" + testHandler).equals(ob)) 
			throw new TestException("Wrong content hander: " + ob, TestException.WRONG_CONTENT_HANDER);
	}

	public void stop(BundleContext context) throws Exception {
		// nothing
	}

}
