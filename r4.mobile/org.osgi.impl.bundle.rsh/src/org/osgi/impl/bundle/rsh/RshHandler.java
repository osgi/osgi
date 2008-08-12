package org.osgi.impl.bundle.rsh;

import java.io.*;
import java.net.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.provisioning.*;
import org.osgi.service.url.*;
import org.osgi.util.tracker.*;

/**
 * This is the BundleActivator and the URLStreamHandler for the RSH protocol.
 * <p>
 * 
 * @author Benjamin Reed
 */
public class RshHandler extends AbstractURLStreamHandlerService implements
		BundleActivator {
	ServiceTracker	st;
	BundleContext	bc;

	/**
	 * Constructor for RshHandler.
	 */
	public RshHandler() {
		super();
	}

	/**
	 * @see org.osgi.framework.BundleActivator#start(BundleContext)
	 */
	public void start(BundleContext bc) throws Exception {
		this.bc = bc;
		st = new ServiceTracker(bc, ProvisioningService.class.getName(), null);
		st.open();
		Hashtable dict = new Hashtable();
		dict.put(URLConstants.URL_HANDLER_PROTOCOL, new String[] {"rsh"});
		bc.registerService("org.osgi.service.url.URLStreamHandlerService",
				this, dict);
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(BundleContext)
	 */
	public void stop(BundleContext arg0) throws Exception {
		st.close();
	}

	String getSPID() throws IOException {
		ProvisioningService prov = (ProvisioningService) st.getService();
		Object spid = prov.getInformation().get(
				ProvisioningService.PROVISIONING_SPID);
		if (spid == null)
			throw new IOException("No Service Platform ID");
		if (!(spid instanceof String))
			throw new IOException("Service Platform ID ");
		return (String) spid;
	}

	byte[] getRshSecret() throws IOException {
		ProvisioningService prov = (ProvisioningService) st.getService();
		Object secret = prov.getInformation().get(
				ProvisioningService.PROVISIONING_RSH_SECRET);
		if (secret == null)
			throw new IOException("No RSH secret set");
		if (!(secret instanceof byte[]))
			throw new IOException("RSH secret is not a byte array");
		return (byte[]) secret;
	}

	public URLConnection openConnection(URL u) throws IOException {
		String clientFG;
		ProvisioningService ps = (ProvisioningService) st.getService();
		if (ps == null) {
			throw new IOException("ProvisioningService not available");
		}
		else {
			clientFG = (String) ps.getInformation().get(
					ProvisioningService.PROVISIONING_SPID);
		}
		return new RshConnection(u, clientFG, getRshSecret());
	}
}
