/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.impl.service.metatype;

import java.util.ArrayList;

import org.osgi.framework.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.metatype.*;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.ObjectClassDefinition;
import org.osgi.util.tracker.ServiceTracker;

public class MetaTypeProviderTracker implements MetaTypeInformation{
	public static final String MANAGED_SERVICE = "org.osgi.service.cm.ManagedService";
	public static final String MANAGED_SERVICE_FACTORY = "org.osgi.service.cm.ManagedServiceFactory";
	public static final String FILTER_STRING = "(|(" + Constants.OBJECTCLASS + '=' + MANAGED_SERVICE + ")(" + Constants.OBJECTCLASS + '=' + MANAGED_SERVICE_FACTORY + "))";

	Bundle _bundle;
	BundleContext _context;
	ServiceTracker _tracker;

	/**
	 * Constructs a MetaTypeProviderTracker which tracks all MetaTypeProviders
	 * registered by the specified bundle.
	 * @param context The BundleContext of the MetaTypeService implementation
	 * @param bundle The bundle to track all MetaTypeProviders for.
	 */
	public MetaTypeProviderTracker(BundleContext context, Bundle bundle) {
		this._context = context;
		this._bundle = bundle;
		// create a filter for ManagedService and ManagedServiceFactory services 
		try {
			Filter filter = context.createFilter(FILTER_STRING);
			// create a service tracker and open it.
			this._tracker = new ServiceTracker(context, filter, null);
			// we never close this, but it is no big deal because we
			// really want to track the services until we are stopped
			// at that point the framework will remove our listeners.
			this._tracker.open();
		}
		catch (InvalidSyntaxException e) {
			// This should never happen; it means we have a bug in the filterString
			e.printStackTrace();
			throw new IllegalArgumentException(FILTER_STRING);
		}
	}

	private String[] getPids(boolean factory) {
		if (_bundle.getState() != Bundle.ACTIVE)
			return new String[0]; // return none if not active
		MetaTypeProviderWrapper[] wrappers = getMetaTypeProviders();
		ArrayList results = new ArrayList();
		for (int i = 0; i < wrappers.length; i++) {
			// return only the correct type of pids (regular or factory)
			if (factory == wrappers[i].factory)
				results.add(wrappers[i].pid);
		}
		return (String[]) results.toArray(new String[results.size()]);
	}

	public String[] getPids() {
		return getPids(false);
	}
	public String[] getFactoryPids() {
		return getPids(true);
	}

	public Bundle getBundle() {
		return _bundle;
	}

	public ObjectClassDefinition getObjectClassDefinition(String id, String locale) {
		if (_bundle.getState() != Bundle.ACTIVE)
			return null; // return none if not active
		MetaTypeProviderWrapper[] wrappers = getMetaTypeProviders();
		for (int i = 0; i < wrappers.length; i++) {
			if (id.equals(wrappers[i].pid))
				// found a matching pid now call the actual provider
				return wrappers[i].provider.getObjectClassDefinition(id, locale);
		}
		return null;
	}

	public String[] getLocales() {
		if (_bundle.getState() != Bundle.ACTIVE)
			return new String[0]; // return none if not active
		MetaTypeProviderWrapper[] wrappers = getMetaTypeProviders();
		ArrayList locales = new ArrayList();
		// collect all the unique locales from all providers we found
		for (int i = 0; i < wrappers.length; i++) {
			String[] wrappedLocales = wrappers[i].provider.getLocales();
			if (wrappedLocales == null)
				continue;
			for (int j = 0; j < wrappedLocales.length; j++)
				if (!locales.contains(wrappedLocales[j]))
					locales.add(wrappedLocales[j]);
		}
		return  (String[]) locales.toArray(new String[locales.size()]);
	}

	private MetaTypeProviderWrapper[] getMetaTypeProviders() {
		ServiceReference[] refs = _tracker.getServiceReferences();
		if (refs == null)
			return new MetaTypeProviderWrapper[0];
		ArrayList results = new ArrayList();
		for (int i = 0; i < refs.length; i++)
			// search for services registered by the bundle
			if (refs[i].getBundle() == _bundle) {
				Object service = _context.getService(refs[i]);
				if (service instanceof MetaTypeProvider) {
					// found one that implements MetaTypeProvider
					// wrap its information in a MetaTypeProviderWrapper
					String pid = (String) refs[i].getProperty(Constants.SERVICE_PID);
					boolean factory = service instanceof ManagedServiceFactory;
					results.add(new MetaTypeProviderWrapper((MetaTypeProvider) service, pid, factory));
				}
				// always unget a service.
				// this leaves us open for the the service going away but who cares.
				// we only use the service for a short period of time.
				_context.ungetService(refs[i]);
			}
		return (MetaTypeProviderWrapper[]) results.toArray(new MetaTypeProviderWrapper[results.size()]);
	}

	// this is a simple class just used to temporarily store information about a provider
	public class MetaTypeProviderWrapper {
		MetaTypeProvider provider;
		String pid;
		boolean factory;
		MetaTypeProviderWrapper(MetaTypeProvider provider, String pid, boolean factory) {
			this.provider = provider;
			this.pid = pid;
			this.factory = factory;
		}
	}
}
