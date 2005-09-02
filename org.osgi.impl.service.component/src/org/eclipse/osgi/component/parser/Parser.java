/*
 * $Header$
 *
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */

package org.eclipse.osgi.component.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.osgi.component.Log;
import org.eclipse.osgi.component.Main;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.component.ComponentConstants;
import org.osgi.util.tracker.ServiceTracker;
import org.xml.sax.SAXException;

/**
 * 
 * Parse the component description xml
 * 
 * @version $Revision$
 */
public class Parser {
	protected Main			main;

	/* ServiceTracker for parser */
	private ServiceTracker	parserTracker;

	/**
	 * Create and open a ServiceTracker which will track registered XML parsers
	 * 
	 * @param main Main object.
	 */
	public Parser(Main main) {
		this.main = main;
		parserTracker = new ServiceTracker(main.context,
				ParserConstants.SAX_FACTORY_CLASS, null);
		parserTracker.open();
	}

	public void dispose() {
		parserTracker.close();
	}

	public List getComponentDescriptions(BundleContext bundleContext) {
		ManifestElement[] xml = parseManifestHeader(bundleContext.getBundle());
		int length = xml.length;
		List result = new ArrayList(length);

		for (int i = 0; i < length; i++) {
			List components = parseComponentDescription(bundleContext, xml[i]
					.getValue());
			result.addAll(components);
		}

		return result;
	}

	/*
	 * Get the xml files from the bundle
	 * 
	 * @param bundle Bundle @return Vector holding all the xmlfiles for the
	 * specifed Bundle
	 */
	public ManifestElement[] parseManifestHeader(Bundle bundle) {

		Dictionary headers = bundle.getHeaders();
		String files = (String) headers
				.get(ComponentConstants.SERVICE_COMPONENT);

		try {
			return ManifestElement.parseHeader(
					ComponentConstants.SERVICE_COMPONENT, files);
		}
		catch (BundleException e) {
			Log
					.log(
							1,
							"[SCR] Error attempting parse Manifest Element Header. ",
							e);
			return new ManifestElement[0];
		}
	}

	/**
	 * Given the bundle and the xml filename, parset it!
	 * 
	 * @param bundle Bundle
	 * @param xml String
	 */
	public List parseComponentDescription(BundleContext bundleContext,
			String xml) {
		List result = new ArrayList();
		int fileIndex = xml.lastIndexOf('/');
		String path = fileIndex != -1 ? xml.substring(0, fileIndex) : "/";
		try {
			Enumeration urls = bundleContext.getBundle().findEntries(path,
					xml.substring(fileIndex + 1), false);
			if (urls == null || !urls.hasMoreElements()) {
				throw new BundleException("resource not found: " + xml);
			}
			URL url = (URL) urls.nextElement();
			InputStream is = url.openStream();
			SAXParserFactory parserFactory = (SAXParserFactory) parserTracker
					.getService();
			parserFactory.setNamespaceAware(true);
			parserFactory.setValidating(false);
			SAXParser saxParser = parserFactory.newSAXParser();
			saxParser.parse(is, new ParserHandler(main, bundleContext, result));
		}
		catch (IOException e) {
			Log
					.log(
							1,
							"[SCR] IOException attempting to parse ComponentDescription XML. ",
							e);
		}
		catch (BundleException e) {
			Log
					.log(
							1,
							"[SCR] BundleException attempting to parse ComponentDescription XML. ",
							e);
		}
		catch (SAXException e) {
			Log
					.log(
							1,
							"[SCR] SAXException attempting to parse ComponentDescription XML. ",
							e);
		}
		catch (ParserConfigurationException e) {
			Log
					.log(
							1,
							"[SCR] ParserConfigurationException attempting to parse ComponentDescription XML. ",
							e);
		}

		return result;
	}
}
