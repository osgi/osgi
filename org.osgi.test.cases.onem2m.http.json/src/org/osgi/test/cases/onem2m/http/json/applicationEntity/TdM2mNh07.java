package org.osgi.test.cases.onem2m.http.json.applicationEntity;

import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TdM2mNh07 {
	private static final Logger LOGGER = LoggerFactory.getLogger(TdM2mNh07.class);

	public ResourceDTO retrieveAERequest(ServiceLayer serviceLayerService, String uri) {
		LOGGER.info("----START AE RETRIEVE!!!----");

		ResourceDTO testResponse = null;

		try {
			// Request for retrieving AE data.
			testResponse = serviceLayerService.retrieve(uri).getValue();
			LOGGER.info("----END AE RETRIEVE!!!----");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warn("----AE RETRIEVE failed----");
			return null;
		}
		return testResponse;
	}

//	public ResourceDTO retrieveAERequest(ServiceLayer serviceLayerService, String uri) {
//		LOGGER.debug("Debug Return");
//		return null;
//	}
}