package org.osgi.test.cases.onem2m.http.json.applicationEntity;

import org.osgi.service.onem2m.ServiceLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TdM2mNh09{
	private static final Logger LOGGER = LoggerFactory.getLogger(TdM2mNh09.class);

	public Boolean deleteAERequest(ServiceLayer serviceLayerService, String uri)  {
		LOGGER.info("----START AE DELETE!!!----");

		Boolean successFlg = null;
		try {
			// Request for updating AE data.
			successFlg = serviceLayerService.delete(uri).getValue();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warn("----AE DELETE failed----");
		}
		return successFlg;
	}

//	public Boolean deleteAERequest(ServiceLayer serviceLayerService, String uri)  {
//		LOGGER.debug("Debug Return");
//		return false;
//	}
}