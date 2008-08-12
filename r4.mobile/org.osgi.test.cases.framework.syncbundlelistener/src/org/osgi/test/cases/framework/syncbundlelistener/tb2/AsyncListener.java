/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.framework.syncbundlelistener.tb2;

import org.osgi.framework.*;
import org.osgi.test.cases.logproxy.*;

/**
 * Bundle for the CauseFrameworkEvent test.
 * 
 * @author Ericsson Telecom AB
 */
public class AsyncListener implements BundleActivator, BundleListener {
	ServiceReference	_linkRef;
	LogProxy			_link;
	BundleContext		bc;

	/**
	 * Starts the bundle.
	 */
	public void start(BundleContext context) {
		bc = context;
		try {
			_linkRef = bc.getServiceReference(LogProxy.class.getName());
			if (_linkRef != null) {
				_link = (LogProxy) bc.getService(_linkRef);
				if (_link != null) {
					try {
						bc.addBundleListener(this);
						bc.addBundleListener(this);
						bc.addBundleListener(this);
						_link.log("Registered a listener multiple times", "OK");
					}
					catch (Exception e) {
						_link
								.log(
										"Got exception when registering multiple bunldelisteners",
										"FAIL");
						;
					}
				}
				else
					System.out.println("Didn't get a link");
			}
			else {
				System.out.println("Didn't get a service ref");
			}
		}
		catch (Exception e) {
			System.out.println("exception in start of cause event");
			e.printStackTrace();
		}
	}

	public void stop(BundleContext bc) {
		bc.ungetService(_linkRef);
		bc.removeBundleListener(this);
	}

	public synchronized void bundleChanged(BundleEvent fe) {
		try {
			if (fe.getBundle() != bc.getBundle()) {
				switch (fe.getType()) {
					case BundleEvent.INSTALLED :
						_link.log("BundleListener: BundleEvent.INSTALLED", "");
						break;
					case BundleEvent.STARTED :
						_link.log("BundleListeber: Bundle.STARTED", "");
						break;
					case BundleEvent.STOPPED :
						_link.log("BundleListener: Bundle.STOPPED", "");
						break;
					case BundleEvent.UNINSTALLED :
						_link.log("BundleListener: Bundle.UNINSTALL.", "");
						break;
					case BundleEvent.UPDATED :
						_link.log("BundleListener: Bundle.UPDATED", "");
						break;
				}
			}
			else {
				if (fe.getType() == BundleEvent.INSTALLED)
					_link.log("BundleListener caught it's own install event",
							"FAIL");
				//It's ok for a bundle to catch it's own events except for the
				// install event
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
