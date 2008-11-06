/* 
 * $Header$
 * 
 * (c) Copyright 2008 Siemens Communications
 *
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Siemens Communications and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 */
package org.osgi.impl.service.discovery.slp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.discovery.AbstractDiscovery;
import org.osgi.impl.service.discovery.InformListenerTask;
import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import ch.ethz.iks.slp.Advertiser;
import ch.ethz.iks.slp.Locator;
import ch.ethz.iks.slp.ServiceLocationEnumeration;
import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceURL;

/**
 * TODO: remove printStackTrace and do logging instead
 * 
 * @author Phillip Konradi
 * @author Thomas Kiesslich
 * 
 */
public class SLPHandlerImpl extends AbstractDiscovery {
	private ServiceTracker locatorTracker = null;
	private ServiceTracker advertiserTracker = null;

	private Locator locator = null;
	private Advertiser advertiser = null;

	private final int POLLDELAY = 20000; // 20 sec

	private Timer t = null;

	/**
	 * 
	 * @param context
	 * @param logService
	 */
	public SLPHandlerImpl(final BundleContext context,
			final LogService logService) {
		super(context, logService);
		locatorTracker = new ServiceTracker(context, Locator.class.getName(),
				new LocatorServiceTracker(context));
		advertiserTracker = new ServiceTracker(context, Advertiser.class
				.getName(), new AdvertiserServiceTracker(context));
	}

	public void init() {
		super.init();
		locatorTracker.open();
		advertiserTracker.open();
		t = new Timer(false);
		t.schedule(new InformListenerTask(this), 0, POLLDELAY);
	}

	public void destroy() {
		locatorTracker.close();
		advertiserTracker.close();
		t.cancel();
		t = null;
		super.destroy();
	}

	private synchronized Locator getLocator() {
		return locator;
	}

	private synchronized void setLocator(final Locator locator) {
		this.locator = locator;
	}

	private synchronized Advertiser getAdvertiser() {
		return advertiser;
	}

	private synchronized void setAdvertiser(final Advertiser advertiser) {
		this.advertiser = advertiser;
	}

	/**
	 * 
	 * @see org.osgi.impl.service.discovery.ProtocolHandler#findService(org.osgi.service.discovery.ServiceDescription)
	 */
	public Collection/*<ServiceEndpointDescription>*/ findService(final String interfaceName,
			final String filter) {
		validateFilter(filter);
		List result = new ArrayList();
		// check whether SLP-Locator service exists
		Locator locator = getLocator();
		if (locator == null) {
			log(LogService.LOG_WARNING,
					"No SLP-Locator. Find operation is not executed.");
			return result;
		}
		// TODO first look at cache
		// find appropriate services
		ServiceLocationEnumeration se;
		try {
			ServiceURL svcURL = SLPServiceDescriptionAdapter.createServiceURL(
					interfaceName, null, null, null);
			log(LogService.LOG_DEBUG, "try to find services with URL="
					+ svcURL.toString());
			se = locator.findServices(svcURL.getServiceType(), null, filter);
			// TODO: in case result is empty do a search implying that service
			// interface is not java???
		} catch (Exception e) {
			log(LogService.LOG_ERROR, "Failed to find service", e);
			return result;
		}
		// iterate through found services and retrieve their attributes
		while (se.hasMoreElements()) {
			try {
				ServiceURL url = (ServiceURL) se.next();
				log(LogService.LOG_DEBUG, "try to find attributes for " + url);
				ServiceLocationEnumeration a = locator.findAttributes(url,
						null, null); // takes some time :-(
				SLPServiceDescriptionAdapter descriptionAdapter = new SLPServiceDescriptionAdapter(
						url);
				while (a.hasMoreElements()) {
					String attributes = (String) a.next();
					String key = null;
					Object value = null;
					attributes = attributes.substring(1,
							attributes.length() - 1);
					key = attributes.substring(0, attributes.indexOf("="));
					// if the value is not a String we cannot handle that value!
					// This is a limitation of the API.
					value = attributes.substring(attributes.indexOf("=") + 1);
					descriptionAdapter.addProperty(key, value);
				}
				log(LogService.LOG_DEBUG, "adding serviceURL=" + url);
				result.add(descriptionAdapter); // add to the result list
			} catch (Exception e) {
				log(LogService.LOG_ERROR, "Failed to find service", e);
			}
		}
		if (!result.isEmpty()) {
			StringBuffer buff = new StringBuffer();
			buff.append("number of services = ");
			buff.append(result.size());
			buff.append("; services = ");
			Iterator it = result.iterator();
			while(it.hasNext()) {
				buff.append("(");
				Iterator/*String*/ ifNames = ((ServiceEndpointDescription) it.next()).getInterfaceNames().iterator();
				while(ifNames.hasNext()) {
					buff.append((String) ifNames.next());
					if (ifNames.hasNext()) {
						buff.append(",");
					}
				}
				buff.append(")");
				if (it.hasNext()) {
					buff.append(",(");
				}
			}
			log(LogService.LOG_DEBUG, buff.toString());
		} else {
			log(LogService.LOG_DEBUG, "0 services found");
		}
		// TODO add to cache
		return result;
	}

