package org.osgi.impl.service.upnp.cp.basedriver;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Dictionary;
import java.util.Enumeration;
import org.osgi.framework.*;
import org.osgi.service.upnp.*;
import org.osgi.impl.service.upnp.cp.description.*;

public class UPnPDeviceImpl implements UPnPDevice {
	private UPnPBaseDriver	basedriver;
	private RootDevice		deviceinfo;
	private Hashtable		services;
	private Properties		props;
	private UPnPIcon[]		icons;
	private String			devid;
	private String			devtype;
	private BundleContext	bc;

	UPnPDeviceImpl() {
	}

	UPnPDeviceImpl(UPnPBaseDriver basedriver, RootDevice deviceinfo,
			Properties props, BundleContext bc) {
		this.basedriver = basedriver;
		this.deviceinfo = deviceinfo;
		this.props = props;
		this.bc = bc;
		devid = deviceinfo.getUDN();
		devtype = deviceinfo.getDeviceType();
		services = new Hashtable(10);
		ServiceInfo[] serviceinfo = deviceinfo.getServices();
		extractDeviceInfo();
	}

	// This method sets all the service and icon details to the services and the
	// icons object.
	void extractDeviceInfo() {
		ServiceInfo[] serviceinfo = deviceinfo.getServices();
		if (serviceinfo != null) {
			for (int i = 0; i < serviceinfo.length; i++) {
				if (serviceinfo[i] != null) {
					UPnPServiceImpl upnpservice = new UPnPServiceImpl(
							serviceinfo[i], basedriver, devid, devtype, bc);
					services.put(serviceinfo[i].getServiceID(), upnpservice);
				}
				else
					System.out.println("##Service info is null " + devid);
			}
		}
		Icon[] iconss = deviceinfo.getIcons();
		if (iconss != null) {
			icons = new UPnPIcon[iconss.length];
			for (int j = 0; j < icons.length; j++) {
				UPnPIconImpl upnpicon = new UPnPIconImpl(iconss[j],
						basedriver.deviceinfo.getURLBase());
				icons[j] = upnpicon;
			}
		}
	}

	// This method returns the UPnPService object based on the given name of the
	// service.
	public UPnPService getService(String s) {
		if (services.get(s) != null) {
			return (UPnPService) services.get(s);
		}
		return null;
	}

	// This method returns all UPnPServices
	public UPnPService[] getServices() {
		UPnPService[] upnpservs = new UPnPService[services.size()];
		int i = 0;
		for (Enumeration e = services.elements(); e.hasMoreElements(); i++) {
			upnpservs[i] = (UPnPService) e.nextElement();
		}
		return upnpservs;
	}

	// This method returns the upnp icons based on the given name.
	public UPnPIcon[] getIcons(String s) {
		return icons;
	}

	// This method returns all the device properties based on the given locale
	public Dictionary getDescriptions(String locale1) {
		return props;
	}

	// This method invokes the unsubscribe method from all the UPnP services.
	public void unsubscribe() {
		for (Enumeration enumeration = services.elements(); enumeration.hasMoreElements();) {
			try {
				UPnPServiceImpl serv = (UPnPServiceImpl) enumeration.nextElement();
				serv.unsubscribe();
				serv.closeTracker();
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
