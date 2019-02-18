package org.osgi.test.cases.onem2m.http.json.contentInstance;

import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TdM2mNh15{
	private static final Logger LOGGER = LoggerFactory.getLogger(TdM2mNh15.class);

	public ResourceDTO retrieveCinRequest(ServiceLayer serviceLayerService, String uri) {
		ResourceDTO testResponse = null;
		try {
		// Request for Retrieving AE data.
		LOGGER.info("----START CONTENTINSTANCE RETRIEVE!!!----");
		testResponse  = serviceLayerService.retrieve(uri).getValue();
		LOGGER.info("----END CONTENTINSTANCE RETRIEVE!!!----");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warn("----CONTENTINSTANCE RETRIEVE failed----");
			return null;
		}
		return testResponse;

//		LOGGER.debug("Debug Return");
//		return null;
	}
}
