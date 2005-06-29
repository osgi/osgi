/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.packageadmin.tc2;

import org.osgi.framework.*;
import org.osgi.test.cases.util.*;

/**
 *  
 *  @testcase org.osgi.service.packageadmin
 */
public class TC2 extends DefaultTestCase implements BundleActivator {
	static final String			NAME	= "test.cases.packageadmin.tc2";
	private BundleContext		BC;
	private ServiceRegistration	serviceReg;

	public String getDescription() {
		return "Exporting packages, updating";
	}

	public String getName() {
		return NAME;
	}
}
