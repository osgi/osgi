package org.osgi.test.cases.onem2m.http.json.notification;

import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.NotificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TdM2mNh48 {
	private static final Logger LOGGER = LoggerFactory.getLogger(TdM2mNh48.class);

	public Boolean notifyRequest(ServiceLayer serviceLayerService, String uri, NotificationDTO notification) {
		Boolean testResponse = null;
		try {
			LOGGER.info("----START CHECKING NOTIFICATION!!!----");
			// Request for notifying.
			testResponse = serviceLayerService.notify(uri, notification)
					.getValue();

			LOGGER.info("----START CHECKING NOTIFICATION!!!----");

			/* After proccess is over, check the logs. */

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warn("----CHECKING NOTIFICATION failed----");
			return testResponse;
		}
		return testResponse;

//		LOGGER.debug("Debug Return");
//		return false;
	}
}
