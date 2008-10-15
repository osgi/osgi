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
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.discovery.AbstractDiscovery;
import org.osgi.service.discovery.FindServiceCallback;
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
 * TODO: check for null before calling logger or put it in an extra method TODO:
 * remove printStackTrace and do logging instead
 * 
 * @author Tim Diekmann
 * @author Thomas Kiesslich
 * 
 */
public class SLPHandlerImpl extends AbstractDiscovery {
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
	}

	public void destroy() {
		locatorTracker.close();
		advertiserTracker.close();
		super.destroy();
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
	 * @see org.osgi.impl.service.discovery.ProtocolHandler#findService(org.osgi.service.discovery.ServiceDescription)
	 */
	public ServiceEndpointDescription[] findService(final String interfaceName,
			final String filter) {
		validateFilter(filter);

		List result = new ArrayList();

		// check whether SLP-Locator service exists
		Locator locator = getLocator();
		if (locator == null) {
			getLogService().log(LogService.LOG_WARNING,
					"No SLP-Locator. Find operation is not executed.");
			return (ServiceEndpointDescription[]) result.toArray();
		}

		// TODO first look at cache

		// find appropriate services
		ServiceLocationEnumeration se;
		try {
			ServiceURL svcURL = SLPServiceDescriptionAdapter.createServiceURL(
					interfaceName, null, null, null);
			getLogService().log(LogService.LOG_DEBUG,
					"try to find services with URL=" + svcURL.toString());
			se = locator.findServices(svcURL.getServiceType(), null, filter);

			// TODO: in case result is empty do a search impying that service
			// interface is not java???
		} catch (Exception e) {
			e.printStackTrace();
			getLogService().log(LogService.LOG_WARNING,
					"Failed to find service", e);
			return (ServiceEndpointDescription[]) result.toArray();
		}

		// iterate through found services and retrieve their attributes
		while (se.hasMoreElements()) {
			try {
				ServiceURL url = (ServiceURL) se.next();
				getLogService().log(LogService.LOG_DEBUG,
						"adding serviceURL=" + url);
				getLogService().log(LogService.LOG_DEBUG,
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
					// if the value is not a String we cannot handle that value!
					// This is a limitation of the API.
					value = attributes.substring(attributes.indexOf("=") + 1);
					descriptionAdapter.addProperty(key, value);
				}
				result.add(descriptionAdapter); // add to the result list
			} catch (Exception e) {
				e.printStackTrace();
				getLogService().log(LogService.LOG_WARNING,
						"Failed to find service", e);
			}
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
				String[] ifNames = results[i].getInterfaceNames();
				for (int k = 0; k < ifNames.length; k++) {
					buff.append(ifNames[k]);
					if (k != ifNames.length - 1) {
						buff.append(",");
					}
				}
				buff.append(")");
				if (i != results.length - 1) {
					buff.append(",(");
				}
			}
			getLogService().log(LogService.LOG_DEBUG, buff.toString());
		} else {
			getLogService().log(LogService.LOG_DEBUG, "0 services found");
		}

		// TODO add to cache

		return results;
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#findService(String,
	 *      FindServiceCallback)
	 */
	public void findService(final String interfaceName, final String filter,
			final FindServiceCallback callback) {
		if (callback == null) {
			throw new IllegalArgumentException("callback must not be null");
		}

		validateFilter(filter);

		Thread executor = new Thread(new Runnable() {
			public void run() {
				try {
					// do lookup
					ServiceEndpointDescription[] services = findService(
							interfaceName, filter);
					// return result via callback
					try {
						callback.servicesFound(services);
					} catch (Exception e) {
						if (getLogService() != null) {
							getLogService()
									.log(
											LogService.LOG_ERROR,
											"Exceptions where thrown in the callback of findService operation.",
											e);
						}
					}
				} catch (Exception e) {
					if (getLogService() != null) {
						getLogService().log(LogService.LOG_ERROR,
								"Failed to execute async findService", e);
					}
				}
			}
		});
		executor.start();
	}

	// TODO: think whether we need a version with autopublish parameter
	/*
	 * @see org.osgi.service.discovery.Discovery#publishService(java.util.Map,
	 * java.util.Map, java.util.Map, boolean)
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
			// TODO log
			return null;
		}

		// TODO: act according autopublish parameter
		// publish service via SLP
		Advertiser advertiser = getAdvertiser();
		if (advertiser != null) {
			getLogService().log(LogService.LOG_DEBUG,
					"Following service is published: " + svcDescr);

			String[] interfaces = svcDescr.getInterfaceNames();
			for (int k = 0; k < interfaces.length; k++) {
				try {
					advertiser.register(svcDescr.getServiceURL(interfaces[k]),
							new Hashtable(svcDescr.getProperties()));
				} catch (ServiceLocationException e) {
					e.printStackTrace();
					// TODO log
				}
			}
		} else {
			getLogService().log(LogService.LOG_WARNING, "no Advertiser");
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

		if (getLogService() != null) {
			getLogService().log(LogService.LOG_DEBUG,
					"unpublish service " + serviceDescription.toString());
		}

		if (serviceDescription instanceof SLPServiceDescriptionAdapter) {
			SLPServiceDescriptionAdapter slpSvcDescr = (SLPServiceDescriptionAdapter) serviceDescription;

			Advertiser advertiser = getAdvertiser();
			if (advertiser != null) {
				String[] interfaceNames = slpSvcDescr.getInterfaceNames();
				for (int k = 0; k < interfaceNames.length; k++) {
					try {
						getLogService()
								.log(
										LogService.LOG_DEBUG,
										"unpublish service "
												+ slpSvcDescr
														.getServiceURL(interfaceNames[k]));
						advertiser.deregister(slpSvcDescr
								.getServiceURL(interfaceNames[k]));

						// inform listeners about removal
						notifyListenersOnRemovedServiceDescription(serviceDescription);
					} catch (ServiceLocationException e) {
						e.printStackTrace();
						// TODO: log
					}
				}
			} else {
				getLogService().log(LogService.LOG_WARNING, "no Advertiser");
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

			if (getLogService() != null) {
				getLogService().log(LogService.LOG_INFO, "bound Locator");
			}
			return loc;
		}

		public void modifiedService(ServiceReference reference, Object service) {
			Locator loc = (Locator) context.getService(reference);
			setLocator(loc);

			if (getLogService() != null) {
				getLogService().log(LogService.LOG_INFO, "rebound Locator");
			}
		}

		public void removedService(ServiceReference reference, Object service) {
			context.ungetService(reference);
			setLocator(null);

			if (getLogService() != null) {
				getLogService().log(LogService.LOG_INFO, "unbound Locator");
			}
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

			if (getLogService() != null) {
				getLogService().log(LogService.LOG_INFO, "bound Advertiser");
			}
			return adv;
		}

		public void modifiedService(final ServiceReference reference,
				Object service) {
			Advertiser adv = (Advertiser) context.getService(reference);
			setAdvertiser(adv);

			if (getLogService() != null) {
				getLogService().log(LogService.LOG_INFO, "rebound Advertiser");
			}
		}

		public void removedService(final ServiceReference reference,
				Object service) {
			context.ungetService(reference);
			setAdvertiser(null);

			if (getLogService() != null) {
				getLogService().log(LogService.LOG_INFO, "unbound Advertiser");
			}
		}
	}
}
