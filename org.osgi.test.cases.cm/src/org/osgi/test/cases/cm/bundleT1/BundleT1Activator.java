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
package org.osgi.test.cases.cm.bundleT1;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.test.cases.cm.shared.ModifyPid;
import org.osgi.test.cases.cm.shared.Synchronizer;
import org.osgi.test.cases.cm.shared.Util;

/**
 * Register two ManagedServices with pid1 and pid2 respectively. <br>
 * String filter1 = "(" +
 * org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID + "=sync1-1)"; <br>
 * registerService(context, pid1, msClazz, filter1); <br>
 * String filter2 = "(" +
 * org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID + "=sync1-2)";<br>
 * registerService(context, pid2, msClazz, filter2);
 * 
 * Register two ManagedServiceFactory-s with fpid1 and fpid2 respectively.<br>
 * String filter1 = "(" +
 * org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID + "=sync1-1)";<br>
 * registerService(context, pid1, msfClazz, filter1); String filter2 = "(" +
 * org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID + "=sync1-2)";<br>
 * registerService(context, pid2, msfClazz, filter2);
 * 
 * @author Ikuo YAMASAKI, NTT Corporation
 * 
 * 
 */
public class BundleT1Activator implements BundleActivator {

	private BundleContext context;
	static List<ServiceRegistration< ? >>	srList	= new ArrayList<>(
			4);
	private static final boolean DEBUG = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		log("going to start. BundleT1");
		this.context = context;
		final String pid1 = Util.createPid("pid1");
		final String pid2 = Util.createPid("pid2");
		final String fpid1 = Util.createPid("factoryPid1");
		final String fpid2 = Util.createPid("factoryPid2");


		String filter1 = "("
				+ org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID
				+ "=sync1-1)";
		ServiceRegistration<ManagedService> sr1 = registerService(context, pid1,
				ManagedService.class,
				filter1);
		// srList.add(registerService(context, pid1, msClazz, filter1));
		srList.add(sr1);
		String filter2 = "("
				+ org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID
				+ "=sync1-2)";
		ServiceRegistration<ManagedService> sr2 = registerService(context, pid2,
				ManagedService.class,
				filter2);
		srList.add(sr2);

		String filterF1 = "("
				+ org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID
				+ "=syncF1-1)";
		ServiceRegistration<ManagedServiceFactory> srF1 = registerService(
				context, fpid1,
				ManagedServiceFactory.class,
				filterF1);
		srList.add(srF1);

