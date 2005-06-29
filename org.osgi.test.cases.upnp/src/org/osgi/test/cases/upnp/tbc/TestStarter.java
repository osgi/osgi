package org.osgi.test.cases.upnp.tbc;

import org.osgi.service.http.*;
import org.osgi.test.cases.upnp.tbc.device.*;
import org.osgi.test.cases.upnp.tbc.device.description.*;
import org.osgi.test.cases.upnp.tbc.device.discovery.*;
import org.osgi.test.cases.util.*;

/**
 * 
 * 
 */
public class TestStarter {
	private HttpService					http;
	private DiscoveryServer				server;
	private DiscoveryMsgSender			sender;
	private DefaultTestBundleControl	logger;

	public TestStarter(HttpService http, DefaultTestBundleControl logger)
			throws Exception {
		this.http = http;
		this.logger = logger;
		DServletContext dscontext = new DServletContext();
		logger.log("Register Http Servlet");
		http.registerServlet(UPnPConstants.SR_DESC, new DeviceServlet(logger),
				null, dscontext);
		http.registerServlet(UPnPConstants.SR_IM, new DeviceServlet(logger),
				null, dscontext);
		http.registerServlet(UPnPConstants.SR_CON, new DeviceServlet(logger),
				null, dscontext);
		http.registerServlet(UPnPConstants.SR_EV, new DeviceServlet(logger),
				null, dscontext);
		http.registerServlet(UPnPConstants.SR_PRES, new DeviceServlet(logger),
				null, dscontext);
		logger.log("Start Discovery Live and Bye messages creator");
		DiscoveryMsgCreator creator = new DiscoveryMsgCreator();
		server = new DiscoveryServer(logger);
		sender = new DiscoveryMsgSender(server, creator);
		server.registerSender(sender);
	}

	public void stop() throws Exception {
		server.unregisterSender(sender);
		while (!server.isDone) {
			Thread.sleep(20);
		}
		try {
			Thread.sleep(3000);
		}
		catch (Exception e) {
		}
		server.finish();
	}
}