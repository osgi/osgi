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
package org.osgi.test.cases.url.junit;

import java.io.InputStream;
import java.net.ContentHandler;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.url.URLConstants;
import org.osgi.service.url.URLStreamHandlerService;
import org.osgi.test.support.OSGiTestCase;

public class UrlHandlerControl extends OSGiTestCase {
	// ******************************************************
	// Fields
	// ******************************************************
	// The url protocol for testing
	private String					protocol1	= "osgitest1";
	private String					protocol2	= "osgitest2";
	// url handlers
	private CustomUrlHandler1		urlHandler1;
	private CustomUrlHandler2		urlHandler2;
	private ServiceRegistration<URLStreamHandlerService>	urlHandlerSerReg1;
	private ServiceRegistration<URLStreamHandlerService>	urlHandlerSerReg2;
	// properties of url handlers
	private Hashtable<String,Object>	urlServiceProperty1;
	private Hashtable<String,Object>	urlServiceProperty2;
	// content handlers
	private CustomContentHandler1	contentHandler1;
	private CustomContentHandler2	contentHandler2;
	private ServiceRegistration<ContentHandler>				contentHandlerSerReg1;
	private ServiceRegistration<ContentHandler>				contentHandlerSerReg2;
	// properties of content handlers
	private Hashtable<String,Object>	contentServiceProperty1;
	private Hashtable<String,Object>	contentServiceProperty2;

	protected void setUp() {
		urlServiceProperty1 = new Hashtable<>();
		urlServiceProperty1.put(URLConstants.URL_HANDLER_PROTOCOL,
				new String[] {protocol1, protocol2});
		urlServiceProperty1.put(Constants.SERVICE_RANKING, Integer.valueOf(10));
		urlServiceProperty2 = new Hashtable<>();
		urlServiceProperty2.put(URLConstants.URL_HANDLER_PROTOCOL,
				new String[] {protocol1});
		urlServiceProperty2.put(Constants.SERVICE_RANKING, Integer.valueOf(5));
		urlHandler1 = new CustomUrlHandler1();
		urlHandler2 = new CustomUrlHandler2();
		contentServiceProperty1 = new Hashtable<>();
		contentServiceProperty1.put(URLConstants.URL_CONTENT_MIMETYPE,
				new String[] {"osgi/test"});
		contentServiceProperty1.put(Constants.SERVICE_RANKING, Integer.valueOf(10));
		contentServiceProperty2 = new Hashtable<>();
		contentServiceProperty2.put(URLConstants.URL_CONTENT_MIMETYPE,
				new String[] {"osgi/test"});
		contentServiceProperty2.put(Constants.SERVICE_RANKING, Integer.valueOf(5));
		contentHandler1 = new CustomContentHandler1();
		contentHandler2 = new CustomContentHandler2();
		// register services
		urlHandlerSerReg1 = getContext().registerService(
				URLStreamHandlerService.class, urlHandler1,
				urlServiceProperty1);
		urlHandlerSerReg2 = getContext().registerService(
				URLStreamHandlerService.class, urlHandler2,
				urlServiceProperty2);
		// register services
		contentHandlerSerReg1 = getContext().registerService(
				ContentHandler.class, contentHandler1,
				contentServiceProperty1);
		contentHandlerSerReg2 = getContext().registerService(
				ContentHandler.class, contentHandler2,
				contentServiceProperty2);
	}

	protected void tearDown() {
		unregisterService(urlHandlerSerReg2);
		unregisterService(urlHandlerSerReg1);
		unregisterService(contentHandlerSerReg2);
		unregisterService(contentHandlerSerReg1);
	}

	private void unregisterService(ServiceRegistration< ? > reg) {
		if (reg == null) {
			return;
		}
		try {
			reg.unregister();
		}
		catch (IllegalStateException e) {
			// already unregistered
		}
	}

	// ******************************************************
	// ******************************************************
	/**
	 * Test accessing a url when there is no associated url handler for it
	 */
	public void testAccessingUrlWhenNoHandlerRegistered() throws Exception {
		urlHandlerSerReg1.unregister();
		urlHandlerSerReg2.unregister();
		try {
			String url = protocol1 + "://test";
			new URL(url);
			fail("url creation should have failed: " + url);
		}
		catch (MalformedURLException e) {
			// expected
		}
		catch (Exception e) {
			fail("unexpected exception", e);
		}
	}

