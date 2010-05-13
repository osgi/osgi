/*
 * $Id$
 *
 * Copyright (c) OSGi Alliance (2000, 2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi (OSGI)
 * Specification may be subject to third party intellectual property rights,
 * including without limitation, patent rights (such a third party may or may
 * not be a member of OSGi). OSGi is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL NOT
 * INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY LOSS OF
 * PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF BUSINESS,
 * OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL, PUNITIVE OR
 * CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS DOCUMENT OR THE
 * INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.impl.service.io;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.io.*;

/**
 * Activator class - it is addressed by the framework, when the connector bundle
 * needs to be started or stopped.
 * 
 * @author OSGi Alliance
 * @version $Id$
 */
public class Activator implements BundleActivator {
	private ServiceRegistration		reg;
	private ConnectorServiceImpl	connector;

	/**
	 * Invoked when the connector bundle is started. A <tt>ConnectorService</tt>
	 * is registered.
	 * 
	 * @param bc context of this bundle
	 */
	public void start(BundleContext bc) {
		connector = new ConnectorServiceImpl(bc);
		reg = bc.registerService(ConnectorService.class.getName(), connector,
				null);
	}

	/**
	 * Invoked when a connector bundle is stopped.
	 * 
	 * @param bc the context of this bundle
	 */
	public void stop(BundleContext bc) {
		reg.unregister();
		connector.close();
	}
}
