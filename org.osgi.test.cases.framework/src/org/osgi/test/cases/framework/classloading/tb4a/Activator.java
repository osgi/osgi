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

package org.osgi.test.cases.framework.classloading.tb4a;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.framework.classloading.exports.service.SomeService;

/**
 * A bundle activator which execute some tests
 * 
 * @author left
 * @author $Id$
 */
public class Activator implements BundleActivator {

	/**
	 * Creates a new instance of Activator
	 */
	public Activator() {

	}

	/**
	 * Start the bundle and the tests
	 * 
	 * @param context the bundle context
	 * @throws Exception if some problem occur
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		int index;
		int ranking;
		int highestRanking;
		long id;
		long oid;
		@SuppressWarnings("unused")
		long lowestId;
		Object rankingProperty;
		ServiceReference< ? > sr;
		ServiceReference< ? >[] srs;
		
		srs = context.getServiceReferences(SomeService.class.getName(),
				"(version=*)");
		sr = context.getServiceReference(SomeService.class.getName());

		try {
			// Check the test pre-conditions
			if (srs.length < 2) {
				throw new BundleException(
						"This test needs at least two registered services to run");
			}

			// Get the service reference with the highest rank
			index = -1;
			highestRanking = Integer.MIN_VALUE;
			lowestId = Long.MAX_VALUE;
			for (int i = 0; i < srs.length; i++) {
				rankingProperty = srs[i].getProperty(Constants.SERVICE_RANKING);

				if (rankingProperty instanceof Integer) {
					ranking = ((Integer) srs[i]
							.getProperty(Constants.SERVICE_RANKING)).intValue();
				}
				else {
					ranking = 0;
				}

				if (ranking > highestRanking) {
					highestRanking = ranking;
					index = i;
				}
				else {
					if (ranking == highestRanking) {
						oid = ((Long) srs[index]
											.getProperty(Constants.SERVICE_ID)).longValue();

						id = ((Long) srs[i]
											.getProperty(Constants.SERVICE_ID)).longValue();
						
						if (oid > id) {
							index = i;
						}
						
					}
				}
			}
			
			if (!sr.equals(srs[index])) {
				throw new BundleException(
						"The service reference is not the highest ranked thats BundleContext.getServiceReferences() returns");
			}
		}
		finally {
			context.ungetService(sr);
			for (int i = 0; i < srs.length; i++) {
				context.ungetService(srs[i]);
			}
		}
	}

	/**
	 * Stop the bundle
	 * 
	 * @param context the bundle context
	 * @throws Exception if some problem occur
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

}
