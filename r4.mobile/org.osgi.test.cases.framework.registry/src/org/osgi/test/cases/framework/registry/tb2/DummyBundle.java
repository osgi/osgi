/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.framework.registry.tb2;

import java.util.*;

import org.osgi.framework.*;

public class DummyBundle
	implements
		BundleActivator
{
	public void start(BundleContext context) throws Exception {
		Dictionary properties = new Hashtable();
		context.registerService( BundleContext.class.getName(), context, properties );
	}

	public void stop(BundleContext context) throws Exception {
	}
}

