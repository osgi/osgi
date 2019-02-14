package org.osgi.test.cases.onem2m.http.json.applicationEntity;

import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TdM2mNh08{
	private static final Logger LOGGER = LoggerFactory.getLogger(TdM2mNh08.class);

	public  ResourceDTO updateAERequest(ServiceLayer serviceLayerService, String uri, ResourceDTO req)  {
		LOGGER.info("----START AE UPDATE!!!----");

		ResourceDTO testResponse = null;

		try {
			// Request for updating AE data.
			testResponse = serviceLayerService.update(uri, req).getValue();
			LOGGER.info("----END AE UPDATE!!!----");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warn("----AE UPDATE failed----");
			return null;
		}
		return testResponse;
	}

//	public  ResourceDTO updateAERequest(ServiceLayer serviceLayerService, String uri, ResourceDTO req)  {
//		LOGGER.debug("Debug Return");
//		return null;
//	}
}