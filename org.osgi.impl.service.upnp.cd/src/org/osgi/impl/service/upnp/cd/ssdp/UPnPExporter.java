package org.osgi.impl.service.upnp.cd.ssdp;

import java.util.*;
import java.io.*;
import java.net.*;
import org.osgi.util.tracker.*;
import org.osgi.service.http.*;
import javax.servlet.http.*;
import org.osgi.framework.*;
import org.osgi.service.upnp.*;

// This class used for listening UPnPDevice registration and unregiration. It 
// maintains the all databses related to exporting devices and gives serviceinformation all 
// other layers. It gives description and icon resources for http requests. 
public class UPnPExporter implements ServiceTrackerCustomizer, HttpContext {
	private SSDPComponent	ssdpcomp;
	private ServiceTracker	tracker;
	private BundleContext	bc;
	private HttpService		httpService;
	private Hashtable		resources;
	private Hashtable		iconsTable;
	private Hashtable		createdFiles;
	private File			storeDir;
	public static Hashtable	eventTable;
	public static Hashtable	controlTable;

	// This constructor constructs the UPnPExporter.
	UPnPExporter(SSDPComponent comp, BundleContext bc, HttpService httpService) {
		//System.out.println("creating the exporter");
		this.bc = bc;
		this.httpService = httpService;
		ssdpcomp = comp;
		resources = new Hashtable(10);
		eventTable = new Hashtable(10);
		controlTable = new Hashtable(10);
		iconsTable = new Hashtable(10);
		createdFiles = new Hashtable(10);
	}

