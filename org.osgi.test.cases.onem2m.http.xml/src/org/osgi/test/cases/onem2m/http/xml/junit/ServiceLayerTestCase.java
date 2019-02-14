package org.osgi.test.cases.onem2m.http.xml.junit;

import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.onem2m.NotificationListener;
import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.osgi.test.cases.onem2m.http.xml.cseBaseManagement.TdM2mNh01;
import org.osgi.test.cases.onem2m.http.xml.notificationlistener.NotificationListenerImpl;
import org.osgi.test.support.OSGiTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceLayerTestCase extends OSGiTestCase{

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLayerTestCase.class);

	private String bundleSymbolicName = null;
	private ServiceLayer serviceLayerService = null;
	private String uri = null;
	private BundleContext con = null;


    protected void setUp() throws Exception {
    	if(serviceLayerService == null){
    		con = getContext();
			bundleSymbolicName = con.getBundle().getSymbolicName();
			// NotificationListener Registration
			Hashtable<String, String> tbl = new Hashtable<String, String>();
			tbl.put("symbolicName", bundleSymbolicName);
			NotificationListener notificationListener = new NotificationListenerImpl();
			con.registerService(NotificationListener.class, notificationListener, tbl);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				fail();
				return;
			}

			//Get ServiceLayerFactory
			ServiceReference<?> serviceLayerFactory = con.getServiceReference(ServiceLayer.class.getName());
			serviceLayerService = (ServiceLayer) con.getService(serviceLayerFactory);

			if (serviceLayerService == null) {
				fail();
				return;
			}
    	}
    }

    protected void tearDown() throws Exception {

    }


	public void testTdM2mNh01_xml(){
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
			return;
		}
		LOGGER.info("----Start xmlTdM2mNh01 Test----");

		ResourceDTO res = null;

		uri = "http://127.0.0.1:8080/in-name";
		TdM2mNh01 tn1 = new TdM2mNh01();
		// Get CSE data.
		res = tn1.retrieveCSERequest(serviceLayerService, uri);

		// Check result
		assertNotNull("Response is Null.", res);
		assertNotNull("ResourceName is Null.", res.resourceName);
		assertNotNull("ResourceID is Null.", res.resourceID);
		assertNotNull("ResourceType is Null.", res.resourceType);
		assertNotNull("LastModifiedTime is Null", res.lastModifiedTime);
		assertNotNull("CreationTime is Null.", res.creationTime);
		assertNotNull("Attribute-Map is Null.", res.getAttribute());
		assertNull("Label is Not Null.", res.labels);
		assertNull("ParentID is Not Null.", res.parentID);

		LOGGER.info("----xmlTdM2mNh01 Test is complete----");
	}



}
