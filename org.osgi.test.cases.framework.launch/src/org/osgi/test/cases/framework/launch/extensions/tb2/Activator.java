/*
 * Copyright (c) OSGi Alliance (2013, 2016). All Rights Reserved.
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
package org.osgi.test.cases.framework.launch.extensions.tb2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
	private static final String name = Activator.class.getPackage().getName();
	private static final String RESULTS = "org.osgi.tests.cases.framework.launch.results";
	public void start(BundleContext context) throws Exception {
		List<String> results = getResults(name);
		results.add("START");
		throw new RuntimeException(name);
	}

	private static List<String> getResults(String test) {
		@SuppressWarnings("unchecked")
		Map<String, List<String>> resultMap = (Map<String, List<String>>) System
				.getProperties().get(RESULTS);
		if (resultMap == null) {
			resultMap = new HashMap<String, List<String>>();
			System.getProperties().put(RESULTS, resultMap);
		}

		List<String> results = resultMap.get(test);
		if (results == null) {
			results = new ArrayList<String>();
			resultMap.put(test, results);
		}
		return results;
	}


	public void stop(BundleContext context) throws Exception {
		getResults(name).add("STOP");
	}

}
