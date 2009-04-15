/*
 * $Id$
 *
 * Copyright (c) OSGi Alliance (2000-2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.xml.junit;

import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.util.xml.XMLParserActivator;

/**
 * This is the XML Parser Service test suite
 */
public class XMLControl extends DefaultTestBundleControl {
	/**
	 * This test iterates over all of the SAXParserFactories registered with the
	 * framework and checks that their service properties match their
	 * capabilities
	 */
	public void testSAXParserFactories() throws Exception {
		ServiceReference[] references = getContext().getServiceReferences(
				SAXParserFactory.class.getName(), null);
		if (references == null) {
			log("[WARNING: No SAXParserFactories found; testSAXParserFactories will not run]");
			return;
		}
		for (int i = 0; i < references.length; i++) {
			checkSAXParserFactory(references[i]);
		}
	}

	private void checkSAXParserFactory(ServiceReference reference)
			throws Exception {
		log("[Found: " + summarize(reference) + "]");
		Boolean ns = (Boolean) reference
				.getProperty(XMLParserActivator.PARSER_NAMESPACEAWARE);
		boolean namespaceAware = Boolean.TRUE.equals(ns);
		log("[Namespace Aware? " + namespaceAware + "]");
		Boolean v = (Boolean) reference
				.getProperty(XMLParserActivator.PARSER_VALIDATING);
		boolean validating = Boolean.TRUE.equals(v);
		log("[Validating? " + validating + "]");
		SAXParserFactory factory = (SAXParserFactory) getContext().getService(
				reference);
		try {
			factory.setNamespaceAware(namespaceAware);
			factory.setValidating(validating);
			// Try to create a parser instance
			factory.newSAXParser();
			log("[SAXParserFactory created successfully]");
		}
		finally {
			getContext().ungetService(reference);
		}
	}

	/**
	 * This test iterates over all of the DocumentBuilderFactories registered
	 * with the framework and checks that their service properties match their
	 * capabilities
	 */
	public void testDocumentBuilderFactories() throws Exception {
		ServiceReference[] references = getContext().getServiceReferences(
				DocumentBuilderFactory.class.getName(), null);
		if (references == null) {
			log("[WARNING: No DocumentBuilderFactories found; testDocumentBuilderFactories will not run]");
			return;
		}
		for (int i = 0; i < references.length; i++) {
			checkDocumentBuilderFactory(references[i]);
		}
	}

	private void checkDocumentBuilderFactory(ServiceReference reference)
			throws Exception {
		log("[Found: " + summarize(reference) + "]");
		Boolean ns = (Boolean) reference
				.getProperty(XMLParserActivator.PARSER_NAMESPACEAWARE);
		boolean namespaceAware = Boolean.TRUE.equals(ns);
		log("[Namespace Aware? " + namespaceAware + "]");
		Boolean v = (Boolean) reference
				.getProperty(XMLParserActivator.PARSER_VALIDATING);
		boolean validating = Boolean.TRUE.equals(v);
		log("[Validating? " + validating + "]");
		DocumentBuilderFactory factory = (DocumentBuilderFactory) getContext()
				.getService(reference);
		try {
			factory.setNamespaceAware(namespaceAware);
			factory.setValidating(validating);
			// Try to create a parser instance
			factory.newDocumentBuilder();
			log("[DocumentBuilderFactory created successfully]");
		}
		finally {
			getContext().ungetService(reference);
		}
	}

	/**
	 * This test verifies that the XMLParserActivator class will correctly read
	 * the META-INF/services/javax.xml.parsers.SAXParserFactory file
	 */
	public void testXMLParserActivator_SAXParserFactories() throws Exception {
		// NOTE: Issue 153 will cause this test to fail. To temporarily
		// workaround the problem and allow the
		//       test to pass, comment out all the lines marked with "Issue 153"
		Bundle tb1 = installBundle("tb1.jar");
		try {
			tb1.start();
			ServiceReference[] references = getServiceReferences(
					SAXParserFactory.class.getName(), tb1);
			assertEquals("Wrong number of services registered", 4,
					references.length); // Issue 153
			int seen = 1;
			for (int i = 0; i < references.length; i++) {
				String s = getContext().getService(references[i]).getClass()
						.getName();
				try {
					if ("org.osgi.test.cases.xml.tb1.A".equals(s)) {
						checkProperties(references[i], false, false);
						seen *= 2;
					}
					else
						if ("org.osgi.test.cases.xml.tb1.B".equals(s)) {
							checkProperties(references[i], false, true);
							seen *= 3;
						}
						else
							if ("org.osgi.test.cases.xml.tb1.C".equals(s)) {
								checkProperties(references[i], true, false);
								seen *= 5;
							}
							else
								if ("org.osgi.test.cases.xml.tb1.D".equals(s)) {
									checkProperties(references[i], true, true);
									seen *= 7;
								}
								else {
									fail("Unexpected service: " + s);
								}
				}
				finally {
					getContext().ungetService(references[i]);
				}
			}
			assertEquals(
					"Did not see all four services or the same service was registered more than once",
					2 * 3 * 5 * 7, seen); // Issue 153
		}
		finally {
			uninstallBundle(tb1);
		}
	}

