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
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.discovery.ProtocolHandler;
import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import ch.ethz.iks.slp.Advertiser;
import ch.ethz.iks.slp.Locator;
import ch.ethz.iks.slp.ServiceLocationEnumeration;
import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceType;
import ch.ethz.iks.slp.ServiceURL;

/**
 * @author Tim Diekmann
 * @author Thomas Kiesslich
 * 
 */
public class SLPHandlerImpl implements ProtocolHandler {
	private LogService logService = null;
	private ServiceTracker locatorTracker = null;
	private ServiceTracker advertiserTracker = null;

	private Locator locator = null;
	private Advertiser advertiser = null;

	/**
	 * 
	 * @param context
	 * @param logService
	 */
	public SLPHandlerImpl(final BundleContext context,
			final LogService logService) {
		this.logService = logService;

		locatorTracker = new ServiceTracker(context, Locator.class.getName(),
				new LocatorServiceTracker(context));

		advertiserTracker = new ServiceTracker(context, Advertiser.class
				.getName(), new AdvertiserServiceTracker(context));
	}

	public void init() {
		locatorTracker.open();
		advertiserTracker.open();
	}

	public void destroy() {
		locatorTracker.close();
		advertiserTracker.close();
	}

	/**
	 * 
	 * @see org.osgi.impl.service.discovery.ProtocolHandler#findService(org.osgi.service.discovery.ServiceDescription)
	 */
	public ServiceEndpointDescription[] findService(final String interfaceName,
			final Filter filter) {
		List result = new ArrayList();
		// create SLP service description
		SLPServiceDescriptionAdapter serviceDescriptionAdapter;
		try {
			serviceDescriptionAdapter = new SLPServiceDescriptionAdapter(
					interfaceName, null, filter);
		} catch (ServiceLocationException e1) {
			e1.printStackTrace();
			return (ServiceEndpointDescription[]) result.toArray();
		}
		// check whether SLP-Locator service exists
		Locator locator = getLocator();
		if (locator == null) {
			logService.log(LogService.LOG_WARNING, "no SLP-Locator");
			return (ServiceEndpointDescription[]) result.toArray();
		}
		try {
			String serviceType = serviceDescriptionAdapter.getServiceType();
			logService.log(LogService.LOG_DEBUG,"serviceType to find = " + serviceType);
			logService.log(LogService.LOG_DEBUG, "try to find services with URL="
					+ serviceDescriptionAdapter.getServiceURL().toString());
			ServiceLocationEnumeration se = locator
					.findServices(new ServiceType(serviceType),
							serviceDescriptionAdapter.getScopes(),
							serviceDescriptionAdapter.getFilter());
			// iterate through found services and retrieve their attributes
			while (se.hasMoreElements()) {
				ServiceURL url = (ServiceURL) se.next();
				logService
						.log(LogService.LOG_DEBUG, "adding serviceURL=" + url);
				logService.log(LogService.LOG_DEBUG,
						"try to find attributes for " + url);
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
					// if the value is not a String we cannot handle that value! This is a limitation of the API.
					value = attributes.substring(attributes.indexOf("=") + 1);
					descriptionAdapter.addProperty(key, value);
				}
				result.add(descriptionAdapter); // add to the result list
			}
		} catch (Exception e) {
			e.printStackTrace();
			logService.log(LogService.LOG_WARNING, "Failed to find service", e);
		}
		ServiceEndpointDescription[] results = (ServiceEndpointDescription[]) result
				.toArray(new ServiceEndpointDescription[0]);
		if (results.length > 0) {
			StringBuffer buff = new StringBuffer();
			buff.append("number of services = ");
			buff.append(results.length);
			buff.append("; services = ");
			for (int i = 0; i < results.length; i++) {
				buff.append("(");
				String[] ifNames= results[i].getInterfaceNames();
				for (int k=0; k< ifNames.length; k++) {
					buff.append(ifNames[k]);
					if(k != ifNames.length-1) {
						buff.append(",");
					}
				}
				buff.append(")");
				if (i != results.length - 1) {
					buff.append(",(");
				}
			}
			logService.log(LogService.LOG_DEBUG, buff.toString());
		} else {
			logService.log(LogService.LOG_DEBUG, "0 services found");
		}
		return results;
	}

	public void setLogService(final LogService logService) {
		this.logService = logService;
	}

	synchronized Locator getLocator() {
		return locator;
	}

	synchronized void setLocator(final Locator locator) {
		this.locator = locator;
	}

	synchronized Advertiser getAdvertiser() {
		return advertiser;
	}

	synchronized void setAdvertiser(final Advertiser advertiser) {
		this.advertiser = advertiser;
	}

	/**
	 * 
	 * @see org.osgi.impl.service.discovery.ProtocolHandler#publishService(org.osgi.service.discovery.ServiceDescription)
	 */
	public boolean publishService(
			final ServiceEndpointDescription serviceDescription) {
		boolean published = false;
		List serviceDescriptionAdapter = new ArrayList();
		for (int i = 0; i < serviceDescription.getInterfaceNames().length; i++) {
			try {
				SLPServiceDescriptionAdapter sd = new SLPServiceDescriptionAdapter(
						serviceDescription.getInterfaceNames()[i],
						serviceDescription.getProperties(), null);
				serviceDescriptionAdapter.add(sd);
			} catch (ServiceLocationException e1) {
				e1.printStackTrace();
				return published;
			}
		}

		Advertiser advertiser = getAdvertiser();
		if (advertiser != null) {
			logService.log(LogService.LOG_DEBUG, "publish service "
					+ serviceDescriptionAdapter);
			for (int k = 0; k < serviceDescriptionAdapter.size(); k++) {
				try {
					SLPServiceDescriptionAdapter sd = (SLPServiceDescriptionAdapter) serviceDescriptionAdapter
							.get(k);
					advertiser.register(sd.getServiceURL(), new Hashtable(sd
							.getProperties()));
					published = true;
				} catch (ServiceLocationException e) {
					e.printStackTrace();
				}
			}
		} else {
			logService.log(LogService.LOG_DEBUG, "no Advertiser");
		}
		return published;
	}

	/**
	 * 
	 * @see org.osgi.impl.service.discovery.ProtocolHandler#unpublishService(org.osgi.service.discovery.ServiceDescription)
	 */
	public void unpublishService(
			final ServiceEndpointDescription serviceDescription) {
		List serviceDescriptionAdapter = new ArrayList();
		for (int i = 0; i < serviceDescription.getInterfaceNames().length; i++) {
			try {
				SLPServiceDescriptionAdapter sd = new SLPServiceDescriptionAdapter(
						serviceDescription.getInterfaceNames()[i],
						serviceDescription.getProperties(), null);
				serviceDescriptionAdapter.add(sd);
			} catch (ServiceLocationException e1) {
				e1.printStackTrace();
				return;
			}
		}

		Advertiser advertiser = getAdvertiser();
		if (advertiser != null) {
			for (int k = 0; k < serviceDescriptionAdapter.size(); k++) {
				try {
					SLPServiceDescriptionAdapter sd = (SLPServiceDescriptionAdapter) serviceDescriptionAdapter
							.get(k);
					logService.log(LogService.LOG_DEBUG, "unpublish service "
							+ sd.getServiceURL());
					advertiser.deregister(sd.getServiceURL());
				} catch (ServiceLocationException e) {
					e.printStackTrace();
				}
			}
		} else {
			logService.log(LogService.LOG_DEBUG, "no Advertiser");
		}
	}

	/**
	 * 
	 * @see org.osgi.impl.service.discovery.ProtocolHandler#publishService(org.osgi.service.discovery.ServiceDescription,
	 *      boolean)
	 */
	public boolean publishService(
			final ServiceEndpointDescription serviceDescription,
			boolean autopublish) {
		return publishService(serviceDescription);
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

			logService.log(LogService.LOG_INFO, "bound Locator");

			return loc;
		}

		public void modifiedService(ServiceReference reference, Object service) {
			Locator loc = (Locator) context.getService(reference);
			setLocator(loc);

			logService.log(LogService.LOG_INFO, "rebound Locator");
		}

		public void removedService(ServiceReference reference, Object service) {
			context.ungetService(reference);
			setLocator(null);

			logService.log(LogService.LOG_INFO, "unbound Locator");
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

			logService.log(LogService.LOG_INFO, "bound Advertiser");

			return adv;
		}

		public void modifiedService(final ServiceReference reference,
				Object service) {
			Advertiser adv = (Advertiser) context.getService(reference);
			setAdvertiser(adv);

			logService.log(LogService.LOG_INFO, "rebound Advertiser");
		}

		public void removedService(final ServiceReference reference,
				Object service) {
			context.ungetService(reference);
			setAdvertiser(null);

			logService.log(LogService.LOG_INFO, "unbound Advertiser");
		}
	}
}