	// ******************************************************
	// ******************************************************
	/**
	 * Test accessing a url after registering a url handler for it
	 */
	public void testAccessingUrlWhenHandlerRegistered() throws Exception {
		// test protocol1
		URL url = new URL(protocol1 + "://test");
		URLConnection con = url.openConnection();
		URLConnection reqd = urlHandler1.getConnectionObject();
		assertEquals("Opening connection after registering handler for "
				+ protocol1, reqd, con);
		// test protocol2
		url = new URL(protocol2 + "://test");
		con = url.openConnection();
		reqd = urlHandler1.getConnectionObject();
		assertEquals("Opening connection after registering handler for "
				+ protocol2, reqd, con);
	}

	// ******************************************************
	// ******************************************************
	/**
	 * Test getting the content after registering a content handler for it
	 */
	public void testContentHandlerWhenHandlerRegistered() throws Exception {
		URL url = new URL(protocol1 + "://test");
		Object ob = url.getContent();
		assertEquals("Get content", "CustomContentHandler1", ob);
	}
	
	// ******************************************************
	// ******************************************************
	/**
	 * Test whether the url handler points to the highest ranking service
	 */
	public void testUrlHandlerAfterChangingRank() throws Exception {
		// increase url handler ranking for handler2
		urlServiceProperty2.put(Constants.SERVICE_RANKING, Integer.valueOf(15));
		urlHandlerSerReg2.setProperties(urlServiceProperty2);
		// test protocol1
		URL url = new URL(protocol1 + "://test");
		URLConnection con = url.openConnection();
		// handler 2 now has the highest rank
		URLConnection reqd = urlHandler2.getConnectionObject();
		assertEquals("Opening connection after changing rank", reqd, con);
		// test protocol2
		url = new URL(protocol2 + "://test");
		con = url.openConnection();
		// no change for this
		reqd = urlHandler1.getConnectionObject();
		assertEquals("Opening connection for protocol" + protocol2, reqd, con);
	}

	// ******************************************************
	// ******************************************************
	/**
	 * Test whether the content handler points to the highest ranking service
	 */
	public void testContentHandlerAfterChangingRank() throws Exception {
		// increase content handler ranking for handler2
		contentServiceProperty2.put(Constants.SERVICE_RANKING, Integer.valueOf(15));
		contentHandlerSerReg2.setProperties(contentServiceProperty2);
		URL url = new URL(protocol1 + "://test");
		Object ob = url.getContent();
		assertEquals("Get content", "CustomContentHandler2", ob);
	}

	// ******************************************************
	// ******************************************************
	/**
	 * Test whether the content handler returns the "input stream" when there is
	 * no associated content handler present
	 */
	public void testContentHandlerWhenHandlerIsUnregisterd() throws Exception {
		contentHandlerSerReg2.unregister();
		URL url = new URL(protocol1 + "://test");
		Object ob = url.getContent();
		// content handler 1 still remains.
		assertEquals("Get content", "CustomContentHandler1", ob);
		// unregister the renaming handler
		contentHandlerSerReg1.unregister();
		url = new URL(protocol1 + "://test");
		InputStream in = (InputStream) url.getContent();
		byte[] buf = new byte[in.available()];
		in.read(buf);
		assertEquals("Get content should return input stream",
				"CustomUrlConnection1", new String(buf));
	}

	// ******************************************************
	// ******************************************************
	/**
	 * Test whether the url handler throws the required exception when the
	 * registered handlers are removed
	 */
	public void testUrlHandlerWhenHandlerIsUnregistered() throws Exception {
		// unregister service 2
		urlHandlerSerReg2.unregister();
		// test protocol1
		URL url = new URL(protocol1 + "://test");
		URLConnection con = url.openConnection();
		// only handler 1 remains
		URLConnection reqd = urlHandler1.getConnectionObject();
		assertEquals("Opening connection", reqd, con);
		urlHandlerSerReg1.unregister();
		try {
			String u = protocol1 + "://test";
			new URL(u);
			fail("URL creation did not fail as expected: " + u);
		}
		catch (MalformedURLException e) {
			// expected
		}
		catch (Exception e) {
			fail("Expecting a Malformed Url Exception", e);
		}
		try {
			url.toExternalForm();
			fail("toExternalForm did not fail as expected");
		}
		catch (IllegalStateException e) {
			// expected
		}
		catch (Exception e) {
			fail("Expecting an IllegalState Exception", e);
		}
	}
}
