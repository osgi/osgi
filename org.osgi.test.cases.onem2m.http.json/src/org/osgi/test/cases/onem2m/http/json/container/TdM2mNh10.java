package org.osgi.test.cases.onem2m.http.json.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.osgi.test.cases.onem2m.http.json.applicationEntity.TdM2mNh06;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TdM2mNh10 {
	private static final Logger LOGGER = LoggerFactory.getLogger(TdM2mNh10.class);

	public ResourceDTO createConRequest(ServiceLayer serviceLayerService, String uri, ResourceDTO con) {

		// Create SampleAE before TdM2mNh10 test.
		TdM2mNh06 tn6 = new TdM2mNh06();
		ResourceDTO befReq = new ResourceDTO();
		String aeUri = "http://127.0.0.1:8080/in-name";
		befReq.resourceType = 2;
		befReq.resourceName = "SampleAE";
		befReq.attribute = new HashMap<String, Object>();
		befReq.setAttribute("App-ID", "SampleAE");
		befReq.setAttribute("requestReachability", true);
		List<String> poa = new ArrayList<String>();
		poa.add("http://127.0.0.1:8080/complianceTest");
		befReq.setAttribute("pointOfAccess", poa);
		tn6.createAERequest(serviceLayerService, aeUri, befReq);

		LOGGER.info("----START CONTAINER CREATE!!!----");

		ResourceDTO testResponse = null;

		try {
			// Request for creating a container.
			testResponse = serviceLayerService.create(uri, con).getValue();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warn("----CONTAINER CREATE failed----");
			return null;
		}
		return testResponse;

//		LOGGER.debug("Debug Return");
//		return null;
	}
}
