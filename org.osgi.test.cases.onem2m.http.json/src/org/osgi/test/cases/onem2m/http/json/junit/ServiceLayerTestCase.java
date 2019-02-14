package org.osgi.test.cases.onem2m.http.json.junit;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.onem2m.NotificationListener;
import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.FilterCriteriaDTO;
import org.osgi.service.onem2m.dto.FilterCriteriaDTO.FilterUsage;
import org.osgi.service.onem2m.dto.NotificationDTO;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.osgi.test.cases.onem2m.http.json.applicationEntity.TdM2mNh06;
import org.osgi.test.cases.onem2m.http.json.applicationEntity.TdM2mNh07;
import org.osgi.test.cases.onem2m.http.json.applicationEntity.TdM2mNh08;
import org.osgi.test.cases.onem2m.http.json.applicationEntity.TdM2mNh09;
import org.osgi.test.cases.onem2m.http.json.container.TdM2mNh10;
import org.osgi.test.cases.onem2m.http.json.contentInstance.TdM2mNh14_1;
import org.osgi.test.cases.onem2m.http.json.contentInstance.TdM2mNh14_2;
import org.osgi.test.cases.onem2m.http.json.contentInstance.TdM2mNh14_3;
import org.osgi.test.cases.onem2m.http.json.contentInstance.TdM2mNh15;
import org.osgi.test.cases.onem2m.http.json.contentInstance.TdM2mNh49_1;
import org.osgi.test.cases.onem2m.http.json.contentInstance.TdM2mNh49_2;
import org.osgi.test.cases.onem2m.http.json.contentInstance.TdM2mNh49_3;
import org.osgi.test.cases.onem2m.http.json.cseBaseManagement.TdM2mNh01;
import org.osgi.test.cases.onem2m.http.json.discovery.TdM2mNh18_1;
import org.osgi.test.cases.onem2m.http.json.discovery.TdM2mNh18_2;
import org.osgi.test.cases.onem2m.http.json.discovery.TdM2mNh19;
import org.osgi.test.cases.onem2m.http.json.discovery.TdM2mNh21;
import org.osgi.test.cases.onem2m.http.json.notification.TdM2mNh48;
import org.osgi.test.cases.onem2m.http.json.notificationlistener.NotificationListenerImpl;
import org.osgi.test.cases.onem2m.http.json.subscription.TdM2mNh22;
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


	public void testTdM2mNh01(){
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
			return;
		}
		LOGGER.info("----Start TdM2mNh01 Test----");

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

		LOGGER.info("----TdM2mNh01 Test is complete----");
	}


	public void testTdM2mNh06(){
		LOGGER.info("----Start TdM2mNh06 Test----");

		ResourceDTO req = new ResourceDTO();
		ResourceDTO res = null;

		uri = "http://127.0.0.1:8080/in-name";
		TdM2mNh06 tn6 = new TdM2mNh06();

		// Specify the resource type and name.
		req.resourceType = 2;
		req.resourceName = "SampleAE";

		// Specify attribute name and value.
		req.attribute = new HashMap<String, Object>();
		req.setAttribute("App-ID", "SampleAE");
		req.setAttribute("requestReachability", true);
		List<String> poa = new ArrayList<String>();
		poa.add("http://127.0.0.1:8080/complianceTest");
		req.setAttribute("pointOfAccess", poa);

		// Create AE.
		res = tn6.createAERequest(serviceLayerService, uri, req);

		// Check result
		assertNotNull("Response of TdM2mNh06 is NULL.", res);

		Map<String, Object> reqAttrMap = req.getAttribute();
		Map<String, Object> resAttrMap = res.getAttribute();

		// Check result
		assertNotNull("Attribute of Response of TdM2mNh06 is NULL.", res.getAttribute());
		assertEquals("ResourceName are unmatch :  Req = " + req.resourceName + "Res = " + res.resourceName,
				req.resourceName, res.resourceName);
		assertEquals("ResourceType are unmatch : Req = " + req.resourceType + "Res = " + res.resourceType,
				req.resourceType, res.resourceType);
		assertEquals("Attribute[App-ID] are unmatch : Req = " + reqAttrMap.get("App-ID") + ", Res = "
				+ resAttrMap.get("App-ID"), reqAttrMap.get("App-ID"), resAttrMap.get("App-ID"));
		assertEquals(
				"Attribute[RequestReachability] are unmatch : Req = " + reqAttrMap.get("requestReachability") + ", Res = "
						+ resAttrMap.get("requestReachability"),
				reqAttrMap.get("requestReachability"), resAttrMap.get("requestReachability"));
		assertEquals(
				"Attribute[PointOfAccess] are unmatch : Req = " + reqAttrMap.get("pointOfAccess") + ", Res = "
						+ resAttrMap.get("pointOfAccess"),
				reqAttrMap.get("pointOfAccess"), resAttrMap.get("pointOfAccess"));
		assertNotNull("ResourceID is Null", res.resourceID);
		assertNotNull("LastModifiedTime is Null", res.lastModifiedTime);
		assertNotNull("CreationTime is Null.", res.creationTime);
		assertNotNull("ParentID is Null.", res.parentID);
		assertNull("Label is Not Null.", res.labels);

		LOGGER.info("----TdM2mNh06 Test is complete----");
	}

	public void testTdM2mNh07() {
		LOGGER.info("----Start TdM2mNh07 Test----");

		ResourceDTO res = null;

		uri = "http://127.0.0.1:8080/in-name/SampleAE";
		TdM2mNh07 tn7 = new TdM2mNh07();

		// Get AE data.
		res = tn7.retrieveAERequest(serviceLayerService, uri);

		// Check result
		assertNotNull("Response is Null.", res);
		assertNotNull("ResourceName is Null.", res.resourceName);
		assertNotNull("ResourceID is Null.", res.resourceID);
		assertNotNull("ResourceType is Null.", res.resourceType);
		assertNotNull("LastModifiedTime is Null", res.lastModifiedTime);
		assertNotNull("CreationTime is Null.", res.creationTime);
		assertNotNull("Attribute-Map is Null.", res.getAttribute());
		assertNotNull("ParentID is Not Null.", res.parentID);
		assertNull("Label is Not Null.", res.labels);

		LOGGER.info("----TdM2mNh07 Test is complete----");
	}

	public void testTdM2mNh08() throws Exception {
		LOGGER.info("----Start TdM2mNh08 Test----");

		ResourceDTO req = new ResourceDTO();
		ResourceDTO res = null;

		TdM2mNh07 tn7 = new TdM2mNh07();
		TdM2mNh08 tn8 = new TdM2mNh08();
		uri = "http://127.0.0.1:8080/in-name/SampleAE";

		// Get data before update.
		ResourceDTO befData = tn7.retrieveAERequest(serviceLayerService, uri);

		// Specify the resource type.
		req.resourceType = 2;
		// Specify attribute name and value to update.
		req.labels = new ArrayList<String>();
		req.labels.add("SampleData1");
		req.labels.add("SampleData2");
		req.labels.add("SampleData3");

		// Update data.
		res = tn8.updateAERequest(serviceLayerService, uri, req);

		// Check result
		assertNotNull("Response is Null", res);
		// Check for "labels" of the Response data is not Null.
		assertNotEquals("AE Data is not updated", res.labels, befData.labels);
		// Check for Response data after update is the same as Request data.
		assertEquals(req.labels.size(), res.labels.size());
		for (int i = 0; req.labels.size() > i; i++) {
			assertEquals(req.labels.get(i), res.labels.get(i));
		}
		LOGGER.info("----TdM2mNh08 Test is complete----");
	}

	public void testTdM2mNh09() {
		LOGGER.info("----Start TdM2mNh09 Test----");

		uri = "http://127.0.0.1:8080/in-name/SampleAE";
		TdM2mNh09 tn9 = new TdM2mNh09();

		// Delete AE
		Boolean deleteResult = tn9.deleteAERequest(serviceLayerService, uri);

		// Check result
		assertTrue("AE-Delete is failure.", deleteResult);

		LOGGER.info("----TdM2mNh09 Test is complete----");
	}

	public void testTdM2mNh10() {
		LOGGER.info("----Start TdM2mNh10 Test----");

		ResourceDTO req = new ResourceDTO();
		ResourceDTO res = null;

		uri = "http://127.0.0.1:8080/in-name/SampleAE";
		TdM2mNh10 tn10 = new TdM2mNh10();
		// Specify the resource type and name.
		req.resourceType = 3;
		req.resourceName = "SampleCon";

		// Create Container.
		res = tn10.createConRequest(serviceLayerService, uri, req);

		// Check result
		assertNotNull("Response is Null", res);
		assertEquals("ResourceName are unmatch :  Req = " + req.resourceName + ", Res = " + res.resourceName,
				req.resourceName, res.resourceName);
		assertEquals("ResourceType are unmatch : Req = " + req.resourceType + ", Res = " + res.resourceType,
				req.resourceType, res.resourceType);
		assertNotNull("ResourceID is Null", res.resourceID);
		assertNotNull("LastModifiedTime is Null", res.lastModifiedTime);
		assertNotNull("CreationTime is Null.", res.creationTime);
		assertNotNull("ParentID is Not Null.", res.parentID);
		assertNull("Labels is Not Null.", res.labels);
		assertNotNull("Attribute is Null.", res.getAttribute());

		LOGGER.info("----TdM2mNh10 Test is complete----");
	}

	public void testTdM2mNh14() {
		LOGGER.info("----Start TdM2mNh14 Test----");

		ResourceDTO req = new ResourceDTO();
		ResourceDTO res = null;

		uri = "http://127.0.0.1:8080/in-name/SampleAE/SampleCon";
		// Get Container data --- before creating ContentInstance
		TdM2mNh14_1 tn141 = new TdM2mNh14_1();
		ResourceDTO tn141res = tn141.retrieveConBeforeRequest(serviceLayerService, uri);

		// Specify the resource type and name.
		TdM2mNh14_2 tn142 = new TdM2mNh14_2();
		req.resourceType = 4;
		req.resourceName = "SampleCin";
		// Specify attribute name and value.
		req.attribute = new HashMap<String, Object>();
		req.setAttribute("content", "1");
		req.labels = new ArrayList<String>();
		req.labels.add("1");
		req.labels.add("2");
		res = tn142.createCinRequest(serviceLayerService, uri, req);

		// Get Container data --- after creating ContentInstance
		TdM2mNh14_3 tn143 = new TdM2mNh14_3();
		ResourceDTO tn143res = tn143.retrieveConAfterRequest(serviceLayerService, uri);

		// Check result of create ContentInstance.
		assertNotNull("Response is Null", res);
		assertEquals("ResourceName are unmatch :  Req = " + req.resourceName + ", Res = " + res.resourceName,
				req.resourceName, res.resourceName);
		assertEquals("ResourceType are unmatch : Req = " + req.resourceType + ", Res = " + res.resourceType,
				req.resourceType, res.resourceType);
		assertNotNull("ResourceID is Null", res.resourceID);
		assertNotNull("LastModifiedTime is Null", res.lastModifiedTime);
		assertNotNull("CreationTime is Null.", res.creationTime);
		assertNotNull("ParentID is Null.", res.parentID);

		assertNotNull("Labels is Null.", res.labels);
		assertNotEquals("Labels_Size is 0", res.labels.size(), 0);

		assertEquals(res.labels.get(0), "1");
		assertEquals(res.labels.get(1), "2");
		assertNotNull("Attribute is Null.", res.getAttribute());
		assertEquals("Attribute[content] are unmatch", req.getAttribute().get("content"),
				res.getAttribute().get("content"));

		// Check data after and before creating ContentInstance.
		assertEquals(1, tn143res.getAttribute().get("stateTag"));
		assertEquals(1, tn143res.getAttribute().get("currentNrOfInstances"));

		LOGGER.info("----TdM2mNh14 Test is complete----");
	}

	public void testTdM2mNh15() {
		LOGGER.info("----Start TdM2mNh15 Test----");

		uri = "http://127.0.0.1:8080/in-name/SampleAE/SampleCon/SampleCin";
		TdM2mNh15 tn15 = new TdM2mNh15();
		ResourceDTO res = null;

		// Get ContentInstance data
		res = tn15.retrieveCinRequest(serviceLayerService, uri);

		// Check result
		assertNotNull("Response is Null.", res);
		assertEquals("SampleCin", res.resourceName);
		assertEquals(new Integer(4), res.resourceType);
		assertEquals(new Integer(0), res.getAttribute().get("stateTag"));
		assertEquals("1", res.labels.get(0));
		assertEquals("2", res.labels.get(1));

		LOGGER.info("----TdM2mNh15 Test is complete----");
	}

	public void testTdM2mNh49() {
		LOGGER.info("----Start TdM2mNh49 Test----");

		uri = "http://127.0.0.1:8080/in-name/SampleAE/SampleCon/la";
		// create a second ContentInstance
		TdM2mNh49_1 tn491 = new TdM2mNh49_1();
		ResourceDTO tn491res = tn491.createSecondCinRequest(serviceLayerService,
				"http://127.0.0.1:8080/in-name/SampleAE/SampleCon");

		if (tn491res == null) {
			fail("TdM2mNh49_1 : create a second ContentInstance.");
		}

		/*
		 * After creating the ContentInstance, get the latest ContentInstance
		 * data in Container[SampleCon]
		 */
		TdM2mNh49_2 tn492 = new TdM2mNh49_2();
		ResourceDTO tn492res1 = tn492.retrieveCinRequest(serviceLayerService, uri);

		// delete the second ContentInstance
		TdM2mNh49_3 tn493 = new TdM2mNh49_3();
		Boolean tn493res = tn493.deleteCinRequest(serviceLayerService,
				"http://127.0.0.1:8080/in-name/SampleAE/SampleCon/" + tn491res.resourceName);

		if (tn493res == false) {
			fail("TdM2mNh49_3 : delete a second ContentInstance.");
		}

		/*
		 * After deleting the second ContentInstance, get the latest
		 * ContentInstance data in Container[SampleCon]
		 */
		ResourceDTO tn492res2 = tn492.retrieveCinRequest(serviceLayerService, uri);

		// Check Result
		assertNotEquals("ResourceName are match : " + tn492res1.resourceName, tn492res1.resourceName,
				tn492res2.resourceName);
		assertNotEquals("ResourceID are match : " + tn492res1.resourceID, tn492res1.resourceID, tn492res2.resourceID);
		assertNotEquals("lastModifiedTime are match : " + tn492res1.lastModifiedTime, tn492res1.lastModifiedTime,
				tn492res2.lastModifiedTime);
		assertNotEquals("creationTime are match : " + tn492res1.creationTime, tn492res1.creationTime,
				tn492res2.creationTime);

		LOGGER.info("----TdM2mNh49 Test is complete----");
	}

	public void testTdM2mNh18() {
		LOGGER.info("----Start Preparations for TdM2mNh18 Test----");
		List<String> resList = new ArrayList<String>();

		uri = "http://127.0.0.1:8080/in-name/SampleAE/SampleCon";
		TdM2mNh18_1 tn181pre = new TdM2mNh18_1();
		Boolean prepareFlg = tn181pre.createCinRequest(serviceLayerService, uri);

		if(!prepareFlg) {
			fail("Preparations for TdM2mNh18 Test is failure.");
		}
		LOGGER.info("----Preparations for TdM2mNh18 Test is complete----");

		LOGGER.info("----Start TdM2mNh18 Test");

		uri = "http://127.0.0.1:8080/in-name";
		TdM2mNh18_2 tn18 = new TdM2mNh18_2();
		// Set value for FilterUsage.
		FilterCriteriaDTO fc = new FilterCriteriaDTO();
		fc.filterUsage = FilterUsage.DiscoveryCriteria;

		// Discovery
		resList = tn18.discoveryCinRequest(serviceLayerService, uri, fc);

		// Check result
		assertNotNull("Discovery Response is Null.", resList);
		assertNotEquals("Discovery List Size is 0.", resList.size(), 0);

		String badRes = null;
		Boolean badFlg = false;
		for (int i = 0; resList.size() > i; i++) {
			switch (resList.get(i)) {
			case "/in-cse/in-name":
			case "/in-cse/in-name/SampleAE":
			case "/in-cse/in-name/SampleAE/SampleCon":
			case "/in-cse/in-name/SampleAE/SampleCon/SampleCin":
			case "/in-cse/in-name/SampleAE/SampleCon/SampleCin88":
			case "/in-cse/in-name/SampleAE/SampleCon/SampleCin99":
			case "/in-cse/in-name/acp_admin":
				break;
			default:
				if (badRes == null) {
					badRes = "[" + resList.get(i) + "]";
					badFlg = true;
				} else {
					badRes += ", [" + resList.get(i) + "]";
				}
				break;
			}
		}
		assertFalse("Unexpected data is included in this response result : " + badRes, badFlg);

		LOGGER.info("----TdM2mNh18 Test is complete----");
	}

	public void testTdM2mNh19() {
		LOGGER.info("----Start TdM2mNh19 Test----");
		List<String> resList = new ArrayList<String>();
		uri = "http://127.0.0.1:8080/in-name";
		TdM2mNh19 tn19 = new TdM2mNh19();

		// Set a value for FilterUsage and Attribute.
		FilterCriteriaDTO fc = new FilterCriteriaDTO();
		fc.resourceType = new ArrayList<Integer>();
		fc.resourceType.add(4);
		fc.filterUsage = FilterUsage.DiscoveryCriteria;

		// Discovery(Label)
		resList = tn19.discoveryCinRequest(serviceLayerService, uri, fc);

		// Check result
		assertNotNull("Discovery Response is Null.", resList);
		assertNotEquals("Discovery List Size is 0.", resList.size(), 0);

		String badRes = null;
		Boolean badFlg = false;
		for (int i = 0; resList.size() > i; i++) {
			switch (resList.get(i)) {
			case "/in-cse/in-name/SampleAE/SampleCon/SampleCin":
			case "/in-cse/in-name/SampleAE/SampleCon/SampleCin88":
			case "/in-cse/in-name/SampleAE/SampleCon/SampleCin99":
				break;
			default:
				if (badRes == null) {
					badRes = "[" + resList.get(i) + "]";
					badFlg = true;
				} else {
					badRes += ", [" + resList.get(i) + "]";
				}
				break;
			}
		}
		assertFalse("Unexpected data is included in this response result : " + badRes, badFlg);

		LOGGER.info("----TdM2mNh19 Test is complete----");
	}

	public void testTdM2mNh21() {
		LOGGER.info("----Start TdM2mNh21 Test----");
		List<String> resList = new ArrayList<String>();
		uri = "http://127.0.0.1:8080/in-name";
		TdM2mNh21 tn21 = new TdM2mNh21();

		// Set a value for FilterUsage and SemanticsFilter.
		FilterCriteriaDTO fc = new FilterCriteriaDTO();
		fc.resourceType = new ArrayList<Integer>();
		fc.resourceType.add(4);
		fc.labels = new ArrayList<String>();
		fc.labels.add("1");
		fc.filterUsage = FilterUsage.DiscoveryCriteria;

		// Discovery(Multiple)
		resList = tn21.discoveryCinRequest(serviceLayerService, uri, fc);

		// Check result
		assertNotNull("Discovery Response is Null.", resList);
		assertNotEquals("Discovery List Size is 0.", resList.size(), 0);

		for (int i = 0; resList.size() > i; i++) {
			switch (resList.get(i)) {
			case "/in-cse/in-name/SampleAE/SampleCon/SampleCin":
			case "/in-cse/in-name/SampleAE/SampleCon/SampleCin88":
				break;
			default:
				fail();
				break;
			}
		}

		LOGGER.info("----TdM2mNh21 Test is complete----");
	}

	public void testTdM2mNh22() {
		LOGGER.info("----Start TdM2mNh22 Test----");
		ResourceDTO req = new ResourceDTO();
		ResourceDTO res = null;

		uri = "http://127.0.0.1:8080/in-name/SampleAE";
		TdM2mNh22 tn22 = new TdM2mNh22();

		// Specify the resource type and name.
		req.resourceType = 23;
		req.resourceName = "SampleSub";
		req.attribute = new HashMap<String, Object>();
		List<String> nu = new ArrayList<String>();
		nu.add("http://127.0.0.1:8080/complianceTest");
		req.setAttribute("notificationURI", nu);

		// Notification Request
		res = tn22.createSubRequest(serviceLayerService, uri, req);

		// Check result
		assertNotNull("Response is Null", res);

		Map<String, Object> reqAttrMap = req.getAttribute();
		Map<String, Object> resAttrMap = res.getAttribute();

		// Check result
		assertEquals("ResourceName are unmatch :  Req = " + req.resourceName + ", Res = " + res.resourceName,
				req.resourceName, res.resourceName);
		assertEquals("ResourceType are unmatch : Req = " + req.resourceType + ", Res = " + res.resourceType,
				req.resourceType, res.resourceType);
		assertNotNull("ResourceID is Null", res.resourceID);
		assertNotNull("LastModifiedTime is Null", res.lastModifiedTime);
		assertNotNull("CreationTime is Null.", res.creationTime);
		assertNotNull("ParentID is Null.", res.parentID);
		assertNotNull("Attribute is Not Null.", res.getAttribute());
		assertEquals(
				"Attribute[notificationURI] are unmatch : Req = " + reqAttrMap.get("notificationURI") + ", Res = "
						+ resAttrMap.get("notificationURI"),
				reqAttrMap.get("notificationURI"), resAttrMap.get("notificationURI"));

		LOGGER.info("----TdM2mNh22 Test is complete----");
	}

	//NotificationListener

	public void testTdM2mNh48() {
		LOGGER.info("----Start TdM2mNh48 Test----");

		uri = "http://127.0.0.1:8080/in-name/SampleAE";
		TdM2mNh48 tn48 = new TdM2mNh48();
		NotificationDTO notifyReq = new NotificationDTO();

		// Specify the reference.
		notifyReq.notificationEvent = new HashMap<String, Object>();
		notifyReq.notificationEvent.put("representation", 1);
		notifyReq.subscriptionDeletion = false;
		notifyReq.subscriptionReference = "http://127.0.0.1:8080/in-name/SampleAE";
		Boolean notifyRes = tn48.notifyRequest(serviceLayerService, uri, notifyReq);

		// Check result
		if(notifyRes){
			System.out.println("true");
		} else {
			System.out.println("false");
		}
		assertTrue("Notification request is failure.", notifyRes);

		LOGGER.info("----TdM2mNh48 Test is complete----");
	}

}
