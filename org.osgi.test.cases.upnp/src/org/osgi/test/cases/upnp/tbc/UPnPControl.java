package org.osgi.test.cases.upnp.tbc;

import java.net.*;
import javax.servlet.http.*;
import org.osgi.framework.*;
import org.osgi.service.http.*;
import org.osgi.service.upnp.*;
import org.osgi.test.cases.upnp.tbc.device.discovery.*;
import org.osgi.test.cases.upnp.tbc.export.*;
import org.osgi.test.cases.util.*;

/**
 * 
 * 
 */
public class UPnPControl extends DefaultTestBundleControl {
	private ServiceRegistration	srf;
	private ServiceReference	httpRef;
	private HttpService			http;
	private ServiceReference	deviceRef;
	private TestStarter			start;
	 static String[] methods = new String[] {"testDiscovery",
		  "testControl",
		  "testEvent",
		  "testExport"};

	public String[] getMethods() {
		return methods;
	}

	public void prepare() throws Exception {
		log("Prepare for UPnP Test Case");
		httpRef = getContext().getServiceReference(
				"org.osgi.service.http.HttpService");
		http = (HttpService) getContext().getService(httpRef);
		UPnPConstants.init();
		//UPnPConstants.HTTP_PORT = ((Integer)
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

	//==========================================TEST
	// METHODS====================================================================//
	public void testDiscovery() {
		try {
			new UPnPTester(this, UPnPTester.DISCOVERY,
					ServicesListener.getUPnPDevice(), getContext());
		}
		catch (Exception er) {
			er.printStackTrace();
		}
	}

	public void testControl() {
		try {
			UPnPTester device = new UPnPTester(this, UPnPTester.CONTROL,
					ServicesListener.getUPnPDevice(), getContext());
		}
		catch (Exception er) {
			er.printStackTrace();
		}
	}

	public void testEvent() {
		try {
			UPnPTester device = new UPnPTester(this, UPnPTester.EVENTING,
					ServicesListener.getUPnPDevice(), getContext());
		}
		catch (Exception er) {
			er.printStackTrace();
		}
	}

	public void testExport() {
		log("Creating new device and making it UPnP device");
		UPnPExportedDevice ex_device = UPnPExportedDevice.newUPnPTestervice();
		srf = getContext().registerService(UPnPDevice.class.getName(),
				ex_device, ex_device.getDescriptions(null));
		try {
			Thread.sleep(4000);
		}
		catch (Exception er) {
		}
		UPnPTester device = new UPnPTester(this, UPnPTester.EXPORT,
				ServicesListener.getUPnPDevice(), getContext());
	}

	public void unprepare() {
		try {
			getContext().ungetService(httpRef);
			start.stop();
		}
		catch (Exception er) {
			er.printStackTrace();
		}
	}
}