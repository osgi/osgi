/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.cm.tb1;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.test.cases.cm.common.ConfigurationListenerImpl;
import org.osgi.test.cases.cm.common.Synchronizer;

/**
 * 
 * This test bundle checks if the framework prevents a bundle without permission
 * from registering a configuration listener. The permission file associated
 * with this bundle (tb1.perm) does not include the permission to registering
 * configuration listeners (
 * <code>ServicePermission[ConfigurationListener,REGISTER]</code>).
 * 
 * @author Jorge Mascena
 */
public class Activator implements BundleActivator {

	/**
	 * Tries to register a <code>ConfigurationListener</code> instance. Since
	 * this bundle doesn't have
	 * <code>ServicePermission[ConfigurationListener,REGISTER]</code>, the
	 * framework should prevent it from doing so. An exception should be thrown.
	 * 
	 * @param context the execution environment where the bundle is executed
	 * @throws java.lang.Exception if the framework prevented the bundle from
	 *         registering a <code>ConfigurationListener</code>
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		Synchronizer synchronizer = new Synchronizer();
		ConfigurationListenerImpl cl = new ConfigurationListenerImpl(
				synchronizer);
		context
				.registerService(ConfigurationListener.class.getName(), cl,
						null);
	}

	/**
	 * Nothing special to be done when finished with this bundle.
	 * 
	 * @param context the execution environment where the bundle is executed
	 * @throws java.lang.Exception
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

}
