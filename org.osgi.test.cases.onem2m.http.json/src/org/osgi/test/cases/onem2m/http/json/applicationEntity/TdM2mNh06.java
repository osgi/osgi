package org.osgi.test.cases.onem2m.http.json.applicationEntity;

import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TdM2mNh06 {
	private final Logger LOGGER = LoggerFactory.getLogger(TdM2mNh06.class);

	public ResourceDTO createAERequest(ServiceLayer serviceLayerService, String uri, ResourceDTO request) {
		LOGGER.info("----START AE CREATE!!!----");

		ResourceDTO testResponse = null;
		try {
			// Request for creating an AE.
			testResponse = serviceLayerService.create(uri, request).getValue();
			LOGGER.info("----END AE CREATE!!!----");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warn("----AE CREATE Failed  ----");
			return null;
		}
		return testResponse;

//		LOGGER.debug("Debug Return");
//		return null;
	}
}
