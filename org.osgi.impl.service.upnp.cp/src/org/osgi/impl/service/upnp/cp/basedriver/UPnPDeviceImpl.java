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
package org.osgi.impl.service.upnp.cp.basedriver;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.osgi.framework.BundleContext;
import org.osgi.impl.service.upnp.cp.description.Icon;
import org.osgi.impl.service.upnp.cp.description.RootDevice;
import org.osgi.impl.service.upnp.cp.description.ServiceInfo;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPIcon;
import org.osgi.service.upnp.UPnPService;

public class UPnPDeviceImpl implements UPnPDevice {
	private UPnPBaseDriver	basedriver;
	private RootDevice		deviceinfo;
	private Hashtable<String,UPnPServiceImpl>	services;
    private Dictionary<String, Object> props;
	private UPnPIconImpl[]  icons;
	private String			devid;
	private String			devtype;
	private BundleContext	bc;

	UPnPDeviceImpl() {
	}

	UPnPDeviceImpl(UPnPBaseDriver basedriver, RootDevice deviceinfo,
            Dictionary<String, Object> props, BundleContext bc) {
		this.basedriver = basedriver;
		this.deviceinfo = deviceinfo;
		this.props = props;
		this.bc = bc;
		devid = deviceinfo.getUDN();
		devtype = deviceinfo.getDeviceType();
		services = new Hashtable<>(10);
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
			icons = new UPnPIconImpl[iconss.length];
			for (int j = 0; j < icons.length; j++) {
				UPnPIconImpl upnpicon = new UPnPIconImpl(iconss[j],
						basedriver.deviceinfo.getURLBase());
				icons[j] = upnpicon;
			}
		}
	}

	// This method returns the UPnPService object based on the given name of the
	// service.
	@Override
	public UPnPService getService(String s) {
		checkState();
		if (services.get(s) != null) {
			return services.get(s);
		}
		return null;
	}

	// This method returns all UPnPServices
	@Override
	public UPnPService[] getServices() {
		checkState();
		UPnPService[] upnpservs = new UPnPService[services.size()];
		int i = 0;
		for (Enumeration<UPnPServiceImpl> e = services.elements(); e
				.hasMoreElements(); i++) {
			upnpservs[i] = e.nextElement();
		}
		return upnpservs;
	}

	// This method returns the upnp icons based on the given name.
	@Override
	public UPnPIcon[] getIcons(String s) {
		checkState();
		return icons;
	}

	// This method returns all the device properties based on the given locale
	@Override
	public Dictionary<String,Object> getDescriptions(String locale1) {
		return props;
	}

	// This method invokes the unsubscribe method from all the UPnP services.
	public void unsubscribe() {
		for (Enumeration<UPnPServiceImpl> enumeration = services
				.elements(); enumeration.hasMoreElements();) {
			try {
				UPnPServiceImpl serv = enumeration.nextElement();
				serv.unsubscribe();
				serv.closeTracker();
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	/* package-private */void release() {
		this.bc = null;
		this.basedriver = null;
		this.deviceinfo = null;
		releaseServices();
		releaseIcons();
	}

	private void checkState() {
		if (null == this.basedriver) {
			throw new IllegalStateException(
					"UPnP device has been removed from the network. Device id: "
							+ devid);
		}
	}

	private void releaseServices() {
		Iterator<UPnPServiceImpl> servicesIter = this.services.values()
				.iterator();
		for (int i = 0, servicesCount = this.services.size(); i < servicesCount; i++) {
			servicesIter.next().release();
		}
	}

	private void releaseIcons() {
		if (null == this.icons) {
			return;
		}
		for (int i = 0; i < icons.length; i++) {
			icons[i].release();
		}
	}
	
}
