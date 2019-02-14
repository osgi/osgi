package org.osgi.test.cases.onem2m.http.json.contentInstance;

import org.osgi.service.onem2m.ServiceLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TdM2mNh49_3 {
	private static final Logger LOGGER = LoggerFactory.getLogger(TdM2mNh49_3.class);

	public Boolean deleteCinRequest(ServiceLayer serviceLayerService, String uri) {
		Boolean deleteResponse = null;
		try {

			// Request for deleting a ContentInstance.
			LOGGER.info("----START CONTENTINSTANCE DELETE!!!----");
			deleteResponse = serviceLayerService.delete(uri).getValue();
			LOGGER.info("----END CONTENTINSTANCE DELETE!!!----");

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warn("----CONTENTINSTANCE DELETE failed----");
			return deleteResponse;
		}
		return deleteResponse;

//		LOGGER.debug("Debug Return");
//		return null;
	}
}
