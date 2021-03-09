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
package org.osgi.test.cases.framework.junit.syncbundlelistener;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.test.support.BundleEventCollector;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.SynchronousBundleEventCollector;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This is the bundle initially installed and started by the TestCase when
 * started. It performs the various tests and reports back to the TestCase.
 * 
 * @author Ericsson Telecom AB
 */
public class SyncBundleListenerTests extends DefaultTestBundleControl {
	static final int	timeout	= 2000 * OSGiTestCaseProperties
												.getScaling();
	
	public void testListenerOrdering() throws Exception {
		final int mask = BundleEvent.INSTALLED | BundleEvent.STARTED
				| BundleEvent.STOPPED | BundleEvent.UPDATED
				| BundleEvent.UNINSTALLED;
		final BundleEventCollector abec = new BundleEventCollector(mask);
		final BundleEventCollector ibec = new BundleEventCollector(mask);
		final BundleEventCollector sbec = new SynchronousBundleEventCollector(
				mask) {
			public void bundleChanged(BundleEvent event) {
				if ((event.getType() & mask) == 0) {
					return;
				}
				/*
				 * wait to see if async event is delivered while we block sync
				 * delivery.
				 */
				List<BundleEvent> async = ibec.getList(1, timeout);
				if (async.isEmpty()) {
					super.bundleChanged(event);
				}
			}
		};
		BundleContext context = getContext();
		context.addBundleListener(sbec);
		context.addBundleListener(abec);
		context.addBundleListener(ibec);

		Bundle tb = getContext().installBundle(
				getWebServer() + "syncbundlelistener.tb3.jar");
		try {
			ibec.getList(1, timeout); // wait for async event
			
			tb.start();
			ibec.getList(1, timeout); // wait for async event
			
			tb.stop();
			ibec.getList(1, timeout); // wait for async event

			tb.update();
			ibec.getList(1, timeout); // wait for async event
		}
		finally {
			tb.uninstall();
			ibec.getList(1, timeout); // wait for async event
			context.removeBundleListener(sbec);
			context.removeBundleListener(abec);
			context.removeBundleListener(ibec);
		}
		
		List<BundleEvent> expected = new ArrayList<>(5);
		expected.add(new BundleEvent(BundleEvent.INSTALLED, tb));
		expected.add(new BundleEvent(BundleEvent.STARTED, tb));
		expected.add(new BundleEvent(BundleEvent.STOPPED, tb));
		expected.add(new BundleEvent(BundleEvent.UPDATED, tb));
		expected.add(new BundleEvent(BundleEvent.UNINSTALLED, tb));
		List<BundleEvent> sresult = sbec.getList(5, 1);
		List<BundleEvent> aresult = abec.getList(5, 1);
		assertEquals("SBL missed events", sbec.getComparator(), expected,
				sresult);
		assertEquals("BL missed events", abec.getComparator(), expected, aresult);
	}
}
