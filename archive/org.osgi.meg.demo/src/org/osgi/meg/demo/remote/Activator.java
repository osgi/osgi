/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.meg.demo.remote;

import java.io.*;
import org.osgi.framework.*;
import org.osgi.impl.service.dmt.api.RemoteAlertSender;
import org.osgi.service.dmt.DmtFactory;

public class Activator implements BundleActivator {
	private static final int	PORT	= 7777;
	private ServiceReference	serviceRef;
	private ServiceRegistration	remoteAlertSenderReg;
	private ClientAdaptor		clientAdaptor;

	public void start(BundleContext bc) throws BundleException {
		System.out.println("Remote adapter activated.");
		try {
			serviceRef = bc.getServiceReference(DmtFactory.class.getName());
			DmtFactory factory = (DmtFactory) bc.getService(serviceRef);
			// TODO get the parameters from configuration
			String host = null;
			System.out
					.println("Enter host name (press 'enter' for localhost): ");
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(System.in));
				host = reader.readLine();
			}
			catch (IOException e) {
				throw new BundleException("I/O Error reading host name.", e);
			}
			if (host == null || "".equals(host))
				host = "localhost";
			//creating the adaptor
			clientAdaptor = new ClientAdaptor(factory, host, PORT);
			System.out.println("Remote adapter connected to server " + host
					+ ":" + PORT);
			//registering the remote alert sender service
			remoteAlertSenderReg = bc.registerService(RemoteAlertSender.class
					.getName(), clientAdaptor, null);
		}
		catch (Throwable e) {
			System.out.println("Exception while starting remote adapter:");
			e.printStackTrace(System.out);
			throw new BundleException("Failure in start() method.", e);
		}
	}

	public void stop(BundleContext bc) throws BundleException {
		//unregistering the service
		remoteAlertSenderReg.unregister();
		clientAdaptor.stop();
		bc.ungetService(serviceRef);
	}
}
