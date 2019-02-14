package org.osgi.test.cases.onem2m.http.json.contentInstance;

import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TdM2mNh14_2 {
	private static final Logger LOGGER = LoggerFactory.getLogger(TdM2mNh14_2.class);

	public ResourceDTO createCinRequest(ServiceLayer serviceLayerService, String uri, ResourceDTO cin) {
		LOGGER.info("----START CONTENTINSTANCE CREATE!!!----");

		ResourceDTO testResponse = null;

		try {
			// Request for creating an AE.
			testResponse = serviceLayerService.create(uri, cin).getValue();
			LOGGER.info("----END CONTENTINSTANCE CREATE!!!----");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warn("----CONTENTINSTANCE CREATE failed----");
			return null;
		}
		return testResponse;

//		LOGGER.debug("Debug Return");
//		return null;
	}
}
