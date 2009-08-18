/*
 * Copyright (c) 2008 TIBCO Software Inc.
 * All Rights Reserved.
 */

package org.osgi.test.cases.distribution.tb2;

import java.util.Hashtable;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.distribution.DistributionConstants;
import org.osgi.test.cases.distribution.common.A;
import org.osgi.test.cases.distribution.common.B;
import org.osgi.test.cases.distribution.common.TestServiceImpl;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 */
public class Activator implements BundleActivator {

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		TestServiceImpl impl = new TestServiceImpl();
		
		Hashtable properties = new Hashtable();
		properties.put(DistributionConstants.REMOTE_INTERFACES, "*");
		properties.put("implementation", "2");
		context.registerService(new String[]{A.class.getName(), B.class.getName()}, impl, properties);
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

}
