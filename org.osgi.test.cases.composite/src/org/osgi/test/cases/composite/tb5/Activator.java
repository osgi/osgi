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
package org.osgi.test.cases.composite.tb5;

import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.url.URLConstants;
import org.osgi.service.url.URLStreamHandlerService;

public class Activator implements BundleActivator {
	private static String TEST_PROTOCOL = "Test-Protocol";
	private static String TEST_HANDLER = "Test-Handler";
	private static String TEST_MIMETYPE = "Test-MimeType";

	public void start(BundleContext context) throws Exception {
		Dictionary headers = context.getBundle().getHeaders("");

		ArrayList protocolList = new ArrayList();
		for (int i = 1; true; i++) {
			String protocol = (String) headers.get(TEST_PROTOCOL + i);
			if (protocol == null)
				break;
			protocolList.add(protocol);
		}

		String[] protocols = (String[]) protocolList.toArray(new String[protocolList.size()]);
		String testHandler = (String) headers.get(TEST_HANDLER);
		String testMimeType = (String) headers.get(TEST_MIMETYPE);

		Hashtable streamHandlerProps = new Hashtable();
		streamHandlerProps.put(URLConstants.URL_HANDLER_PROTOCOL, protocols);

		Hashtable contentHandlerProps = new Hashtable();
		contentHandlerProps.put(URLConstants.URL_CONTENT_MIMETYPE, testMimeType);

		URLStreamHandlerService streamHandler;
		ContentHandler contentHandler;
		if ("1".equals(testHandler)) {
			streamHandler = new CustomUrlHandler1();
			contentHandler = new CustomContentHandler1();
		} else {
			streamHandler = new CustomUrlHandler2();
			contentHandler = new CustomContentHandler2();
		}
			
		context.registerService(URLStreamHandlerService.class.getName(), streamHandler, streamHandlerProps);
		context.registerService(ContentHandler.class.getName(), contentHandler, contentHandlerProps);
	}

	public void stop(BundleContext context) throws Exception {
		// nothing
	}

}
