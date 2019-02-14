package org.osgi.service.onem2m.ae.sample;

import org.osgi.service.onem2m.NotificationListener;
import org.osgi.service.onem2m.dto.RequestPrimitiveDTO;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationListenerImpl implements NotificationListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationListenerImpl.class);

	@Override
	public void notified(RequestPrimitiveDTO request) {
		LOGGER.info("receive Notify!!!");

		ResourceDTO dto = request.content.resource;

		LOGGER.debug(dto.toString());
	}

}
