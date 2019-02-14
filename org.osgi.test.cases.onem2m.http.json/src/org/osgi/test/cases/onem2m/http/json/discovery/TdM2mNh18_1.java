package org.osgi.test.cases.onem2m.http.json.discovery;

import java.util.ArrayList;
import java.util.HashMap;

import org.osgi.service.onem2m.ServiceLayer;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TdM2mNh18_1 {
	private static final Logger LOGGER = LoggerFactory.getLogger(TdM2mNh18_1.class);

	public Boolean createCinRequest(ServiceLayer serviceLayerService, String uri) {
		try {
			ResourceDTO tn181res1 = new ResourceDTO();
			tn181res1.resourceName = "SampleCin99";
			tn181res1.resourceType = 4;
			tn181res1.attribute = new HashMap<String, Object>();
			tn181res1.setAttribute("content", "99");
			serviceLayerService.create(uri, tn181res1);

			ResourceDTO tn181res2 = new ResourceDTO();
			tn181res2.resourceName = "SampleCin88";
			tn181res2.resourceType = 4;
			tn181res2.attribute = new HashMap<String, Object>();
			tn181res2.setAttribute("content", "88");
			tn181res2.labels = new ArrayList<String>();
			tn181res2.labels.add("1");
			serviceLayerService.create(uri, tn181res2);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warn("----failed : Preparations for TdM2mNh18 Test ----");
			return false;
		}
		return true;

//		LOGGER.debug("Debug Return");
//		return false;
	}
}
