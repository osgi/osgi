package org.osgi.test.cases.onem2m.http.json.contentInstance;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TdM2mNh49_1 {
	private static final Logger LOGGER = LoggerFactory.getLogger(TdM2mNh49_1.class);

	public ResourceDTO createSecondCinRequest(ServiceLayer serviceLayerService, String uri) {
		ResourceDTO createResponse = null;
		try {
			// Request for creating a ContentInstance; The preparation for TdM2mNh49 Test.
			LOGGER.info("----START CONTENTINSTANCE CREATE!!!----");
			ResourceDTO cin = new ResourceDTO();
			cin.resourceType = 4;
			cin.resourceName = "SampleCin999";
			Map<String, Object> attr1 = new HashMap<String, Object>();
			attr1.put("content", "1");
			cin.attribute = attr1;

			createResponse = serviceLayerService.create(uri, cin).getValue();
			LOGGER.info("----END CONTENTINSTANCE CREATE!!!----");

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warn("----CONTENTINSTANCE CREATE failed----");
			return null;
		}
		return createResponse;

//		LOGGER.debug("Debug Return");
//		return null;
	}
}
