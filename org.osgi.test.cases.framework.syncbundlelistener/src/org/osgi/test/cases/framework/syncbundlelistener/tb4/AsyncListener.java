/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.framework.syncbundlelistener.tb4;

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
					_link.log("log proxy: Bundle tb4 starting", "");
					bc.addBundleListener(this);
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
		_link.log("log proxy: Bundle tb4 stopping", "");
		bc.ungetService(_linkRef);
		bc.removeBundleListener(this);
	}

	public synchronized void bundleChanged(BundleEvent fe) {
		try {
			if (fe.getBundle() != bc.getBundle()) {
				switch (fe.getType()) {
					case BundleEvent.INSTALLED :
						_link
								.log("BundleListener: INSTALLED",
										"This asynclistener should be called after the synchronous");
						break;
					case BundleEvent.STARTED :
						_link
								.log("BundleListener: STARTED.",
										"This asynclistener should be called after the synchronous");
						break;
					case BundleEvent.STOPPED :
						_link
								.log("BundleListener: STOPPED.",
										"This asynclistener should be called after the synchronous");
						break;
					case BundleEvent.UNINSTALLED :
						_link
								.log("BundleListener: UNINSTALLED .",
										"This asynclistener should be called after the synchronous");
						break;
					case BundleEvent.UPDATED :
						_link
								.log("BundleListener: UPDATED.",
										"This asynclistener should be called after the synchronous");
						break;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
