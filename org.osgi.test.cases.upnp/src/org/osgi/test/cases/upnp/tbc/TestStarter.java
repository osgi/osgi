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
package org.osgi.test.cases.upnp.tbc;

import org.osgi.service.http.HttpService;
import org.osgi.test.cases.upnp.tbc.device.DiscoveryServer;
import org.osgi.test.cases.upnp.tbc.device.description.DServletContext;
import org.osgi.test.cases.upnp.tbc.device.description.DeviceServlet;
import org.osgi.test.cases.upnp.tbc.device.discovery.DiscoveryMsgCreator;
import org.osgi.test.cases.upnp.tbc.device.discovery.DiscoveryMsgSender;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.sleep.Sleep;

/**
 *
 *
 */
public class TestStarter {
	private final DiscoveryServer		server;
	private final DiscoveryMsgSender	sender;

	public TestStarter(HttpService http)
			throws Exception {
		DServletContext dscontext = new DServletContext();
		UPnPControl.log("Register Http Servlet");
		http.registerServlet(UPnPConstants.SR_DESC, new DeviceServlet(),
				null, dscontext);
		http.registerServlet(UPnPConstants.SR_IM, new DeviceServlet(),
				null, dscontext);
		http.registerServlet(UPnPConstants.SR_CON, new DeviceServlet(),
				null, dscontext);
		http.registerServlet(UPnPConstants.SR_EV, new DeviceServlet(),
				null, dscontext);
		http.registerServlet(UPnPConstants.SR_PRES, new DeviceServlet(),
				null, dscontext);
		DefaultTestBundleControl
				.log("Start Discovery Live and Bye messages creator");
		DiscoveryMsgCreator creator = new DiscoveryMsgCreator();
		server = new DiscoveryServer();
		sender = new DiscoveryMsgSender(server, creator);
		server.registerSender(sender);
	}

	public void stop(HttpService http) throws Exception {
		server.unregisterSender(sender);
		while (!sender.isDone()) {
			Sleep.sleep(20);
		}
		try {
			Sleep.sleep(3000);
		}
		catch (Exception e) {
			// ignored
		}
		server.finish();
		http.unregister(UPnPConstants.SR_DESC);
		http.unregister(UPnPConstants.SR_IM);
		http.unregister(UPnPConstants.SR_CON);
		http.unregister(UPnPConstants.SR_EV);
		http.unregister(UPnPConstants.SR_PRES);
	}
}
