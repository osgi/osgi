package org.osgi.test.cases.onem2m.http.json.cseBaseManagement;

import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TdM2mNh01 {
	private static final Logger LOGGER = LoggerFactory.getLogger(TdM2mNh01.class);

	public ResourceDTO retrieveCSERequest(ServiceLayer serviceLayerService, String uri) {
		ResourceDTO testResponse = null;
		try {
			// Request for Retrieving CSE-BASE data
			LOGGER.info("----START CSE RETRIEVE!!!----");
			testResponse = serviceLayerService.retrieve(uri).getValue();
			LOGGER.info("----END CSE RETRIEVE!!!----");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warn("----CSE RETRIEVE failed----");
			return null;
		}
		return testResponse;
	}

//	public ResourceDTO retrieveCSERequest(ServiceLayer serviceLayerService, String uri) {
//		LOGGER.debug("Debug Return");
//		return null;
//	}
}
