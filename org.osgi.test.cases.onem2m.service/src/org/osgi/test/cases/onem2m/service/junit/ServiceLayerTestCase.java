package org.osgi.test.cases.onem2m.service.junit;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.onem2m.NotificationListener;
import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.FilterCriteriaDTO;
import org.osgi.service.onem2m.dto.FilterCriteriaDTO.FilterUsage;
import org.osgi.service.onem2m.dto.NotificationDTO;
import org.osgi.service.onem2m.dto.NotificationEventDTO;
import org.osgi.service.onem2m.dto.RequestPrimitiveDTO;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.promise.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Case for oneM2M
 * 
 * '-' is a special character to indicate CSEBase, that is root of resource
 * tree.
 * 
 */
public class ServiceLayerTestCase extends OSGiTestCase {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLayerTestCase.class);

	private ServiceLayer serviceLayerService = null;
	private String uri = null;
	private BundleContext con = null;

	protected void setUp() throws Exception {
		if (serviceLayerService == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				fail();
				return;
			}
			con = getContext();
			// Get ServiceLayerFactory
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

	@Test
	public void testRetrieve1() {
		LOGGER.info("----Start RETRIEVE Test 1----");

		ResourceDTO res = null;

		try {
			// retrieve with short cut of csebase.
			res = serviceLayerService.retrieve("-").getValue();
		} catch (Exception e) {
			e.printStackTrace();
			assertNotNull(null);// TODO
			res = null;
		}

		// Check result
		LOGGER.info("Response Data:\n" + res.toString());

		assertNotNull("Response is Null.", res);
		assertEquals(String.valueOf(res.resourceType), "5");
		assertNotNull("ResourceID is Null.", res.resourceID);
		assertNotNull("LastModifiedTime is Null", res.lastModifiedTime);
		assertNotNull("CreationTime is Null.", res.creationTime);

		assertNotNull("CSE attribute is null.", res.attribute);
		String cseid = (String) res.attribute.get("CSE-ID");
		assertNotNull("CSE-ID is null.", cseid);
		String csebaseName = res.resourceName;

		try {
			// retrieve with short cut of csebase with SP-relative name.
			res = serviceLayerService.retrieve("/" + cseid + "/-").getValue();
		} catch (Exception e) {
			e.printStackTrace();
			assertNotNull(null);// TODO
			res = null;
		}

		// Check result
		LOGGER.info("Response Data:\n" + res.toString());

		assertNotNull("Response is Null.", res);
		assertEquals(String.valueOf(res.resourceType), "5");
		assertNotNull("ResourceID is Null.", res.resourceID);
		assertNotNull("LastModifiedTime is Null", res.lastModifiedTime);
		assertNotNull("CreationTime is Null.", res.creationTime);

		assertNotNull("CSE's attribute is null.", res.attribute);
		String cseid2 = (String) res.attribute.get("CSE-ID");
		assertNotNull("CSE-ID is null.", cseid2);
		assertEquals(cseid, cseid2);

		try {
			// retrieve regular SP-relative name.
			res = serviceLayerService.retrieve("/" + cseid + "/" + csebaseName).getValue();
		} catch (Exception e) {
			e.printStackTrace();
			assertNotNull(null);// TODO
			res = null;
		}

		// Check result
		LOGGER.info("Response Data:\n" + res.toString());

		assertNotNull("Response is Null.", res);
		assertEquals(String.valueOf(res.resourceType), "5");
		assertNotNull("ResourceID is Null.", res.resourceID);
		assertNotNull("LastModifiedTime is Null", res.lastModifiedTime);
		assertNotNull("CreationTime is Null.", res.creationTime);

		assertNotNull("CSE's attribute is null.", res.attribute);
		String cseid3 = (String) res.attribute.get("CSE-ID");
		assertNotNull("CSE-ID is null.", cseid3);
		assertEquals(cseid, cseid3);

		LOGGER.info("----RETRIEVE Test 1 is complete----");
	}

	@Test
	public void testRetrieve2() {
		LOGGER.info("----Start RETRIEVE Test 2----");

		ResourceDTO res = null;

		uri = "/in-cse2/-";

		try {
			res = serviceLayerService.retrieve(uri).getValue();
		} catch (Exception e) {
			res = null;
		}

		// Check result
		assertEquals(null, res);

		LOGGER.info("----RETRIEVE Test 2 is complete----");
	}

	@Test
	public void testCreate1() {
		LOGGER.info("----Start CREATE Test 1----");

		ResourceDTO req = new ResourceDTO();
		ResourceDTO res = null;

		uri = "-";
		req.resourceName = "cnt1";
		req.resourceType = 3;

		try {
			res = serviceLayerService.create(uri, req).getValue();
		} catch (Exception e) {
			res = null;
		}

		// Check result
		assertNotNull("Response is Null.", res);
		assertEquals(req.resourceName, res.resourceName);
		assertNotNull("ResourceID IS  is Null.", res.resourceID);
		assertNotNull("LastModifiedTime is Null", res.lastModifiedTime);
		assertNotNull("CreationTime is Null.", res.creationTime);

		LOGGER.info("----CREATE Test 1 is complete----");
	}

	@Test
	public void testCreate2() {
		LOGGER.info("----Start CREATE Test 2----");

		ResourceDTO req = new ResourceDTO();
		ResourceDTO res = null;

		uri = "-";
		req.resourceName = "cnt1";
		req.resourceType = 3;

		try {
			res = serviceLayerService.create(uri, req).getValue();
		} catch (Exception e) {
			fail();
		}

		// Check result
		assertEquals(null, res);

		LOGGER.info("----CREATE Test 2 is complete----");
	}

	/**
	 * create under wrong path.
	 */
	@Test
	public void testCreate3() {
		LOGGER.info("----Start CREATE Test 3----");

		ResourceDTO req = new ResourceDTO();
		ResourceDTO res = new ResourceDTO();

		uri = "/in-cse2/-";
		req.resourceName = "cnt2";
		req.resourceType = 3;

		try {
			res = serviceLayerService.create(uri, req).getValue();
		} catch (Exception e) {
			res = null;
		}

		// Check result
		assertEquals(null, res);// TODO check return code.

		LOGGER.info("----CREATE Test 3 is complete----");
	}

	@Test
	public void testUpdate1() {
		LOGGER.info("----Start Update Test 1----");

		ResourceDTO req = new ResourceDTO();
		ResourceDTO res = null;

		uri = "-";
		req.resourceName = "updateCnt";
		req.resourceType = 3;

		try {
			res = serviceLayerService.create(uri, req).getValue();
		} catch (Exception e) {
			fail();
		}
		System.out.println(res.toString());

		ResourceDTO req2 = new ResourceDTO();
		String lblText = "updateTest";

		uri = "-/updateCnt";
		req2.resourceName = res.resourceName;
		req2.resourceType = res.resourceType;
		req2.resourceID = res.resourceID;
		List<String> lbl = new ArrayList<String>();
		lbl.add(lblText);
		req2.labels = lbl;

		try {
			Thread.sleep(1200);// to ensure second digit to change
			serviceLayerService.update(uri, req2).getValue();
		} catch (Exception e) {
			fail();
		}

		LOGGER.info("finished first update");// TODO remove this.

		ResourceDTO res3 = null;

		// retrieve updated resource
		uri = "-/updateCnt";

		try {
			res3 = serviceLayerService.retrieve(uri).getValue();
		} catch (Exception e) {
			fail();
		}

		// Check result
		System.out.println(res3.toString());
		assertEquals(res.resourceName, res3.resourceName);
		assertEquals(res.resourceType, res3.resourceType);
		assertEquals(res.resourceID, res3.resourceID);
		assertEquals(res.creationTime, res3.creationTime);
		if (res.lastModifiedTime.equals(res3.lastModifiedTime)) {
			LOGGER.warn("modification time is expected changed." + " before:" + res.lastModifiedTime + " after:"
					+ res3.lastModifiedTime);
			fail();
		}
		assertEquals(lblText, res3.labels.get(0));

		LOGGER.info("----Update Test 1 is complete----");
	}

	@Test
	public void testUpdate2() {
		LOGGER.info("----Start Update Test 2----");

		ResourceDTO req = new ResourceDTO();
		ResourceDTO res = null;

		String lblText = "updateTest";

		uri = "-/updateCnt2";
		List<String> lbl = new ArrayList<String>();
		lbl.add(lblText);
		req.labels = lbl;

		try {
			res = serviceLayerService.update(uri, req).getValue();
		} catch (Exception e) {
			res = null;
		}

		// Check result
		assertEquals(null, res);

		LOGGER.info("----Update Test 2 is complete----");
	}

	@Test
	public void testDelete1() {
		LOGGER.info("----Start Delete Test 1----");

		ResourceDTO req = new ResourceDTO();

		uri = "-";
		req.resourceName = "deleteCnt";
		req.resourceType = 3;

		try {
			serviceLayerService.create(uri, req).getValue();
		} catch (Exception e) {
			fail();
		}

		uri = "-/deleteCnt";

		try {
			if (!serviceLayerService.delete(uri).getValue()) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}

		LOGGER.info("----Delete Test 1 is complete----");
	}

	@Test
	public void testDelete2() {
		LOGGER.info("----Start Delete Test 2----");

		uri = "-/deleteCnt2";

		try {
			if (serviceLayerService.delete(uri).getValue()) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}

		LOGGER.info("----Delete Test 2 is complete----");
	}

	@Test
	public void testDiscovery1() {
		LOGGER.info("----Start Discovery Test 1----");

		ResourceDTO req1 = new ResourceDTO();

		uri = "-";
		req1.resourceName = "disCnt1";
		req1.resourceType = 3;

		try {
			serviceLayerService.create(uri, req1).getValue();
		} catch (Exception e) {
			fail();
		}

		ResourceDTO req2 = new ResourceDTO();

		uri = "-";
		req2.resourceName = "disCnt2";
		req2.resourceType = 3;

		try {
			serviceLayerService.create(uri, req2).getValue();
		} catch (Exception e) {
			fail();
		}

		ResourceDTO req3 = new ResourceDTO();

		uri = "-/disCnt1";
		req3.resourceName = "disCnt3";
		req3.resourceType = 3;

		try {
			serviceLayerService.create(uri, req3).getValue();
		} catch (Exception e) {
			fail();
		}

		List<String> uril = null;
		FilterCriteriaDTO fc = new FilterCriteriaDTO();
		fc.filterUsage = FilterUsage.DiscoveryCriteria;
		uri = "-/disCnt1";

		try {
			uril = serviceLayerService.discovery(uri, fc).getValue();
		} catch (Exception e) {
			fail();
		}

		for (String uri : uril) {
			System.out.println(uri);
			switch (uri) {
			case "cb/disCnt1":
			case "cb/disCnt1/disCnt3":
				break;
			default:
				fail();
			}
		}

		LOGGER.info("----Discovery Test 1 is complete----");
	}

	@Test
	public void testDiscovery2() {
		LOGGER.info("----Start Discovery Test 2----");

		List<String> uril = null;
		FilterCriteriaDTO fc = new FilterCriteriaDTO();
		fc.filterUsage = FilterUsage.DiscoveryCriteria;
		uri = "-/disCnt0";

		try {
			uril = serviceLayerService.discovery(uri, fc).getValue();
		} catch (Exception e) {
			fail();
		}

		assertEquals(null, uril);

		LOGGER.info("----Discovery Test 2 is complete----");
	}

	/**
	 * Class for receiving notification
	 */
	class MyListener implements NotificationListener {
		Set<RequestPrimitiveDTO> holder;

		public MyListener(Set<RequestPrimitiveDTO> holder) {
			this.holder = holder;
		}

		public void notified(RequestPrimitiveDTO request) {
			holder.add(request);
		}
	}

	@Test
	public void testNotify1() {
		LOGGER.info("----Start Notify Test 1----");
		HashSet<RequestPrimitiveDTO> set = new HashSet<RequestPrimitiveDTO>();

		NotificationListener listener = new MyListener(set);
		con.registerService(NotificationListener.class.getName(), listener, null);

		NotificationDTO notif = new NotificationDTO();
		notif.notificationEvent = new NotificationEventDTO();
		notif.notificationEvent.representation = "dummy:URI";

		Promise<Boolean> pb = serviceLayerService.notify("/in-cse/Cae1", notif);

		boolean ret = false;
		try {
			ret = pb.getValue();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
			fail();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			fail();
		}

		assertEquals(true, ret);
		assertEquals(false, set.isEmpty());
		LOGGER.info("----Discovery Notify 1 is complete----");
	}

	@Test
	public void testNotify2() {
		LOGGER.info("----Start Notify Test 2----");
		HashSet<RequestPrimitiveDTO> set = new HashSet<RequestPrimitiveDTO>();

		NotificationListener listener = new MyListener(set);
		con.registerService(NotificationListener.class.getName(), listener, null);

		NotificationDTO notif = new NotificationDTO();
		notif.notificationEvent = new NotificationEventDTO();
		notif.notificationEvent.representation = "dummy:URI";

		Promise<Boolean> pb = serviceLayerService.notify("/mn-cse/Cae2", notif);// disconnected cse

		boolean ret = false;
		try {
			ret = pb.getValue();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
			fail();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			fail();
		}

		assertEquals(false, ret);
		assertEquals(true, set.isEmpty());
		LOGGER.info("----Discovery Notify 2 is complete----");
	}

//    @Test
//   	public void testDummy(){
//    	// try to fail
//    	assertEquals("foo","bar");
//   	
//   }
}