	// This method starts the exporting devices functionality.
	public void startExporter() {
		//System.out.println("starting the exporter");
		ServiceReference[] regExpdevices = null;
		Filter filter = null;
		try {
			httpService.registerResources("/samsungcd", "/samsungcd", this);
			filter = bc.createFilter("(&(objectclass="
					+ UPnPDevice.class.getName() + ")(UPnP.export=*))");
			//System.out.println(filter);
			tracker = new ServiceTracker(bc, filter, this);
			tracker.open();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// This methods exports perticuler device.
	void exportDevice(UPnPDevice device) {
		DeviceExporter exporter = new DeviceExporter(ssdpcomp, device, this, bc);
		(new Thread(exporter)).start();
	}

	// This methods adds a entry into event table.
	synchronized void addEventEntry(String subUrl, UPnPService service) {
		if ((subUrl != null) && (service != null)) {
			eventTable.put(subUrl, service);
		}
	}

	// This methods adds a entry into control table.
	synchronized void addControlEntry(String subUrl, UPnPService service) {
		if ((subUrl != null) && (service != null)) {
			controlTable.put(subUrl, service);
		}
	}

	// This methods returns a event table entry.
	synchronized public static UPnPService getEventEntry(String subUrl) {
		if (subUrl != null) {
			return (UPnPService) eventTable.get(subUrl);
		}
		return null;
	}

	// This methods returns a control table entry.
	synchronized public static UPnPService getControlEntry(String subUrl) {
		if (subUrl != null) {
			return (UPnPService) controlTable.get(subUrl);
		}
		return null;
	}

	// This methods removes a event table entry.
	synchronized void removeEventEntry(String uuid) {
		for (Enumeration enumeration = eventTable.keys(); enumeration.hasMoreElements();) {
			String key1 = (String) enumeration.nextElement();
			if (key1.indexOf(uuid) != -1) {
				eventTable.remove(key1);
				ssdpcomp.eventregistry.removeServiceId(key1);
			}
		}
	}

	// This methods removes a control table entry.
	synchronized void removeControlEntry(String uuid) {
		for (Enumeration enumeration = controlTable.keys(); enumeration.hasMoreElements();) {
			String key1 = (String) enumeration.nextElement();
			if (key1.indexOf(uuid) != -1) {
				controlTable.remove(key1);
			}
		}
	}

	// This methods adds a entry into resources table.
	synchronized void addResource(String name, String xmlfile) {
		if ((name != null) && (xmlfile != null)) {
			resources.put(name, xmlfile);
		}
	}

	// This methods removes a resources table entry.
	synchronized void removeResource(String uuid) {
		for (Enumeration enumeration = resources.keys(); enumeration.hasMoreElements();) {
			String key1 = (String) enumeration.nextElement();
			if (key1.indexOf(uuid) != -1) {
				resources.remove(key1);
			}
		}
	}

	// This methods adds a entry into icon table.
	synchronized void addIconEntry(String iconudn, UPnPIcon icon) {
		if ((iconudn != null) && (icon != null)) {
			iconsTable.put(iconudn, icon);
		}
	}

	// This methods removes a icon table entry.
	synchronized void removeIconEntry(String uuid) {
		for (Enumeration enumeration = iconsTable.keys(); enumeration.hasMoreElements();) {
			String key1 = (String) enumeration.nextElement();
			if (key1.indexOf(uuid) != -1) {
				iconsTable.remove(key1);
			}
		}
	}

	// This methods returns URL of requested resource.
	public URL getResource(String name) {
		if (name != null) {
			if (name.startsWith("/samsungcd/")) {
				name = name.substring(name.indexOf("/samsungcd/") + 11);
				String xmlfile = (String) resources.get(name);
				if (xmlfile != null) {
					return getURL(name, xmlfile, null);
				}
				else {
					UPnPIcon icon = (UPnPIcon) iconsTable.get(name);
					if (icon != null) {
						try {
							InputStream in = icon.getInputStream();
							if (in != null) {
								return getURL(name, null, in);
							}
						}
						catch (Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}
			}
		}
		return null;
	}

	// This methods returns translated file name.
	String changeXMLFileName(String fName) {
		return fName.replace(':', '-');
	}

	// This methods returns URL.
	URL getURL(String name, String source, InputStream in) {
		storeDir = new File("cdresources");
		if (!storeDir.exists()) {
			storeDir.mkdir();
		}
		if (source != null) {
			String fname = "cdresources/" + changeXMLFileName(name);
			File resXmlFile = new File(fname);
			try {
				FileOutputStream xmlOut = new FileOutputStream(resXmlFile);
				xmlOut.write(source.getBytes());
				xmlOut.close();
				createdFiles.put(name, resXmlFile);
				return resXmlFile.toURL();
			}
			catch (Exception e) {
			}
		}
		else
			if (in != null) {
				try {
					int nofBytes = in.available();
					byte[] bytes = new byte[nofBytes];
					int resBytes = in.read(bytes, 0, nofBytes);
					if (resBytes == nofBytes) {
						String fname = "cdresources/" + name;
						File icon = new File(fname);
						FileOutputStream iconOut = new FileOutputStream(icon);
						iconOut.write(bytes);
						iconOut.close();
						createdFiles.put(name, icon);
						return icon.toURL();
					}
				}
				catch (Exception e1) {
				}
			}
		return null;
	}

	// This methods returns the mime type for http request.
	// It checks request is for xml file or icon
	public String getMimeType(String name) {
		if (name != null) {
			if (name.startsWith("/samsungcd/")) {
				name = name.substring(name.indexOf("/samsungcd/") + 11);
				if (resources.get(name) != null) {
					return "text/xml";
				}
				else
					if (iconsTable.get(name) != null) {
						UPnPIcon icon = (UPnPIcon) iconsTable.get(name);
						return icon.getMimeType();
					}
			}
		}
		return null;
	}

	// This methods is checks security for response.
	// It return true cos currently not checking any security.
	public boolean handleSecurity(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		return true;
	}

	// This method called by service tracker when ever it tracks the new
	// UPnPDevice Service added
	public Object addingService(ServiceReference sr) {
		System.out.println("adding service upnp exported device");
		UPnPDevice device = (UPnPDevice) bc.getService(sr);
		if (sr.getProperty("UPnP.device.parentUDN") == null) {
			exportDevice(device);
		}
		return device;
	}

	// This method called by service tracker when ever it tracks the new
	// UPnPDevice Service removed
	public void removedService(ServiceReference sr, Object obj) {
		String udn = (String) sr.getProperty("UPnP.device.UDN");
		removeDevice(udn);
	}

	// This method called by service tracker when ever the new UPnPDevice
	// Service modified
	public void modifiedService(ServiceReference sr, Object obj) {
	}

	// This method removes all databases information and sends NOTIFY BYE
	// meessages of perticuler device.
	void removeDevice(String udn) {
		ssdpcomp.sendDeviceForNotifyBye(udn);
		removeEventEntry(udn);
		removeControlEntry(udn);
		removeResource(udn);
		removeIconEntry(udn);
		for (Enumeration enumeration = createdFiles.keys(); enumeration.hasMoreElements();) {
			String key1 = (String) enumeration.nextElement();
			if (key1.indexOf(udn) != -1) {
				try {
					File storedFile = (File) createdFiles.get(key1);
					storedFile.delete();
				}
				catch (Exception e) {
				}
			}
		}
	}

	// This method kills all databases of exporter
	public void stopExporter() {
		tracker.close();
		resources = null;
		eventTable = null;
		controlTable = null;
		iconsTable = null;
		try {
			for (Enumeration enumeration = createdFiles.keys(); enumeration.hasMoreElements();) {
				String key1 = (String) enumeration.nextElement();
				try {
					File storedFile = (File) createdFiles.get(key1);
					storedFile.delete();
				}
				catch (Exception e) {
				}
			}
			if (storeDir != null)
				storeDir.delete();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
