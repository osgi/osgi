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
package org.osgi.impl.bundle.rsh;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.provisioning.ProvisioningService;
import org.osgi.service.url.AbstractURLStreamHandlerService;
import org.osgi.service.url.URLConstants;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This is the BundleActivator and the URLStreamHandler for the RSH protocol.
 * <p>
 * 
 * @author Benjamin Reed
 */
public class RshHandler extends AbstractURLStreamHandlerService implements
		BundleActivator {
	ServiceTracker<ProvisioningService,ProvisioningService>	st;
	BundleContext	bc;

	/**
	 * Constructor for RshHandler.
	 */
	public RshHandler() {
		super();
	}

	/**
	 * @see org.osgi.framework.BundleActivator#start(BundleContext)
	 */
	@Override
	public void start(@SuppressWarnings("hiding") BundleContext bc)
			throws Exception {
		this.bc = bc;
		st = new ServiceTracker<>(bc, ProvisioningService.class, null);
		st.open();
		Hashtable<String,Object> dict = new Hashtable<>();
		dict.put(URLConstants.URL_HANDLER_PROTOCOL, new String[] {"rsh"});
		bc.registerService("org.osgi.service.url.URLStreamHandlerService",
				this, dict);
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(BundleContext)
	 */
	@Override
	public void stop(BundleContext arg0) throws Exception {
		st.close();
	}

	String getSPID() throws IOException {
		ProvisioningService prov = st.getService();
		Object spid = prov.getInformation().get(
				ProvisioningService.PROVISIONING_SPID);
		if (spid == null)
			throw new IOException("No Service Platform ID");
		if (!(spid instanceof String))
			throw new IOException("Service Platform ID ");
		return (String) spid;
	}

	byte[] getRshSecret() throws IOException {
		ProvisioningService prov = st.getService();
		Object secret = prov.getInformation().get(
				ProvisioningService.PROVISIONING_RSH_SECRET);
		if (secret == null)
			throw new IOException("No RSH secret set");
		if (!(secret instanceof byte[]))
			throw new IOException("RSH secret is not a byte array");
		return (byte[]) secret;
	}

	@Override
	public URLConnection openConnection(URL u) throws IOException {
		String clientFG;
		ProvisioningService ps = st.getService();
		if (ps == null) {
			throw new IOException("ProvisioningService not available");
		}
		else {
			clientFG = (String) ps.getInformation().get(
					ProvisioningService.PROVISIONING_SPID);
		}
		return new RshConnection(u, clientFG, getRshSecret());
	}
}
