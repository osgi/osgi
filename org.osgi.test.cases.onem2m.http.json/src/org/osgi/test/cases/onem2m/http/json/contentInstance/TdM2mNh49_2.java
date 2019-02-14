package org.osgi.test.cases.onem2m.http.json.contentInstance;

import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TdM2mNh49_2 {
	private static final Logger LOGGER = LoggerFactory.getLogger(TdM2mNh49_2.class);

	public ResourceDTO retrieveCinRequest(ServiceLayer serviceLayerService, String uri) {
		ResourceDTO testResponse = null;
		try {
			// Request for retrieving the latest ContentInstance in a Container.
			LOGGER.info("----START CONTAINER RETRIEVE!!!----");
			testResponse = serviceLayerService.retrieve(uri, new ResourceDTO()).getValue();
			LOGGER.info("----END CONTAINER RETRIEVE!!!----");

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warn("----CONTAINER RETRIEVE failed----");
			return null;
		}
		return testResponse;

//		LOGGER.debug("Debug Return");
//		return null;
	}
}
