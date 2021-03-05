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
package org.osgi.impl.service.prefs;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.prefs.PreferencesService;

/**
 * @author $Id$
 */
public class Activator
		implements BundleActivator, ServiceFactory<PreferencesService> {
	private static final String	PREFERENCES_SERVICE	= "org.osgi.service.prefs.PreferencesService";
	private BundleContext		bundleContext		= null;

	@Override
	public synchronized void start(
			@SuppressWarnings("hiding") BundleContext bundleContext) {
		this.bundleContext = bundleContext;
		Dictionary<String,Object> properties = new Hashtable<>(4);
		properties.put("Description", "The OSGi Preferences Service");
		properties.put("BackingStore", "file");
		bundleContext.registerService(PREFERENCES_SERVICE, this, properties);
	}

	@Override
	public synchronized void stop(
			@SuppressWarnings("hiding") BundleContext bundleContext) {
		// empty
	}

	@Override
	public synchronized PreferencesService getService(Bundle bundle,
			ServiceRegistration<PreferencesService> reg) {
		return new PreferencesServiceImpl(bundleContext, bundle.getBundleId());
	}

	@Override
	public synchronized void ungetService(Bundle bundle,
			ServiceRegistration<PreferencesService> reg,
			PreferencesService service) {
		((PreferencesServiceImpl) service).flushAll();
	}
}