	// TODO: think whether we need a version with autopublish parameter
	/**
	 * @see org.osgi.service.discovery.Discovery#publishService(java.util.Map,
	 *      java.util.Map, java.util.Map, boolean)
	 */
	public ServiceEndpointDescription publishService(
			Map/* <String, String> */javaInterfacesAndVersions,
			Map/* <String, String> */javaInterfacesAndEndpointInterfaces,
			Map/* <String, Object> */properties, boolean autopublish) {
		SLPServiceDescriptionAdapter svcDescr;
		try {
			svcDescr = new SLPServiceDescriptionAdapter(
					javaInterfacesAndVersions,
					javaInterfacesAndEndpointInterfaces, properties);
		} catch (ServiceLocationException e1) {
			e1.printStackTrace();
			log(LogService.LOG_ERROR, "Unable to create Service Description",
					e1);
			return null;
		}
		// TODO: act according autopublish parameter
		Advertiser advertiser = getAdvertiser();
		if (advertiser != null) {
			log(LogService.LOG_DEBUG, "Following service is published: "
					+ svcDescr);

			Iterator interfaces = svcDescr.getInterfaceNames().iterator();
			while(interfaces.hasNext()) {
				try {
					advertiser.register(svcDescr.getServiceURL((String) interfaces.next()),
							new Hashtable(svcDescr.getProperties()));
				} catch (ServiceLocationException e) {
					e.printStackTrace();
					log(LogService.LOG_ERROR, "failed registering service", e);
				}
			}
		} else {
			log(LogService.LOG_WARNING, "no Advertiser");
		}
		// inform the listener about the new available service
		notifyListenersOnNewServiceDescription(svcDescr);
		return svcDescr;
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#unpublish(org.osgi.service.discovery.ServiceEndpointDescription)
	 */
	public void unpublishService(
			final ServiceEndpointDescription serviceDescription) {
		validateServiceDescription(serviceDescription);
		log(LogService.LOG_DEBUG, "unpublish service "
				+ serviceDescription.toString());
		if (serviceDescription instanceof SLPServiceDescriptionAdapter) {
			SLPServiceDescriptionAdapter slpSvcDescr = (SLPServiceDescriptionAdapter) serviceDescription;
			Advertiser advertiser = getAdvertiser();
			if (advertiser != null) {
				Iterator interfaceNames = slpSvcDescr.getInterfaceNames().iterator();
				while(interfaceNames.hasNext()) {
					String interfaceName = (String) interfaceNames.next();
					try {
						log(LogService.LOG_DEBUG, "unpublish service "
								+ slpSvcDescr.getServiceURL(interfaceName));
						advertiser.deregister(slpSvcDescr
								.getServiceURL(interfaceName));

						// inform listeners about removal
						notifyListenersOnRemovedServiceDescription(serviceDescription);
					} catch (ServiceLocationException e) {
						e.printStackTrace();
						log(LogService.LOG_ERROR,
								"failed to deregister service for interface "
										+ interfaceName, e);
					}
				}
			} else {
				log(LogService.LOG_WARNING, "no Advertiser");
			}
		}
	}

	/**
	 * Private tracker class to track the locator services.
	 * 
	 * @author kt32483
	 * 
	 */
	private class LocatorServiceTracker implements ServiceTrackerCustomizer {
		BundleContext context = null;

		public LocatorServiceTracker(BundleContext bc) {
			context = bc;
		}

		public Object addingService(ServiceReference reference) {
			Locator loc = (Locator) context.getService(reference);
			setLocator(loc);
			log(LogService.LOG_INFO, "bound Locator");
			return loc;
		}

		public void modifiedService(ServiceReference reference, Object service) {
			Locator loc = (Locator) context.getService(reference);
			setLocator(loc);
			log(LogService.LOG_INFO, "rebound Locator");
		}

		public void removedService(ServiceReference reference, Object service) {
			context.ungetService(reference);
			setLocator(null);
			log(LogService.LOG_INFO, "unbound Locator");
		}
	}

	/**
	 * 
	 */
	private class AdvertiserServiceTracker implements ServiceTrackerCustomizer {
		BundleContext context = null;

		public AdvertiserServiceTracker(BundleContext bc) {
			context = bc;
		}

		public Object addingService(final ServiceReference reference) {
			Advertiser adv = (Advertiser) context.getService(reference);
			setAdvertiser(adv);
			log(LogService.LOG_INFO, "bound Advertiser");
			return adv;
		}

		public void modifiedService(final ServiceReference reference,
				Object service) {
			Advertiser adv = (Advertiser) context.getService(reference);
			setAdvertiser(adv);
			log(LogService.LOG_INFO, "rebound Advertiser");
		}

		public void removedService(final ServiceReference reference,
				Object service) {
			context.ungetService(reference);
			setAdvertiser(null);
			log(LogService.LOG_INFO, "unbound Advertiser");
		}
	}

	/**
	 * For test purposes only.
	 * 
	 * @return
	 */
	protected Set getListener() {
		return getListenerAndFilter().keySet();
	}
}
