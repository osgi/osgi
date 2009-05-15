/*
 * Copyright (c) OSGi Alliance 2002.
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
package org.osgi.test.cases.framework.dynpkgimport.tb3;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.framework.dynpkgimport.exported.TestService;
import org.osgi.test.cases.framework.dynpkgimport.tlx.TestLib;

public class Activator implements BundleActivator, TestService {
	ServiceRegistration	testServiceReg	= null;

	public void start(BundleContext bc) {
		Dictionary props = new Hashtable();
		testServiceReg = bc.registerService(TestService.class.getName(), this,
				props);
	}

	public void stop(BundleContext bc) {
		testServiceReg.unregister();
		testServiceReg = null;
	}

	public void test1() throws Exception {
		// This test has changed for OSGi R4;
		// dynamic wires are only established after trying to load 
		// from the private bundle first
		TestLib tl = new TestLib();
		if (!"tb3".equals(tl.version())) {
			throw new Exception("TestLib version != tb3");
		}
	}
}
