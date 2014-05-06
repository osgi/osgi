/*
 *	Licensed Materials - Property of IBM.
 *	(C) Copyright IBM Corporation 2011
 *	All Rights Reserved.
 *
 *	US Government Users Restricted Rights -
 *	Use, duplication or disclosure restricted by
 *	GSA ADP Schedule Contract with IBM Corporation.
 *
 *  Created by Jan S. Rellermeyer
 *  Copyright 2011 ibm.com. All rights reserved.
 */
package org.osgi.impl.service.rest;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.restlet.Component;
import org.restlet.data.Protocol;

public class Activator implements BundleActivator {

	private Component component;

	public void start(final BundleContext context) throws Exception {
		component = new Component();
		component.getServers().add(Protocol.HTTP, 8888);
		component.getClients().add(Protocol.CLAP);
		component.getDefaultHost().attach("", new RestAPI(context));
		component.start();
	}

	public void stop(final BundleContext context) throws Exception {
		component.stop();
		component = null;
	}

}
