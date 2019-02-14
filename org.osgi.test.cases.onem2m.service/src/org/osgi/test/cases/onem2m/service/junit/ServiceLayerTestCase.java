package org.osgi.test.cases.onem2m.service.junit;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.FilterCriteriaDTO;
import org.osgi.service.onem2m.dto.FilterCriteriaDTO.FilterUsage;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.osgi.test.support.OSGiTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceLayerTestCase extends OSGiTestCase{

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLayerTestCase.class);

	private ServiceLayer serviceLayerService = null;
	private String uri = null;
	private BundleContext con = null;

    protected void setUp() throws Exception {
    	if(serviceLayerService == null){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				fail();
				return;
			}
			con = getContext();
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


	public void testRetrieve1(){
		LOGGER.info("----Start RETRIEVE Test 1----");

		ResourceDTO res = null;

		uri = "/in-cse";

		try {
			res = serviceLayerService.retrieve(uri, new ResourceDTO()).getValue();
		} catch (Exception e) {
			res = null;
		}

		// Check result
		LOGGER.info("Response Data:\n" + res.toString());

		assertNotNull("Response is Null.", res);
		assertEquals(res.resourceName, "in-cse");
		assertEquals(String.valueOf(res.resourceType), "5");
		assertEquals(res.resourceID, "0");
		assertNotNull("LastModifiedTime is Null", res.lastModifiedTime);
		assertNotNull("CreationTime is Null.", res.creationTime);

		LOGGER.info("----RETRIEVE Test 1 is complete----");
	}

	public void testRetrieve2(){
		LOGGER.info("----Start RETRIEVE Test 2----");

		ResourceDTO res = null;

		uri = "/in-cse2";

		try {
			res = serviceLayerService.retrieve(uri, new ResourceDTO()).getValue();
		} catch (Exception e) {
			res = null;
		}

		// Check result
		assertEquals(null, res);

		LOGGER.info("----RETRIEVE Test 2 is complete----");
	}


	public void testCreate1(){
		LOGGER.info("----Start CREATE Test 1----");

		ResourceDTO req = new ResourceDTO();
		ResourceDTO res = null;

		uri = "/in-cse";
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


	public void testCreate2(){
		LOGGER.info("----Start CREATE Test 2----");

		ResourceDTO req = new ResourceDTO();
		ResourceDTO res = null;

		uri = "/in-cse";
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

	public void testCreate3(){
		LOGGER.info("----Start CREATE Test 3----");

		ResourceDTO req = new ResourceDTO();
		ResourceDTO res = new ResourceDTO();

		uri = "/in-cse2";
		req.resourceName = "cnt2";
		req.resourceType = 3;

		try {
			res = serviceLayerService.create(uri, req).getValue();
		} catch (Exception e) {
			res = null;
		}

		// Check result
		assertEquals(null, res);

		LOGGER.info("----CREATE Test 3 is complete----");
	}

	public void testUpdate1(){
		LOGGER.info("----Start Update Test 1----");

		ResourceDTO req = new ResourceDTO();
		ResourceDTO res = null;

		uri = "/in-cse";
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

		uri = "/in-cse/updateCnt";
		req2.resourceName = res.resourceName;
		req2.resourceType = res.resourceType;
		req2.resourceID = res.resourceID;
		List<String> lbl = new ArrayList<String>();
		lbl.add(lblText);
		req2.labels = lbl;

		try {
			Thread.sleep(1000);
			serviceLayerService.update(uri, req2).getValue();
		} catch (Exception e) {
			fail();
		}

		ResourceDTO res3 = null;

		uri = "/in-cse/updateCnt";

		try {
			res3 = serviceLayerService.retrieve(uri, new ResourceDTO()).getValue();
		} catch (Exception e) {
			fail();
		}

		// Check result
		System.out.println(res3.toString());
		assertEquals(res.resourceName, res3.resourceName);
		assertEquals(res.resourceType, res3.resourceType);
		assertEquals(res.resourceID, res3.resourceID);
		assertEquals(res.creationTime, res3.creationTime);
		if(res.lastModifiedTime.equals(res3.lastModifiedTime)){
			fail();
		}
		assertEquals(lblText, res3.labels.get(0));

		LOGGER.info("----Update Test 1 is complete----");
	}

	public void testUpdate2(){
		LOGGER.info("----Start Update Test 2----");

		ResourceDTO req = new ResourceDTO();
		ResourceDTO res = null;

		String lblText = "updateTest";

		uri = "/in-cse/updateCnt2";
		List<String> lbl = new ArrayList<String>();
		lbl.add(lblText);
		req.labels = lbl;

		try {
			res = serviceLayerService.create(uri, req).getValue();
		} catch (Exception e) {
			res = null;
		}

		// Check result
		assertEquals(null, res);

		LOGGER.info("----Update Test 2 is complete----");
	}


	public void testDelete1(){
		LOGGER.info("----Start Delete Test 1----");

		ResourceDTO req = new ResourceDTO();

		uri = "/in-cse";
		req.resourceName = "deleteCnt";
		req.resourceType = 3;

		try {
			serviceLayerService.create(uri, req).getValue();
		} catch (Exception e) {
			fail();
		}

		uri = "/in-cse/deleteCnt";

		try {
			if(!serviceLayerService.delete(uri).getValue()){
				fail();
			}
		} catch (Exception e) {
			fail();
		}

		LOGGER.info("----Delete Test 1 is complete----");
	}


	public void testDelete2(){
		LOGGER.info("----Start Delete Test 2----");

		uri = "/in-cse/deleteCnt2";

		try {
			if(serviceLayerService.delete(uri).getValue()){
				fail();
			}
		} catch (Exception e) {
			fail();
		}

		LOGGER.info("----Delete Test 2 is complete----");
	}

	public void testDiscovery1(){
		LOGGER.info("----Start Discovery Test 1----");

		ResourceDTO req1 = new ResourceDTO();

		uri = "/in-cse";
		req1.resourceName = "disCnt1";
		req1.resourceType = 3;

		try {
			serviceLayerService.create(uri, req1).getValue();
		} catch (Exception e) {
			fail();
		}

		ResourceDTO req2 = new ResourceDTO();

		uri = "/in-cse";
		req2.resourceName = "disCnt2";
		req2.resourceType = 3;

		try {
			serviceLayerService.create(uri, req2).getValue();
		} catch (Exception e) {
			fail();
		}

		ResourceDTO req3 = new ResourceDTO();

		uri = "/in-cse/disCnt1";
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
		uri = "/in-cse/disCnt1";

		try {
			uril = serviceLayerService.discovery(uri, fc).getValue();
		} catch (Exception e) {
			fail();
		}

		for(String uri : uril){
			System.out.println(uri);
			switch(uri){
				case "/in-cse/disCnt1":
				case "/in-cse/disCnt1/disCnt3":
					break;
				default:
					fail();
			}
		}

		LOGGER.info("----Discovery Test 1 is complete----");
	}


	public void testDiscovery2(){
		LOGGER.info("----Start Discovery Test 2----");

		List<String> uril = null;
		FilterCriteriaDTO fc = new FilterCriteriaDTO();
		fc.filterUsage = FilterUsage.DiscoveryCriteria;
		uri = "/in-cse/disCnt0";

		try {
			uril = serviceLayerService.discovery(uri, fc).getValue();
		} catch (Exception e) {
			fail();
		}

		assertEquals(null, uril);

		LOGGER.info("----Discovery Test 2 is complete----");
	}
}

