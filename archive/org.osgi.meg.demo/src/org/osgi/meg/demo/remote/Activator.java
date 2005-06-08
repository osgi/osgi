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
import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.framework.*;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.RemoteAlertSender;
import org.osgi.service.permissionadmin.PermissionInfo;
//import org.osgi.service.dmt.DmtSession;

public class Activator implements BundleActivator {
	
	private static final String	HOST     = "localhost";
	private static final int	PORT	 = 7777;
	private static final int	PINGTIME = 10;
	
	private ServiceReference	serviceRef;
	private ServiceRegistration	remoteAlertSenderReg;
	private ClientAdaptor		clientAdaptor;

	public void start(BundleContext bc) throws BundleException {
		Bundle bundle = bc.getBundle();
		Dictionary headers = bundle.getHeaders();
		String host = (String) headers.get("Host-name");
		int pingTime = PINGTIME;
		String pingTimeStr = (String) headers.get("Ping-time");
		if (null != pingTimeStr)
			pingTime = Integer.parseInt((String) headers.get("Ping-time"));
		
		try {
			serviceRef = bc.getServiceReference(DmtAdmin.class.getName());
			DmtAdmin factory = (DmtAdmin) bc.getService(serviceRef);
			//factory.getSession(".", DmtSession.LOCK_TYPE_ATOMIC);

            // TODO remove, this is now done in DmtAdminActivator
            //initRemotePermissions(bc, "server");
            
			if (null == host) {
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
			}
			if (host == null || "".equals(host))
				host = HOST;
			
			System.out.println("Remote adapter connects to server: " + host + ":" + PORT);
			clientAdaptor = new ClientAdaptor(factory, host, PORT, pingTime);
			System.out.println("Remote adapter connected to server " + host + ":" + PORT);

            Dictionary properties = new Hashtable();
            properties.put("servers", new String[] { "admin", "test" });
			remoteAlertSenderReg = bc.registerService(RemoteAlertSender.class
					.getName(), clientAdaptor, properties);
			
			System.out.println("Remote adapter activated.");
		}
		catch (Throwable e) {
			System.out.println("Exception while starting remote adapter:");
			e.printStackTrace();
			throw new BundleException("Failure in start() method.", e);
		}
	}

    // TODO remove this method
	private void initRemotePermissions(BundleContext bc, String server)
            throws Exception {
	    System.out.println("Granting all permissions to remote server '" + server + "'.");

        ServiceReference configRef = bc.getServiceReference(ConfigurationAdmin.class.getName());
        if(configRef == null)
            throw new Exception("Cannot find ConfigurationAdmin service.");

        ConfigurationAdmin ca = (ConfigurationAdmin) bc.getService(configRef);
        if(ca == null)
            throw new Exception("ConfigurationAdmin service no longer registered.");


        Configuration config = ca.getConfiguration("org.osgi.impl.service.dmt.permissions", null);
        Dictionary properties = config.getProperties();
        if(properties == null)
            properties = new Hashtable();
        properties.put("server", new String[] { 
                new PermissionInfo(AdminPermission.class.getName(), "", "").getEncoded(), 
                new PermissionInfo(ServicePermission.class.getName(), 
                                   "org.osgi.*", ServicePermission.GET).getEncoded() 
        });
        config.update(properties);
        
        bc.ungetService(configRef);
    }

    public void stop(BundleContext bc) throws BundleException {
		remoteAlertSenderReg.unregister();
		clientAdaptor.stop();
		bc.ungetService(serviceRef);
	}
}
