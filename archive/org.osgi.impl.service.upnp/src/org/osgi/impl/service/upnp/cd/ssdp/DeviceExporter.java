package org.osgi.impl.service.upnp.cd.ssdp;

import java.util.*;
import java.net.*;
import java.io.*;
import org.osgi.framework.*;
import org.osgi.service.upnp.*;

// This class used for making descriptions of device and making device details for NOITFY. 
// When new device exports it sends NOTIFY messages and adds information to exported databases. 
// When device removed it sends NOTIFY BYE messages and removes information from exported databases. 
public class DeviceExporter implements Runnable, SSDPConstants {
	private SSDPComponent	ssdpcomp;
	private UPnPDevice		device;
	private UPnPExporter	exporter;
	private BundleContext	bc;
	private MulticastSocket	multicastsock;
	private InetAddress		ssdpinet;
	private String			notifyMessage;
	private Hashtable		notifyDevices;
	private String			deviceForBye;
	private byte			ttl	= 4;

	// This constructor is the Default constructor.
	DeviceExporter() {
	}

	// This constructor is used for exporting a new device.
	DeviceExporter(SSDPComponent comp, UPnPDevice device,
			UPnPExporter exporter, BundleContext bc) {
		ssdpcomp = comp;
		this.device = device;
		this.exporter = exporter;
		this.bc = bc;
		notifyDevices = new Hashtable(3);
		try {
			multicastsock = new MulticastSocket(HOST_PORT);
			ssdpinet = InetAddress.getByName(HOST_IP);
			multicastsock.joinGroup(ssdpinet);
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	// This method invokes by thread pool thred service
	public void run() {
		if (device != null) {
			if (makeDescriptions()) {
				addDeviceForSsdp();
			}
			else {
				System.out
						.println("Exported device does not have required values");
			}
		}
		multicastsock = null;
		ssdpinet = null;
	}

	// This method makes all necessary descriptions for exported device.
	// UPnP specification does not encourage more than 2 levels.
	boolean makeDescriptions() {
		Dictionary props = device.getDescriptions("en");
		String[] childUDN1 = null;
		String des = null;
		String rootUDN = null;
		String location = null;
		String server = null;
		if (props != null) {
			childUDN1 = (String[]) props.get("UPnP.device.childrenUDN");
			rootUDN = ((String) props.get("UPnP.device.UDN")).replace(':', '-');
			if (props.get("UPnP.device.SERVER") != null) {
				server = (String) props.get("UPnP.device.SERVER");
			}
			else {
				server = ssdpcomp.server;
			}
		}
		if (rootUDN != null) {
			location = "http://" + ssdpcomp.baseURL + "/samsungcd/" + rootUDN;
		}
		String des1 = "<?xml version=\"1.0\"?>\r\n"
				+ "<root xmlns=\"urn:schemas-upnp-org:device-1-0\">\r\n"
				+ "<specVersion>\r\n" + "  <major>1</major>\r\n"
				+ "  <minor>0</minor>\r\n" + "</specVersion>\r\n" + "<URLBase>"
				+ "http://" + ssdpcomp.baseURL + "</URLBase>\r\n";
		String tempDes = makeDeviceDescription(device, "root", location, server);
		if (tempDes == null) {
			return false;
		}
		des1 = des1 + tempDes;
		String des2 = "<presentationURL>"
				+ (String) props.get("UPnP.presentationURL")
				+ "</presentationURL>\r\n" + "</device>\r\n" + "</root>";
		String des3 = null;
		if (childUDN1 != null) {
			des3 = makeEmbDescriptions(childUDN1, location, server);
		}
		if ((des3 != null) && (des3.length() > 40)) {
			des = des1 + des3 + des2;
		}
		else {
			des = des1 + des2;
		}
		exporter.addResource(rootUDN, des);
		return true;
	}

	// This method makes embedded devices desription.
	String makeEmbDescriptions(String[] childUDN1, String location,
			String server) {
		try {
			Thread.sleep(3000);//giving time for ebedded device registraion
		}
		catch (Exception e) {
		}
		String des3 = null;
		des3 = "<deviceList>\r\n";
		for (int i = 0; i < childUDN1.length; i++) {
			try {
				ServiceReference[] sref = bc.getServiceReferences(
						"org.osgi.service.upnp.UPnPDevice", "("
								+ UPnPDevice.UDN + "=" + childUDN1[i] + ")");
				if (sref != null) {
					UPnPDevice embDevice1 = (UPnPDevice) bc.getService(sref[0]);
					String tdes = makeDeviceDescription(embDevice1, "emb",
							location, server);
					if (tdes != null) {
						des3 = des3 + tdes;
						Dictionary embprops1 = embDevice1.getDescriptions("en");
						String[] childUDN2 = (String[]) embprops1
								.get("UPnP.device.childrenUDN");
						if (childUDN2 != null) {
							String embDes = makeEmbDescriptions(childUDN2,
									location, server);
							if (embDes != null) {
								des3 = des3 + embDes;
							}
						}
						des3 = des3
								+ "<presentationURL>"
								+ (String) embprops1
										.get("UPnP.presentationURL")
								+ "</presentationURL>\r\n" + "</device>\r\n";
					}
				}
			}
			catch (Exception e) {
				return null;
			}
		}
		des3 = des3 + "</deviceList>\r\n";
		return des3;
	}

	// This method reads root device and embedded devices for NOTIFY.
	void addDeviceForSsdp() {
		for (Enumeration enum = notifyDevices.elements(); enum
				.hasMoreElements();) {
			DeviceDetails devDet = (DeviceDetails) enum.nextElement();
			sendDeviceForNotify(devDet);
		}
		System.out
				.println("Sending notify messages is finished for intiall devices");
	}

	// This method makes perticuler device desription.
	String makeDeviceDescription(UPnPDevice dev, String type, String location,
			String server) {
		Dictionary props = dev.getDescriptions("en");
		String des = "<device>\r\n";
		String udn = (String) props.get("UPnP.device.UDN");
		DeviceDetails devDet = new DeviceDetails();
		if (type.equals("root")) {
			devDet.setRoot();
		}
		devDet.setServer(server);
		devDet.setLocation(location);
		devDet.setUUID(udn);
		if (props.get("UPnP.device.type") != null) {
			des = des + "<deviceType>" + (String) props.get("UPnP.device.type")
					+ "</deviceType>\r\n";
			devDet.setDevice((String) props.get("UPnP.device.type"));
		}
		else {
			return null;
		}
		if (props.get("UPnP.device.friendlyName") != null) {
			des = des + "<friendlyName>"
					+ (String) props.get("UPnP.device.friendlyName")
					+ "</friendlyName>\r\n";
		}
		else {
			return null;
		}
		if (props.get("UPnP.device.manufacturer") != null) {
			des = des + "<manufacturer>"
					+ (String) props.get("UPnP.device.manufacturer")
					+ "</manufacturer>\r\n";
		}
		else {
			return null;
		}
		if (props.get("UPnP.device.manufacturerURL") != null) {
			des = des + "<manufacturerURL>"
					+ (String) props.get("UPnP.device.manufacturerURL")
					+ "</manufacturerURL>\r\n";
		}
		if (props.get("UPnP.device.modelDescription") != null) {
			des = des + "<modelDescription>"
					+ (String) props.get("UPnP.device.modelDescription")
					+ "</modelDescription>\r\n";
		}
		if (props.get("UPnP.device.modelName") != null) {
			des = des + "<modelName>"
					+ (String) props.get("UPnP.device.modelName")
					+ "</modelName>\r\n";
		}
		else {
			return null;
		}
		if (props.get("UPnP.device.modelNumber") != null) {
			des = des + "<modelNumber>"
					+ (String) props.get("UPnP.device.modelNumber")
					+ "</modelNumber>\r\n";
		}
		if (props.get("UPnP.device.modelURL") != null) {
			des = des + "<modelURL>"
					+ (String) props.get("UPnP.device.modelURL")
					+ "</modelURL>\r\n";
		}
		if (props.get("UPnP.device.serialNumber") != null) {
			des = des + "<serialNumber>"
					+ (String) props.get("UPnP.device.serialNumber")
					+ "</serialNumber>\r\n";
		}
		if (udn != null) {
			des = des + "<UDN>" + udn + "</UDN>\r\n";
		}
		else {
			return null;
		}
		if (props.get("UPnP.device.UPC") != null) {
			des = des + "<UPC>" + (String) props.get("UPnP.device.UPC")
					+ "</UPC>\r\n";
		}
		UPnPIcon[] icons = dev.getIcons("en");
		if (icons != null) {
			String iconDes = makeIconsDescription(icons, udn);
			if (iconDes != null) {
				des = des + iconDes;
			}
		}
		UPnPService[] services = dev.getServices();
		if (services != null) {
			String servDes = makeServicesDescription(services, udn, devDet);
			if (servDes != null) {
				des = des + servDes;
			}
		}
		notifyDevices.put(udn, devDet);
		return des;
	}

	// This method makes all service description of perticuler device.
	String makeServicesDescription(UPnPService[] services, String udn,
			DeviceDetails devDet) {
		String servDes = "<serviceList>\r\n";
		for (int i = 0; i < services.length; i++) {
			String type = services[i].getType();
			String id = services[i].getId();
			String scpd = udn + id;
			scpd = scpd.replace(':', '-');
			if ((type != null) && (id != null)) {
				servDes = servDes + "<service>\r\n" + "<serviceType>" + type
						+ "</serviceType>\r\n" + "<serviceId>" + id
						+ "</serviceId>\r\n" + "<SCPDURL>" + "/samsungcd/"
						+ scpd + "</SCPDURL>\r\n" + "<controlURL>" + "http://"
						+ ssdpcomp.genaURL + "/" + scpd + "</controlURL>\r\n"
						+ "<eventSubURL>" + "http://" + ssdpcomp.genaURL + "/"
						+ scpd + "</eventSubURL>\r\n" + "</service>\r\n";
				String SCPD = makeSCPD(services[i]);
				if (SCPD != null) {
					devDet.setServices(type);
					exporter.addResource(scpd, SCPD);
					exporter.addEventEntry("http://" + ssdpcomp.genaURL + "/"
							+ scpd, services[i]);
					exporter.addControlEntry("http://" + ssdpcomp.genaURL + "/"
							+ scpd, services[i]);
				}
			}
		}
		servDes = servDes + "</serviceList>\r\n";
		return servDes;
	}

	// This method makes perticuler service description.
	String makeSCPD(UPnPService service) {
		String scpdDes = "<?xml version=\"1.0\"?>\r\n"
				+ "<scpd xmlns=\"urn:schemas-upnp-org:service-1-0\">\r\n"
				+ "<specVersion>\r\n" + "    <major>1</major>\r\n"
				+ "    <minor>0</minor>\r\n" + "</specVersion>\r\n";
		UPnPAction[] actions = service.getActions();
		if (actions != null) {
			String actionDes = makeActionsDescription(service, actions);
			if (actionDes != null) {
				scpdDes = scpdDes + actionDes + "</scpd>";
			}
			else {
				return null;
			}
		}
		return scpdDes;
	}

	// This method makes all actions descriptions of perticuler service.
	String makeActionsDescription(UPnPService service, UPnPAction[] actions) {
		String actionDes = "<actionList>\r\n";
		Vector variables = new Vector(5, 5);
		for (int i = 0; i < actions.length; i++) {
			String retval = actions[i].getReturnArgumentName();
			String[] inargs = actions[i].getInputArgumentNames();
			String[] outargs = actions[i].getOutputArgumentNames();
			actionDes = actionDes + "<action>\r\n" + "<name>"
					+ actions[i].getName() + "</name>\r\n";
			if (inargs != null || outargs != null) {
				actionDes = actionDes + "<argumentList>\r\n";
				if (inargs != null) {
					for (int in = 0; in < inargs.length; in++) {
						UPnPStateVariable var = actions[i]
								.getStateVariable(inargs[in]);
						if (var != null) {
							actionDes = actionDes + "<argument>\r\n"
									+ "	<name>" + inargs[in] + "</name>\r\n"
									+ "	<direction>in</direction>\r\n"
									+ "	<relatedStateVariable>" + var.getName()
									+ "</relatedStateVariable>\r\n"
									+ "</argument>\r\n";
							//variables.add(var);
						}
					}
				}
				if (outargs != null) {
					for (int ou = 0; ou < outargs.length; ou++) {
						UPnPStateVariable var = actions[i]
								.getStateVariable(outargs[ou]);
						if (var != null) {
							actionDes = actionDes + "<argument>\r\n"
									+ "	<name>" + outargs[ou] + "</name>\r\n"
									+ "	<direction>out</direction>\r\n";
							if (retval == null) {
								actionDes = actionDes + "	<retval />\r\n";
							}
							else {
								actionDes = actionDes + "	<retval>" + retval
										+ "</retval>\r\n";
							}
							actionDes = actionDes + "	<relatedStateVariable>"
									+ var.getName()
									+ "</relatedStateVariable>\r\n"
									+ "</argument>\r\n";
							//variables.add(var);
						}
					}
				}
				actionDes = actionDes + "</argumentList>\r\n";
			}
			actionDes = actionDes + "</action>\r\n";
		}
		actionDes = actionDes + "</actionList>\r\n";
		UPnPStateVariable[] vars = service.getStateVariables();
		for (int i = 0; i < vars.length; i++)
			variables.add(vars[i]);
		if (variables.size() > 0) {
			return actionDes + makeStatevarDescription(variables);
		}
		return null;
	}

	// This method makes all state variables descriptions of perticuler service.
	String makeStatevarDescription(Vector stVars) {
		String varsDes = "<serviceStateTable>\r\n";
		for (Enumeration enum = stVars.elements(); enum.hasMoreElements();) {
			UPnPStateVariable var = (UPnPStateVariable) enum.nextElement();
			if (var.sendsEvents()) {
				varsDes = varsDes + "<stateVariable sendEvents=\"yes\">\r\n";
			}
			else {
				varsDes = varsDes + "<stateVariable sendEvents=\"no\">\r\n";
			}
			varsDes = varsDes + "	<name>" + var.getName() + "</name>\r\n"
					+ "	<dataType>" + var.getUPnPDataType() + "</dataType>\r\n"
					+ "	<defaultValue>" + var.getDefaultValue()
					+ "</defaultValue>\r\n";
			String[] allowedVals = var.getAllowedValues();
			if (allowedVals != null) {
				varsDes = varsDes + "	<allowedValueList>\r\n";
				for (int i = 0; i < allowedVals.length; i++) {
					varsDes = varsDes + "		<allowedValue>" + allowedVals[i]
							+ "</allowedValue>\r\n";
				}
				varsDes = varsDes + "</allowedValueList>\r\n";
			}
			else {
				Class cs = var.getJavaDataType();
				String type = cs.getName();
				Number max = var.getMaximum();
				Number min = var.getMinimum();
				Number step = var.getStep();
				if ((max != null) && (min != null)) {
					varsDes = varsDes + "	<allowedValueRange>\r\n";
					if (type.equals("java.lang.Integer")) {
						varsDes = varsDes + "		<minimum>" + min.intValue()
								+ "</minimum>\r\n" + "		<maximum>"
								+ max.intValue() + "</maximum>\r\n"
								+ "		<step>" + step.intValue() + "</step>\r\n";
					}
					else
						if (type.equals("java.lang.Short")) {
							varsDes = varsDes + "		<minimum>"
									+ min.shortValue() + "</minimum>\r\n"
									+ "		<maximum>" + max.shortValue()
									+ "</maximum>\r\n" + "		<step>"
									+ step.shortValue() + "</step>\r\n";
						}
						else
							if (type.equals("java.lang.Long")) {
								varsDes = varsDes + "		<minimum>"
										+ min.longValue() + "</minimum>\r\n"
										+ "		<maximum>" + max.longValue()
										+ "</maximum>\r\n" + "		<step>"
										+ step.longValue() + "</step>\r\n";
							}
							else
								if (type.equals("java.lang.Byte")) {
									varsDes = varsDes + "		<minimum>"
											+ min.byteValue()
											+ "</minimum>\r\n" + "		<maximum>"
											+ max.byteValue()
											+ "</maximum>\r\n" + "		<step>"
											+ step.byteValue() + "</step>\r\n";
								}
								else
									if (type.equals("java.lang.Float")) {
										varsDes = varsDes + "		<minimum>"
												+ min.floatValue()
												+ "</minimum>\r\n"
												+ "		<maximum>"
												+ max.floatValue()
												+ "</maximum>\r\n" + "		<step>"
												+ step.floatValue()
												+ "</step>\r\n";
									}
									else
										if (type.equals("java.lang.Double")) {
											varsDes = varsDes + "		<minimum>"
													+ min.doubleValue()
													+ "</minimum>\r\n"
													+ "		<maximum>"
													+ max.doubleValue()
													+ "</maximum>\r\n"
													+ "		<step>"
													+ step.doubleValue()
													+ "</step>\r\n";
										}
					varsDes = varsDes + "	</allowedValueRange>\r\n";
				}
			}
			varsDes = varsDes + "</stateVariable>\r\n";
		}
		varsDes = varsDes + "</serviceStateTable>\r\n";
		return varsDes;
	}

	// This method makes all iconss descriptions of perticuler device.
	String makeIconsDescription(UPnPIcon[] icons, String udn) {
		String icondes = "<iconList>\r\n";
		for (int i = 0; i < icons.length; i++) {
			int width = icons[i].getWidth();
			int height = icons[i].getHeight();
			int depth = icons[i].getDepth();
			String mime = icons[i].getMimeType();
			InputStream in = null;
			try {
				in = icons[i].getInputStream();
			}
			catch (IOException e) {
				System.out.println(e.getMessage());
			}
			String iconudn = udn + i;
			if ((width != -1) && (height != -1) && (depth != -1)
					&& (mime != null) && (in != null)) {
				icondes = icondes + "<icon>\r\n" + "<mimetype>" + mime
						+ "</mimetype>\r\n" + "<width>" + width
						+ "</width>\r\n" + "<height>" + height
						+ "</height>\r\n" + "<depth>" + depth + "</depth>\r\n"
						+ "<url>" + "samsungcd/" + iconudn + "</url>\r\n"
						+ "</icon>\r\n";
			}
			exporter.addIconEntry(iconudn, icons[i]);
		}
		icondes = icondes + "</iconList>\r\n";
		return icondes;
	}

	// This method calls sendNotify() method by passing the required details.
	public void sendDeviceForNotify(DeviceDetails devDet) {
		String uuid = devDet.getUUID();
		if (devDet.isRoot()) {
			sendNotify("root", uuid, devDet.getDevType(), null, devDet
					.getLocation(), devDet.getServer());
		}
		else {
			sendNotify("embdev", uuid, devDet.getDevType(), null, devDet
					.getLocation(), devDet.getServer());
		}
		Hashtable services = devDet.getServices();
		for (Enumeration e = services.elements(); e.hasMoreElements();) {
			String serType = (String) e.nextElement();
			String serVer = (String) services.get(serType);
			sendNotify("service", uuid, null, serType, devDet.getLocation(),
					devDet.getServer());
		}
		if (ssdpcomp.allDeviceDetails.get(uuid) == null) {
			ssdpcomp.allDeviceDetails.put(uuid, devDet);
		}
		long sentPacketsTime = (System.currentTimeMillis() + ssdpcomp.cacheValue * 1000);
		devDet.setTime(sentPacketsTime);
	}

	// This method sends notify messages according input parameters. It sets the
	// expiraion time
	// sent packets time of device.
	void sendNotify(String type, String uuid, String deviceType,
			String serviceType, String location, String server) {
		String usn = null;
		String nt = null;
		if (type.equals("root")) {
			usn = new String(uuid + "::" + ROOTDEVICE);
			sendMessage(ROOTDEVICE, usn, location, server);
			nt = uuid;
			usn = uuid;
			sendMessage(nt, usn, location, server);
			nt = new String(deviceType);
			usn = new String(uuid + "::" + deviceType);
			sendMessage(nt, usn, location, server);
		}
		else
			if (type.equals("embdev")) {
				nt = uuid;
				usn = uuid;
				sendMessage(nt, usn, location, server);
				nt = new String(deviceType);
				usn = new String(uuid + "::" + deviceType);
				sendMessage(nt, usn, location, server);
			}
			else
				if (type.equals("service")) {
					nt = new String(serviceType);
					usn = new String(uuid + "::" + serviceType);
					sendMessage(nt, usn, location, server);
				}
	}

	// This method used by sendNotify method for sending messages
	void sendMessage(String nt, String usn, String location, String server) {
		String notify = null;
		byte data[] = null;
		DatagramPacket packet = null;
		notify = makeNotifyMessage(nt, usn, location, server);
		if (notify != null) {
			data = notify.getBytes();
			packet = new DatagramPacket(data, data.length, ssdpinet, 1900);
			for (int i = 0; i <= 1; i++) {
				try {
					multicastsock.setTimeToLive(ttl);
					multicastsock.send(packet);
				}
				catch (IOException e) {
				}
			}
		}
	}

	// This method used for making NOTIFY messages
	String makeNotifyMessage(String nt, String usn, String location,
			String server) {
		notifyMessage = new String(NOTIFY + "\r\n" + HOST + "\r\n" + CACHE
				+ " max-age = " + ssdpcomp.cacheControl + "\r\n" + LOC + " "
				+ location + "\r\n" + NT + " " + nt + "\r\n" + NTS + " "
				+ ALIVE + "\r\n" + SERVER + " " + server + "\r\n" + USN + " "
				+ usn + "\r\n\r\n");
		return notifyMessage;
	}
}
