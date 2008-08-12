package org.osgi.impl.service.upnp.cd;

import java.net.InetAddress;
import java.util.Hashtable;
import org.osgi.framework.*;
import org.osgi.impl.service.upnp.cd.control.ControlImpl;
import org.osgi.impl.service.upnp.cd.event.*;
import org.osgi.impl.service.upnp.cd.ssdp.SSDPComponent;

public class UPnPController implements BundleActivator {
	private String				devexp;
	private SSDPComponent		ssdpcomp;
	private ControlImpl			control;
	private Hashtable			httpServiceDesc;
	private GenaServer			server;
	private SubscriptionAlive	sa;
	private BundleContext		bc;

	// This method starts the CD bundle
	public void start(BundleContext bc) throws Exception {
		//System.out.println("UPnP : starting CD exporter");
		this.bc = bc;
		String IP = null;
		try {
			devexp = "2100";
			InetAddress inet = InetAddress.getLocalHost();
			IP = inet.getHostAddress();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		//Control starting
		control = new ControlImpl();
		//Eventing starting
		EventRegistry eventregistry = new EventRegistry(IP);
		sa = new SubscriptionAlive();
		sa.start();
		try {
			server = new GenaServer(8180, control, bc, eventregistry);
			server.start();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		//SSDDP starting
		try {
			ssdpcomp = new SSDPComponent(devexp, bc, server.getServerIP(),
					eventregistry);
			ssdpcomp.startSSDPFunctionality();
			//System.out.println("Discovery started");
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// This method stops the bundle
	public void stop(BundleContext bc) throws Exception {
		try {
			if (ssdpcomp != null) {
				ssdpcomp.killSSDP();
			}
			if (server != null) {
				server.shutdown();
			}
			if (sa != null) {
				sa.surrender(false);
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}
}
