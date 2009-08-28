package org.osgi.impl.service.upnp.cp.basedriver;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.upnp.cp.description.RootDevice;
import org.osgi.impl.service.upnp.cp.util.Control;
import org.osgi.impl.service.upnp.cp.util.EventService;
import org.osgi.impl.service.upnp.cp.util.UPnPController;
import org.osgi.impl.service.upnp.cp.util.UPnPDeviceListener;
import org.osgi.service.upnp.UPnPDevice;

public class UPnPBaseDriver implements UPnPDeviceListener {
	private UPnPController	controller;
	public RootDevice		deviceinfo;
	public Control			control;
	public EventService		eventservice;
	private Hashtable		devices;
	private BundleContext	bc;
	private Hashtable		servicerefs;
	private String			parentUDN;

	// This constructor creates the UPnPBaseDriver object based on the
	// controller and the BundleContext object.
	public UPnPBaseDriver(UPnPController controller, BundleContext bc) {
		this.controller = controller;
		this.bc = bc;
		devices = new Hashtable(10);
		servicerefs = new Hashtable(10);
	}

	// This method starts the base driver. And registers with controller for
	// getting notifications.
	public void start() {
		controller.registerDeviceListener(this);
		control = controller.getControl();
		eventservice = controller.getEventService();
	}

