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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
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
 *
 */
public class SLPHandlerImpl {
	private LogService logService;
	private ServiceTracker locatorTracker;
	private ServiceTracker advertiserTracker;
	
	private Locator locator;
	private Advertiser advertiser;
	
	public SLPHandlerImpl(final BundleContext context, LogService logService) {
		this.logService = logService;
		
		locatorTracker = new ServiceTracker(context, Locator.class.getName(), new ServiceTrackerCustomizer(){

			public Object addingService(ServiceReference reference) {
				Locator loc = (Locator) context.getService(reference);
				setLocator(loc);
				
				SLPHandlerImpl.this.logService.log(LogService.LOG_INFO, "bound Locator");
				
				return loc;
			}

			public void modifiedService(ServiceReference reference,
					Object service) {
				Locator loc = (Locator) context.getService(reference);
				setLocator(loc);
				
				SLPHandlerImpl.this.logService.log(LogService.LOG_INFO, "rebound Locator");
			}

			public void removedService(ServiceReference reference,
					Object service) {
				context.ungetService(reference);
				setLocator(null);
				
				SLPHandlerImpl.this.logService.log(LogService.LOG_INFO, "unbound Locator");
			}
			
		});
		advertiserTracker = new ServiceTracker(context, Advertiser.class.getName(), new ServiceTrackerCustomizer() {

			public Object addingService(ServiceReference reference) {
				Advertiser adv = (Advertiser) context.getService(reference);
				setAdvertiser(adv);
				
				SLPHandlerImpl.this.logService.log(LogService.LOG_INFO, "bound Advertiser");

				return adv;
			}

			public void modifiedService(ServiceReference reference,
					Object service) {
				Advertiser adv = (Advertiser) context.getService(reference);
				setAdvertiser(adv);
				
				SLPHandlerImpl.this.logService.log(LogService.LOG_INFO, "rebound Advertiser");
			}

			public void removedService(ServiceReference reference,
					Object service) {
				context.ungetService(reference);
				setAdvertiser(null);
				
				SLPHandlerImpl.this.logService.log(LogService.LOG_INFO, "unbound Advertiser");
			}
			
		});
	}

	public void init() {
		// TODO track slp.Locator and slp.Advertiser
		locatorTracker.open();
		advertiserTracker.open();
	}
	
	public void destroy() {
		locatorTracker.close();
		advertiserTracker.close();
	}
	
	public Collection findService(SLPServiceDescriptionAdapter serviceDescriptionAdapter) {
		List result = new LinkedList();
		
		Locator locator = getLocator();
		if (locator != null) {
			try {
//				ServiceLocationEnumeration sle = locator.findServiceTypes(serviceDescriptionAdapter.getNamingAuthority(),
//						serviceDescriptionAdapter.getScopes());
//				
//				while (sle.hasMoreElements()) {
				String serviceType = serviceDescriptionAdapter.getServiceType();

				logService.log(LogService.LOG_DEBUG, "serviceType=" + serviceType);

				ServiceLocationEnumeration se = locator.findServices(new ServiceType(serviceType),
						serviceDescriptionAdapter.getScopes(),
						serviceDescriptionAdapter.getFilter());

				while (se.hasMoreElements()) {
					ServiceURL url = (ServiceURL) se.next();
					logService.log(LogService.LOG_DEBUG, "serviceURL=" + url);

					SLPServiceDescriptionAdapter descriptionAdapter = new SLPServiceDescriptionAdapter(url);

					result.add(descriptionAdapter); // add to the result list
				}
//				}
			} catch (Exception e) {
				e.printStackTrace();
				logService.log(LogService.LOG_WARNING, "Failed to find service", e);
			}
			
		} else {
			logService.log(LogService.LOG_DEBUG, "no Locator");
		}
		
		logService.log(LogService.LOG_DEBUG, "number of services found " + result.size());

		return result;
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	synchronized Locator getLocator() {
		return locator;
	}

	synchronized void setLocator(Locator locator) {
		this.locator = locator;
	}

	synchronized Advertiser getAdvertiser() {
		return advertiser;
	}

	synchronized void setAdvertiser(Advertiser advertiser) {
		this.advertiser = advertiser;
	}

	public void publishService(SLPServiceDescriptionAdapter serviceDescriptionAdapter) throws ServiceLocationException {
		Advertiser advertiser = getAdvertiser();
		if (advertiser != null) {
			logService.log(LogService.LOG_DEBUG, "publish service " + serviceDescriptionAdapter);
			
			advertiser.register(serviceDescriptionAdapter.getServiceURL(), serviceDescriptionAdapter.getAttributes());
		} else {
			logService.log(LogService.LOG_DEBUG, "no Advertiser");
		}
	}

	public void unpublishService(SLPServiceDescriptionAdapter serviceDescriptionAdapter) throws ServiceLocationException {
		Advertiser advertiser = getAdvertiser();
		if (advertiser != null) {
			logService.log(LogService.LOG_DEBUG, "unpublish service " + serviceDescriptionAdapter);
			
			advertiser.deregister(serviceDescriptionAdapter.getServiceURL());
		} else {
			logService.log(LogService.LOG_DEBUG, "no Advertiser");
		}
	}

}