		String filterF2 = "("
				+ org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID
				+ "=syncF1-2)";
		// String filterF2d = "("
		// + org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID
		// + "=syncF1-2deleted)";
		// registerService(context, fpid2, msfClazz, filterF2, filterF2d);
		ServiceRegistration<ManagedServiceFactory> srF2 = registerService(
				context, fpid2,
				ManagedServiceFactory.class,
				filterF2);
		srList.add(srF2);
		this.context.registerService(ModifyPid.class.getName(),
				new ModifyPidImpl(), null);
	}

	private <S> ServiceRegistration<S> registerService(BundleContext context,
			final String pid, final Class<S> clazz, String filterUpdated)
			throws InvalidSyntaxException, Exception {
		return this.registerService(context, pid, clazz, filterUpdated, null);
	}

	private <S> ServiceRegistration<S> registerService(BundleContext context,
			final String pid, final Class<S> clazz, String filterUpdated,
			String filterDeleted) throws InvalidSyntaxException, Exception {
		Synchronizer syncUpdated = null;
		try {
			syncUpdated = Util.getService(context,
					Synchronizer.class, filterUpdated);
		} catch (IllegalStateException ise) {
			return null;
		}

		@SuppressWarnings("unused")
		Synchronizer syncDeleted = null;
		if (filterDeleted != null) {
			try {
				syncDeleted = Util.getService(context,
						Synchronizer.class, filterDeleted);

			} catch (IllegalStateException ise) {
				return null;
			}
		}
		final Object service;
		if (clazz == ManagedService.class) {
			service = new ManagedServiceImpl(syncUpdated);
		} else {
			service = new ManagedServiceFactoryImpl(syncUpdated);
		}
		Dictionary<String,Object> props = new Hashtable<>();

		log("Going to register " + clazz + ": pid=\n\t" + pid);

		props.put(org.osgi.framework.Constants.SERVICE_PID, pid);
		props.put(org.osgi.framework.Constants.SERVICE_RANKING, "1");

		try {
			@SuppressWarnings("unchecked")
			ServiceRegistration<S> sr = this.context.registerService(clazz,
					(S) service, props);
			log("Succeed in registering service:clazz=" + clazz + ":pid=" + pid);
			return sr;
		} catch (Exception e) {
			log("Fail to register service: " + clazz);
			// e.printStackTrace();
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		log("going to stop. BundleT1");
		srList.clear();
	}

	class ManagedServiceImpl implements ManagedService {
		// private Dictionary props = null;
		final private Synchronizer syncUpdated;

		public ManagedServiceImpl(Synchronizer syncUpdated) {
			this.syncUpdated = syncUpdated;
		}

		public void updated(Dictionary<String, ? > props)
				throws ConfigurationException {
			// this.props = props;
			if (props != null) {
				String pid = (String) props
						.get(org.osgi.framework.Constants.SERVICE_PID);
				log("ManagedService#updated() is called back. pid: " + pid);
			} else {
				log("ManagedService#updated() is called back. props == null ");
			}
			if (syncUpdated != null)
				syncUpdated.signal(props);
			else
				log("sync == null.");

		}

	}

	class ManagedServiceFactoryImpl implements ManagedServiceFactory {
		// private Dictionary props = null;
		final private Synchronizer sync;

		// final private Synchronizer syncDeleted;

		public ManagedServiceFactoryImpl(Synchronizer sync) {
			// public ManagedServiceFactoryImpl(Synchronizer syncUpdated,
			// Synchronizer syncDeleted) {
			this.sync = sync;
			// this.syncDeleted = syncDeleted;
		}

		public void updated(String pid, Dictionary<String, ? > props)
				throws ConfigurationException {
			// this.props = props;
			if (props != null) {
				String fpid = (String) props
						.get(ConfigurationAdmin.SERVICE_FACTORYPID);
				log("ManagedServiceFactory#updated() is called back. pid: "
						+ pid + ", fpid=" + fpid);
			} else {
				log("ManagedServiceFactory#updated() is called back. pid: "
						+ pid + ", props == null ");
			}
			if (sync != null)
				sync.signal(props);
			else
				log("sync == null.");

		}

		public void deleted(String pid) {
			log("ManagedServiceFactory#deleted() is called back. pid: " + pid);
			if (sync != null)
				sync.signalDeleted(pid);
			else
				log("sync == null.");

		}

		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	void log(String msg) {
		if (DEBUG)
			System.out.println("# Register Test> " + msg);
	}

	class ModifyPidImpl implements ModifyPid {

		public void changeMsPid(Long serviceId, String newPid) {
			for (Iterator<ServiceRegistration< ? >> list = srList
					.iterator(); list.hasNext();) {
				ServiceRegistration< ? > sr = list.next();
				if (sr == null)
					continue;
				ServiceReference< ? > reference = sr.getReference();
				if (reference.getProperty(Constants.SERVICE_ID).equals(
						serviceId)) {
					Dictionary<String,Object> props = new Hashtable<>();
					String[] keys = reference.getPropertyKeys();
					for (int i = 0; i < keys.length; i++) {
						props.put(keys[i], reference.getProperty(keys[i]));
					}
					props.put(Constants.SERVICE_PID, newPid);

					sr.setProperties(props);
					break;
				}
			}
		}
	}

}
