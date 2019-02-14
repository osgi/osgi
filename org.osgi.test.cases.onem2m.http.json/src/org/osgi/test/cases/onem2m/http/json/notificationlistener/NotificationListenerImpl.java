package org.osgi.test.cases.onem2m.http.json.notificationlistener;

import org.osgi.service.onem2m.NotificationListener;
import org.osgi.service.onem2m.dto.RequestPrimitiveDTO;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationListenerImpl implements NotificationListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationListenerImpl.class);
	private static String bundleSymbolicName = null;

	public void setBundleSymbolicName(String bundleSymbolicName){
		this.bundleSymbolicName = bundleSymbolicName;
	}

	public String getBundleSymbolicName(){
		return this.bundleSymbolicName;
	}

	/*
	 * (Non Javadoc)
	 * @see org.osgi.service.onem2m.NotificationListener#notified(org.osgi.service.onem2m.dto.RequestPrimitiveDTO)
	 */
	@Override
	public void notified(RequestPrimitiveDTO request) {
		LOGGER.info("receive Notify!!!");

		// receive notification.
		ResourceDTO dto = request.content.resource;
		LOGGER.debug(dto.toString());
	}

}