	/**
	 * This test verifies that the XMLParserActivator class will correctly read
	 * the META-INF/services/javax.xml.parsers.DocumentBuilderFactory file
	 */
	public void testXMLParserActivator_DocumentBuilderFactories()
			throws Exception {
		// NOTE: Issue 153 will cause this test to fail. To temporarily
		// workaround the problem and allow the
		//       test to pass, comment out all the lines marked with "Issue 153"
		Bundle tb2 = installBundle("tb2.jar");
		try {
			tb2.start();
			ServiceReference[] references = getServiceReferences(
					DocumentBuilderFactory.class.getName(), tb2);
			assertEquals("Wrong number of services registered", 4,
					references.length); // Issue 153
			int seen = 1;
			for (int i = 0; i < references.length; i++) {
				String s = getContext().getService(references[i]).getClass()
						.getName();
				try {
					if ("org.osgi.test.cases.xml.tb2.A".equals(s)) {
						checkProperties(references[i], false, false);
						seen *= 2;
					}
					else
						if ("org.osgi.test.cases.xml.tb2.B".equals(s)) {
							checkProperties(references[i], false, true);
							seen *= 3;
						}
						else
							if ("org.osgi.test.cases.xml.tb2.C".equals(s)) {
								checkProperties(references[i], true, false);
								seen *= 5;
							}
							else
								if ("org.osgi.test.cases.xml.tb2.D".equals(s)) {
									checkProperties(references[i], true, true);
									seen *= 7;
								}
								else {
									fail("Unexpected service: " + s);
								}
				}
				finally {
					getContext().ungetService(references[i]);
				}
			}
			assertEquals(
					"Did not see all four services or the same service was registered more than once",
					2 * 3 * 5 * 7, seen); // Issue 153
		}
		finally {
			uninstallBundle(tb2);
		}
	}

	/**
	 * Verifies that the setSAXProperties() and setDOMProperties() methods work
	 * correctly when the XMLParserActivator class is extended.
	 */
	public void testXMLParserActivator_Customizations() throws Exception {
		Bundle tb3 = installBundle("tb3.jar");
		try {
			// Check the SAXParserFactory first
			ServiceReference[] references = getServiceReferences(
					SAXParserFactory.class.getName(), tb3);
			assertEquals(
					"There should be just one SAX parser, but there isn't", 1,
					references.length);
			ServiceReference ref = references[0];
			checkProperties(ref, true, true);
			assertEquals("service.description property has the wrong value",
					"A simple SAX parser", ref
							.getProperty(Constants.SERVICE_DESCRIPTION));
			assertEquals("marsupial property has the wrong value", "kangaroo",
					ref.getProperty("marsupial"));
			// Now check the DocumentBuilderFactory
			references = getServiceReferences(DocumentBuilderFactory.class
					.getName(), tb3);
			assertEquals(
					"There should be just one DOM parser, but there isn't", 1,
					references.length);
			ref = references[0];
			checkProperties(ref, true, true);
			assertEquals("service.description property has the wrong value",
					"A simple DOM parser", ref
							.getProperty(Constants.SERVICE_DESCRIPTION));
			assertEquals("marsupial property has the wrong value", "koala", ref
					.getProperty("marsupial"));
		}
		finally {
			uninstallBundle(tb3);
		}
	}

	/**
	 * Verifies that the properties that should be set by the XMLParserActivator
	 * are set and that they have appropriate values.
	 */
	private void checkProperties(ServiceReference reference,
			boolean validating, boolean namespaceAware) throws Exception {
		log("[Checking: " + summarize(reference) + "]");
		assertNotNull("service.pid property is not set", reference
				.getProperty(Constants.SERVICE_PID));
		ServiceReference[] references = getContext().getServiceReferences(
				null,
				"(service.pid=" + reference.getProperty(Constants.SERVICE_PID)
						+ ")");
		assertEquals("service.pid property is not unique", 1, references.length);
		assertNotNull("service.description property is not set", reference
				.getProperty(Constants.SERVICE_DESCRIPTION));
		Boolean v = (Boolean) reference
				.getProperty(XMLParserActivator.PARSER_VALIDATING);
		assertNotNull("parser.validating property is not set", v);
		assertEquals("parser.validating property has the wrong value",
				new Boolean(validating), v);
		Boolean ns = (Boolean) reference
				.getProperty(XMLParserActivator.PARSER_NAMESPACEAWARE);
		assertNotNull("parser.namespaceAware property is not set", ns);
		assertEquals("parser.namespaceAware property has the wrong value",
				new Boolean(namespaceAware), ns);
	}

	private String summarize(ServiceReference reference) {
		StringBuffer buffer = new StringBuffer();
		Object service = getContext().getService(reference);
		try {
			buffer.append(service);
		}
		finally {
			getContext().ungetService(reference);
		}
		String[] objectClass = (String[]) reference
				.getProperty(Constants.OBJECTCLASS);
		buffer.append(", objectClass = ").append(arrayToString(objectClass));
		Long id = (Long) reference.getProperty(Constants.SERVICE_ID);
		buffer.append(", id = ").append(id);
		String pid = (String) reference.getProperty(Constants.SERVICE_PID);
		if (pid != null) {
			buffer.append(", pid = ").append(pid);
		}
		String vendor = (String) reference
				.getProperty(Constants.SERVICE_VENDOR);
		if (vendor != null) {
			buffer.append(", vendor = ").append(vendor);
		}
		String description = (String) reference
				.getProperty(Constants.SERVICE_DESCRIPTION);
		if (description != null) {
			buffer.append(", ").append(description);
		}
		return buffer.toString();
	}

	/**
	 * Filters the service references from the OSGi registry and returns only
	 * those that were registered by the given bundle.
	 */
	private ServiceReference[] getServiceReferences(String clazz, Bundle bundle)
			throws Exception {
		ServiceReference[] references = getContext().getServiceReferences(
				clazz, null);
		Vector v = new Vector(references.length);
		if (references != null) {
			for (int i = 0; i < references.length; i++) {
				if (references[i].getBundle() == bundle) {
					v.addElement(references[i]);
				}
			}
		}
		ServiceReference[] result = new ServiceReference[v.size()];
		v.copyInto(result);
		return result;
	}
}
