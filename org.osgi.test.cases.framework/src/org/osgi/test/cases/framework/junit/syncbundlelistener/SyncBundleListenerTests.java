/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
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
				List async = ibec.getList(1, timeout); 
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
		
		List expected = new ArrayList(5);
		expected.add(new BundleEvent(BundleEvent.INSTALLED, tb));
		expected.add(new BundleEvent(BundleEvent.STARTED, tb));
		expected.add(new BundleEvent(BundleEvent.STOPPED, tb));
		expected.add(new BundleEvent(BundleEvent.UPDATED, tb));
		expected.add(new BundleEvent(BundleEvent.UNINSTALLED, tb));
		List sresult = sbec.getList(5, 1);
		List aresult = abec.getList(5, 1);
		assertEquals("SBL missed events", sbec.getComparator(), expected,
				sresult);
		assertEquals("BL missed events", abec.getComparator(), expected, aresult);
	}
}
