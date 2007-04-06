/*
 * $Id$
 *
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */
package org.osgi.impl.service.event.mapper;

import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.framework.*;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPEventListener;
import org.osgi.service.upnp.UPnPService;
import org.osgi.service.useradmin.UserAdminEvent;
import org.osgi.service.useradmin.UserAdminListener;
import org.osgi.service.wireadmin.WireAdminEvent;
import org.osgi.service.wireadmin.WireAdminListener;
import org.osgi.service.wireadmin.WireConstants;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Main class for redeliver special events like FrameworkEvents via EventAdmin.
 * 
 * 
 * @version $Revision$
 */
public class EventRedeliverer implements FrameworkListener, BundleListener,
		ServiceListener, LogListener, LogReaderServiceListener,
		ConfigurationListener, WireAdminListener, UPnPEventListener,
		UserAdminListener {
	private ServiceTracker			eventAdminTracker;
	private LogReaderServiceTracker	logTracker;
	private LogReaderService		reader;
	private final static boolean	DEBUG	= false;
	private BundleContext			bc;
	private ServiceRegistration		configurationListenerReg;
	private ServiceRegistration		wireAdminListenerReg;
	private ServiceRegistration		upnpEventListenerReg;
	private ServiceRegistration		userAdminListenerReg;

	public EventRedeliverer(BundleContext bc) {
		this.bc = bc;
	}

	public void close() {
		if (logTracker != null) {
			logTracker.close();
			logTracker = null;
		}
		if (eventAdminTracker != null) {
			eventAdminTracker.close();
			eventAdminTracker = null;
		}
		if (reader != null) {
			reader.removeLogListener(this);
		}
		reader = null;
		bc.removeFrameworkListener(this);
		bc.removeBundleListener(this);
		bc.removeServiceListener(this);
		if (configurationListenerReg != null) {
			configurationListenerReg.unregister();
			configurationListenerReg = null;
		}
		if (wireAdminListenerReg != null) {
			wireAdminListenerReg.unregister();
			wireAdminListenerReg = null;
		}
		if (upnpEventListenerReg != null) {
			upnpEventListenerReg.unregister();
			upnpEventListenerReg = null;
		}
		if (userAdminListenerReg != null) {
			userAdminListenerReg.unregister();
			userAdminListenerReg = null;
		}
	}

	/**
	 * prepare any service trackers and register event listeners which are
	 * necessary to obtain events to be mapped
	 */
	public void open() {
		// open ServiceTracker for EventAdmin
		eventAdminTracker = new ServiceTracker(bc, EventAdmin.class.getName(),
				null);
		eventAdminTracker.open();
		// open ServiceTracker for LogReaderService
		logTracker = new LogReaderServiceTracker(bc, this);
		logTracker.open();
		// add legacy event listener for framework level event
		bc.addFrameworkListener(this);
		bc.addBundleListener(this);
		bc.addServiceListener(this);
		// register configurationListener
		configurationListenerReg = bc.registerService(
				ConfigurationListener.class.getName(), this, null);
		// register WireAdminListener
		Hashtable ht = new Hashtable();
		// create an event mask to receive all the types of WireAdminEvent
		Integer mask = new Integer(WireAdminEvent.WIRE_CONNECTED
				| WireAdminEvent.WIRE_CREATED | WireAdminEvent.WIRE_DELETED
				| WireAdminEvent.WIRE_DISCONNECTED | WireAdminEvent.WIRE_TRACE
				| WireAdminEvent.WIRE_UPDATED
				| WireAdminEvent.CONSUMER_EXCEPTION
				| WireAdminEvent.PRODUCER_EXCEPTION);
		ht.put(WireConstants.WIREADMIN_EVENTS, mask);
		wireAdminListenerReg = bc.registerService(WireAdminListener.class
				.getName(), this, ht);
		// register UPnPEventListener
		// create a Filter object to receive all the UPnP events
		Hashtable ht2 = new Hashtable();
		Filter filter = null;
		try {
			filter = bc.createFilter("(|(|(" + UPnPDevice.TYPE + "=*)("
					+ UPnPDevice.ID + "=*))(|(" + UPnPService.TYPE + "=*)("
					+ UPnPService.ID + "=*)))");
		}
		catch (InvalidSyntaxException e) {
			System.out
					.println("Exception thrown while trying to create Filter. "
							+ e);
			e.printStackTrace(System.out);
		}
		if (filter != null) {
			ht2.put(UPnPEventListener.UPNP_FILTER, filter);
			upnpEventListenerReg = bc.registerService(UPnPEventListener.class
					.getName(), this, ht2);
		}
		// register usrAdminListener
		userAdminListenerReg = bc.registerService(UserAdminListener.class
				.getName(), this, null);
	}

	private EventAdmin getEventAdmin() {
		if (eventAdminTracker == null)
			return null;
		return (EventAdmin) eventAdminTracker.getService();
	}

	/**
	 * @param event
	 * @see org.osgi.framework.FrameworkListener#frameworkEvent(org.osgi.framework.FrameworkEvent)
	 */
	public void frameworkEvent(FrameworkEvent event) {
		EventAdmin eventAdmin = getEventAdmin();
		if (eventAdmin != null) {
			(new FrameworkEventAdapter(event, eventAdmin)).redeliver();
		}
		else {
			printNoEventAdminError();
		}
	}

	private void printNoEventAdminError() {
		if (DEBUG) {
			System.out.println(this.getClass().getName()
					+ ": Cannot find the EventAdmin.");
		}
	}

	/**
	 * @param event
	 * @see org.osgi.framework.BundleListener#bundleChanged(org.osgi.framework.BundleEvent)
	 */
	public void bundleChanged(BundleEvent event) {
		EventAdmin eventAdmin = getEventAdmin();
		if (eventAdmin != null) {
			(new BundleEventAdapter(event, eventAdmin)).redeliver();
		}
		else {
			printNoEventAdminError();
		}
	}

	/**
	 * @param event
	 * @see org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework.ServiceEvent)
	 */
	public void serviceChanged(ServiceEvent event) {
		EventAdmin eventAdmin = getEventAdmin();
		if (eventAdmin != null) {
			(new ServiceEventAdapter(event, eventAdmin)).redeliver();
		}
		else {
			printNoEventAdminError();
		}
	}

	/**
	 * @param entry
	 * @see org.osgi.service.log.LogListener#logged(org.osgi.service.log.LogEntry)
	 */
	public void logged(LogEntry entry) {
		EventAdmin eventAdmin = getEventAdmin();
		if (eventAdmin != null) {
			(new LogEntryAdapter(entry, eventAdmin)).redeliver();
		}
		else {
			printNoEventAdminError();
		}
	}

	/**
	 * @param reference
	 * @param service
	 * @see org.osgi.impl.service.event.mapper.LogReaderServiceListener#logReaderServiceAdding(org.osgi.framework.ServiceReference,
	 *      org.osgi.service.log.LogReaderService)
	 */
	public void logReaderServiceAdding(ServiceReference reference,
			LogReaderService service) {
		if (reader != null) {
			return;
		}
		reader = service;
		reader.addLogListener(this);
	}

	/**
	 * @param reference
	 * @param service
	 * @see org.osgi.impl.service.event.mapper.LogReaderServiceListener#logReaderServiceRemoved(org.osgi.framework.ServiceReference,
	 *      org.osgi.service.log.LogReaderService)
	 */
	public void logReaderServiceRemoved(ServiceReference reference,
			LogReaderService service) {
		if ((reader != null) && reader.equals(service)) {
			reader.removeLogListener(this);
			reader = null;
		}
		// ungetService() will be called after returning to
		// LogReaderServiceTracker's removedService() method.
	}

	/**
	 * @param event
	 * @see org.osgi.service.cm.ConfigurationListener#configurationEvent(org.osgi.service.cm.ConfigurationEvent)
	 */
	public void configurationEvent(ConfigurationEvent event) {
		EventAdmin eventAdmin = getEventAdmin();
		if (eventAdmin != null) {
			(new ConfigurationEventAdapter(event, eventAdmin)).redeliver();
		}
		else {
			printNoEventAdminError();
		}
	}

	/**
	 * @param event
	 * @see org.osgi.service.wireadmin.WireAdminListener#wireAdminEvent(org.osgi.service.wireadmin.WireAdminEvent)
	 */
	public void wireAdminEvent(WireAdminEvent event) {
		EventAdmin eventAdmin = getEventAdmin();
		if (eventAdmin != null) {
			(new WireAdminEventAdapter(event, eventAdmin)).redeliver();
		}
		else {
			printNoEventAdminError();
		}
	}

	/**
	 * @param deviceId
	 * @param serviceId
	 * @param events
	 * @see org.osgi.service.upnp.UPnPEventListener#notifyUPnPEvent(java.lang.String,
	 *      java.lang.String, java.util.Dictionary)
	 */
	public void notifyUPnPEvent(String deviceId, String serviceId,
			Dictionary events) {
		EventAdmin eventAdmin = getEventAdmin();
		if (eventAdmin != null) {
			(new UPnPEventAdapter(deviceId, serviceId, events, eventAdmin))
					.redeliver();
		}
		else {
			printNoEventAdminError();
		}
	}

	/**
	 * @param event
	 * @see org.osgi.service.useradmin.UserAdminListener#roleChanged(org.osgi.service.useradmin.UserAdminEvent)
	 */
	public void roleChanged(UserAdminEvent event) {
		EventAdmin eventAdmin = getEventAdmin();
		if (eventAdmin != null) {
			(new UserAdminEventAdapter(event, eventAdmin)).redeliver();
		}
		else {
			printNoEventAdminError();
		}
	}
}