	// This method stops the base driver. And unregisters with controller for
	// getting notifications.
	public void stop() {
		controller.unRegisterDeviceListener(this);
		for (Enumeration enumeration = devices.elements(); enumeration.hasMoreElements();) {
			UPnPDeviceImpl dev = (UPnPDeviceImpl) enumeration.nextElement();
			dev.unsubscribe();
		}
		for (Enumeration enumeration = servicerefs.elements(); enumeration.hasMoreElements();) {
			ServiceRegistration sreg = (ServiceRegistration) enumeration.nextElement();
			if (sreg != null) {
				try {
					sreg.unregister();
				}
				catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
		control = null;
		eventservice = null;
	}

	// This method is called whenever a new CD is came to the network.
	// addDevice method gets the properties of the new CD and then registers
	// the service in the osgi framework.
	synchronized public void addDevice(String uuid, RootDevice deviceinfo) {
		if (devices.get(uuid) != null) {
			return;
		}
		this.deviceinfo = deviceinfo;
		String names = "org.osgi.service.upnp.UPnPDevice";
		String[] childrenUDN = null;
		Properties props = new Properties();
		RootDevice devinfo = deviceinfo.getDevice();
		RootDevice[] embdevices = devinfo.getEmbededDevices();
		parentUDN = devinfo.getUDN();
		if (parentUDN == null) {
			return;
		}
		if (devices.get(parentUDN) != null) {
			return;
		}
		if (embdevices != null) {
			if (embdevices.length > 0) {
				childrenUDN = new String[embdevices.length];
				for (int i = 0; i < embdevices.length; i++) {
					childrenUDN[i] = embdevices[i].getUDN();
				}
			}
		}
		if (devinfo != null) {
			UPnPDeviceImpl upnpdevice = null;
			String udn = devinfo.getUDN();
			props = getDeviceProps(props, devinfo);
			if (childrenUDN != null) {
				props.put(UPnPDevice.CHILDREN_UDN, childrenUDN);
			}
			try {
				upnpdevice = new UPnPDeviceImpl(this, devinfo, props, bc);
				System.out.println("REGISTERING UPnP DEVICE");
				ServiceRegistration sr = bc.registerService(names, upnpdevice,
						props);
				servicerefs.put(udn, sr);
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
			if (upnpdevice != null) {
				devices.put(udn, upnpdevice);
			}
		}
		if (embdevices != null) {
			if (embdevices.length > 0) {
				regEmbedded(embdevices);
			}
		}
	}

	// This method is called whenever a embeded device is found from the CD.
	// regEmbedded method gets the properties of the new embeded device and then
	// registers
	// the service in the osgi framework.
	private void regEmbedded(RootDevice[] sembdevices) {
		Properties props = new Properties();
		for (int i = 0; i < sembdevices.length; i++) {
			UPnPDeviceImpl upnpdevice = null;
			String euuid = sembdevices[i].getUDN();
			String[] childUDN = null;
			RootDevice[] embdevices111 = sembdevices[i].getEmbededDevices();
			if (devices.get(euuid) == null) {
				if (embdevices111 != null) {
					if (embdevices111.length > 0) {
						childUDN = new String[embdevices111.length];
						for (int em = 0; em < embdevices111.length; em++) {
							childUDN[em] = embdevices111[em].getUDN();
						}
						props.put(UPnPDevice.CHILDREN_UDN, childUDN);
					}
				}
				props = getDeviceProps(props, sembdevices[i]);
				props.put(UPnPDevice.PARENT_UDN, parentUDN);
				try {
					upnpdevice = new UPnPDeviceImpl(this, sembdevices[i],
							props, bc);
					System.out.println("REGISTERING Embedded UPnP DEVICE");
					ServiceRegistration sr = bc.registerService(
							"org.osgi.service.upnp.UPnPDevice", upnpdevice,
							props);
					servicerefs.put(euuid, sr);
				}
				catch (Exception e) {
					System.out.println(e.getMessage());
				}
				devices.put(euuid, upnpdevice);
			}
			if (embdevices111 != null) {
				if (embdevices111.length > 0) {
					regEmbedded(embdevices111);
				}
			}
		}
	}

	// This method is called to get all the device properties.
	Properties getDeviceProps(Properties props, RootDevice devinfo) {
		props.put("DEVICE_CATEGORY", "UPnP");
		if (devinfo.getUDN() != null) {
			props.put(UPnPDevice.UDN, devinfo.getUDN());
			props.put(UPnPDevice.ID, devinfo.getUDN());
		}
		if (devinfo.getDeviceType() != null) {
			props.put(UPnPDevice.TYPE, devinfo.getDeviceType());
		}
		if (devinfo.getManufacturer() != null) {
			props.put(UPnPDevice.MANUFACTURER, devinfo.getManufacturer());
		}
		if (devinfo.getModelName() != null) {
			props.put(UPnPDevice.MODEL_NAME, devinfo.getModelName());
		}
		if (devinfo.getFriendlyName() != null) {
			props.put(UPnPDevice.FRIENDLY_NAME, devinfo.getFriendlyName());
		}
		if (devinfo.getManufacturerURL() != null) {
			props
					.put(UPnPDevice.MANUFACTURER_URL, devinfo
							.getManufacturerURL());
		}
		if (devinfo.getModelDescription() != null) {
			props.put(UPnPDevice.MODEL_DESCRIPTION, devinfo
					.getModelDescription());
		}
		if (devinfo.getModelNumber() != null) {
			props.put(UPnPDevice.MODEL_NUMBER, devinfo.getModelNumber());
		}
		if (devinfo.getModelURL() != null) {
			props.put(UPnPDevice.MODEL_URL, devinfo.getModelURL());
		}
		if (devinfo.getSerialNumber() != null) {
			props.put(UPnPDevice.SERIAL_NUMBER, devinfo.getSerialNumber());
		}
		if (devinfo.getUPC() != null) {
			props.put(UPnPDevice.UPC, devinfo.getUPC());
		}
		if (devinfo.getPresentationURL() != null) {
			props
					.put(UPnPDevice.PRESENTATION_URL, devinfo
							.getPresentationURL());
		}
		return props;
	}

	// This method is called whenever a device is removed from the network to
	// remove the device from the osgi framework.
	synchronized public void removeDevice(String uuid) {
		try {
			if (servicerefs.get(uuid) != null) {
				ServiceRegistration sreg = (ServiceRegistration) servicerefs
						.get(uuid);
				if (sreg != null) {
					UPnPDevice rootdev = (UPnPDevice) devices.get(uuid);
					if (rootdev != null) {
						Dictionary props = rootdev.getDescriptions("en");
						String[] childUDN = (String[]) props
								.get(UPnPDevice.CHILDREN_UDN);
						if (childUDN != null) {
							for (int i = 0; i < childUDN.length; i++) {
								if (!childUDN[i].equals(uuid)) {
									removeDevice(childUDN[i]);
								}
							}
						}
						UPnPDeviceImpl dev = (UPnPDeviceImpl) devices.get(uuid);
						dev.unsubscribe();
						System.out.println("UNREGISTERING UPnP DEVICE");
						sreg.unregister();
						servicerefs.remove(uuid);
						devices.remove(uuid);
					}
				}
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}