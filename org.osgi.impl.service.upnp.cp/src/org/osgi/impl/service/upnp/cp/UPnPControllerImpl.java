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
package org.osgi.impl.service.upnp.cp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.upnp.cp.control.ControlImpl;
import org.osgi.impl.service.upnp.cp.description.Description;
import org.osgi.impl.service.upnp.cp.description.Document;
import org.osgi.impl.service.upnp.cp.description.RootDevice;
import org.osgi.impl.service.upnp.cp.event.EventServiceImpl;
import org.osgi.impl.service.upnp.cp.event.GenaServer;
import org.osgi.impl.service.upnp.cp.event.SubscriptionAlive;
import org.osgi.impl.service.upnp.cp.event.SubscriptionRenew;
import org.osgi.impl.service.upnp.cp.ssdp.SSDPComponent;
import org.osgi.impl.service.upnp.cp.util.Control;
import org.osgi.impl.service.upnp.cp.util.EventService;
import org.osgi.impl.service.upnp.cp.util.UPnPController;
import org.osgi.impl.service.upnp.cp.util.UPnPDeviceListener;
import org.osgi.service.upnp.UPnPDevice;

public class UPnPControllerImpl implements UPnPController {
	private String				IP;
	private Hashtable<String,RootDevice>	devices;
	private Vector<UPnPDeviceListener>		deviceListeners;
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
	public void start(@SuppressWarnings("hiding") BundleContext bc) {
		this.bc = bc;
		devices = new Hashtable<>(10);
		deviceListeners = new Vector<>(10, 10);
		IP = bc.getProperty("org.osgi.service.http.hostname");
		try {
			if (IP == null) {
				IP = InetAddress.getLocalHost().getHostAddress();
			}
			else {
				IP = InetAddress.getByName(IP).getHostAddress();
			}
		}
		catch (UnknownHostException e) {
			IP = "127.0.0.1";
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
	@Override
	synchronized public void addDevice(String uuid, String descurl) {
		RootDevice deviceinfo = null;
		try {
			Collection<ServiceReference<UPnPDevice>> regDevices = bc
					.getServiceReferences(UPnPDevice.class, "(" + UPnPDevice.UDN
							+ "=" + uuid + ")");
			if (regDevices.isEmpty()) {
				description = new Description(bc);
				document = description.getDocument(descurl);
				if (document != null) {
					deviceinfo = document.getRootDevice();
					if (deviceinfo != null) {
						RootDevice devinfo = deviceinfo.getDevice();
						if ((devinfo != null) && (devinfo.getUDN() != null)) {
							for (Enumeration<UPnPDeviceListener> e = deviceListeners
									.elements(); e
									.hasMoreElements();) {
								UPnPDeviceListener devlistener = e
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
	@Override
	synchronized public void removeDevice(String uuid) {
		devices.remove(uuid);
		for (Enumeration<UPnPDeviceListener> e = deviceListeners.elements(); e
				.hasMoreElements();) {
			UPnPDeviceListener devlistener = e
					.nextElement();
			devlistener.removeDevice(uuid);
		}
	}

	// This method returns the product details
	@Override
	public String getProduct() {
		return "SAMSUNG-UPnP-STACK/1.0";
	}

	// This method returns the control object.
	@Override
	public Control getControl() {
		return control;
	}

	// This method returns the event service object.
	@Override
	public EventService getEventService() {
		return eventservice;
	}

	// This method registers the device listener
	@Override
	public void registerDeviceListener(UPnPDeviceListener devlistener) {
		deviceListeners.add(devlistener);
	}

	// This method unregisters from the device listener.
	@Override
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
