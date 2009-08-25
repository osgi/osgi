package org.osgi.test.cases.upnp.tbc;

import org.osgi.service.http.HttpService;
import org.osgi.test.cases.upnp.tbc.device.DiscoveryServer;
import org.osgi.test.cases.upnp.tbc.device.description.DServletContext;
import org.osgi.test.cases.upnp.tbc.device.description.DeviceServlet;
import org.osgi.test.cases.upnp.tbc.device.discovery.DiscoveryMsgCreator;
import org.osgi.test.cases.upnp.tbc.device.discovery.DiscoveryMsgSender;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

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

	public void stop() throws Exception {
		server.unregisterSender(sender);
		while (!sender.isDone()) {
			Thread.sleep(20);
		}
		try {
			Thread.sleep(3000);
		}
		catch (Exception e) {
			// ignored
		}
		server.finish();
	}
}