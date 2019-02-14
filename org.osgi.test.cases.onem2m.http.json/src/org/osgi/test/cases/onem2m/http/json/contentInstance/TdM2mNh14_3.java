package org.osgi.test.cases.onem2m.http.json.contentInstance;

import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TdM2mNh14_3 {
	private static final Logger LOGGER = LoggerFactory.getLogger(TdM2mNh14_3.class);

	public ResourceDTO retrieveConAfterRequest(ServiceLayer serviceLayerService, String uri) {
		ResourceDTO afterResult = null;

		try {
			// Retrieve Container data after creating a ContentInstance
			LOGGER.info("----START CONTAINER RETRIEVE (AFTER)!!!----");
			afterResult = serviceLayerService.retrieve(uri, new ResourceDTO()).getValue();
			LOGGER.info("----END CONTAINER RETRIEVE (AFTER)!!!----");

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("----CONTAINER RETRIEVE (AFTER) failed----");
			return null;
		}
		return afterResult;

//		LOGGER.debug("Debug Return");
//		return null;
	}
}
