package org.osgi.impl.service.upnp.cp;

import java.util.*;
import java.net.InetAddress;
import org.osgi.service.upnp.*;
import org.osgi.framework.*;
import org.osgi.impl.service.upnp.cp.ssdp.SSDPComponent;
import org.osgi.impl.service.upnp.cp.description.*;
import org.osgi.impl.service.upnp.cp.util.*;
import org.osgi.impl.service.upnp.cp.control.*;
import org.osgi.impl.service.upnp.cp.event.*;

public class UPnPControllerImpl implements UPnPController {
	private String				IP;
	private Hashtable			devices;
	private Vector				deviceListeners;
	private SSDPComponent		ssdpcomp;
	private Description			description;
	private Document			document;
	private EventServiceImpl	eventservice;
	private Control				control;
	private GenaServer			server;
	private SubscriptionAlive	subscriptionAlive;
	private SubscriptionRenew	subscriptionRenew;
	private BundleContext		bc;

	// This method starts the upnp controller.It start all layers functionality.
	public void start(BundleContext bc) {
		this.bc = bc;
		devices = new Hashtable(10);
		deviceListeners = new Vector(10, 10);
		try {
			InetAddress inet = InetAddress.getLocalHost();
			IP = inet.getHostAddress();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		//Control starting
		control = new ControlImpl(this);
		//Eventing starting
		eventservice = new EventServiceImpl(this, IP);
		subscriptionAlive = new SubscriptionAlive(eventservice);
		subscriptionAlive.start();
		subscriptionRenew = new SubscriptionRenew(eventservice);
		subscriptionRenew.start();
		//Http starting
		try {
			server = new GenaServer(8090, eventservice);
			server.start();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		//SSDDP starting
		try {
			ssdpcomp = new SSDPComponent(this);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// This method is called when the device is added to the network.
	synchronized public void addDevice(String uuid, String descurl) {
		RootDevice deviceinfo = null;
		try {
			ServiceReference[] regDevices = bc.getServiceReferences(
					"org.osgi.service.upnp.UPnPDevice", "(" + UPnPDevice.UDN
							+ "=" + uuid + ")");
			if (regDevices == null) {
				description = new Description(bc);
				document = description.getDocument(descurl);
				if (document != null) {
					deviceinfo = document.getRootDevice();
					if (deviceinfo != null) {
						RootDevice devinfo = deviceinfo.getDevice();
						if ((devinfo != null) && (devinfo.getUDN() != null)) {
							for (Enumeration e = deviceListeners.elements(); e
									.hasMoreElements();) {
								UPnPDeviceListener devlistener = (UPnPDeviceListener) e
										.nextElement();
								devlistener.addDevice(uuid, deviceinfo);
							}
							devices.put(uuid, deviceinfo);
						}
					}
				}
				else {
					System.out.println("DESCRIPTION : DEVICE INFO IS NULL");
				}
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// This method is got called when the device is removed from the network
	synchronized public void removeDevice(String uuid) {
		devices.remove(uuid);
		for (Enumeration e = deviceListeners.elements(); e.hasMoreElements();) {
			UPnPDeviceListener devlistener = (UPnPDeviceListener) e
					.nextElement();
			devlistener.removeDevice(uuid);
		}
	}

	// This method returns the product details
	public String getProduct() {
		return "SAMSUNG-UPnP-STACK/1.0";
	}

	// This method returns the control object.
	public Control getControl() {
		return control;
	}

	// This method returns the event service object.
	public EventService getEventService() {
		return eventservice;
	}

	// This method registers the device listener
	public void registerDeviceListener(UPnPDeviceListener devlistener) {
		deviceListeners.add(devlistener);
	}

	// This method unregisters from the device listener.
	public void unRegisterDeviceListener(UPnPDeviceListener devlistener) {
		deviceListeners.remove(devlistener);
	}

	// This method stops the ssdp component
	public void stop() {
		try {
			if (ssdpcomp != null) {
				ssdpcomp.killSSDP();
			}
			if (server != null) {
				server.shutdown();
			}
			if (subscriptionAlive != null) {
				subscriptionAlive.surrender(false);
			}
			if (subscriptionRenew != null) {
				subscriptionRenew.surrender(false);
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
