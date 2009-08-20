package org.osgi.test.cases.upnp.tbc;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.test.cases.upnp.tbc.device.discovery.ServicesListener;
import org.osgi.test.cases.upnp.tbc.export.UPnPExportedDevice;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * 
 * 
 */
public class UPnPControl extends DefaultTestBundleControl {
	private static HttpService	http;
	private static TestStarter	start;
	private static boolean		inited	= false;

	public void setUp() throws Exception {
		if (!inited) {
			inited = true;
			log("Prepare for UPnP Test Case");
			http = (HttpService) getService(HttpService.class);
			UPnPConstants.init();
			// UPnPConstants.HTTP_PORT = ((Integer)
			// httpRef.getProperty("openPort")).intValue();
			UPnPConstants.HTTP_PORT = Integer.parseInt(System.getProperty(
					"org.osgi.service.http.port", "80"));
			log("Register Service Listener to listen for service changes");
			ServicesListener listener = new ServicesListener(getContext());
			listener.open();
			log("Start the UPnP Test Starter");
			start = new TestStarter(http, this);
			listener.waitFor(3);
		}
	}

	public void unprepare() throws Exception {
		start.stop();
		ungetService(http);
	}

	// ==========================================TEST
	// METHODS====================================================================//
	public void testDiscovery() {
		new UPnPTester(UPnPTester.DISCOVERY, ServicesListener.getUPnPDevice(),
				getContext());
	}

	public void testControl() {
		new UPnPTester(UPnPTester.CONTROL, ServicesListener.getUPnPDevice(),
				getContext());
	}

	public void testEvent() {
		new UPnPTester(UPnPTester.EVENTING, ServicesListener.getUPnPDevice(),
				getContext());
	}

	public void testExport() {
		log("Creating new device and making it UPnP device");
		UPnPExportedDevice ex_device = UPnPExportedDevice.newUPnPTestervice();
		ServiceRegistration sr = getContext().registerService(
				UPnPDevice.class.getName(), ex_device,
				ex_device.getDescriptions(null));
		try {
			Thread.sleep(4000);
		}
		catch (Exception er) {
			// ignored
		}
		try {
			new UPnPTester(UPnPTester.EXPORT, ServicesListener.getUPnPDevice(),
					getContext());
		}
		finally {
			sr.unregister();
		}
	}
}