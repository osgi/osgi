package org.osgi.test.cases.onem2m.http.json.discovery;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.FilterCriteriaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TdM2mNh18_2 {
	private static final Logger LOGGER = LoggerFactory.getLogger(TdM2mNh18_2.class);

	public List<String> discoveryCinRequest(ServiceLayer serviceLayerService, String uri, FilterCriteriaDTO fc) {
		List<String> testResponse = new ArrayList<String>();
		try {
			LOGGER.info("----START CONTENTINSTANCE DISCOVERY!!!----");
			// Request for discovering an AE.
			testResponse = serviceLayerService.discovery(uri, fc).getValue();
			LOGGER.info("----END CONTENTINSTANCE DISCOVERY!!!----");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warn("----CONTENTINSTANCE DISCOVERY failed----");
			return null;
		}
		return testResponse;

//		LOGGER.debug("Debug Return");
//		return null;
	}
}
