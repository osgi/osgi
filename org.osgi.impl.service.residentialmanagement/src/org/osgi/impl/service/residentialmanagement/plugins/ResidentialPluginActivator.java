/*
 * Copyright (c) OSGi Alliance (2000-2009).
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
package org.osgi.impl.service.residentialmanagement.plugins;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
/**
 * 
 * @author Koya MORI, Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class ResidentialPluginActivator implements BundleActivator {
	private FiltersPluginActivator filters;
	private FrameworkPluginActivator framework;
	private PackageStatePluginActivator packageState;
	private ServiceStatePluginActivator serviceState;
	private BundleResourcesPluginActivator bundleResources;
	private BundleStatePluginActivator bundleState;

	
	public void start(BundleContext context) throws Exception {
		filters = new FiltersPluginActivator();
		filters.start(context);
		
		framework = new FrameworkPluginActivator();
		framework.start(context);
		
		packageState = new PackageStatePluginActivator();
		packageState.start(context);
		
		serviceState = new ServiceStatePluginActivator();
		serviceState.start(context);
		
		bundleState = new BundleStatePluginActivator();
		bundleState.start(context);
		
		bundleResources = new BundleResourcesPluginActivator();
		bundleResources.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		bundleResources.stop(context);
		bundleState.stop(context);
		serviceState.stop(context);
		packageState.stop(context);
		framework.stop(context);
		filters.stop(context);
	}

}
