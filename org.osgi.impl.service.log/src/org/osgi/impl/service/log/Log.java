/*
 * $Id$
 *
 * OSGi Log Service Reference Implementation.
 *

 *
 * (C) Copyright IBM Corporation 2000-2001.
 *
 * This source code is licensed to OSGi as MEMBER LICENSED MATERIALS
 * under the terms of Section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.service.log;

import org.osgi.framework.*;
import org.osgi.service.log.*;
import java.util.*;

/**
 * Main class for LogService and LogReaderService.
 * 
 * @author BJ Hargrave (hargrave@us.ibm.com)
 * @version $Revision$
 */
public class Log implements BundleActivator, BundleListener, ServiceListener,
		FrameworkListener {
	protected Vector				logentries;
	protected EventQueue			eventqueue;
	protected Vector				logreaders;
	protected BundleContext			context;
	protected ServiceRegistration	logservice;
	protected ServiceRegistration	logreaderservice;
	// Someday we may add support for Configuration Admin
	// so that this parameter can be configured.
	protected int					logSize	= 100;

	/**
	 * Default constructor. This is constructed by the framework prior to
	 * calling the BundleActivator.start method.
	 *  
	 */
	public Log() {
	}

	/**
	 * BundleActivator.start method. We can now initialize the bundle and
	 * register the services.
	 *  
	 */
	public void start(BundleContext context) {
		this.context = context;
		logentries = new Vector(logSize);
		eventqueue = new EventQueue();
		logreaders = new Vector();
		register();
		context.addBundleListener(this);
		context.addServiceListener(this);
		context.addFrameworkListener(this);
	}

	/**
	 * BundleActivator.stop method. We must now clean up and terminate
	 * execution.
	 *  
	 */
	public void stop(BundleContext context) {
		/* remove my listeners before unregistering myself */
		this.context.removeBundleListener(this);
		this.context.removeServiceListener(this);
		this.context.removeFrameworkListener(this);
		unregister();
		eventqueue.close();
		eventqueue = null;
		logreaders = null;
		logentries = null;
		this.context = null;
	}

	/**
	 * Register the LogService and LogReaderService. We will use
	 * ServiceFactories to construct unique service object for each bundle.
	 *  
	 */
	protected void register() {
		Bundle self = context.getBundle();
		String bundleId = Long.toString(self.getBundleId());
		Hashtable properties = new Hashtable(7);
		properties.put(Constants.SERVICE_VENDOR, "OSGi Alliance");
		properties.put(Constants.SERVICE_DESCRIPTION,
				"OSGi Log Service 1.1 Reference Implementation");
		properties.put(Constants.SERVICE_PID, bundleId
				+ ".org.osgi.impl.service.log.LogService");
		logservice = context.registerService(
				org.osgi.service.log.LogService.class.getName(),
				new ServiceFactory() {
					/**
					 * ServiceFactory.getService method.
					 */
					public Object getService(Bundle bundle,
							ServiceRegistration registration) {
						return (new LogServiceImpl(Log.this, bundle));
					}

					/**
					 * ServiceFactory.ungetService method.
					 */
					public void ungetService(Bundle bundle,
							ServiceRegistration registration, Object service) {
						((LogServiceImpl) service).close();
					}
				}, properties);
		properties = new Hashtable(7);
		properties.put(Constants.SERVICE_VENDOR, "OSGi Alliance");
		properties.put(Constants.SERVICE_DESCRIPTION,
				"OSGi Log Service 1.1 Reference Implementation");
		properties.put(Constants.SERVICE_PID, bundleId
				+ ".org.osgi.impl.service.log.LogReaderService");
		logreaderservice = context.registerService(
				org.osgi.service.log.LogReaderService.class.getName(),
				new ServiceFactory() {
					/**
					 * ServiceFactory.getService method.
					 */
					public Object getService(Bundle bundle,
							ServiceRegistration registration) {
						LogReaderServiceImpl service = new LogReaderServiceImpl(
								Log.this, bundle);
						logreaders.addElement(service);
						return (service);
					}

					/**
					 * ServiceFactory.ungetService method.
					 */
					public void ungetService(Bundle bundle,
							ServiceRegistration registration, Object service) {
						logreaders.removeElement(service);
						((LogReaderServiceImpl) service).close();
					}
				}, properties);
	}

	/**
	 * Unregister the LogService and LogReaderService.
	 *  
	 */
	protected void unregister() {
		logservice.unregister();
		logreaderservice.unregister();
		logservice = null;
		logreaderservice = null;
	}

	/**
	 * Log a bundle message. This is used internally by all of the public log()
	 * methods as the common point through which all logging must go through.
	 *  
	 */
	protected void log(int level, String message, Bundle bundle,
			ServiceReference reference, Throwable exception) {
		if (context != null) /*
							  * in case someone still calls us after we've
							  * stopped
							  */
		{
			LogEntry logentry = new LogEntryImpl(level, message, bundle,
					reference, exception);
			addLogEntry(logentry);
		}
	}

	/**
	 * Add the new entry to the log. The log is kept in LIFO order for easy
	 * enumeration in LIFO order.
	 *  
	 */
	protected synchronized void addLogEntry(LogEntry logentry) {
		for (int size = logentries.size(); size >= logSize; size = logentries
				.size()) {
			logentries.removeElementAt(size - 1);
		}
		logentries.insertElementAt(logentry, 0);
		eventqueue.publishLogEntry(logreaders, logentry);
	}

	/**
	 * Return an enumeration of the log entries.
	 *  
	 */
	protected synchronized Enumeration logEntries() {
		return (((Vector) logentries.clone()).elements());
	}

	/**
	 * BundleListener.bundleChanged method.
	 *  
	 */
	public void bundleChanged(BundleEvent event) {
		log(LogService.LOG_INFO, getBundleEventTypeName(event.getType()), event
				.getBundle(), null, null);
	}

	/**
	 * ServiceListener.serviceChanged method.
	 *  
	 */
	public void serviceChanged(ServiceEvent event) {
		ServiceReference reference = event.getServiceReference();
		int eventType = event.getType();
		int logType = (eventType == ServiceEvent.MODIFIED) ? LogService.LOG_DEBUG
				: LogService.LOG_INFO;
		log(logType, getServiceEventTypeName(eventType), reference.getBundle(),
				reference, null);
	}

	/**
	 * FrameworkListener.frameworkEvent method.
	 *  
	 */
	public void frameworkEvent(FrameworkEvent event) {
		int eventType = event.getType();
		int logType = (eventType == FrameworkEvent.ERROR) ? LogService.LOG_ERROR
				: LogService.LOG_INFO;
		Throwable t = (eventType == FrameworkEvent.ERROR) ? event
				.getThrowable() : null;
		log(logType, getFrameworkEventTypeName(eventType), event.getBundle(),
				null, t);
	}

	/**
	 * Convert BundleEvent type to a string.
	 *  
	 */
	protected static String getBundleEventTypeName(int type) {
		switch (type) {
			case BundleEvent.UNINSTALLED :
				return ("BundleEvent UNINSTALLED");
			case BundleEvent.INSTALLED :
				return ("BundleEvent INSTALLED");
			case BundleEvent.UPDATED :
				return ("BundleEvent UPDATED");
			case BundleEvent.STARTED :
				return ("BundleEvent STARTED");
			case BundleEvent.STOPPED :
				return ("BundleEvent STOPPED");
			case BundleEvent.RESOLVED :
				return ("BundleEvent RESOLVED");
			case BundleEvent.UNRESOLVED :
				return ("BundleEvent UNRESOLVED");
			default :
				return ("BundleEvent " + Integer.toHexString(type));
		}
	}

	/**
	 * Convert ServiceEvent type to a string.
	 *  
	 */
	protected static String getServiceEventTypeName(int type) {
		switch (type) {
			case ServiceEvent.REGISTERED :
				return ("ServiceEvent REGISTERED");
			case ServiceEvent.MODIFIED :
				return ("ServiceEvent MODIFIED");
			case ServiceEvent.UNREGISTERING :
				return ("ServiceEvent UNREGISTERING");
			default :
				return ("ServiceEvent " + Integer.toHexString(type));
		}
	}

	/**
	 * Convert FrameworkEvent type to a string.
	 *  
	 */
	protected static String getFrameworkEventTypeName(int type) {
		switch (type) {
			case FrameworkEvent.ERROR :
				return ("FrameworkEvent ERROR");
			case FrameworkEvent.STARTED :
				return ("FrameworkEvent STARTED");
			case FrameworkEvent.PACKAGES_REFRESHED :
				return ("FrameworkEvent PACKAGES REFRESHED");
			case FrameworkEvent.STARTLEVEL_CHANGED :
				return ("FrameworkEvent STARTLEVEL CHANGED");
			case FrameworkEvent.WARNING :
				return ("FrameworkEvent WARNING");
			case FrameworkEvent.INFO :
				return ("FrameworkEvent INFO");
			default :
				return ("FrameworkEvent " + Integer.toHexString(type));
		}
	}
}
