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
package org.osgi.test.cases.cm.bundleT5;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.test.cases.cm.shared.Synchronizer;
import org.osgi.test.cases.cm.shared.Util;

/**
 */
public class BundleT5Activator implements BundleActivator {

	private BundleContext context;

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext context) throws Exception {
		log("Starting bundle T5");
		this.context = context;

		final String filter = "("
				+ org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID
				+ "=syncT5-1)";
		final Synchronizer sync = Util.getService(context,
				Synchronizer.class, filter);

		registerService(context, Util.createPid("pid_targeted1"), sync);
		registerService(context, Util.createPid("pid|targeted2"), sync);
		registerFactoryService(context, Util.createPid("pid_targeted3"), sync);
	}

	private void registerService(final BundleContext context, 
			final String pid,
			final Synchronizer sync) throws Exception {

		final Object service = new ManagedServiceImpl(sync);
		final Dictionary<String,Object> props = new Hashtable<>();

		log("Going to register ManagedService. pid:\n\t"
				+pid);
		props.put(org.osgi.framework.Constants.SERVICE_PID, pid);

		try {
			this.context.registerService(ManagedService.class.getName(), service, props);
			log("Succeed in registering service:clazz=" + ManagedService.class.getName() + ":pid="
					+ pid);

		} catch (Exception e) {
			log("Fail to register service: " + ManagedService.class.getName() + ":pid="
					+ pid);
			throw e;
		}
	}

	private void registerFactoryService(final BundleContext context, 
			final String pid,
			final Synchronizer sync) throws Exception {

		final Object service = new ManagedServiceFactoryImpl(sync);
		final Dictionary<String,Object> props = new Hashtable<>();

		log("Going to register ManagedServiceFactory. pid:\n\t"
				+pid);
		props.put(org.osgi.framework.Constants.SERVICE_PID, pid);

		try {
			this.context.registerService(ManagedServiceFactory.class.getName(), service, props);
			log("Succeed in registering service:clazz=" + ManagedServiceFactory.class.getName() + ":pid="
					+ pid);

		} catch (Exception e) {
			log("Fail to register service: " + ManagedServiceFactory.class.getName() + ":pid="
					+ pid);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(final BundleContext context) throws Exception {
		log("Stopping bundle T5");

	}

	class ManagedServiceImpl implements ManagedService {

		final private Synchronizer sync;

		public ManagedServiceImpl(final Synchronizer sync) {
			this.sync = sync;
		}

		public void updated(final Dictionary<String, ? > props)
				throws ConfigurationException {
			if (props != null) {
				String pid = (String) props
						.get(org.osgi.framework.Constants.SERVICE_PID);
				log("ManagedService#updated() is called back. pid: " + pid);
			} else {
				log("ManagedService#updated() is called back. props == null ");
			}
			sync.signal(props);
		}

	}

	class ManagedServiceFactoryImpl implements ManagedServiceFactory {

		final private Synchronizer sync;

		public ManagedServiceFactoryImpl(final Synchronizer sync) {
			this.sync = sync;
		}

		public void updated(final String pid,
				final Dictionary<String, ? > props)
		throws ConfigurationException {
			log("ManagedServicFactorye#updated() is called back. pid: " + pid);
			sync.signal(props);
		}

		public void deleted(final String pid) {
			log("ManagedServicFactorye#deleted() is called back. pid: " + pid);
			final Dictionary<String,Object> props = new Hashtable<>();
			props.put("test", pid);
			props.put("_deleted_", Boolean.TRUE);
			sync.signal(props);
		}
		
		public String getName() { return "T5 Factory"; }
	}

	void log(final String msg) {
		System.out.println("# T5> " + msg);
	}
}
