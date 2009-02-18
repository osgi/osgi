/*
 * $Id$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.framework.fragments.tb7f;

import java.net.MalformedURLException;
import java.net.URL;

import org.osgi.framework.Bundle;

/**
 *
 * Test class used for running test related to URL permissions.
 * 
 * @version $Revision$
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
