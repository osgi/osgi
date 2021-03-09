/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.impl.service.upnp.cd.ssdp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPIcon;
import org.osgi.service.upnp.UPnPService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

// This class used for listening UPnPDevice registration and unregiration. It 
// maintains the all databses related to exporting devices and gives serviceinformation all 
// other layers. It gives description and icon resources for http requests. 
public class UPnPExporter implements
		ServiceTrackerCustomizer<UPnPDevice,UPnPDevice>, HttpContext {
	private SSDPComponent	ssdpcomp;
	private ServiceTracker<UPnPDevice,UPnPDevice>	tracker;
	private BundleContext	bc;
	private HttpService		httpService;
	private Hashtable<String,String>				resources;
	private Hashtable<String,UPnPIcon>				iconsTable;
	private Hashtable<String,File>					createdFiles;
	private File			storeDir;
	public static Hashtable<String,UPnPService>		eventTable;
	public static Hashtable<String,UPnPService>		controlTable;

	// This constructor constructs the UPnPExporter.
	UPnPExporter(SSDPComponent comp, BundleContext bc, HttpService httpService) {
		//System.out.println("creating the exporter");
		this.bc = bc;
		this.httpService = httpService;
		ssdpcomp = comp;
		resources = new Hashtable<>(10);
		eventTable = new Hashtable<>(10);
		controlTable = new Hashtable<>(10);
		iconsTable = new Hashtable<>(10);
		createdFiles = new Hashtable<>(10);
	}

	// This method starts the exporting devices functionality.
	public void startExporter() {
		//System.out.println("starting the exporter");
		Filter filter = null;
		try {
			httpService.registerResources("/samsungcd", "/samsungcd", this);
			filter = bc.createFilter("(&(objectclass="
					+ UPnPDevice.class.getName() + ")(UPnP.export=*))");
			//System.out.println(filter);
			tracker = new ServiceTracker<UPnPDevice,UPnPDevice>(bc, filter,
					this);
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
			return eventTable.get(subUrl);
		}
		return null;
	}

	// This methods returns a control table entry.
	synchronized public static UPnPService getControlEntry(String subUrl) {
		if (subUrl != null) {
			return controlTable.get(subUrl);
		}
		return null;
	}

	// This methods removes a event table entry.
	synchronized void removeEventEntry(String uuid) {
		for (Enumeration<String> enumeration = eventTable.keys(); enumeration
				.hasMoreElements();) {
			String key1 = enumeration.nextElement();
			if (key1.indexOf(uuid) != -1) {
				eventTable.remove(key1);
				ssdpcomp.eventregistry.removeServiceId(key1);
			}
		}
	}

	// This methods removes a control table entry.
	synchronized void removeControlEntry(String uuid) {
		for (Enumeration<String> enumeration = controlTable.keys(); enumeration
				.hasMoreElements();) {
			String key1 = enumeration.nextElement();
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
		for (Enumeration<String> enumeration = resources.keys(); enumeration
				.hasMoreElements();) {
			String key1 = enumeration.nextElement();
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
		for (Enumeration<String> enumeration = iconsTable.keys(); enumeration
				.hasMoreElements();) {
			String key1 = enumeration.nextElement();
			if (key1.indexOf(uuid) != -1) {
				iconsTable.remove(key1);
			}
		}
	}

	// This methods returns URL of requested resource.
	@Override
	public URL getResource(String name) {
		if (name != null) {
			if (name.startsWith("/samsungcd/")) {
				name = name.substring(name.indexOf("/samsungcd/") + 11);
				String xmlfile = resources.get(name);
				if (xmlfile != null) {
					return getURL(name, xmlfile, null);
				}
				else {
					UPnPIcon icon = iconsTable.get(name);
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
    @SuppressWarnings("deprecation")
    URL getURL(String name, String source, InputStream in) {
		storeDir = bc.getDataFile("cdresources");
		if (!storeDir.exists()) {
			storeDir.mkdir();
		}
		if (source != null) {
			String fname = "cdresources/" + changeXMLFileName(name);
			File resXmlFile = bc.getDataFile(fname);
			try {
				FileOutputStream xmlOut = new FileOutputStream(resXmlFile);
				xmlOut.write(source.getBytes());
				xmlOut.close();
				createdFiles.put(name, resXmlFile);
				return resXmlFile.toURL();
			}
			catch (Exception e) {
				// ignored
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
						File icon = bc.getDataFile(fname);
						FileOutputStream iconOut = new FileOutputStream(icon);
						iconOut.write(bytes);
						iconOut.close();
						createdFiles.put(name, icon);
						return icon.toURL();
					}
				}
				catch (Exception e1) {
					// ignored
				}
			}
		return null;
	}

	// This methods returns the mime type for http request.
	// It checks request is for xml file or icon
	@Override
	public String getMimeType(String name) {
		if (name != null) {
			if (name.startsWith("/samsungcd/")) {
				name = name.substring(name.indexOf("/samsungcd/") + 11);
				if (resources.get(name) != null) {
					return "text/xml";
				}
				else
					if (iconsTable.get(name) != null) {
						UPnPIcon icon = iconsTable.get(name);
						return icon.getMimeType();
					}
			}
		}
		return null;
	}

	// This methods is checks security for response.
	// It return true cos currently not checking any security.
	@Override
	public boolean handleSecurity(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		return true;
	}

	// This method called by service tracker when ever it tracks the new
	// UPnPDevice Service added
	@Override
	public UPnPDevice addingService(ServiceReference<UPnPDevice> sr) {
		System.out.println("adding service upnp exported device");
		UPnPDevice device = bc.getService(sr);
		if (sr.getProperty("UPnP.device.parentUDN") == null) {
			exportDevice(device);
		}
		return device;
	}

	// This method called by service tracker when ever it tracks the new
	// UPnPDevice Service removed
	@Override
	public void removedService(ServiceReference<UPnPDevice> sr,
			UPnPDevice obj) {
		String udn = (String) sr.getProperty("UPnP.device.UDN");
		removeDevice(udn);
	}

	// This method called by service tracker when ever the new UPnPDevice
	// Service modified
	@Override
	public void modifiedService(ServiceReference<UPnPDevice> sr,
			UPnPDevice obj) {
		// empty
	}

	// This method removes all databases information and sends NOTIFY BYE
	// meessages of perticuler device.
	void removeDevice(String udn) {
		ssdpcomp.sendDeviceForNotifyBye(udn);
		removeEventEntry(udn);
		removeControlEntry(udn);
		removeResource(udn);
		removeIconEntry(udn);
		for (Enumeration<String> enumeration = createdFiles.keys(); enumeration
				.hasMoreElements();) {
			String key1 = enumeration.nextElement();
			if (key1.indexOf(udn) != -1) {
				try {
					File storedFile = createdFiles.get(key1);
					storedFile.delete();
				}
				catch (Exception e) {
					// ignored
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
			for (Enumeration<String> enumeration = createdFiles
					.keys(); enumeration.hasMoreElements();) {
				String key1 = enumeration.nextElement();
				try {
					File storedFile = createdFiles.get(key1);
					storedFile.delete();
				}
				catch (Exception e) {
					// ignored
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
