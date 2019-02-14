package org.osgi.test.cases.onem2m.http.json.subscription;

import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TdM2mNh22 {
	private static final Logger LOGGER = LoggerFactory.getLogger(TdM2mNh22.class);

	public ResourceDTO createSubRequest(ServiceLayer serviceLayerService, String uri, ResourceDTO sub) {
		ResourceDTO testResponse = null;
		try {
			LOGGER.info("----START SUBSCRIPTION CREATE!!!----");
			// Request for creating a Subscription..
			testResponse = serviceLayerService.create(uri, sub).getValue();
			LOGGER.info("----END SUBSCRIPTION CREATE!!!----");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warn("----SUBSCRIPTION CREATE failed----");
			return null;
		}
		return testResponse;

//		LOGGER.debug("Debug Return");
//		return null;
	}
}
