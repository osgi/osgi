/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.framework.syncbundlelistener.tb3;

import org.osgi.framework.*;
import org.osgi.test.cases.logproxy.*;

/**
 * Bundle for the CauseFrameworkEvent test.
 * 
 * @author Ericsson Telecom AB
 */
public class CauseBundleEvent implements BundleActivator {
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
				if (_link == null)
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
	}
}
