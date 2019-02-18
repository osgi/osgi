package org.osgi.test.cases.onem2m.http.json.contentInstance;

import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TdM2mNh14_1 {
	private static final Logger LOGGER = LoggerFactory.getLogger(TdM2mNh14_1.class);

	public ResourceDTO retrieveConBeforeRequest(ServiceLayer serviceLayerService, String uri) {
		ResourceDTO beforeResult = null;
		try {
			// Retrieve Container data before creating a ContentInstance
			LOGGER.info("----START CONTAINER RETRIEVE (BEFORE)!!!----");
			beforeResult = serviceLayerService.retrieve(uri).getValue();
			LOGGER.info("----END CONTAINER RETRIEVE (BEFORE)!!!----");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warn("----CONTAINER RETRIEVE (BEFORE) FAILED----");
			return null;
		}
		return beforeResult;

//		LOGGER.debug("Debug Return");
//		return null;
	}
}
