/*
 * $Header$
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

package org.osgi.impl.service.metatype;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;

import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.*;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * MetaType Activator
 * 
 * @author Julian Chen
 * @version 1.0
 */
public class MetaTypeActivator implements BundleActivator,
		ServiceTrackerCustomizer {

	protected final String			mtsClazz				= "org.osgi.service.metatype.MetaTypeService";		//$NON-NLS-1$
	protected final String			mtsPid					= "org.osgi.impl.service.metatype.MetaTypeService"; //$NON-NLS-1$
	protected final static String	saxFactoryClazz			= "javax.xml.parsers.SAXParserFactory";			//$NON-NLS-1$

	private ServiceTracker			_parserTracker;
	private BundleContext			_context;
	private ServiceRegistration		_mtsReg;
	private MetaTypeServiceImpl		_mts					= null;

	/**
	 * The current SaxParserFactory being used by the WebContainer
	 */
	private SAXParserFactory		_currentParserFactory	= null;

	/**
	 * The lock used when modifying the currentParserFactory
	 */
	private Object					lock					= new Object();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {

		this._context = context;
		_parserTracker = new ServiceTracker(context, saxFactoryClazz, this);
		_parserTracker.open();
		ServiceReference ref = context.getServiceReference(PackageAdmin.class.getName());
		FragmentUtils.packageAdmin = ref == null ? null : (PackageAdmin) context.getService(ref);
		Logging.debug("====== Meta Type Service starting ! =====");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {

		Logging.debug("====== Meta Type Service stoping ! =====");
		_parserTracker.close();
		_parserTracker = null;
		FragmentUtils.packageAdmin = null;
		context = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.osgi.framework.ServiceReference)
	 */
	public Object addingService(ServiceReference ref) {

		SAXParserFactory parserFactory = (SAXParserFactory) _context
				.getService(ref);
		synchronized (lock) {
			if (_mts == null) {
				// Save this parserFactory as the currently used parserFactory
				_currentParserFactory = parserFactory;
				registerMetaTypeService();
			}
		}
		return parserFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference,
	 *      java.lang.Object)
	 */
	public void modifiedService(ServiceReference ref, Object object) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org.osgi.framework.ServiceReference,
	 *      java.lang.Object)
	 */
	public void removedService(ServiceReference ref, Object object) {

		SAXParserFactory parserFactory = (SAXParserFactory) _context
				.getService(ref);

		if (parserFactory == _currentParserFactory) {
			// This means that this SAXParserFactory was used to start the
			// MetaType Service.
			synchronized (lock) {
				_currentParserFactory = null;

				if (_mtsReg != null) {
					_mtsReg.unregister();
					_mtsReg = null;
					_context.removeBundleListener(_mts);
					_mts = null;
					parserFactory = null;
				}
				// See if another factory is available
				Object[] parsers = _parserTracker.getServices();
				if (parsers != null && parsers.length > 0) {
					_currentParserFactory = (SAXParserFactory) parsers[0];
					// We have another parser so lets restart the MetaType
					// Service
					registerMetaTypeService();
				}
			}
		}
	}

	/**
	 * Internal method in MetaTypeActivator for implementing
	 * ServiceTrackerCustomizer.
	 */
	private void registerMetaTypeService() {

		final Hashtable properties = new Hashtable(7);

		properties.put(Constants.SERVICE_VENDOR, "IBM"); //$NON-NLS-1$
		properties.put(Constants.SERVICE_DESCRIPTION, Msg.formatter
				.getString("SERVICE_DESCRIPTION")); //$NON-NLS-1$
		properties.put(Constants.SERVICE_PID, mtsPid);

		_mts = new MetaTypeServiceImpl(_context, _currentParserFactory);
		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				_context.addBundleListener(_mts);
				_mtsReg = _context.registerService(mtsClazz, _mts, properties);
				return null;
			}
		});
	}
}
